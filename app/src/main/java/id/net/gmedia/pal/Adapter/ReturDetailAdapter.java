package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.DialogFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import id.net.gmedia.pal.Activity.ReturBarangDetail;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.ReturModel;
import id.net.gmedia.pal.Model.UploadModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class ReturDetailAdapter extends RecyclerView.Adapter<ReturDetailAdapter.ReturDetailViewHolder> {

    public final int UPLOAD_FOTO_RETUR = 901;

    private Activity activity;
    private List<BarangModel> listBarang;
    private int type;
    private UploadModel upload;
    private Dialog dialog;
    private ImageView img_retur, overlay_retur;
    private ProgressBar bar_retur;

    public ReturDetailAdapter(Activity activity, List<BarangModel> listBarang, int type){
        this.activity = activity;
        this.listBarang = listBarang;
        this.type = type;
    }

    @NonNull
    @Override
    public ReturDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ReturDetailViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_retur_barang_detail, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ReturDetailViewHolder viewHolder, int i) {
        final BarangModel barang = listBarang.get(i);

        if(type == Constant.PENJUALAN_SO){
            viewHolder.layout_parent.setBackground(activity.getResources().getDrawable(R.drawable.layout_so));
        }
        else{
            viewHolder.layout_parent.setBackground(activity.getResources().getDrawable(R.drawable.layout_canvas));
        }

        viewHolder.txt_nama.setText(barang.getNama());
        String jumlah = barang.getJumlah() + " " + barang.getSatuan();
        viewHolder.txt_jumlah.setText(jumlah);
        viewHolder.txt_diskon.setText(Converter.doubleToRupiah(barang.getDiskon()));
        viewHolder.txt_harga.setText(Converter.doubleToRupiah(barang.getHarga()));
        viewHolder.txt_total.setText(Converter.doubleToRupiah(barang.getSubtotal()));

        viewHolder.cb_retur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.cb_retur.isChecked()){
                    itemClicked(true, barang, viewHolder.cb_retur);
                }
                else{
                    itemClicked(false, barang, viewHolder.cb_retur);
                }
            }
        });

        viewHolder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.cb_retur.setChecked(!viewHolder.cb_retur.isChecked());
                if(viewHolder.cb_retur.isChecked()){
                    itemClicked(true, barang, viewHolder.cb_retur);
                }
                else{
                    itemClicked(false, barang, viewHolder.cb_retur);
                }
            }
        });
    }

    private void itemClicked(boolean status, final BarangModel barang, final CheckBox cb_retur){
        if(status){
            dialog = DialogFactory.getInstance().createDialog(activity, R.layout.popup_retur,
                    80, 80);

            final EditText txt_jumlah = dialog.findViewById(R.id.txt_jumlah);
            final EditText txt_alasan = dialog.findViewById(R.id.txt_alasan);
            final AppCompatSpinner spn_jenis = dialog.findViewById(R.id.spn_jenis);
            overlay_retur = dialog.findViewById(R.id.overlay_retur);
            img_retur = dialog.findViewById(R.id.img_retur);
            bar_retur = dialog.findViewById(R.id.bar_retur);

            txt_jumlah.setText(String.valueOf(barang.getJumlah()));

            dialog.findViewById(R.id.img_upload_retur).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pix.start((FragmentActivity) activity, UPLOAD_FOTO_RETUR, 1);
                }
            });

            dialog.findViewById(R.id.btn_batal).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            dialog.findViewById(R.id.btn_tambah).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Tambah barang retur
                    if(upload == null){
                        Toast.makeText(activity, "Upload foto barang retur terlebih dahulu", Toast.LENGTH_SHORT).show();
                    }
                    else if(!upload.isUploaded()){
                        Toast.makeText(activity, "Foto barang retur belum selesai ter-upload", Toast.LENGTH_SHORT).show();
                    }
                    else if(txt_jumlah.getText().toString().equals("") || Integer.parseInt(txt_jumlah.getText().toString()) < 1){
                        Toast.makeText(activity, "Jumlah barang retur tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    }
                    else if(Integer.parseInt(txt_jumlah.getText().toString()) > barang.getJumlah()){
                        Toast.makeText(activity, "Jumlah retur tidak boleh melebihi jumlah barang", Toast.LENGTH_SHORT).show();
                        txt_jumlah.setText(String.valueOf(barang.getJumlah()));
                    }
                    else if(txt_alasan.getText().toString().equals("")){
                        Toast.makeText(activity, "Alasan retur tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        ReturModel retur = new ReturModel(barang.getKode(), Integer.parseInt(txt_jumlah.getText().toString()),
                                barang.getSatuan(), txt_alasan.getText().toString(), upload.getId(),
                                spn_jenis.getSelectedItemPosition() == 0);
                        ((ReturBarangDetail)activity).tambahBarangRetur(retur);
                        upload = null;
                        dialog.dismiss();
                    }
                }
            });

            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cb_retur.setChecked(false);
                }
            });
            dialog.show();
        }
        else{
            ((ReturBarangDetail)activity).hapusBarangRetur(barang.getId());
        }
    }

    public void initUpload(Intent data){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(),
                    Uri.fromFile(new File(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0))));
            bitmap = Converter.resizeBitmap(bitmap, 750);

            img_retur.setImageBitmap(bitmap);
            overlay_retur.setVisibility(View.VISIBLE);
            bar_retur.setVisibility(View.VISIBLE);

            upload = new UploadModel(bitmap);
            upload.setUrl(Uri.fromFile(new File(data.getStringArrayListExtra(Pix.IMAGE_RESULTS).get(0))).toString());
            upload();
        } catch (IOException e) {
            Log.e(Constant.TAG, e.getMessage());
        }
    }

    private void upload(){
        ApiVolleyManager.getInstance().addMultipartRequest(activity, Constant.URL_UPLOAD_FOTO_RETUR,
                Constant.getTokenHeader(AppSharedPreferences.getId(activity)), "pic",
                Converter.getFileDataFromDrawable(upload.getBitmap()), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject jsonresult = new JSONObject(result);
                            int status = jsonresult.getJSONObject("metadata").getInt("status");
                            String message = jsonresult.getJSONObject("metadata").getString("message");

                            if(status == 200){
                                upload.setUploaded(true);
                                upload.setId(jsonresult.getJSONObject("response").getString("id"));

                                overlay_retur.setVisibility(View.INVISIBLE);
                                bar_retur.setVisibility(View.INVISIBLE);
                            }
                            else{
                                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(activity, "Upload gambar gagal", Toast.LENGTH_SHORT).show();
                        Log.e(Constant.TAG, result);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class ReturDetailViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layout_parent;
        TextView txt_nama, txt_harga, txt_diskon, txt_jumlah, txt_total;
        CheckBox cb_retur;

        ReturDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_diskon = itemView.findViewById(R.id.txt_diskon);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_total = itemView.findViewById(R.id.txt_total);
            cb_retur = itemView.findViewById(R.id.cb_retur);
            layout_parent = itemView.findViewById(R.id.layout_parent);
        }
    }
}
