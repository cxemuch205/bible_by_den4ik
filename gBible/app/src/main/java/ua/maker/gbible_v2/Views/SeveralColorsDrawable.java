package ua.maker.gbible_v2.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by daniilpavenko on 10/5/15.
 */
public class SeveralColorsDrawable extends Drawable {

    private int colorLeft, colorRight;
    private Paint paint;
    int width = 300;
    int height = 200;

    public SeveralColorsDrawable(int colorLeft, int colorRight, int width, int height) {
        super();
        this.width = width;
        this.height = height;
        this.colorLeft = colorLeft;
        this.colorRight = colorRight;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void draw(Canvas canvas) {
        drawColors(canvas, width / 2, 0, width, height, colorRight);
        drawColors(canvas, 0, 0, width / 2, height, colorLeft);
    }

    private void drawColors(Canvas canvas, int startX, int startY, int width, int height, int color) {
        Rect rect = new Rect(startX, startY, width, height);
        paint.setColor(color);
        canvas.drawRect(rect, paint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
