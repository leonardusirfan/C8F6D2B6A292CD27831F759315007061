package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Activity.DaftarSO.DaftarSODetail;
import id.net.gmedia.pal.Model.NotaSOModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class DaftarSOAdapter extends RecyclerView.Adapter<DaftarSOAdapter.DaftarSOViewHolder> {

    private Activity activity;
    private List<NotaSOModel> listNota;

    public DaftarSOAdapter(Activity activity, List<NotaSOModel> listNota){
        this.activity = activity;
        this.listNota = listNota;
    }

    @NonNull
    @Override
    public DaftarSOViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DaftarSOViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_daftar_so, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DaftarSOViewHolder daftarSOViewHolder, int i) {
        final NotaSOModel nota = listNota.get(i);

        daftarSOViewHolder.txt_tanggal.setText(nota.getTanggal());
        daftarSOViewHolder.txt_nama.setText(nota.getCustomer().getNama());
        daftarSOViewHolder.txt_nota.setText(nota.getId());
        daftarSOViewHolder.txt_total.setText(Converter.doubleToRupiah(nota.getTotal()));
        daftarSOViewHolder.txt_status.setText(nota.getStatus());
        switch (nota.getStatus()){
            case "Disetujui":daftarSOViewHolder.txt_status.setTextColor(activity.getResources().getColor(R.color.green));break;
            case "Ditolak":daftarSOViewHolder.txt_status.setTextColor(activity.getResources().getColor(R.color.red));break;
            case "Dibatalkan":daftarSOViewHolder.txt_status.setTextColor(activity.getResources().getColor(R.color.orange));break;
            case "Butuh Approval BM":daftarSOViewHolder.txt_status.setTextColor(activity.getResources().getColor(R.color.yellow));
                        daftarSOViewHolder.img_edit.setVisibility(View.VISIBLE);break;
        }

        daftarSOViewHolder.txt_status_kirim.setText(nota.isStatus_kirim()?"Terkirim":"Menunggu dikirim");
        if(nota.isStatus_kirim()){
            daftarSOViewHolder.layout_kirim.setVisibility(View.VISIBLE);
            daftarSOViewHolder.txt_nota_pengeluaran.setText(nota.getNota_pengeluaran());
        }

        daftarSOViewHolder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(activity, DaftarSODetail.class);
                if(nota.getStatus().equals("Proses")){
                    i.putExtra(Constant.EXTRA_EDIT, true);
                }
                i.putExtra(Constant.EXTRA_NOTA, gson.toJson(nota));
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listNota.size();
    }

    class DaftarSOViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView layout_parent;
        ImageView img_edit;
        LinearLayout layout_kirim;
        TextView txt_nama, txt_total, txt_nota, txt_status, txt_tanggal,
                txt_nota_pengeluaran, txt_status_kirim;

        DaftarSOViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_total = itemView.findViewById(R.id.txt_total);
            txt_nota = itemView.findViewById(R.id.txt_nota);
            txt_status = itemView.findViewById(R.id.txt_status);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_nota_pengeluaran = itemView.findViewById(R.id.txt_nota_pengeluaran);
            txt_status_kirim = itemView.findViewById(R.id.txt_status_kirim);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            layout_kirim = itemView.findViewById(R.id.layout_kirim);
            img_edit = itemView.findViewById(R.id.img_edit);
        }
    }
}
