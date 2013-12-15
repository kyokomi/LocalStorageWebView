package com.kyokomi.localstoragewebviewapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            if (rootView == null) {
                throw new RuntimeException("inflater root view null error.");
            }

            try {
                String rootDir = "web";
                copy2Local(rootDir);

                WebView webView = (WebView) rootView.findViewById(R.id.webView);
                webView.loadUrl("file:///"+ getActivity().getFilesDir() + "/" + rootDir + "/index.html");

            } catch (IOException e) {
                Log.e("TAG", e.getLocalizedMessage(), e);
            }

            return rootView;
        }

        private void copy2Local(String rootDir) throws IOException {
            AssetManager as = getResources().getAssets();

            // assetsから読み込み、出力する
            String[] fileList = as.list(rootDir);
            if(fileList == null || fileList.length == 0){
                Log.d("TAG", "not file");
                return;
            }
            Log.d("TAG", "fileList = " + fileList);
            InputStream input = null;
            FileOutputStream output = null;

            for(String file : fileList) {
                String filePath = rootDir + "/" + file;
                Log.d("TAG", "filePath = " + filePath);
                // ディレクトリだったら再帰的に処理する
                if (as.list(filePath).length > 0) {
                    copy2Local(filePath);
                    continue;
                }
                input = as.open(filePath);

                // ディレクトリはほっておく
                String outputFilePath = getActivity().getFilesDir() + "/" + filePath;
                File outputFile = new File(outputFilePath);
                Log.d("TAG", "outputFilePath = " + outputFilePath);
                if (!outputFile.getParentFile().exists()) {
                    outputFile.getParentFile().mkdirs();
                }

                output = new FileOutputStream(outputFile);

                int DEFAULT_BUFFER_SIZE = 1024 * 4;

                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int n = 0;
                while (-1 != (n = input.read(buffer))) {
                    output.write(buffer, 0, n);
                }
                output.close();
                input.close();
            }
        }
    }
}
