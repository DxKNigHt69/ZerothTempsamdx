package com.whereismytransport.sdktemplateapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;

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
}
