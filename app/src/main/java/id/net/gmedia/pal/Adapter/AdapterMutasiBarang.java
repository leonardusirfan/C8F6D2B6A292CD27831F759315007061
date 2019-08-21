package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.DialogFactory;

import java.util.List;

import id.net.gmedia.pal.Activity.PengajuanMutasi.PengajuanMutasiActivity;
import id.net.gmedia.pal.Activity.ReturCanvas.ReturCanvas;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.Model.SatuanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

import static android.app.Activity.RESULT_OK;

public class AdapterMutasiBarang extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean MODE_TAMBAH;
    private Activity activity;
    private List<BarangModel> listBarang;

    public AdapterMutasiBarang(Activity context, List<BarangModel> listBarang, boolean MODE_TAMBAH){
        this.activity = context;
        this.listBarang = listBarang;
        this.MODE_TAMBAH = MODE_TAMBAH;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(MODE_TAMBAH){
            return new ViewHolderMutasiBarangTambah(LayoutInflater.from(activity).
                    inflate(R.layout.item_barang_mutasi_tambah, viewGroup, false));
        }
        else{
            return new ViewHolderMutasiBarang(LayoutInflater.from(activity).
                    inflate(R.layout.item_barang_mutasi, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if(holder instanceof ViewHolderMutasiBarang){
            ((ViewHolderMutasiBarang)holder).bind(listBarang.get(i));
        }
        else if(holder instanceof ViewHolderMutasiBarangTambah){
            ((ViewHolderMutasiBarangTambah)holder).bind(listBarang.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class ViewHolderMutasiBarang extends RecyclerView.ViewHolder{

        View layout_parent;
        TextView txt_nama, txt_harga, txt_jumlah;
        ImageView img_hapus;

        ViewHolderMutasiBarang(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            img_hapus = itemView.findViewById(R.id.img_hapus);
        }

        void bind(final BarangModel b){
            txt_nama.setText(b.getNama());
            String harga = "Harga : " + Converter.doubleToRupiah(b.getHarga());
            txt_harga.setText(harga);
            String jumlah = "Jumlah : " + b.getJumlah() + " " + b.getSatuan();
            txt_jumlah.setText(jumlah);

            img_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(activity instanceof PengajuanMutasiActivity){
                        ((PengajuanMutasiActivity)activity).hapusBarang(getAdapterPosition());
                    }
                    else if(activity instanceof ReturCanvas){
                        ((ReturCanvas)activity).hapusBarang(getAdapterPosition());
                    }
                }
            });
        }
    }

    class ViewHolderMutasiBarangTambah extends RecyclerView.ViewHolder{

        View layout_parent;
        TextView txt_nama, txt_harga, txt_jumlah;
        Button btn_tambah;

        ViewHolderMutasiBarangTambah(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            btn_tambah = itemView.findViewById(R.id.btn_tambah);
        }

        void bind(final BarangModel b){
            txt_nama.setText(b.getNama());
            String harga = "Harga : " + Converter.doubleToRupiah(b.getHarga());
            txt_harga.setText(harga);

            String stok = "Stok : " + b.getListSatuan().get(0).getJumlah() +
                    " " + b.getListSatuan().get(0).getSatuan();
            txt_jumlah.setText(stok);

            btn_tambah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInputJumlah(b);
                }
            });
        }
    }

    private void showInputJumlah(final BarangModel b){
        final Dialog dialog = DialogFactory.getInstance().createDialog(activity,
                R.layout.popup_jumlah_stok, 90);

        final EditText txt_jumlah = dialog.findViewById(R.id.txt_jumlah);
        final AppCompatSpinner spn_satuan = dialog.findViewById(R.id.spn_satuan);

        ArrayAdapter<SatuanModel> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item, b.getListSatuan());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_satuan.setAdapter(adapter);

        dialog.findViewById(R.id.btn_tambah).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_jumlah.getText().toString().isEmpty()){
                    Toast.makeText(activity, "Masukkan jumlah terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else if(spn_satuan.getSelectedItemPosition() < 0){
                    Toast.makeText(activity, "Pilih satuan terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    int jumlah = Integer.parseInt(txt_jumlah.getText().toString());
                    int stok = b.getListSatuan().get(spn_satuan.getSelectedItemPosition()).getJumlah();

                    //cek stok
                    if(jumlah > stok){
                        Toast.makeText(activity, "Jumlah yang diinput tidak boleh melebihi stok", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        b.setJumlah(jumlah);
                        b.setSatuan(b.getListSatuan().get(spn_satuan.getSelectedItemPosition()).getSatuan());

                        Gson gson = new Gson();
                        Intent i = new Intent();
                        i.putExtra(Constant.EXTRA_BARANG, gson.toJson(b));
                        activity.setResult(RESULT_OK, i);
                        activity.finish();

                        dialog.dismiss();
                        activity.onBackPressed();
                    }
                }
            }
        });

        dialog.show();
    }
}
