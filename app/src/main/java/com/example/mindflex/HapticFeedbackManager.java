package com.example.mindflex;

import android.os.Build;
import android.view.HapticFeedbackConstants;
import android.view.View;

public class HapticFeedbackManager {

    private static boolean HapticEnabled = true;

    public static void EnableHapticFeedback(boolean hapticEnabled) {
        HapticEnabled = hapticEnabled;
    }

    public static boolean CheckHapticFeedback() {
        return HapticEnabled;
    }

    public static void HapticFeedbackLight(View view) {
        if (HapticEnabled && view != null) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        }
    }

    public static void HapticFeedbackStrong(View view) {
        if (HapticEnabled && view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_PRESS);
            } else {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
            }
        }
    }

}
