package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class BarangDetailAdapter extends RecyclerView.Adapter<BarangDetailAdapter.BarangDetailViewHolder> {

    private Activity activity;
    private List<BarangModel> listBarang;
    private int type;

    public BarangDetailAdapter(Activity activity, List<BarangModel> listBarang, int type){
        this.activity = activity;
        this.listBarang = listBarang;
        this.type = type;
    }

    @NonNull
    @Override
    public BarangDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BarangDetailViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_barang_detail, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BarangDetailViewHolder barangDetailViewHolder, int i) {
        BarangModel barang = listBarang.get(i);

        if(type == Constant.PENJUALAN_SO){
            barangDetailViewHolder.layout_parent.setBackground(activity.getResources().getDrawable(R.drawable.layout_so));
        }
        else{
            barangDetailViewHolder.layout_parent.setBackground(activity.getResources().getDrawable(R.drawable.layout_canvas));
        }

        barangDetailViewHolder.txt_nama.setText(barang.getNama());
        String jumlah = barang.getJumlah() + " " + barang.getSatuan();
        barangDetailViewHolder.txt_jumlah.setText(jumlah);
        barangDetailViewHolder.txt_diskon.setText(Converter.doubleToRupiah(barang.getDiskon()));
        barangDetailViewHolder.txt_harga.setText(Converter.doubleToRupiah(barang.getHarga()));
        barangDetailViewHolder.txt_total.setText(Converter.doubleToRupiah(barang.getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class BarangDetailViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layout_parent;
        TextView txt_nama, txt_harga, txt_diskon, txt_jumlah, txt_total;

        BarangDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_diskon = itemView.findViewById(R.id.txt_diskon);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_total = itemView.findViewById(R.id.txt_total);
            layout_parent = itemView.findViewById(R.id.layout_parent);
        }
    }
}
