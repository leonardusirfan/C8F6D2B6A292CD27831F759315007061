package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.pal.Activity.Approval.ApprovalRetur;
import id.net.gmedia.pal.Activity.Approval.ApprovalReturDetail;
import id.net.gmedia.pal.Activity.ReturKonfirmasi;
import id.net.gmedia.pal.Model.NotaPenjualanModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalReturAdapter extends RecyclerView.Adapter<ApprovalReturAdapter.ApprovalReturViewHolder> {

    private Activity activity;
    private List<NotaPenjualanModel> listNota;
    private List<SimpleObjectModel> listApproval = new ArrayList<>();

    public ApprovalReturAdapter(Activity activity, List<NotaPenjualanModel> listNota){
        this.activity = activity;
        this.listNota = listNota;
    }

    public void setListApproval(List<SimpleObjectModel> listApproval) {
        this.listApproval = listApproval;
    }

    @NonNull
    @Override
    public ApprovalReturViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ApprovalReturViewHolder(LayoutInflater.from(activity).
                inflate(R.layout.item_approval_retur, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ApprovalReturViewHolder approvalReturViewHolder, int i) {
        final NotaPenjualanModel m = listNota.get(i);

        approvalReturViewHolder.txt_nama_customer.setText(m.getCustomer().getNama());
        approvalReturViewHolder.txt_nota.setText(m.getNama());
        approvalReturViewHolder.txt_total.setText(Converter.doubleToRupiah(m.getTotal()));

        approvalReturViewHolder.img_approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listApproval.size() > 0){
                    showApproval(approvalReturViewHolder.img_approval, m.getId());
                }
                else{
                    Toast.makeText(activity, "Data approval belum termuat", Toast.LENGTH_SHORT).show();
                    if(activity instanceof ApprovalRetur){
                        ((ApprovalRetur)activity).loadApproval();
                    }
                    else if(activity instanceof ReturKonfirmasi){
                        ((ReturKonfirmasi)activity).loadApproval();
                    }
                }
            }
        });

        approvalReturViewHolder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(activity, ApprovalReturDetail.class);
                i.putExtra(Constant.EXTRA_NOTA, gson.toJson(m));
                activity.startActivity(i);
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
                if(activity instanceof ApprovalRetur){
                    ((ApprovalRetur)activity).responRetur(id, listApproval.get(menuItem.getItemId()).getId());
                }
                else if(activity instanceof ReturKonfirmasi){
                    ((ReturKonfirmasi)activity).responRetur(id, listApproval.get(menuItem.getItemId()).getId());
                }
                return false;
            }
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return listNota.size();
    }

    class ApprovalReturViewHolder extends RecyclerView.ViewHolder{

        LinearLayout layout_parent;
        LinearLayout layout_header;
        ImageView img_approval;
        TextView txt_nama_customer, txt_total, txt_nota;

        ApprovalReturViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            layout_header = itemView.findViewById(R.id.layout_header);
            img_approval = itemView.findViewById(R.id.img_approval);
            txt_nama_customer = itemView.findViewById(R.id.txt_nama_customer);
            txt_total = itemView.findViewById(R.id.txt_total);
            txt_nota = itemView.findViewById(R.id.txt_nota);
        }
    }
}
