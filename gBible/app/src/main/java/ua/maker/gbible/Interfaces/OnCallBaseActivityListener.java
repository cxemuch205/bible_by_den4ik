package ua.maker.gbible.Interfaces;

import android.support.v4.app.Fragment;

/**
 * Created by daniil on 11/6/14.
 */
public interface OnCallBaseActivityListener {
    void callShowHideBottomToolBar(boolean show);

    void switchFragment(String tag, Fragment fragment);
}
