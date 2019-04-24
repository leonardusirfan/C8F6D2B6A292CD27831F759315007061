package id.net.gmedia.pal.Adapter;

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

public class SuratJalanDetailAdapter extends RecyclerView.Adapter<SuratJalanDetailAdapter.SuratJalanDetailViewHolder> {

    private List<BarangModel> listBarang;

    public SuratJalanDetailAdapter(List<BarangModel> listBarang){
        this.listBarang = listBarang;
    }


    @NonNull
    @Override
    public SuratJalanDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SuratJalanDetailViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_surat_jalan_detail, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SuratJalanDetailViewHolder holder, int i) {
        BarangModel b = listBarang.get(i);

        holder.txt_nama.setText(b.getNama());
        String jumlah = b.getJumlah() + " " + b.getSatuan();
        holder.txt_jumlah.setText(jumlah);
        holder.txt_harga.setText(Converter.doubleToRupiah(b.getHarga()));
        holder.txt_total.setText(Converter.doubleToRupiah(b.getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class SuratJalanDetailViewHolder extends RecyclerView.ViewHolder{

        TextView txt_nama, txt_jumlah, txt_harga, txt_total;

        SuratJalanDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_total = itemView.findViewById(R.id.txt_total);
        }
    }
}
