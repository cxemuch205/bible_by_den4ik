package ua.maker.gbible_v2.Interfaces;

import android.util.SparseBooleanArray;

/**
 * Created by daniilpavenko on 10/5/15.
 */
public interface OnColorChangedListener {
    void onColorChanged(int newColor, SparseBooleanArray array);

    void deleteColor(SparseBooleanArray array);
}
