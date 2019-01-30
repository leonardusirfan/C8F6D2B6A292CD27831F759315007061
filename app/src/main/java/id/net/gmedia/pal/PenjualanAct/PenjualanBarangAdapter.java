package id.net.gmedia.pal.PenjualanAct;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppKeranjangPenjualan;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.Converter;

public class PenjualanBarangAdapter extends RecyclerView.Adapter<PenjualanBarangAdapter.PenjualanBarangViewHolder> {

    private boolean canvas = false;
    private Activity activity;
    private List<BarangModel> listBarang;

    public PenjualanBarangAdapter(Activity activity, List<BarangModel> listBarang, boolean canvas){
        this.activity = activity;
        this.listBarang = listBarang;
        this.canvas = canvas;
    }

    PenjualanBarangAdapter(Activity activity, List<BarangModel> listBarang){
        this.activity = activity;
        this.listBarang = listBarang;
    }

    @NonNull
    @Override
    public PenjualanBarangViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(canvas){
            return new PenjualanBarangCanvasViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_penjualan_barang_canvas, viewGroup, false));
        }
        else{
            return new PenjualanBarangViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_penjualan_barang, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PenjualanBarangViewHolder viewHolder, int i) {
        final BarangModel barang = listBarang.get(i);

        viewHolder.txt_nama.setText(barang.getNama());
        viewHolder.txt_harga.setText(Converter.doubleToRupiah(barang.getHarga()));

        viewHolder.item_penjualan_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AppKeranjangPenjualan.getInstance().isBarangAda(barang.getId())){
                    Gson gson = new Gson();
                    Intent i = new Intent(activity, PenjualanDetail.class);
                    i.putExtra(Constant.EXTRA_BARANG, gson.toJson(barang));
                    i.putExtra(Constant.EXTRA_TANGGAL_TEMPO, ((PenjualanBarang)activity).tempo);
                    i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(((PenjualanBarang)activity).customer));
                    i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, ((PenjualanBarang)activity).JENIS_PENJUALAN);
                    if(!((PenjualanBarang)activity).no_bukti.equals("")){
                        i.putExtra(Constant.EXTRA_NO_BUKTI, ((PenjualanBarang)activity).no_bukti);
                    }
                    activity.startActivity(i);
                }
                else {
                    Toast.makeText(activity, "Barang sudah ada di penjualan anda", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if(viewHolder instanceof PenjualanBarangCanvasViewHolder){
            String stok = "Stok : " + barang.getStok();
            ((PenjualanBarangCanvasViewHolder)viewHolder).txt_stok.setText(stok);
        }
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    public class PenjualanBarangViewHolder extends RecyclerView.ViewHolder{

        public RelativeLayout item_penjualan_barang;
        public TextView txt_nama, txt_harga;

        PenjualanBarangViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            item_penjualan_barang = itemView.findViewById(R.id.item_penjualan_barang);
        }
    }

    public class PenjualanBarangCanvasViewHolder extends PenjualanBarangViewHolder{

        public TextView txt_stok;
        PenjualanBarangCanvasViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_stok = itemView.findViewById(R.id.txt_stok);
        }
    }
}
