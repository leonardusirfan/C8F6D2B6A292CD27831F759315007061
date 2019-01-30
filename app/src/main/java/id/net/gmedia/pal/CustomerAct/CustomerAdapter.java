package id.net.gmedia.pal.CustomerAct;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import id.net.gmedia.pal.ApprovalPelangganAct.ApprovalPelanggan;
import id.net.gmedia.pal.MerchandiserAct.MerchandiserCustomer;
import id.net.gmedia.pal.MerchandiserAct.MerchandiserTambah;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.PenjualanAct.Penjualan;
import id.net.gmedia.pal.PenjualanAct.PenjualanBarang;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private Activity activity;
    private List<CustomerModel> listCustomer;

    public CustomerAdapter(Activity activity, List<CustomerModel> listCustomer){
        this.activity = activity;
        this.listCustomer = listCustomer;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CustomerViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_customer, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder customerViewHolder, int i) {
        final CustomerModel customer = listCustomer.get(i);

        customerViewHolder.txt_nama.setText(customer.getNama());
        final String alamat = customer.getKota() + " - " + customer.getAlamat();
        customerViewHolder.txt_alamat.setText(alamat);
        if(customer.getStatus().equals("terverifikasi")){
            customerViewHolder.txt_status.setTextColor(activity.getResources().getColor(R.color.green));
        }
        else{
            customerViewHolder.txt_status.setTextColor(activity.getResources().getColor(R.color.yellow));
        }
        customerViewHolder.txt_status.setText(customer.getStatus());

        if(activity instanceof Customer){
            customerViewHolder.item_customer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, CustomerDetail.class);
                    i.putExtra(Constant.EXTRA_ID_CUSTOMER, customer.getId());
                    activity.startActivity(i);
                }
            });
        }
        else if(activity instanceof Penjualan){
            customerViewHolder.item_customer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    Intent i = new Intent(new Intent(activity, PenjualanBarang.class));
                    i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(customer));
                    i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, ((Penjualan)activity).JENIS_PENJUALAN);
                    activity.startActivity(i);
                }
            });
        }
        else if(activity instanceof MerchandiserCustomer){
            customerViewHolder.item_customer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(new Intent(activity, MerchandiserTambah.class));
                    i.putExtra(Constant.EXTRA_ID_CUSTOMER, customer.getId());
                    activity.startActivity(i);
                }
            });
        }
        else if(activity instanceof ApprovalPelanggan){
            customerViewHolder.item_customer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ApprovalPelanggan)activity).showApproval(customer.getId());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listCustomer.size();
    }

    class CustomerViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout item_customer;
        TextView txt_nama, txt_alamat, txt_status;

        CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            item_customer = itemView.findViewById(R.id.item_customer);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            txt_status = itemView.findViewById(R.id.txt_status);
        }
    }
}
