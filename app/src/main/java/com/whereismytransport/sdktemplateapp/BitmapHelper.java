package com.whereismytransport.sdktemplateapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;

import androidx.annotation.ColorInt;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public final class BitmapHelper {

    public static Bitmap getVectorAsBitmap(Context context, int vectorDrawableId, int widthPixels, int heightPixels) {

        Drawable drawable = VectorDrawableCompat.create(context.getResources(), vectorDrawableId, context.getTheme());

        drawable.setBounds(0, 0, widthPixels, heightPixels);

        Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        drawable.draw(canvas);

        return bitmap;
    }

    public static Icon getVectorAsMapBoxIcon(Context context, int vectorDrawableId, int widthPixels, int heightPixels) {
        IconFactory iconFactory = IconFactory.getInstance(context);
        Bitmap bitmap = BitmapHelper.getVectorAsBitmap(context, vectorDrawableId, widthPixels, heightPixels);
        return iconFactory.fromBitmap(bitmap);
    }

    public static Bitmap getVectorAsBitmap(Context context, int vectorDrawableId, int widthPixels, int heightPixels, @ColorInt int packedIntColour) {

        Drawable drawable = VectorDrawableCompat.create(context.getResources(), vectorDrawableId, context.getTheme());

        // Wrap the drawable so that future tinting calls work
        // on pre-v21 devices. Always use the returned drawable.
        Drawable wrapDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrapDrawable.mutate(), packedIntColour);

        wrapDrawable.setBounds(0, 0, widthPixels, heightPixels);

        Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        wrapDrawable.draw(canvas);

        return bitmap;
    }

    public static Icon getVectorAsMapBoxIcon(Context context, int vectorDrawableId, int widthPixels, int heightPixels, @ColorInt int packedIntColour) {
        IconFactory iconFactory = IconFactory.getInstance(context);
        Bitmap bitmap = BitmapHelper.getVectorAsBitmap(context, vectorDrawableId, widthPixels, heightPixels, packedIntColour);
        return iconFactory.fromBitmap(bitmap);
    }

    public static Icon getIntermediateStopAsMapBoxIcon(Context context, int widthPixels, int heightPixels, @ColorInt int packedIntColour) {
        Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(packedIntColour);

        int centerX = bitmap.getWidth() / 2;
        int centerY = bitmap.getHeight() / 2;
        canvas.drawCircle(centerX, centerY, centerX, paint);

        paint.setColor(Color.WHITE);
        canvas.drawCircle(centerX, centerY, centerX / 2, paint);

        IconFactory iconFactory = IconFactory.getInstance(context);
        return iconFactory.fromBitmap(bitmap);
    }
}
