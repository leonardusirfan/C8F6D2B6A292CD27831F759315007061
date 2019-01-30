package id.net.gmedia.pal.PiutangAct;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.Converter;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.R;

public class PiutangAdapter extends RecyclerView.Adapter<PiutangAdapter.PiutangViewHolder> {

    private Activity activity;
    private List<CustomerModel> listPiutang;

    PiutangAdapter(Activity activity, List<CustomerModel> listPiutang) {
        this.activity = activity;
        this.listPiutang = listPiutang;
    }

    @NonNull
    @Override
    public PiutangViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PiutangViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_piutang, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PiutangViewHolder piutangViewHolder, int i) {
        final CustomerModel piutang = listPiutang.get(i);

        piutangViewHolder.txt_nama.setText(piutang.getNama());
        piutangViewHolder.txt_jumlah.setText(Converter.doubleToRupiah(piutang.getTotalPiutang()));

        piutangViewHolder.item_piutang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(activity, PiutangDetail.class);
                i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(piutang));
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPiutang.size();
    }

    class PiutangViewHolder extends RecyclerView.ViewHolder {

        TextView txt_nama, txt_jumlah;
        LinearLayout item_piutang;

        PiutangViewHolder(@NonNull View itemView) {
            super(itemView);
            item_piutang = itemView.findViewById(R.id.item_piutang);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
        }
    }
}
