package id.net.gmedia.pal.Activity.Approval;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.net.gmedia.pal.Model.NotaModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class AdapterApprovalPengajuanMutasi extends RecyclerView.Adapter<AdapterApprovalPengajuanMutasi.ViewHolderApprovalPengajuanMutasi> {

    private Activity activity;
    private List<NotaModel> listPengajuan;

    AdapterApprovalPengajuanMutasi(Activity activity, List<NotaModel> listPengajuan){
        this.activity = activity;
        this.listPengajuan = listPengajuan;
    }

    @NonNull
    @Override
    public ViewHolderApprovalPengajuanMutasi onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolderApprovalPengajuanMutasi(LayoutInflater.from(activity).inflate(R.layout.item_approval_mutasi_header, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderApprovalPengajuanMutasi viewHolderApprovalPengajuanMutasi, int i) {
        viewHolderApprovalPengajuanMutasi.bind(listPengajuan.get(i));
    }

    @Override
    public int getItemCount() {
        return listPengajuan.size();
    }

    class ViewHolderApprovalPengajuanMutasi extends RecyclerView.ViewHolder{

        View layout_parent;
        TextView txt_nomor, txt_tanggal, txt_sales, txt_keterangan;

        ViewHolderApprovalPengajuanMutasi(@NonNull View itemView) {
            super(itemView);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            txt_nomor = itemView.findViewById(R.id.txt_nomor);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_sales = itemView.findViewById(R.id.txt_sales);
            txt_keterangan = itemView.findViewById(R.id.txt_keterangan);
        }

        void bind(final NotaModel m){
            txt_nomor.setText(m.getId());
            txt_tanggal.setText(m.getTanggal());
            txt_sales.setText(m.getNama());
            txt_keterangan.setText(m.getKeterangan());
            layout_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(activity, ApprovalPengajuanMutasiDetail.class);
                    i.putExtra(Constant.EXTRA_NO_NOTA, m.getId());
                    activity.startActivity(i);
                }
            });
        }
    }
}
