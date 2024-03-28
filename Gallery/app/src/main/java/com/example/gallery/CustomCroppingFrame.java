package com.example.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;

public class CustomCroppingFrame extends View {
    private Paint fillPaint;
    private Paint borderPaint;
    private int width, height;
    ImageView myImage;
    private int topHeight, middleHeight, bottomHeight;

    public CustomCroppingFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint();
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(10);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        width = metrics.widthPixels;
        height = metrics.heightPixels;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.atrociraptor);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 800, 800, true);
        Matrix matrix = new Matrix();
        matrix.postRotate(45);
        matrix.postScale(2F, 2F);

        topHeight = height / 16;
        middleHeight = height * 13 / 16;
        bottomHeight = height * 2 / 16;

        // Define the outer rectangle
        int outerLeft1 = 0;
        int outerTop1 = 0;
        int outerRight1 = width / 10;
        int outerBottom1 = height;
        Rect outerRect1 = new Rect(outerLeft1, outerTop1, outerRight1, outerBottom1);

        int outerLeft2 = width * 9 / 10;
        int outerTop2 = 0;
        int outerRight2 = width;
        int outerBottom2 = height;
        Rect outerRect2 = new Rect(outerLeft2, outerTop2, outerRight2, outerBottom2);

        int outerLeft3 = width / 10;
        int outerTop3 = 0;
        int outerRight3 = width * 9 / 10;
        int outerBottom3 = topHeight + middleHeight * 1 / 6;
        Rect outerRect3 = new Rect(outerLeft3, outerTop3, outerRight3, outerBottom3);

        int outerLeft4 = width / 10;
        int outerTop4 = topHeight +  middleHeight * 4 / 6;
        int outerRight4 = width * 9 / 10;
        int outerBottom4 = height;
        Rect outerRect4 = new Rect(outerLeft4, outerTop4, outerRight4, outerBottom4);

        // Define the inner rectangle (hole)
        int innerLeft = width / 10;
        int innerTop = topHeight +  middleHeight * 1 / 6;
        int innerRight = width * 9 / 10;
        int innerBottom = topHeight +  middleHeight * 4 / 6;
        Rect innerRect = new Rect(innerLeft, innerTop, innerRight, innerBottom);

        fillPaint.setColor(Color.argb(128, 128, 128, 128));
        canvas.drawRect(outerRect1, fillPaint);
        canvas.drawRect(outerRect2, fillPaint);
        canvas.drawRect(outerRect3, fillPaint);
        canvas.drawRect(outerRect4, fillPaint);

        fillPaint.setColor(Color.TRANSPARENT);
        canvas.drawRect(innerRect, fillPaint);
        canvas.drawRect(innerRect, borderPaint);

        Bitmap rotated = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

        canvas.drawBitmap(rotated, width / 10, topHeight + middleHeight * 1 / 6, null);

    }
}
