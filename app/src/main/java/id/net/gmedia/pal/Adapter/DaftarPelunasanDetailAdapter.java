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

import id.net.gmedia.pal.Model.NotaPenjualanModel;
import id.net.gmedia.pal.R;

public class DaftarPelunasanDetailAdapter extends RecyclerView.Adapter<DaftarPelunasanDetailAdapter.DaftarPelunasanDetailViewHolder> {

    private Activity activity;
    private List<NotaPenjualanModel> listNota;

    public DaftarPelunasanDetailAdapter(Activity activity, List<NotaPenjualanModel> listNota){
        this.activity = activity;
        this.listNota = listNota;
    }

    @NonNull
    @Override
    public DaftarPelunasanDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DaftarPelunasanDetailViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_daftar_pelunasan_detail, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DaftarPelunasanDetailViewHolder holder, int i) {
        NotaPenjualanModel nota = listNota.get(i);

        holder.txt_no_bukti.setText(nota.getId());
        holder.txt_terbayar.setText(Converter.doubleToRupiah(nota.getTerbayar()));
        holder.txt_total.setText(Converter.doubleToRupiah(nota.getTotal()));
    }

    @Override
    public int getItemCount() {
        return listNota.size();
    }

    class DaftarPelunasanDetailViewHolder extends RecyclerView.ViewHolder{

        TextView txt_no_bukti, txt_total, txt_terbayar;

        DaftarPelunasanDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_no_bukti = itemView.findViewById(R.id.txt_no_bukti);
            txt_total = itemView.findViewById(R.id.txt_total);
            txt_terbayar = itemView.findViewById(R.id.txt_terbayar);
        }
    }
}
