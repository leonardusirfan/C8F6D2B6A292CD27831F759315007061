package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Activity.Approval.ApprovalPenambahanPlafonDetail;
import id.net.gmedia.pal.Activity.PengajuanPlafon;
import id.net.gmedia.pal.Model.PengajuanPlafonModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalPenambahanPlafonAdapter extends RecyclerView.Adapter
        <ApprovalPenambahanPlafonAdapter.ApprovalPenambahanPlafonViewHolder> {

    private Activity activity;
    private List<PengajuanPlafonModel> listPengajuan;

    public ApprovalPenambahanPlafonAdapter(Activity activity, List<PengajuanPlafonModel> listPengajuan){
        this.activity = activity;
        this.listPengajuan = listPengajuan;
    }

    @NonNull
    @Override
    public ApprovalPenambahanPlafonViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ApprovalPenambahanPlafonViewHolder(LayoutInflater.from(activity).
                inflate(R.layout.item_approval_plafon, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovalPenambahanPlafonViewHolder holder, int i) {
        final PengajuanPlafonModel p = listPengajuan.get(i);

        holder.txt_nama_customer.setText(p.getCustomer().getNama());
        holder.txt_plafon_nota.setText(String.valueOf(p.getPlafon_nota()));
        holder.txt_plafon_nominal.setText(Converter.doubleToRupiah(p.getPlafon_nominal()));
        holder.txt_alasan.setText(p.getAlasan());

        holder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, ApprovalPenambahanPlafonDetail.class);
                i.putExtra(Constant.EXTRA_ID_PENGAJUAN_PLAFON, p.getId());
                activity.startActivity(i);
            }
        });

        holder.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(activity, PengajuanPlafon.class);
                i.putExtra(Constant.EXTRA_PENGAJUAN_PLAFON, gson.toJson(p));
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPengajuan.size();
    }

    class ApprovalPenambahanPlafonViewHolder extends RecyclerView.ViewHolder{

        View layout_root;
        TextView txt_nama_customer, txt_plafon_nota, txt_plafon_nominal, txt_alasan;
        ImageView img_edit;

        ApprovalPenambahanPlafonViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            txt_nama_customer = itemView.findViewById(R.id.txt_nama_customer);
            txt_plafon_nota = itemView.findViewById(R.id.txt_plafon_nota);
            txt_plafon_nominal = itemView.findViewById(R.id.txt_plafon_nominal);
            txt_alasan = itemView.findViewById(R.id.txt_alasan);
            img_edit = itemView.findViewById(R.id.img_edit);
        }
    }
}
