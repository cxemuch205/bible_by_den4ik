package ua.maker.gbible_v2.Views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import ua.maker.gbible_v2.Interfaces.OnColorChangedListener;
import ua.maker.gbible_v2.R;

/**
 * Created by daniilpavenko on 10/5/15.
 */
public class ColorFullPickerView extends View {

    private static final int[] GRAD_COLORS = new int[]{Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED};

    private Paint mPaint;
    private RectF mGradientRect = new RectF();

    private float[] mHSV = new float[]{1f, 1f, 1f};

    private int[] mSelectedColorGradient = new int[]{0, Color.BLACK};
    private float mRadius = 0;
    private int mSelectedColor = Color.BLACK;
    private int mLastX = Integer.MIN_VALUE;
    private SparseBooleanArray array;

    private OnColorChangedListener mOnColorChangedListener;

    public ColorFullPickerView(Context context, SparseBooleanArray array) {
        super(context);
        this.array = array;
        init();
    }

    private void init() {
        setClickable(true);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setLayerType(View.LAYER_TYPE_SOFTWARE, isInEditMode() ? null : mPaint);
        mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        //MUST CALL THIS
        setMeasuredDimension(widthSize, heightSize);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setShader(new LinearGradient(
                mGradientRect.left,
                mGradientRect.top,
                mGradientRect.right,
                mGradientRect.bottom,
                GRAD_COLORS, null, Shader.TileMode.CLAMP));
        canvas.drawPaint(mPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mGradientRect.set(getPaddingLeft(),
                getPaddingTop(), right - left - getPaddingRight(), bottom - top - getPaddingBottom());
    }

    public void setRadius(float radius) {
        if (radius != mRadius) {
            mRadius = radius;
            invalidate();
        }
    }

    public float getRadius() {
        return mRadius;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isEnabled()) {
            return super.onTouchEvent(event);
        }
        mLastX = (int) event.getX();
        onUpdateColorSelection(mLastX);
        postInvalidate();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Update color based on touch events
     *
     * @param x
     * @param y
     */
    protected void onUpdateColorSelection(int x) {
        x = (int) (Math.max(mGradientRect.left, Math.min(x, mGradientRect.right)));

        float hue = pointToHue(x);
        mHSV[0] = hue;
        mHSV[1] = 1f;
        mHSV[2] = 1f;
        mSelectedColor = Color.HSVToColor(mHSV);

        String color = Integer.toHexString(mSelectedColor);

        dispatchColorChanged(mSelectedColor, color);
    }


    protected void dispatchColorChanged(int color, String colorVal) {
        if (mOnColorChangedListener != null) {
            mOnColorChangedListener.onColorChanged(color, array);
        }
    }

    /**
     * Get current selectec color
     *
     * @return
     */
    public int getSelectedColor() {
        return mSelectedColor;
    }

    /**
     * Update current color
     *
     * @param selectedColor
     */
    public void setColor(int selectedColor) {
        Color.colorToHSV(selectedColor, mHSV);
        mSelectedColor = selectedColor;
        postInvalidate();
        dispatchColorChanged(mSelectedColor, null);
    }

    /**
     * Get start color for gradient
     *
     * @param hsv
     * @return
     */
    private int getColorForGradient(float[] hsv) {
        if (hsv[2] != 1f) {
            float oldV = hsv[2];
            hsv[2] = 1;
            int color = Color.HSVToColor(hsv);
            hsv[2] = oldV;
            return color;
        } else {
            return Color.HSVToColor(hsv);
        }
    }

    public void setOnColorChangedListener(OnColorChangedListener onColorChangedListener) {
        mOnColorChangedListener = onColorChangedListener;
    }

    //region HSL math

    /**
     * @param x x coordinate of gradient
     * @return
     */
    private float pointToHue(float x) {
        x = x - mGradientRect.left;
        return x * 360f / mGradientRect.width();
    }

    private int hueToPoint(float hue) {
        return (int) (mGradientRect.left + ((hue * mGradientRect.width()) / 360));
    }

    /**
     * Get saturation
     *
     * @param y
     * @return
     */
    private float pointToSaturation(float y) {
        y = (y - mGradientRect.top);
        if (y > ((mGradientRect.bottom - mGradientRect.top) / 2)) {
            y = Math.abs((y - (mGradientRect.bottom - mGradientRect.top) / 2)) * 2;
        } else {
            y = 0;
        }
        return 1 - (1.f / (mGradientRect.height()) * y);
    }

    private int saturationToPoint(float sat) {
        sat = 1 - sat;
        int res = (int) (mGradientRect.top + ((mGradientRect.height()) * sat));
        if (res < ((mGradientRect.bottom - mGradientRect.top) / 2)) {
            res += (mGradientRect.bottom - mGradientRect.top) / 2;
        }
        return res;
    }

    /**
     * Get value of brightness
     *
     * @param x
     * @return
     */
    private float pointToValueBrightness(float x) {
        x = x - mGradientRect.left;
        float width = mGradientRect.width();
        return 1 - (1.f / width * x);
    }

    /**
     * Get value of brightness
     *
     * @param y
     * @return
     */
    private float pointToValueBrightnessY(float y) {
        y = y - mGradientRect.top;

        if (y > ((mGradientRect.bottom - mGradientRect.top) / 2)) {
            y = (int) ((y - (mGradientRect.bottom - mGradientRect.top)) * 2);
        } else {
            y = (int) mGradientRect.bottom;
        }

        float height = mGradientRect.height();

        return Math.abs(1.f / height * y);
    }

    private int brightnessToPoint(float val) {
        val = 1 - val;

        int res = (int) (mGradientRect.top + ((mGradientRect.height()) * val));

        if (res < ((mGradientRect.bottom - mGradientRect.top) / 2)) {
            res += (mGradientRect.bottom - mGradientRect.top) / 2;
        }

        return res;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.color = mSelectedColor;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        setColor(ss.color);
    }

    private static class SavedState extends BaseSavedState {
        int color;
        boolean isBrightnessGradient;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            color = in.readInt();
            isBrightnessGradient = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(color);
            out.writeInt(isBrightnessGradient ? 1 : 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
