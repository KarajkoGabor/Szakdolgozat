package hu.blogspot.limarapeksege.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.io.File;

import hu.blogspot.limarapeksege.R;
import hu.blogspot.limarapeksege.asyncs.AsyncPrepareRecipeDatas;
import hu.blogspot.limarapeksege.util.GlobalStaticVariables;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setFullScreen();

        setContentView(R.layout.activity_splash);
//        setLatestUploadDate();
//        clearApplicationData();

        AsyncPrepareRecipeDatas asyncPrepareRecipeDatas = new AsyncPrepareRecipeDatas(SplashActivity.this, SplashActivity.this);
        asyncPrepareRecipeDatas.execute();

    }

    private void setFullScreen() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getActionBar();
            assert actionBar != null;
            actionBar.hide();
        }
    }

    private void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i(GlobalStaticVariables.LOG_TAG, "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        assert dir != null;
        return dir.delete();
    }

    private void setLatestUploadDate(){
        SharedPreferences savedSettings = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = savedSettings.edit();

        long dateLong = 1456790400000L; //2016.03.01.
        editor.putLong("last_modified", dateLong);
        editor.commit();
    }

}
