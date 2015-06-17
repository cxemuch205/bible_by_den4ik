package ua.maker.gbible_v2.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;

import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.GBApplication;
import ua.maker.gbible_v2.SettingsActivity;

/**
 * Created by Daniil on 14-Mar-15.
 */
public class DropBoxTools {

    private static DropBoxTools instance;
    public static final int REQUEST_LINK_DBX = 121;
    private Context context;

    public static DropBoxTools getInstance() {
        if (instance == null) {
            instance = new DropBoxTools(GBApplication.getInstance());
        }
        return instance;
    }

    private static DbxAccountManager dbxAccountManager = null;
    private static DbxDatastoreManager dbxDatastoreManager = null;

    public DropBoxTools(Context context) {
        instance = this;
        this.context = context;
        dbxAccountManager = DbxAccountManager.getInstance(context, App.DropBox.API_KEY, App.DropBox.API_SECRET);
    }

    public static DbxDatastoreManager getDbxDatastoreManager() {
        if (dbxDatastoreManager == null) {
            try {
                dbxDatastoreManager = DbxDatastoreManager.forAccount(getDbxAccountManager().getLinkedAccount());
            } catch (DbxException.Unauthorized unauthorized) {
                unauthorized.printStackTrace();
            }
        }
        return dbxDatastoreManager;
    }

    public static DbxAccountManager getDbxAccountManager() {
        return dbxAccountManager;
    }

    public static void toggleSyncWithDropBox(boolean enable) {
        if (enable) {
            if (getDbxAccountManager().hasLinkedAccount()) {
                try {
                    dbxDatastoreManager = DbxDatastoreManager.forAccount(getDbxAccountManager().getLinkedAccount());
                } catch (DbxException.Unauthorized unauthorized) {
                    unauthorized.printStackTrace();
                }
            }
            if (dbxDatastoreManager == null) {
                dbxDatastoreManager = DbxDatastoreManager.localManager(dbxAccountManager);
            }
        } else {
            dbxAccountManager.unlink();
        }
    }

    public static void startLink(Activity activity) {
        getDbxAccountManager().startLink(activity, REQUEST_LINK_DBX);
    }

    public static boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                DbxAccount account = getDbxAccountManager().getLinkedAccount();
                try {
                    getDbxDatastoreManager().migrateToAccount(account);
                    setupDbxDatastoreManager(account);
                    return true;
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            } else {
                Tools.showToastCenter(getInstance().context, "CAN`T CONNECT TO YOUR DROPBOX");
            }
        }
        return false;
    }

    private static void setupDbxDatastoreManager(DbxAccount account) {
        if (account != null) {
            try {
                dbxDatastoreManager = DbxDatastoreManager.forAccount(account);
            } catch (DbxException.Unauthorized unauthorized) {
                unauthorized.printStackTrace();
            }
        }
    }
}
