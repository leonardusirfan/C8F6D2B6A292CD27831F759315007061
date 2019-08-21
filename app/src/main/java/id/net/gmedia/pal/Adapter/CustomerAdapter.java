package id.net.gmedia.pal.Adapter;

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

import id.net.gmedia.pal.Activity.Approval.ApprovalPelanggan;
import id.net.gmedia.pal.Activity.Customer;
import id.net.gmedia.pal.Activity.CustomerDetail;
import id.net.gmedia.pal.Activity.DaftarPelunasan;
import id.net.gmedia.pal.Activity.DispensasiPiutang;
import id.net.gmedia.pal.Activity.ListGiroDetail;
import id.net.gmedia.pal.Activity.MerchandiserTambah;
import id.net.gmedia.pal.Activity.PengajuanPlafon;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.Activity.PenjualanSoCanvas.Penjualan;
import id.net.gmedia.pal.Activity.PenjualanSoCanvas.PenjualanBarang;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppKeranjangPenjualan;
import id.net.gmedia.pal.Util.Constant;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private Activity activity;
    private List<CustomerModel> listCustomer;
    private int ACT_CODE = 0;

    public CustomerAdapter(Activity activity, List<CustomerModel> listCustomer){
        this.activity = activity;
        this.listCustomer = listCustomer;
    }

    public CustomerAdapter(Activity activity, List<CustomerModel> listCustomer, int act_code){
        this.activity = activity;
        this.listCustomer = listCustomer;
        this.ACT_CODE = act_code;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CustomerViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_customer, viewGroup, false));
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
            if(ACT_CODE == 0){
                customerViewHolder.item_customer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(activity, CustomerDetail.class);
                        i.putExtra(Constant.EXTRA_ID_CUSTOMER, customer.getId());
                        activity.startActivity(i);
                    }
                });
            }
            else if(ACT_CODE == Constant.ACT_DISPENSASI){
                customerViewHolder.item_customer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(activity, DispensasiPiutang.class);
                        i.putExtra(Constant.EXTRA_ID_CUSTOMER, customer.getId());
                        activity.startActivity(i);
                    }
                });
            }
            else if(ACT_CODE == Constant.ACT_GIRO){
                customerViewHolder.item_customer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(activity, ListGiroDetail.class);
                        i.putExtra(Constant.EXTRA_ID_CUSTOMER, customer.getId());
                        activity.startActivity(i);
                    }
                });
            }
            else if(ACT_CODE == Constant.ACT_MERCHANDISER){
                customerViewHolder.item_customer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(new Intent(activity, MerchandiserTambah.class));
                        i.putExtra(Constant.EXTRA_ID_CUSTOMER, customer.getId());
                        activity.startActivity(i);
                    }
                });
            }
            else if(ACT_CODE == Constant.ACT_PENAMBAHAN_PLAFON){
                customerViewHolder.item_customer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gson gson = new Gson();
                        Intent i = new Intent(new Intent(activity, PengajuanPlafon.class));
                        i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(customer));
                        activity.startActivity(i);
                    }
                });
            }
            else if(ACT_CODE == Constant.ACT_DAFTAR_PELUNASAN){
                customerViewHolder.item_customer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Gson gson = new Gson();
                        Intent i = new Intent(new Intent(activity, DaftarPelunasan.class));
                        i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(customer));
                        activity.startActivity(i);
                    }
                });
            }
        }
        else if(activity instanceof Penjualan){
            customerViewHolder.item_customer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppKeranjangPenjualan.getInstance().startPenjualan(customer,
                            ((Penjualan)activity).JENIS_PENJUALAN);

                    Intent i = new Intent(new Intent(activity, PenjualanBarang.class));
                    activity.startActivity(i);
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
