package nodopezzz.android.wishlist;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class Vibration {

    public static void vibrate(Context context){
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && v != null) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.EFFECT_TICK));
        } else {
            v.vibrate(100);
        }
    }
}
