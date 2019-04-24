package id.net.gmedia.pal.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Model.GiroModel;
import id.net.gmedia.pal.R;

public class ListGiroAdapter extends RecyclerView.Adapter<ListGiroAdapter.ListGiroViewHolder> {

    private List<GiroModel> listGiro;

    public ListGiroAdapter(List<GiroModel> listGiro){
        this.listGiro = listGiro;
    }

    @NonNull
    @Override
    public ListGiroAdapter.ListGiroViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ListGiroViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_giro, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListGiroAdapter.ListGiroViewHolder viewHolder, int i) {
        GiroModel giro = listGiro.get(i);

        viewHolder.txt_nomor.setText(giro.getNomor());
        viewHolder.txt_nama.setText(giro.getBank());
        viewHolder.txt_nominal.setText(Converter.doubleToRupiah(giro.getNominal()));
        viewHolder.txt_tgl_terbit.setText(giro.getTanggal_terbit());
        viewHolder.txt_tgl_kadaluarsa.setText(giro.getTanggal_kadaluarsa());
    }

    @Override
    public int getItemCount() {
        return listGiro.size();
    }

    class ListGiroViewHolder extends RecyclerView.ViewHolder{

        TextView txt_nomor, txt_nama, txt_nominal, txt_tgl_terbit, txt_tgl_kadaluarsa;

        ListGiroViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nomor = itemView.findViewById(R.id.txt_nomor);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_nominal = itemView.findViewById(R.id.txt_nominal);
            txt_tgl_terbit = itemView.findViewById(R.id.txt_tgl_terbit);
            txt_tgl_kadaluarsa = itemView.findViewById(R.id.txt_tgl_kadaluarsa);
        }
    }

}
