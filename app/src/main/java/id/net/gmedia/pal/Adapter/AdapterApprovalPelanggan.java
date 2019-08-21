package id.net.gmedia.pal.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.pal.Activity.Approval.ApprovalPelanggan;
import id.net.gmedia.pal.Activity.CustomerDetail;
import id.net.gmedia.pal.Model.CustomerModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class AdapterApprovalPelanggan extends RecyclerView.Adapter<AdapterApprovalPelanggan.ViewHolderApprovalPelanggan> {

    private ApprovalPelanggan activity;
    private List<CustomerModel> listCustomer;

    public AdapterApprovalPelanggan(ApprovalPelanggan activity, List<CustomerModel> listCustomer){
        this.activity = activity;
        this.listCustomer = listCustomer;
    }

    @NonNull
    @Override
    public ViewHolderApprovalPelanggan onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderApprovalPelanggan(LayoutInflater.from(activity).
                inflate(R.layout.item_approval_customer, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderApprovalPelanggan viewHolderApprovalPelanggan, int i) {
        viewHolderApprovalPelanggan.bind(listCustomer.get(i));
    }

    @Override
    public int getItemCount() {
        return listCustomer.size();
    }

    class ViewHolderApprovalPelanggan extends RecyclerView.ViewHolder{

        View layout_parent;
        TextView txt_nama, txt_alamat;
        Button btn_approval;

        ViewHolderApprovalPelanggan(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            btn_approval = itemView.findViewById(R.id.btn_approval);
        }

        void bind(final CustomerModel c){
            txt_nama.setText(c.getNama());
            txt_alamat.setText(c.getAlamat());

            layout_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, CustomerDetail.class);
                    i.putExtra(Constant.EXTRA_ID_CUSTOMER, c.getId());
                    activity.startActivity(i);
                }
            });

            btn_approval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.showApproval(c.getId());
                }
            });
        }
    }
}
