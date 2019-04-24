package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Model.BlacklistModel;
import id.net.gmedia.pal.Activity.Piutang.PiutangDetail;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class BlacklistCustomerAdapter extends RecyclerView.Adapter<BlacklistCustomerAdapter.BlacklistCustomerViewHolder> {

    private Activity activity;
    private List<BlacklistModel> listBlacklist;

    public BlacklistCustomerAdapter(Activity activity, List<BlacklistModel> listBlacklist){
        this.activity = activity;
        this.listBlacklist = listBlacklist;
    }

    @NonNull
    @Override
    public BlacklistCustomerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BlacklistCustomerViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_blacklist_customer, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BlacklistCustomerViewHolder viewHolder, int i) {
        final BlacklistModel blacklist = listBlacklist.get(i);

        viewHolder.txt_nama.setText(blacklist.getCustomer().getNama());
        viewHolder.txt_alamat.setText(blacklist.getCustomer().getAlamat());
        viewHolder.txt_kota.setText(blacklist.getCustomer().getKota());
        viewHolder.txt_piutang.setText(Converter.doubleToRupiah(blacklist.getPiutang()));
        String tanggungan = (blacklist.getTanggungan() * 100) + "%";
        viewHolder.txt_tanggungan.setText(tanggungan);
        viewHolder.txt_bayar.setText(Converter.doubleToRupiah(Math.round(blacklist.getPiutang() * blacklist.getTanggungan())));

        viewHolder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(activity, PiutangDetail.class);
                i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(blacklist.getCustomer()));
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listBlacklist.size();
    }

    class BlacklistCustomerViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layout_parent;
        TextView txt_nama, txt_alamat, txt_kota, txt_piutang, txt_bayar, txt_tanggungan;

        BlacklistCustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            txt_kota = itemView.findViewById(R.id.txt_kota);
            txt_piutang = itemView.findViewById(R.id.txt_piutang);
            txt_bayar = itemView.findViewById(R.id.txt_bayar);
            txt_tanggungan = itemView.findViewById(R.id.txt_tanggungan);
        }
    }


}
