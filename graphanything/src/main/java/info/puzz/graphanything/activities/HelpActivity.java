package info.puzz.graphanything.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import info.puzz.graphanything.R;
import info.puzz.graphanything.databinding.ActivityHelpBinding;

public class HelpActivity extends BaseActivity {

    private static final String ARG_HTML = "html";
    private static final String ARG_TITLE = "title";
    private WebView webView;

    public static void start(BaseActivity activity, String title, String html) {
        Intent intent = new Intent(activity, HelpActivity.class);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_HTML, html);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityHelpBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_help);

        String title = getIntent().getStringExtra(ARG_TITLE);
        String html = getIntent().getStringExtra(ARG_HTML);

        setTitle(title);
        binding.helpViewer.loadData(html, "text/html", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    public void onClose(MenuItem item) {
        onBackPressed();
    }
}
