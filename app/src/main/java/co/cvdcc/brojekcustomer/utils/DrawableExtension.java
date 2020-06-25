package co.cvdcc.brojekcustomer.utils;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

/**
 * Created by bradhawk on 10/29/2016.
 */

public class DrawableExtension {

    public static void setTintSelector(ImageView imageView, @ColorRes int colorId) {
        ColorStateList colors = ContextCompat.getColorStateList(imageView.getContext(), colorId);
        Drawable drawable = imageView.getDrawable();
        DrawableCompat.setTintList(drawable, colors);
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_ATOP);
        imageView.setImageDrawable(drawable);
    }

}
