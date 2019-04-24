package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.SimpleObjectModel;

import java.util.List;

import id.net.gmedia.pal.Activity.Approval.ApprovalSo;
import id.net.gmedia.pal.Activity.DaftarSO.DaftarSODetail;
import id.net.gmedia.pal.Model.NotaPenjualanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalSOAdapter extends RecyclerView.Adapter<ApprovalSOAdapter.ApprovalSOViewHolder> {

    private Activity activity;
    private List<NotaPenjualanModel> listNota;
    private List<SimpleObjectModel> listApproval;

    public ApprovalSOAdapter(Activity activity, List<NotaPenjualanModel> listNota){
        this.activity = activity;
        this.listNota = listNota;
    }

    public void setListApproval(List<SimpleObjectModel> listApproval){
        this.listApproval = listApproval;
    }

    @NonNull
    @Override
    public ApprovalSOViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ApprovalSOViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_approval_so, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovalSOViewHolder viewHolder, int i) {
        final NotaPenjualanModel m = listNota.get(i);

        final ApprovalSOViewHolder finalHolder = viewHolder;
        viewHolder.txt_nama_customer.setText(m.getCustomer().getNama());
        viewHolder.txt_nota.setText(m.getId());
        viewHolder.txt_total.setText(Converter.doubleToRupiah(m.getTotal()));
        viewHolder.txt_tanggal.setText(m.getTanggal());

        final Gson gson = new Gson();
        viewHolder.layout_header.setBackgroundColor(activity.getResources().getColor(R.color.orange));
        viewHolder.item_riwayat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, DaftarSODetail.class);
                i.putExtra(Constant.EXTRA_NOTA, gson.toJson(m));
                activity.startActivity(i);
            }
        });

        viewHolder.img_approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listApproval != null){
                    showApproval(finalHolder.img_approval, m.getId());
                }
                else{
                    Toast.makeText(activity, "Data approval belum termuat", Toast.LENGTH_SHORT).show();
                    ((ApprovalSo)activity).loadApproval();
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
                ((ApprovalSo)activity).responApproval(id, listApproval.get(menuItem.getItemId()).getId());
                return false;
            }
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return listNota.size();
    }

    class ApprovalSOViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView item_riwayat;
        LinearLayout layout_header;
        TextView txt_tanggal, txt_nama_customer, txt_total, txt_nota;
        ImageView img_approval;

        ApprovalSOViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_nama_customer = itemView.findViewById(R.id.txt_nama_customer);
            txt_total = itemView.findViewById(R.id.txt_total);
            txt_nota = itemView.findViewById(R.id.txt_nota);
            layout_header = itemView.findViewById(R.id.layout_header);
            item_riwayat = itemView.findViewById(R.id.item_riwayat);
            img_approval = itemView.findViewById(R.id.img_approval);
        }
    }
}
