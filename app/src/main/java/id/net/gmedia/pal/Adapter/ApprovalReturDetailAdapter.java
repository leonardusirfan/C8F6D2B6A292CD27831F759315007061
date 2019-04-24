package id.net.gmedia.pal.Adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import id.net.gmedia.pal.Model.ReturModel;
import id.net.gmedia.pal.R;

public class ApprovalReturDetailAdapter extends RecyclerView.Adapter<ApprovalReturDetailAdapter.ApprovalReturDetailViewHolder> {

    private Context context;
    private List<ReturModel> listRetur;

    public ApprovalReturDetailAdapter(Context context, List<ReturModel> listRetur){
        this.context = context;
        this.listRetur = listRetur;
    }

    @NonNull
    @Override
    public ApprovalReturDetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ApprovalReturDetailViewHolder(LayoutInflater.from(context).inflate(R.layout.item_approval_retur_detail, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ApprovalReturDetailViewHolder approvalReturDetailViewHolder, int i) {
        ReturModel retur = listRetur.get(i);

        Glide.with(context).load(retur.getGambar()).apply(RequestOptions.placeholderOf(new ColorDrawable(context.getResources().
                getColor(R.color.gray)))).into(approvalReturDetailViewHolder.img_retur);
        approvalReturDetailViewHolder.txt_kode_barang.setText(retur.getKode_barang());
        approvalReturDetailViewHolder.txt_nama_barang.setText(retur.getNama_barang());
        String jumlah = retur.getJumlah() + " " + retur.getSatuan();
        approvalReturDetailViewHolder.txt_jumlah.setText(jumlah);
        approvalReturDetailViewHolder.txt_alasan.setText(retur.getAlasan());
    }

    @Override
    public int getItemCount() {
        return listRetur.size();
    }

    class ApprovalReturDetailViewHolder extends RecyclerView.ViewHolder{

        ImageView img_retur;
        TextView txt_kode_barang, txt_nama_barang, txt_jumlah, txt_alasan;

        ApprovalReturDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            img_retur = itemView.findViewById(R.id.img_retur);
            txt_kode_barang = itemView.findViewById(R.id.txt_kode_barang);
            txt_nama_barang = itemView.findViewById(R.id.txt_nama_barang);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_alasan = itemView.findViewById(R.id.txt_alasan);
        }
    }
}
