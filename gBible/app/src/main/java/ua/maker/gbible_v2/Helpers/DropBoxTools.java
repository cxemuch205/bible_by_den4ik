package ua.maker.gbible_v2.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;
import com.nispok.snackbar.Snackbar;

import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.DataBases.UserDB;
import ua.maker.gbible_v2.GBApplication;
import ua.maker.gbible_v2.R;
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
        return dbxDatastoreManager;
    }

    public static DbxAccountManager getDbxAccountManager() {
        return dbxAccountManager;
    }

    public static void toggleSyncWithDropBox(boolean enable) {
        if (enable) {
            getDbxDatastoreManager();
            getInstance().sync();
        } else {
            getDbxAccountManager().unlink();
            dbxDatastoreManager = null;
            getDbxDatastoreManager();

            UserDB.openDbxDatastore();
        }
    }

    public static void startLink(Activity activity) {
        getDbxAccountManager().startLink(activity, REQUEST_LINK_DBX);
    }

    public static boolean onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                DbxAccount account = getDbxAccountManager().getLinkedAccount();
                try {
                    if (UserDB.getDbxDatastore() != null && UserDB.getDbxDatastore().isOpen()) {
                        UserDB.getDbxDatastore().close();
                    }
                    dbxDatastoreManager.migrateToAccount(account);
                    setupDbxDatastoreManager(account);
                    return true;
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            } else {
                Snackbar.with(activity)
                        .text(R.string.error_connect_dropbox)
                        .show(activity);
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

    public void sync() {
        try {
            if (UserDB.getDbxDatastore() != null) {
                if (UserDB.getDbxDatastore().isOpen()) {
                    UserDB.getDbxDatastore().sync();
                } else {
                    UserDB.openDbxDatastore();
                    sync();
                }
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }
}
