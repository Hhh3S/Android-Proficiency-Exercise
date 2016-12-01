package com.anvata.gankio;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.anvata.gankio.util.SizeUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.wv_page)
    WebView mWebView;
    @BindView(R.id.toolBar)
    Toolbar toolBar;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    private static final String TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        toolBar.setTitle(getIntent().getStringExtra("title"));
        toolBar.setTitleMarginEnd(SizeUtils.dp2px(this,50));
        setSupportActionBar(toolBar);
        toolBar.setNavigationIcon(R.mipmap.ic_arrow_back_white_24dp);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        initWebView(getIntent().getStringExtra("url"));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.setVisibility(View.GONE);

    }

    private void initWebView(String url) {

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        // 开启 DOM storage API 功能
        mWebView.getSettings().setDomStorageEnabled(true);
        //开启 database storage API 功能
        mWebView.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME;
//      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        Log.i(TAG, "cacheDirPath=" + cacheDirPath);
        //设置数据库缓存路径
        mWebView.getSettings().setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        mWebView.getSettings().setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        mWebView.getSettings().setAppCacheEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onLoadResource(WebView view, String url) {

                Log.i(TAG, "onLoadResource url=" + url);

                super.onLoadResource(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webview, String url) {

                Log.i(TAG, "intercept url=" + url);

                webview.loadUrl(url);

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                Log.e(TAG, "onPageStarted");

                mProgressBar.setVisibility(View.VISIBLE); // 显示加载界面
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                mProgressBar.setVisibility(View.GONE); // 隐藏加载界面
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                mProgressBar.setVisibility(View.GONE); // 隐藏加载界面

                Toast.makeText(getApplicationContext(), "",
                        Toast.LENGTH_LONG).show();
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                Log.e(TAG, "onJsAlert " + message);

                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                result.confirm();

                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {

                Log.e(TAG, "onJsConfirm " + message);

                return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {

                Log.e(TAG, "onJsPrompt " + url);

                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });

        mWebView.loadUrl(url);
    }


    /**
     * 清除WebView缓存
     */
    public void clearWebViewCache() {

        //清理Webview缓存数据库
        try {
            deleteDatabase("webview.db");
            deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件
        File appCacheDir = new File(getFilesDir().getAbsolutePath() + APP_CACAHE_DIRNAME);
        Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());

        File webviewCacheDir = new File(getCacheDir().getAbsolutePath() + "/webviewCache");
        Log.e(TAG, "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

        //删除webview 缓存目录
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir);
        }
        //删除webview 缓存 缓存目录
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir);
        }
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    public void deleteFile(File file) {

        Log.i(TAG, "delete file path=" + file.getAbsolutePath());

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
        }
    }
}
