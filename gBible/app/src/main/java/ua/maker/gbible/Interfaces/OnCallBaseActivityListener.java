package ua.maker.gbible.Interfaces;

import ua.maker.gbible.Fragments.BaseFragment;

/**
 * Created by daniil on 11/6/14.
 */
public interface OnCallBaseActivityListener {
    void callShowHideBottomToolBar(boolean show);

    void switchFragment(String tag, BaseFragment fragment);
}
