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
import android.widget.TextView;

import com.google.gson.Gson;
import com.leonardus.irfan.Converter;

import java.util.List;

import id.net.gmedia.pal.Activity.Approval.ApprovalPODetail;
import id.net.gmedia.pal.Activity.Approval.ApprovalPOEdit;
import id.net.gmedia.pal.Model.BarangPOModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class ApprovalPODetailAdapter extends RecyclerView.Adapter<ApprovalPODetailAdapter.ApprovalPODetailViewHolder> {

    private Activity activity;
    private List<BarangPOModel> listPo;

    public ApprovalPODetailAdapter(Activity activity, List<BarangPOModel> listPo){
        this.activity = activity;
        this.listPo = listPo;
    }

    @NonNull
    @Override
    public ApprovalPODetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ApprovalPODetailViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_approval_po_detail, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ApprovalPODetailViewHolder approvalPODetailViewHolder, int i) {
        final BarangPOModel barang = listPo.get(i);

        approvalPODetailViewHolder.txt_nama.setText(barang.getNama());
        String jumlah = barang.getJumlah() + " " + barang.getSatuan();
        approvalPODetailViewHolder.txt_jumlah.setText(jumlah);
        approvalPODetailViewHolder.txt_harga.setText(Converter.doubleToRupiah(barang.getHarga()));
        approvalPODetailViewHolder.txt_ppn.setText(String.valueOf(barang.getPpn()));
        approvalPODetailViewHolder.txt_total.setText(Converter.doubleToRupiah(barang.getSubtotal()));

        /*approvalPODetailViewHolder.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(activity, approvalPODetailViewHolder.img_edit, Gravity.END);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_item_penjualan, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_hapus){
                            ((ApprovalPODetail)activity).hapusBarangPO(barang.getId());
                            return true;
                        }
                        else if(item.getItemId() == R.id.action_edit){
                            Gson gson = new Gson();
                            Intent i = new Intent(activity, ApprovalPOEdit.class);
                            i.putExtra(Constant.EXTRA_BARANG_PO, gson.toJson(barang));
                            i.putExtra(Constant.EXTRA_NO_NOTA, ((ApprovalPODetail)activity).nota.getId());
                            activity.startActivity(i);

                            return true;
                        }

                        return false;
                    }
                });

                popup.show(); //showing popup menu
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return listPo.size();
    }

    class ApprovalPODetailViewHolder extends RecyclerView.ViewHolder{

        TextView txt_nama, txt_harga, txt_jumlah, txt_ppn, txt_total;
        //ImageView img_edit;

        ApprovalPODetailViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_ppn = itemView.findViewById(R.id.txt_ppn);
            txt_total = itemView.findViewById(R.id.txt_total);
            //img_edit = itemView.findViewById(R.id.img_edit);
        }
    }
}
