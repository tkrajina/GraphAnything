package info.puzz.graphanything.utils;

import android.app.Activity;
import android.app.AlertDialog;

/**
 * Created by puzz on 02/11/2016.
 */

public final class DialogUtils {
    private DialogUtils() throws Exception {
        throw new Exception();
    }

    public static void showWarningDialog(Activity activity, String title, String message) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
