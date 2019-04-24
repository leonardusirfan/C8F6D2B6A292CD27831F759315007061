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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.SimpleObjectModel;

import java.util.List;

import id.net.gmedia.pal.Activity.Approval.ApprovalPO;
import id.net.gmedia.pal.Activity.Approval.ApprovalPODetail;
import id.net.gmedia.pal.Model.PurchaseOrderModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;


public class ApprovalPOAdapter extends RecyclerView.Adapter<ApprovalPOAdapter.ApprovalPOViewHolder> {

    private List<PurchaseOrderModel> listPO;
    private List<SimpleObjectModel> listApproval;
    private Activity activity;

    public ApprovalPOAdapter(Activity activity, List<PurchaseOrderModel> listPO){
        this.activity = activity;
        this.listPO = listPO;
    }

    public void setListApproval(List<SimpleObjectModel> listApproval){
        this.listApproval = listApproval;
    }

    @NonNull
    @Override
    public ApprovalPOViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ApprovalPOViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_approval_po, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ApprovalPOViewHolder viewHolder, int i) {
        final PurchaseOrderModel po = listPO.get(i);

        viewHolder.txt_nama.setText(po.getId());
        viewHolder.txt_tanggal.setText(po.getTanggal());
        viewHolder.txt_total.setText(Converter.doubleToRupiah(po.getTotal()));
        viewHolder.txt_ppn.setText(Converter.doubleToRupiah(po.getPpn()));
        viewHolder.txt_biaya_lain.setText(Converter.doubleToRupiah(po.getBiaya_lain()));
        viewHolder.txt_kode_supplier.setText(po.getKode_supplier());
        viewHolder.txt_tanggal_tempo.setText(po.getTanggal_tempo());
        viewHolder.txt_keterangan.setText(po.getKeterangan());

        viewHolder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(activity, ApprovalPODetail.class);
                i.putExtra(Constant.EXTRA_NOTA, gson.toJson(po));
                activity.startActivity(i);
            }
        });

        viewHolder.img_approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listApproval != null){
                    showApproval(viewHolder.img_approval, po.getId());
                }
                else{
                    Toast.makeText(activity, "Data approval belum termuat", Toast.LENGTH_SHORT).show();
                    ((ApprovalPO)activity).loadApproval();
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
                ((ApprovalPO)activity).responApproval(id, listApproval.get(menuItem.getItemId()).getId());
                return false;
            }
        });

        popup.show();
    }

    @Override
    public int getItemCount() {
        return listPO.size();
    }

    class ApprovalPOViewHolder extends RecyclerView.ViewHolder{

        MaterialCardView layout_parent;
        TextView txt_nama, txt_tanggal, txt_total, txt_ppn, txt_biaya_lain,
        txt_tanggal_tempo, txt_kode_supplier, txt_keterangan;
        ImageView img_approval;

        ApprovalPOViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_total = itemView.findViewById(R.id.txt_total);
            txt_ppn = itemView.findViewById(R.id.txt_ppn);
            txt_biaya_lain = itemView.findViewById(R.id.txt_biaya_lain);
            txt_kode_supplier = itemView.findViewById(R.id.txt_kode_supplier);
            txt_tanggal_tempo = itemView.findViewById(R.id.txt_tanggal_tempo);
            txt_keterangan = itemView.findViewById(R.id.txt_keterangan);
            img_approval = itemView.findViewById(R.id.img_approval);
            layout_parent = itemView.findViewById(R.id.layout_parent);
        }
    }
}
