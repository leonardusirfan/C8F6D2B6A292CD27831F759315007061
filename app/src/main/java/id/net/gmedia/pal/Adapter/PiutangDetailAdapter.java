package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Activity.Piutang.PiutangDetail;
import id.net.gmedia.pal.Activity.Piutang.PiutangDetailNota;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Model.PiutangModel;
import id.net.gmedia.pal.R;

public class PiutangDetailAdapter extends RecyclerView.Adapter<PiutangDetailAdapter.PiutangDetailViewHolder> {

    private Activity activity;
    private List<PiutangModel> listPiutang;
    private boolean lunasi = false;

    public PiutangDetailAdapter(Activity activity, List<PiutangModel> listPiutang){
        this.activity = activity;
        this.listPiutang = listPiutang;
    }

    @NonNull
    @Override
    public PiutangDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PiutangDetailViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_piutang_detail, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PiutangDetailViewHolder piutangDetailViewHolder, int i) {
        final PiutangModel piutang = listPiutang.get(i);
        final CheckBox cb_piutang = piutangDetailViewHolder.cb_piutang;

        String nota = "No nota : " + piutang.getNama();
        piutangDetailViewHolder.txt_nama_piutang.setText(nota);
        piutangDetailViewHolder.txt_piutang.setText(Converter.doubleToRupiah(piutang.getJumlah()));
        piutangDetailViewHolder.txt_terbayar.setText(Converter.doubleToRupiah(piutang.getTerbayar()));
        piutangDetailViewHolder.txt_tanggal.setText(piutang.getTanggal());
        piutangDetailViewHolder.txt_tanggal_tempo.setText(piutang.getTanggal_tempo());
        piutangDetailViewHolder.txt_tempo.setText(String.valueOf(piutang.getTempo()));

        if(piutang.isSelected()){
            piutangDetailViewHolder.cb_piutang.setChecked(true);
        }
        else{
            piutangDetailViewHolder.cb_piutang.setChecked(false);
        }

        piutangDetailViewHolder.cb_piutang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb_piutang.isChecked()){
                    piutang.setSelected(true);
                    ((PiutangDetail)activity).updateJumlah(piutang.getJumlah());
                }
                else{
                    piutang.setSelected(false);
                    ((PiutangDetail)activity).updateJumlah(-piutang.getJumlah());
                }
            }
        });

        if(piutang.getType() == Constant.PENJUALAN_CANVAS){
            piutangDetailViewHolder.txt_nama_piutang.setBackgroundColor(activity.getResources().getColor(R.color.yellow));
            piutangDetailViewHolder.item_piutang_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lunasi){
                        cb_piutang.setChecked(!cb_piutang.isChecked());
                        if(cb_piutang.isChecked()){
                            piutang.setSelected(true);
                            ((PiutangDetail)activity).updateJumlah(piutang.getJumlah());
                        }
                        else{
                            piutang.setSelected(false);
                            ((PiutangDetail)activity).updateJumlah(-piutang.getJumlah());
                        }
                    }
                    else{
                        Intent i = new Intent(activity, PiutangDetailNota.class);
                        i.putExtra(Constant.EXTRA_TYPE_NOTA, Constant.PENJUALAN_CANVAS);
                        i.putExtra(Constant.EXTRA_NO_NOTA, piutang.getId());
                        i.putExtra(Constant.EXTRA_NAMA_CUSTOMER, ((PiutangDetail)activity).customer.getNama());
                        activity.startActivity(i);
                    }
                }
            });
        }
        else{
            piutangDetailViewHolder.txt_nama_piutang.setBackgroundColor(activity.getResources().getColor(R.color.orange));
            piutangDetailViewHolder.item_piutang_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lunasi){
                        cb_piutang.setChecked(!cb_piutang.isChecked());
                        if(cb_piutang.isChecked()){
                            piutang.setSelected(true);
                            ((PiutangDetail)activity).updateJumlah(piutang.getJumlah());
                        }
                        else{
                            piutang.setSelected(false);
                            ((PiutangDetail)activity).updateJumlah(-piutang.getJumlah());
                        }
                    }
                    else{
                        Intent i = new Intent(activity, PiutangDetailNota.class);
                        i.putExtra(Constant.EXTRA_TYPE_NOTA, Constant.PENJUALAN_SO);
                        i.putExtra(Constant.EXTRA_NO_NOTA, piutang.getId());
                        i.putExtra(Constant.EXTRA_NAMA_CUSTOMER, ((PiutangDetail)activity).customer.getNama());
                        activity.startActivity(i);
                    }
                }
            });
        }

        if(lunasi){
            piutangDetailViewHolder.cb_piutang.setVisibility(View.VISIBLE);
        }
        else{
            piutangDetailViewHolder.cb_piutang.setVisibility(View.GONE);
        }
    }

    public void setLunasi(boolean lunasi) {
        this.lunasi = lunasi;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listPiutang.size();
    }

    class PiutangDetailViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView item_piutang_detail;
        TextView txt_nama_piutang, txt_piutang, txt_tanggal, txt_tanggal_tempo, txt_tempo, txt_terbayar;
        CheckBox cb_piutang;

        PiutangDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            item_piutang_detail = itemView.findViewById(R.id.item_piutang_detail);
            txt_nama_piutang = itemView.findViewById(R.id.txt_nama_piutang);
            txt_piutang = itemView.findViewById(R.id.txt_piutang);
            txt_terbayar = itemView.findViewById(R.id.txt_terbayar);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_tanggal_tempo = itemView.findViewById(R.id.txt_tanggal_tempo);
            txt_tempo = itemView.findViewById(R.id.txt_tempo);
            cb_piutang = itemView.findViewById(R.id.cb_piutang);
        }
    }
}
