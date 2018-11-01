package com.jbufa.firefighting.activity;

import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.jbufa.firefighting.R;

public class HelperActivity extends BaseActivity {

    private ImageButton back_btn;
    private WebView webHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);
        back_btn = findViewById(R.id.back_btn);
        webHelp = findViewById(R.id.webHelp);
        webHelp.getSettings().setSupportZoom(true);
        webHelp.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        webHelp.getSettings().setJavaScriptEnabled(true);
//        webHelp.getSettings().setUseWideViewPort(true);
//        webHelp.getSettings().setLoadWithOverviewMode(true);
                    webHelp.loadUrl("https://shimo.im/docs/vDIQd5aBkMARFfeZ/");
        back_btn.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick (View view){
                finish();
            }
            });
        }

        @Override
        protected void onDestroy () {
            super.onDestroy();
            webHelp.removeAllViews();
            webHelp.destroy();
        }
        @Override
        public boolean onKeyDown ( int keyCode, KeyEvent event){
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish();
            }
            return false;
        }
    }
