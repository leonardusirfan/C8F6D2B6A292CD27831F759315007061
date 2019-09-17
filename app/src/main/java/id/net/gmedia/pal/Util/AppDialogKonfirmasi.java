package id.net.gmedia.pal.Util;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.TextView;

import com.leonardus.irfan.DialogFactory;

import id.net.gmedia.pal.R;

public class AppDialogKonfirmasi {

    private static Dialog dialog;

    public static void showKonfirmasi(Activity activity, String title, String message, final KonfirmasiListener listener){
        dismissKonfirmasi();
        dialog = DialogFactory.getInstance().createDialog(activity,
                R.layout.popup_konfirmasi, 90);
        TextView txt_title, txt_message;
        txt_title = dialog.findViewById(R.id.txt_title);
        txt_message = dialog.findViewById(R.id.txt_message);

        txt_title.setText(title);
        txt_message.setText(message);

        dialog.findViewById(R.id.btn_batal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.findViewById(R.id.btn_lanjut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLanjut();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void showKonfirmasi(Activity activity, final KonfirmasiListener listener){
        dismissKonfirmasi();
        dialog = DialogFactory.getInstance().createDialog(activity,
                R.layout.popup_konfirmasi, 90);

        dialog.findViewById(R.id.btn_batal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.findViewById(R.id.btn_ya).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLanjut();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public static void dismissKonfirmasi(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public interface KonfirmasiListener{
        void onLanjut();
    }
}
