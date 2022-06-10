package kr.ac.kpu.diyequipmentapplication.menu;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import kr.ac.kpu.diyequipmentapplication.R;

public class LocationSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new BridgeInterface(), "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //안드로이드에서 자바스크립트 함수 호출!
                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        //최초 웹뷰 로드
        webView.loadUrl("https://diy-equipment.web.app");
    }

    private class BridgeInterface {
        @JavascriptInterface
        public void processDATA(String data) {
            //주소 검색 결과 값이 브릿지 통로를 통해 전달 받는메소드
            Intent intent = new Intent();
            intent.putExtra("data", data);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}