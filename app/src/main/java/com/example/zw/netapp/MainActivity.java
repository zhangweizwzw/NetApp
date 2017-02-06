package com.example.zw.netapp;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private ImageView title_left, title_right;
    private TextView title_center;
    private Context context;
    private Handler handler;
    private String context1;
    private String weburl = "";
    private String shareimgpath = "";
    private String title = "";
    private WebView jiazai;
    private RelativeLayout netping;
    private ImageView pllist;
    private Button gopl;
    private EditText plcon;
    private String urlid = "";
    WebView webView1;
    WebSettings mWebSettings;

    private Location location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();
    }

    private void setUpViews() {
        webView1= (WebView) findViewById(R.id.aaa);
        webView1.setWebChromeClient(new WebChromeClient());

        mWebSettings = webView1.getSettings();
        mWebSettings.setJavaScriptEnabled(true); // 允许加载javascript
        mWebSettings.setSupportZoom(true); // 允许缩放
        mWebSettings.setBuiltInZoomControls(true); // 原网页基础上缩放
        mWebSettings.setUseWideViewPort(true); // 任意比例缩放
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        mWebSettings.setAppCacheEnabled(true);//是否使用缓存
        mWebSettings.setDomStorageEnabled(true);//DOM Storage

        /*****************************************************************
         * 在点击请求的是链接时才会调用，重写此方法返回true表明点击网页里 面的链接还是在当前的WebView里跳转，不会跳到浏览器上运行。
         *****************************************************************/
        webView1.setWebViewClient(new WebViewClient() {
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                handler.proceed();  // 接受所有网站的证书
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);

                    return true;
                } else {
                    view.loadUrl(url);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
//        webView1.loadUrl("http://192.168.1.109:88/rm/jyzxggInfo?bh=1");
        webView1.loadUrl("http://www.baidu.com/");
    }

}
