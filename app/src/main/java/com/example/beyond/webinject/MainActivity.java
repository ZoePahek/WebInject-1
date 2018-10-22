package com.example.beyond.webinject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;

import static android.webkit.WebSettings.LOAD_CACHE_ELSE_NETWORK;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "myVersion";
    private ProgressBar pbProgress;
    private WebView wvWeb;
    private String strGetTicketJs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        strGetTicketJs = readJsFromAsset("get_ticket.js");
    }

    private void initView() {
        pbProgress = findViewById(R.id.pbProgress);
        wvWeb = findViewById(R.id.wbWeb);
        initWebView();
        wvWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                setProgressBar(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });
        wvWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                injectJs(strGetTicketJs);
            }
        });
        wvWeb.setDownloadListener(new MyWebViewDownLoadListener());
        loadUrl("https://detail.m.tmall.com/item.htm?spm=a320p.7692363.0.0.130a7821KUofFL&id=15058659488&pic=//img.alicdn.com/bao/uploaded/i1/845001562/O1CN011NPR7g3mvFIsa8l_!!0-item_pic.jpg_Q50s50.jpg_.webp&itemTitle=innisfree%E6%82%A6%E8%AF%97%E9%A3%8E%E5%90%9F%E7%81%AB%E5%B1%B1%E5%B2%A9%E6%B3%A5%E6%AF%9B%E5%AD%94%E6%B8%85%E6%B4%81%E6%B4%97%E9%9D%A2%E5%A5%B6%E6%B4%81%E9%9D%A2%E4%B9%B3%E3%80%90%E5%8F%8C11%E9%A2%84%E5%94%AE%E3%80%91&price=70.00&from=h5&skuId=25745423228");
    }

    // webview下载
    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    private void injectJs(final String strJs) {
        wvWeb.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadUrl("javascript:" + strJs);
            }
        }, 500);
    }

    private void loadUrl(String strUrl) {
        if (wvWeb != null) {
            wvWeb.loadUrl(strUrl);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        String cachePath = this.getCacheDir() + "/org.chromium.android_webview";
        WebSettings webSettings = wvWeb.getSettings();
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);// 默认的缓存方式
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheMaxSize(100 * 1024 * 1024L);// 上限设置为100M
        webSettings.setAppCachePath(cachePath);
        webSettings.setDatabasePath(cachePath);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setBuiltInZoomControls(true); // 设置显示缩放按钮
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setTextZoom(100);
        webSettings.setCacheMode(LOAD_CACHE_ELSE_NETWORK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webSettings.setJavaScriptEnabled(true); // 允许加载JS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);  //chrome联调
        }
    }

    public String readJsFromAsset(String fileName) {
        InputStream inputStream;
        AssetManager assetManager = getAssets();
        byte[] buffer;
        int size;

        if (fileName == null || fileName.length() == 0) {
            return null;
        }

        try {
            inputStream = assetManager.open(fileName);
            size = inputStream.available();
            buffer = new byte[size];
            int result = inputStream.read(buffer);
            if (result != size) {
                Log.e(TAG, "read size error.");
            }
            inputStream.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e(TAG, "open asset fail " + e.toString());
        }
        return null;
    }


    private void setProgressBar(int progress) {
        if (progress > 0 && progress < 100) {
            pbProgress.setProgress(progress);
            pbProgress.setVisibility(View.VISIBLE);
        } else if (pbProgress.getVisibility() == View.VISIBLE){
            pbProgress.setVisibility(View.GONE);
        }
    }

    private class TicketJavaScriptInterface {

    }
}
