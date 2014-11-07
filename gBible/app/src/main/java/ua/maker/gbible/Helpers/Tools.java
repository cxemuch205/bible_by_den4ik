package ua.maker.gbible.Helpers;

import android.graphics.PorterDuff;
import android.widget.ProgressBar;

import ua.maker.gbible.GBApplication;
import ua.maker.gbible.R;

/**
 * Created by daniil on 11/7/14.
 */
public class Tools {

    public static void initProgressBar(ProgressBar progressBar) {
        progressBar.getIndeterminateDrawable()
                .setColorFilter(GBApplication.getInstance().getResources()
                        .getColor(R.color.progress_load_color), PorterDuff.Mode.MULTIPLY);
    }
}
