package nodopezzz.android.wishlist.MemoryUtils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class DimensionsCalculator {

    public static float calculateDipToPx(Context context, float dp){
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

}
