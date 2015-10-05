package ua.maker.gbible_v2.Views;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ua.maker.gbible_v2.Helpers.Tools;
import ua.maker.gbible_v2.Interfaces.OnColorChangedListener;
import ua.maker.gbible_v2.R;

/**
 * Created by daniilpavenko on 10/5/15.
 */
public class ColorChoicerContainer extends LinearLayout {

    private Context context;
    private OnColorChangedListener colorChangedListener;
    private SparseBooleanArray array;

    public ColorChoicerContainer(Context context, OnColorChangedListener listener, SparseBooleanArray booleanArray) {
        super(context);
        this.context = context;
        this.colorChangedListener = listener;
        this.array = booleanArray;
        init();
    }

    private void init() {
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(HORIZONTAL);
        setBackgroundResource(R.color.bottom_bar);

        addDeleteButton();

        ColorFullPickerView colorPickerLine = new ColorFullPickerView(context, array);
        colorPickerLine.setOnColorChangedListener(colorChangedListener);

        addView(colorPickerLine);
    }

    private void addDeleteButton() {
        ImageView ivDelete = new ImageView(context);
        ivDelete.setLayoutParams(new ViewGroup.LayoutParams(Tools.dpToPx(context, 52), Tools.dpToPx(context, 52)));

        ivDelete.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ivDelete.setImageResource(R.drawable.ic_close_white_24dp);
        ivDelete.setClickable(true);

        addView(ivDelete);

        ivDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (colorChangedListener != null) {
                    colorChangedListener.deleteColor(array);
                }
            }
        });
    }
}
