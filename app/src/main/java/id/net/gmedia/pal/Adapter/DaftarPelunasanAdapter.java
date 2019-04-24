package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Activity.DaftarPelunasanDetail;
import id.net.gmedia.pal.Model.NotaPelunasanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class DaftarPelunasanAdapter extends RecyclerView.Adapter<DaftarPelunasanAdapter.DaftarPelunasanViewHolder> {

    private Activity activity;
    private List<NotaPelunasanModel> listNota;

    public DaftarPelunasanAdapter(Activity activity, List<NotaPelunasanModel> listNota){
        this.activity = activity;
        this.listNota = listNota;
    }

    @NonNull
    @Override
    public DaftarPelunasanViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DaftarPelunasanViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_daftar_pelunasan, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DaftarPelunasanViewHolder holder, int i) {
        final NotaPelunasanModel n = listNota.get(i);

        holder.txt_no_bukti.setText(n.getNama());
        holder.txt_tanggal.setText(n.getTanggal());
        holder.txt_keterangan.setText(n.getKeterangan());
        holder.txt_cara_bayar.setText(n.getCara_bayar());
        holder.txt_total.setText(Converter.doubleToRupiah(n.getTotal()));

        holder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(activity, DaftarPelunasanDetail.class);
                i.putExtra(Constant.EXTRA_ID_NOTA, n.getNama());
                i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(n.getCustomer()));
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNota.size();
    }

    class DaftarPelunasanViewHolder extends RecyclerView.ViewHolder{

        View layout_parent;
        TextView txt_no_bukti, txt_tanggal, txt_total, txt_cara_bayar, txt_keterangan;

        DaftarPelunasanViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            txt_no_bukti = itemView.findViewById(R.id.txt_no_bukti);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_cara_bayar = itemView.findViewById(R.id.txt_cara_bayar);
            txt_keterangan = itemView.findViewById(R.id.txt_keterangan);
            txt_total = itemView.findViewById(R.id.txt_total);
        }
    }
}
