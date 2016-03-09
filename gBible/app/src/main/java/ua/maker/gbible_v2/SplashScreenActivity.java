package ua.maker.gbible_v2;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Managers.IntentManager;
import ua.maker.gbible_v2.Managers.PermissionsManager;

@ContentView(R.layout.activity_splash_screen)
public class SplashScreenActivity extends RoboActivity {

    @Inject PermissionsManager permissionsManager;
    @Inject IntentManager intentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        if (permissionsManager.requestPermissions()) {
            startApp();
        } else {
            Tools.showToastCenter(this, getString(R.string.you_need_acces_permissions_granted));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            startApp();
        } else {
            finish();
        }
    }

    private void startApp() {
        intentManager.startBaseActivity();
    }
}
