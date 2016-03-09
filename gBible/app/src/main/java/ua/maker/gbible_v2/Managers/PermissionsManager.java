package ua.maker.gbible_v2.Managers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.inject.Inject;

import ua.maker.gbible_v2.Constants.IntentData;

/**
 * Created by daniilpavenko on 3/9/16.
 */
public class PermissionsManager {

    private Activity activity;

    @Inject
    public PermissionsManager(Activity activity) {
        this.activity = activity;
    }

    public boolean requestPermissions() {
        if (!checkReadStoragePermissionGranted()
                && !checkWriteStoragePermissionGranted()
                && !checkNetworkStatePermissionGranted())
        {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    IntentData.REQUEST_PERMISSIONS_GBIBLE);
            return false;
        }
        return true;
    }

    public boolean onRequestPermissionsResult(int requestCode,
                                              String permissions[], int[] grantResults) {
        switch (requestCode) {
            case IntentData.REQUEST_PERMISSIONS_GBIBLE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Permissions granted!");
                    return true;
                } else {
                    System.out.println("Permissions blocked!");
                    activity.finish();
                    return false;
                }
            }
        }
        return false;
    }

    private boolean checkNetworkStatePermissionGranted() {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkWriteStoragePermissionGranted() {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkReadStoragePermissionGranted() {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }
}
