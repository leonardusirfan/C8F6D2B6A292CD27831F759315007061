package id.net.gmedia.pal.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Activity.SuratJalan;
import id.net.gmedia.pal.Activity.SuratJalanDetail;
import id.net.gmedia.pal.Model.NotaModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class SuratJalanAdapter extends RecyclerView.Adapter<SuratJalanAdapter.SuratJalanViewHolder> {

    private SuratJalan activity;
    private List<NotaModel> listNota;

    public SuratJalanAdapter(SuratJalan activity, List<NotaModel> listNota){
        this.activity = activity;
        this.listNota = listNota;
    }

    @NonNull
    @Override
    public SuratJalanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SuratJalanViewHolder(LayoutInflater.from(activity).
                inflate(R.layout.item_surat_jalan, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SuratJalanViewHolder holder, int i) {
        final NotaModel n = listNota.get(i);

        holder.txt_nomor.setText(n.getNama());
        holder.txt_tanggal.setText(n.getTanggal());
        holder.txt_total.setText(Converter.doubleToRupiah(n.getTotal()));
        holder.txt_keterangan.setText(n.getKeterangan());

        holder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, SuratJalanDetail.class);
                i.putExtra(Constant.EXTRA_ID_NOTA, n.getId());
                i.putExtra(Constant.EXTRA_NO_NOTA, n.getNama());
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNota.size();
    }

    class SuratJalanViewHolder extends RecyclerView.ViewHolder{

        View layout_root;
        TextView txt_nomor, txt_tanggal, txt_keterangan, txt_total;

        SuratJalanViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            txt_nomor = itemView.findViewById(R.id.txt_nomor);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_keterangan = itemView.findViewById(R.id.txt_keterangan);
            txt_total = itemView.findViewById(R.id.txt_total);
        }
    }
}
