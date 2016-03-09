package ua.maker.gbible_v2.Helpers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxDatastoreManager;
import com.dropbox.sync.android.DbxException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import ua.maker.gbible_v2.Constants.App;
import ua.maker.gbible_v2.DataBases.UserDB;
import ua.maker.gbible_v2.GBApplication;
import ua.maker.gbible_v2.R;

/**
 * Created by Daniil on 14-Mar-15.
 */
@Singleton
public class DropBoxTools {

    public static final int REQUEST_LINK_DBX = 121;

    private Context context;
    private Provider<UserDB> userDB;

    private static DbxAccountManager dbxAccountManager = null;
    private DbxDatastoreManager dbxDatastoreManager = null;

    @Inject
    public DropBoxTools(Context context, Provider<UserDB> userDB) {
        this.context = context;
        this.userDB = userDB;
    }

    public static void initDbxAccountManager(Application application) {
        DropBoxTools.dbxAccountManager = DbxAccountManager.getInstance(application, App.DropBox.API_KEY, App.DropBox.API_SECRET);
    }

    public DbxDatastoreManager getDbxDatastoreManager() {
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

    public DbxAccountManager getDbxAccountManager() {
        return dbxAccountManager;
    }

    public void toggleSyncWithDropBox(boolean enable) {
        if (enable) {
            getDbxDatastoreManager();
            sync();
        } else {
            getDbxAccountManager().unlink();
            dbxDatastoreManager = null;
            getDbxDatastoreManager();

            userDB.get().openDbxDatastore();
        }
    }

    public void startLink(Activity activity) {
        getDbxAccountManager().startLink(activity, REQUEST_LINK_DBX);
    }

    public boolean onActivityResult(View view, int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                DbxAccount account = getDbxAccountManager().getLinkedAccount();
                try {
                    if (userDB.get().getDbxDatastore() != null && userDB.get().getDbxDatastore().isOpen()) {
                        userDB.get().getDbxDatastore().close();
                    }
                    dbxDatastoreManager.migrateToAccount(account);
                    setupDbxDatastoreManager(account);
                    return true;
                } catch (DbxException e) {
                    e.printStackTrace();
                }
            } else {
                Snackbar.make(
                        view,
                        R.string.error_connect_dropbox,
                        Snackbar.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    private void setupDbxDatastoreManager(DbxAccount account) {
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
            if (userDB.get().getDbxDatastore() != null) {
                if (userDB.get().getDbxDatastore().isOpen()) {
                    userDB.get().getDbxDatastore().sync();
                } else {
                    userDB.get().openDbxDatastore();
                    sync();
                }
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }
}
