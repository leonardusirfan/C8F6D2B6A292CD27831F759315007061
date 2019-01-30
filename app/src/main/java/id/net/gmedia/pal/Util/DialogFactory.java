package id.net.gmedia.pal.Util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Window;

public class DialogFactory {
    private static final DialogFactory ourInstance = new DialogFactory();

    public static DialogFactory getInstance() {
        return ourInstance;
    }

    private DialogFactory() {
    }

    public Dialog createDialog(Context context, int res_id, int widthpercentage, int heightpercentage){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        int device_TotalWidth = metrics.widthPixels;
        int device_TotalHeight = metrics.heightPixels;

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(res_id);
        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout(device_TotalWidth * widthpercentage / 100 , device_TotalHeight * heightpercentage / 100); // set here your value
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return dialog;
    }
}

