package ua.maker.gbible_v2.Helpers;

import android.support.annotation.NonNull;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;

import ua.maker.gbible_v2.Constants.App;

/**
 * Created by Daniil on 14-Mar-15.
 */
public class DropBoxTools {

    @NonNull
    private static DropboxAPI<AndroidAuthSession> mDBApi;

    public DropBoxTools() {
        AppKeyPair appKeys = new AppKeyPair(App.DropBox.API_KEY, App.DropBox.API_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<>(session);
    }
}
