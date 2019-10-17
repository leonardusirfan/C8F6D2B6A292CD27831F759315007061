package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.content.Context;
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

public class AdapterRiwayatSetoran extends RecyclerView.Adapter<AdapterRiwayatSetoran.ViewHolderRiwayatSetoran>{

    private Context context;
    private List<SetoranModel> listRiwayat;

    public AdapterRiwayatSetoran(Context context, List<SetoranModel> listRiwayat){
        this.context = context;
        this.listRiwayat = listRiwayat;
    }

    @NonNull
    @Override
    public ViewHolderRiwayatSetoran onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderRiwayatSetoran(LayoutInflater.from(context).inflate(R.layout.item_riwayat_setoran, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderRiwayatSetoran viewHolderRiwayatSetoran, int i) {
        viewHolderRiwayatSetoran.bind(listRiwayat.get(i));
    }

    @Override
    public int getItemCount() {
        return listRiwayat.size();
    }

    class ViewHolderRiwayatSetoran extends RecyclerView.ViewHolder{

        TextView txt_nomor, txt_tanggal, txt_nama_customer,
                txt_cara_bayar, txt_keterangan, txt_total;

        ViewHolderRiwayatSetoran(@NonNull View itemView) {
            super(itemView);
            txt_nomor = itemView.findViewById(R.id.txt_nomor);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_nama_customer = itemView.findViewById(R.id.txt_nama_customer);
            txt_cara_bayar = itemView.findViewById(R.id.txt_cara_bayar);
            txt_keterangan = itemView.findViewById(R.id.txt_keterangan);
            txt_total = itemView.findViewById(R.id.txt_total);
        }

        void bind(SetoranModel s){
            txt_nomor.setText(s.getNota());
            txt_nama_customer.setText(s.getCustomer());
            txt_cara_bayar.setText(s.getPembayaran());
            txt_tanggal.setText(s.getTanggal());
            txt_total.setText(Converter.doubleToRupiah(s.getJumlah()));
            txt_keterangan.setText(s.getKeterangan());
        }
    }
}
