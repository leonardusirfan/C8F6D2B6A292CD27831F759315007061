package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.R;

public class StokCanvasAdapter extends RecyclerView.Adapter<StokCanvasAdapter.StokCanvasViewHolder> {
    private Activity activity;
    private List<BarangModel> listBarang;

    public StokCanvasAdapter(Activity activity, List<BarangModel> listBarang){
        this.activity = activity;
        this.listBarang = listBarang;
    }

    @NonNull
    @Override
    public StokCanvasViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new StokCanvasViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_stok_canvas, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StokCanvasViewHolder viewHolder, int i) {
        final BarangModel barang = listBarang.get(i);

        viewHolder.txt_nama.setText(barang.getNama());
        viewHolder.txt_harga.setText(Converter.doubleToRupiah(barang.getHarga()));
        //String stok = "Stok : " + barang.getStokString();
        viewHolder.txt_stok.setText(barang.getStokString());
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class StokCanvasViewHolder extends RecyclerView.ViewHolder{

        TextView txt_nama, txt_harga;
        TextView txt_stok;

        StokCanvasViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_stok = itemView.findViewById(R.id.txt_stok);
        }
    }
}
