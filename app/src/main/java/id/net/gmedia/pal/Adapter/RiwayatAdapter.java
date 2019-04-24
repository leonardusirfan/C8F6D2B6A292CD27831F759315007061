package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Activity.RiwayatDetail;
import id.net.gmedia.pal.Model.NotaPenjualanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Activity.ReturBarang;
import id.net.gmedia.pal.Activity.ReturBarangDetail;
import id.net.gmedia.pal.Util.Constant;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.RiwayatViewHolder>{

    private Activity activity;
    private List<NotaPenjualanModel> listNota;

    public RiwayatAdapter(Activity activity, List<NotaPenjualanModel> listNota){
        this.activity = activity;
        this.listNota = listNota;
    }

    @NonNull
    @Override
    public RiwayatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RiwayatViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_riwayat, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RiwayatViewHolder riwayatViewHolder, int i) {
        final NotaPenjualanModel m = listNota.get(i);

        riwayatViewHolder.txt_nama_customer.setText(m.getCustomer().getNama());
        riwayatViewHolder.txt_nota.setText(m.getId());
        riwayatViewHolder.txt_total.setText(Converter.doubleToRupiah(m.getTotal()));
        riwayatViewHolder.txt_tanggal.setText(m.getTanggal());

        final Gson gson = new Gson();
        if(activity instanceof ReturBarang){
            if(m.getType() == Constant.PENJUALAN_SO){
                riwayatViewHolder.layout_header.setBackgroundColor(activity.getResources().getColor(R.color.orange));
                riwayatViewHolder.item_riwayat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(activity, ReturBarangDetail.class);
                        i.putExtra(Constant.EXTRA_NOTA, gson.toJson(m));
                        i.putExtra(Constant.EXTRA_TYPE_NOTA, Constant.PENJUALAN_SO);
                        activity.startActivity(i);
                    }
                });
            }
            else{
                riwayatViewHolder.layout_header.setBackgroundColor(activity.getResources().getColor(R.color.yellow));
                riwayatViewHolder.item_riwayat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(activity, ReturBarangDetail.class);
                        i.putExtra(Constant.EXTRA_NOTA, gson.toJson(m));
                        i.putExtra(Constant.EXTRA_TYPE_NOTA, Constant.PENJUALAN_CANVAS);
                        activity.startActivity(i);
                    }
                });
            }
        }
        else{
            if(m.getType() == Constant.PENJUALAN_SO){
                riwayatViewHolder.layout_header.setBackgroundColor(activity.getResources().getColor(R.color.orange));
                riwayatViewHolder.item_riwayat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(activity, RiwayatDetail.class);
                        i.putExtra(Constant.EXTRA_NOTA, gson.toJson(m));
                        i.putExtra(Constant.EXTRA_TYPE_NOTA, Constant.PENJUALAN_SO);
                        activity.startActivity(i);
                    }
                });
            }
            else{
                riwayatViewHolder.layout_header.setBackgroundColor(activity.getResources().getColor(R.color.yellow));
                riwayatViewHolder.item_riwayat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(activity, RiwayatDetail.class);
                        i.putExtra(Constant.EXTRA_NOTA, gson.toJson(m));
                        i.putExtra(Constant.EXTRA_TYPE_NOTA, Constant.PENJUALAN_CANVAS);
                        activity.startActivity(i);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return listNota.size();
    }

    class RiwayatViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView item_riwayat;
        LinearLayout layout_header;
        TextView txt_tanggal, txt_nama_customer, txt_total, txt_nota;

        RiwayatViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_nama_customer = itemView.findViewById(R.id.txt_nama_customer);
            txt_total = itemView.findViewById(R.id.txt_total);
            txt_nota = itemView.findViewById(R.id.txt_nota);
            layout_header = itemView.findViewById(R.id.layout_header);
            item_riwayat = itemView.findViewById(R.id.item_riwayat);
        }
    }
}
