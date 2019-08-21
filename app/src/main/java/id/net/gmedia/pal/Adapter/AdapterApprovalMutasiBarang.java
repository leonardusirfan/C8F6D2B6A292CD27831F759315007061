package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leonardus.irfan.DialogFactory;

import java.util.List;

import id.net.gmedia.pal.Activity.Approval.ApprovalPengajuanMutasiDetail;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.R;

public class AdapterApprovalMutasiBarang extends RecyclerView.Adapter
        <AdapterApprovalMutasiBarang.ViewHolderApprovalMutasiBarang> {

    private Activity activity;
    private List<BarangModel> listBarang;

    public AdapterApprovalMutasiBarang(Activity activity, List<BarangModel> listBarang){
        this.activity = activity;
        this.listBarang = listBarang;
    }

    @NonNull
    @Override
    public ViewHolderApprovalMutasiBarang onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderApprovalMutasiBarang(LayoutInflater.from(activity).
                inflate(R.layout.item_approval_barang_mutasi, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderApprovalMutasiBarang viewHolderApprovalMutasiBarang, int i) {
        viewHolderApprovalMutasiBarang.bind(listBarang.get(i));
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class ViewHolderApprovalMutasiBarang extends RecyclerView.ViewHolder{

        View layout_parent;
        TextView txt_nama, txt_jumlah;
        ImageView img_hapus;

        ViewHolderApprovalMutasiBarang(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            img_hapus = itemView.findViewById(R.id.img_hapus);
        }

        void bind(final BarangModel b){
            txt_nama.setText(b.getNama());
            String jumlah = "Jumlah : " + b.getJumlah() + " " + b.getSatuan();
            txt_jumlah.setText(jumlah);
            img_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(activity instanceof ApprovalPengajuanMutasiDetail){
                        showDialogHapus(b.getId());
                    }
                }
            });
        }
    }

    private void showDialogHapus(final String id){
        final Dialog dialog = DialogFactory.getInstance().createDialog(activity,
                R.layout.popup_konfirmasi_hapus, 80);

        dialog.findViewById(R.id.btn_ya).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ApprovalPengajuanMutasiDetail)activity).hapusItem(id);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btn_tidak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }
}
