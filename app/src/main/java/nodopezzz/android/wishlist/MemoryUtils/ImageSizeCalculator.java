package nodopezzz.android.wishlist.MemoryUtils;

import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageSizeCalculator {
    private static final String TAG = "ImageSizeCalculator";

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {

        reqHeight /= 2;
        reqWidth /= 2;
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
