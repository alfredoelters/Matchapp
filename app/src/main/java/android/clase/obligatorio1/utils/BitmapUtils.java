package android.clase.obligatorio1.utils;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by alfredo on 04/06/15.
 */
public class BitmapUtils {

    public static Bitmap scaleDownBitmap(Bitmap photo, int newScale, Context context) {
        if (photo == null || context == null) {
            return null;
        }

        if (photo.getHeight() > newScale) {
            final float densityMultiplier = context.getResources().getDisplayMetrics().density;

            int h = (int) (newScale * densityMultiplier);
            int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));
            if (w > h) {
                w = h;
            }

            photo = Bitmap.createScaledBitmap(photo, w, h, true);
        }
        return photo;
    }
}
