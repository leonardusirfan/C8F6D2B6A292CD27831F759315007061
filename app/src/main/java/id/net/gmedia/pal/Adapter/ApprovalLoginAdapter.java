package id.net.gmedia.pal.Adapter;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.pal.Activity.Approval.ApprovalLoginPengganti;
import id.net.gmedia.pal.Model.LoginPenggantiModel;
import id.net.gmedia.pal.R;

public class ApprovalLoginAdapter extends RecyclerView.Adapter<ApprovalLoginAdapter.ApprovalLoginViewHolder> {

    private ApprovalLoginPengganti activity;
    private List<LoginPenggantiModel> listPengganti;

    public ApprovalLoginAdapter(ApprovalLoginPengganti activity, List<LoginPenggantiModel> listPengganti){
        this.activity = activity;
        this.listPengganti = listPengganti;
    }

    @NonNull
    @Override
    public ApprovalLoginViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ApprovalLoginViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_approval_login_pengganti, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovalLoginViewHolder holder, int i) {
        final LoginPenggantiModel pengganti = listPengganti.get(i);

        holder.txt_sales.setText(pengganti.getSales());
        holder.txt_pengganti.setText(pengganti.getPengganti());
        final String penggantian = pengganti.getMulai() + " s/d " + pengganti.getSelesai();
        holder.txt_penggantian.setText(penggantian);
        holder.txt_pengajuan.setText(pengganti.getPengajuan());

        holder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.approval(pengganti.getId(), v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPengganti.size();
    }

    class ApprovalLoginViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layout_root;
        TextView txt_sales, txt_pengganti, txt_penggantian, txt_pengajuan;

        ApprovalLoginViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            txt_sales = itemView.findViewById(R.id.txt_sales);
            txt_pengganti = itemView.findViewById(R.id.txt_pengganti);
            txt_penggantian = itemView.findViewById(R.id.txt_penggantian);
            txt_pengajuan = itemView.findViewById(R.id.txt_pengajuan);
        }
    }
}
