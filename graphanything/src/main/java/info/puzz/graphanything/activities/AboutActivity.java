package info.puzz.graphanything.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import info.puzz.graphanything.R;

public class AboutActivity extends BaseActivity {

    private TextView versionCodeTextView;
    private TextView versionNameTextView;

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, AboutActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        PackageManager manager = this.getPackageManager();

        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            throw new Error(e.getMessage(), e);
        }

        versionCodeTextView = (TextView) findViewById(R.id.about__version_code);
        versionNameTextView = (TextView) findViewById(R.id.about__version_name);

        versionCodeTextView.setText(String.valueOf(info.versionCode));
        versionNameTextView.setText(info.versionName);

        setTitle(R.string.app_info);
    }
}
