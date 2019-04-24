package id.net.gmedia.pal.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Model.SetoranModel;
import id.net.gmedia.pal.R;

public class SetoranSalesAdapter extends RecyclerView.Adapter<SetoranSalesAdapter.SetoranSalesViewHolder> {

    private List<SetoranModel> listSetoran;

    public SetoranSalesAdapter(List<SetoranModel> listSetoran){
        this.listSetoran = listSetoran;
    }

    @NonNull
    @Override
    public SetoranSalesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SetoranSalesViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_setoran_sales, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SetoranSalesViewHolder setoranSalesViewHolder, int i) {
        SetoranModel setoran = listSetoran.get(i);

        setoranSalesViewHolder.txt_tanggal.setText(setoran.getTanggal());
        setoranSalesViewHolder.txt_customer.setText(setoran.getCustomer());
        setoranSalesViewHolder.txt_nota.setText(setoran.getNota());
        setoranSalesViewHolder.txt_jumlah.setText(Converter.doubleToRupiah(setoran.getJumlah()));
        setoranSalesViewHolder.txt_pembayaran.setText(setoran.getPembayaran());
    }

    @Override
    public int getItemCount() {
        return listSetoran.size();
    }

    class SetoranSalesViewHolder extends RecyclerView.ViewHolder{

        TextView txt_customer, txt_nota, txt_jumlah, txt_tanggal, txt_pembayaran;

        SetoranSalesViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_customer = itemView.findViewById(R.id.txt_customer);
            txt_nota = itemView.findViewById(R.id.txt_nota);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_pembayaran = itemView.findViewById(R.id.txt_pembayaran);
        }
    }
}
