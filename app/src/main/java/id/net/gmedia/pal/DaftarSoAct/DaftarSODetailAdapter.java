package id.net.gmedia.pal.DaftarSoAct;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.TaskStackBuilder;
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

import java.util.List;

import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.ApiVolleyManager;
import id.net.gmedia.pal.Util.AppLoading;
import id.net.gmedia.pal.Util.AppRequestCallback;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;
import id.net.gmedia.pal.Util.Converter;

public class DaftarSODetailAdapter extends RecyclerView.Adapter<DaftarSODetailAdapter.DaftarSODetailViewHolder>{

    private Activity activity;
    private List<BarangModel> listBarang;
    private int type;

    DaftarSODetailAdapter(Activity activity, List<BarangModel> listBarang, int type){
        this.activity = activity;
        this.listBarang = listBarang;
        this.type = type;
    }

    @NonNull
    @Override
    public DaftarSODetailViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DaftarSODetailViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_barang_detail_editable, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final DaftarSODetailViewHolder barangDetailViewHolder, int i) {
        final BarangModel barang = listBarang.get(i);

        if(type == Constant.PENJUALAN_SO){
            barangDetailViewHolder.layout_parent.setBackground(activity.getResources().getDrawable(R.drawable.layout_so));
        }
        else{
            barangDetailViewHolder.layout_parent.setBackground(activity.getResources().getDrawable(R.drawable.layout_canvas));
        }

        barangDetailViewHolder.txt_nama.setText(barang.getNama());
        String jumlah = barang.getJumlah() + " " + barang.getSatuan();
        barangDetailViewHolder.txt_jumlah.setText(jumlah);
        barangDetailViewHolder.txt_diskon.setText(Converter.doubleToRupiah(barang.getDiskon()));
        barangDetailViewHolder.txt_harga.setText(Converter.doubleToRupiah(barang.getHarga()));
        barangDetailViewHolder.txt_total.setText(Converter.doubleToRupiah(barang.getSubtotal()));

        barangDetailViewHolder.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(activity, barangDetailViewHolder.img_edit, Gravity.END);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_item_penjualan, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_hapus){
                            AppLoading.getInstance().showLoading(activity);
                            hapusBarang(barang.getId());

                            return true;
                        }
                        else if(item.getItemId() == R.id.action_edit){
                            Gson gson = new Gson();

                            //BarangModel edit = AppKeranjangPenjualan.getInstance().getBarang(viewHolder.getAdapterPosition());

                            Intent i = new Intent(activity, DaftarSOEdit.class);
                            i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(((DaftarSODetail)activity).customer));
                            i.putExtra(Constant.EXTRA_BARANG, gson.toJson(barang));
                            i.putExtra(Constant.EXTRA_NO_BUKTI, ((DaftarSODetail)activity).nota.getId());
                            /*i.putExtra(Constant.EXTRA_BARANG, gson.toJson(barang));
                            i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(((PenjualanNota)activity).customer));
                            i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, ((PenjualanNota)activity).JENIS_PENJUALAN);
                            i.putExtra(Constant.EXTRA_EDIT, viewHolder.getAdapterPosition());*/

                            activity.startActivity(i);
                            return true;
                        }

                        return false;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
    }

    private void hapusBarang(String id){
        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_SO_HAPUS + id, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(AppSharedPreferences.getId(activity)),
                new AppRequestCallback(new AppRequestCallback.AdvancedRequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        AppLoading.getInstance().stopLoading();
                        Intent resultIntent = new Intent(activity, DaftarSO.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        stackBuilder.startActivities();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class DaftarSODetailViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layout_parent;
        TextView txt_nama, txt_harga, txt_diskon, txt_jumlah, txt_total;
        ImageView img_edit;

        DaftarSODetailViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_diskon = itemView.findViewById(R.id.txt_diskon);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_total = itemView.findViewById(R.id.txt_total);
            img_edit = itemView.findViewById(R.id.img_edit);
            layout_parent = itemView.findViewById(R.id.layout_parent);
        }
    }
}
