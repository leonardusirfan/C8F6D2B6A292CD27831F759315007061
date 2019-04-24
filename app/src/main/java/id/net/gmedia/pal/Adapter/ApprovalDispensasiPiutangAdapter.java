package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.Converter;
import com.leonardus.irfan.SimpleObjectModel;

import java.util.List;

import id.net.gmedia.pal.Activity.Approval.ApprovalDispensasiPiutang;
import id.net.gmedia.pal.Activity.Approval.ApprovalPO;
import id.net.gmedia.pal.Model.DispensasiPiutangModel;
import id.net.gmedia.pal.R;

public class ApprovalDispensasiPiutangAdapter extends RecyclerView.Adapter<ApprovalDispensasiPiutangAdapter.ApprovalDispensasiViewHolder> {

    private ApprovalDispensasiPiutang activity;
    private List<SimpleObjectModel> listApproval;
    private List<DispensasiPiutangModel> listDispensasi;

    public ApprovalDispensasiPiutangAdapter(ApprovalDispensasiPiutang activity, List<DispensasiPiutangModel> listDispensasi){
        this.activity = activity;
        this.listDispensasi = listDispensasi;
    }

    public void setListApproval(List<SimpleObjectModel> listApproval){
        this.listApproval = listApproval;
    }

    @NonNull
    @Override
    public ApprovalDispensasiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ApprovalDispensasiViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_approval_dispensasi_piutang, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ApprovalDispensasiViewHolder holder, int i) {
        final DispensasiPiutangModel d = listDispensasi.get(i);
        holder.txt_nama_customer.setText(d.getCustomer().getNama());
        holder.txt_total_pengajuan.setText(Converter.doubleToRupiah(d.getTotal()));
        holder.txt_keterangan.setText(d.getKeterangan());

        holder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listApproval != null){
                    showApproval(holder.layout_root, d.getId());
                }
                else{
                    Toast.makeText(activity, "Data approval belum termuat", Toast.LENGTH_SHORT).show();
                    activity.loadApproval();
                }
            }
        });
    }

    private void showApproval(View anchor_view, final String id){
        PopupMenu popup = new PopupMenu(activity, anchor_view, Gravity.END);

        for(int i = 0; i < listApproval.size(); i++){
            popup.getMenu().add(0, i, i, listApproval.get(i).getValue());
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                activity.responApproval(id, listApproval.get(menuItem.getItemId()).getId());
                return false;
            }
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return listDispensasi.size();
    }

    class ApprovalDispensasiViewHolder extends RecyclerView.ViewHolder{

        View layout_root;
        TextView txt_keterangan, txt_total_pengajuan, txt_nama_customer;

        ApprovalDispensasiViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            txt_nama_customer = itemView.findViewById(R.id.txt_nama_customer);
            txt_total_pengajuan = itemView.findViewById(R.id.txt_total_pengajuan);
            txt_keterangan = itemView.findViewById(R.id.txt_keterangan);
        }
    }
}
