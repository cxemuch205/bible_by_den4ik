package ua.maker.gbible.Interfaces;

import java.util.ArrayList;

/**
 * Created by daniil on 11/7/14.
 */
public interface OnGetContentListener {
    void onStartGet();
    void onProgressGet();
    void onEndGet(ArrayList<? extends Object> result, String tag);
}
