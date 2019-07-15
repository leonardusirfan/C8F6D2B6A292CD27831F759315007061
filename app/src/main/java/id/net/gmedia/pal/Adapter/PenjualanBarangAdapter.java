package id.net.gmedia.pal.Adapter;

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
import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Activity.PenjualanSoCanvas.PenjualanDetail;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppKeranjangPenjualan;
import id.net.gmedia.pal.Util.Constant;

public class PenjualanBarangAdapter extends RecyclerView.Adapter<PenjualanBarangAdapter.PenjualanBarangViewHolder> {

    private boolean canvas = false;
    private Activity activity;
    private List<BarangModel> listBarang;

    public PenjualanBarangAdapter(Activity activity, List<BarangModel> listBarang, boolean canvas){
        this.activity = activity;
        this.listBarang = listBarang;
        this.canvas = canvas;
    }

    public PenjualanBarangAdapter(Activity activity, List<BarangModel> listBarang){
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
        viewHolder.txt_batch.setText(barang.getNo_batch());

        viewHolder.item_penjualan_barang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppKeranjangPenjualan.getInstance().isBarangBelumAda(barang.getId())){
                    Gson gson = new Gson();
                    Intent i = new Intent(activity, PenjualanDetail.class);
                    i.putExtra(Constant.EXTRA_BARANG, gson.toJson(barang));
                    /*if(!((PenjualanBarang)activity).no_bukti.equals("")){
                        i.putExtra(Constant.EXTRA_NO_NOTA, ((PenjualanBarang)activity).no_bukti);
                    }*/
                    activity.startActivity(i);
                }
                else {
                    Toast.makeText(activity, "Barang sudah ada di penjualan anda", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if(viewHolder instanceof PenjualanBarangCanvasViewHolder){
            //String stok = "Stok : " + barang.getStokString();
            ((PenjualanBarangCanvasViewHolder)viewHolder).txt_stok.setText(barang.getStokString());
        }
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    public class PenjualanBarangViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout item_penjualan_barang;
        public TextView txt_nama, txt_harga;
        TextView txt_batch;

        PenjualanBarangViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            item_penjualan_barang = itemView.findViewById(R.id.item_penjualan_barang);
            txt_batch = itemView.findViewById(R.id.txt_batch);
        }
    }

    public class PenjualanBarangCanvasViewHolder extends PenjualanBarangViewHolder{

        TextView txt_stok;
        PenjualanBarangCanvasViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_stok = itemView.findViewById(R.id.txt_stok);
        }
    }
}
