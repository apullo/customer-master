package co.cvdcc.brojekcustomer.home.submenu.setting;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import co.cvdcc.brojekcustomer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TermOfServiceActivity extends AppCompatActivity {
    @BindView(R.id.web_view)
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_of_service);
        ButterKnife.bind(this);

        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl("http://gantar.greative.co.id/asset/syaratketentuan.html");

    }

}
