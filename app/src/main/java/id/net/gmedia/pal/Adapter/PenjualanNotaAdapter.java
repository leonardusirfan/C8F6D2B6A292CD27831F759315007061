package id.net.gmedia.pal.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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

import id.net.gmedia.pal.Activity.PenjualanSoCanvas.PenjualanDetail;
import id.net.gmedia.pal.Activity.PenjualanSoCanvas.PenjualanNota;
import id.net.gmedia.pal.Model.BarangModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppKeranjangPenjualan;
import id.net.gmedia.pal.Util.Constant;

public class PenjualanNotaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<BarangModel> listBarang;
    private int type;

    public PenjualanNotaAdapter(Activity activity, List<BarangModel> listBarang, int type){
        this.activity = activity;
        this.listBarang = listBarang;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PenjualanNotaViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_barang_detail_editable, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        final BarangModel barang = listBarang.get(i);
        final PenjualanNotaViewHolder viewHolder = (PenjualanNotaViewHolder) holder;

        if(type == Constant.PENJUALAN_SO){
            viewHolder.layout_parent.setBackground(activity.getResources().getDrawable(R.drawable.layout_so));
        }
        else{
            viewHolder.layout_parent.setBackground(activity.getResources().getDrawable(R.drawable.layout_canvas));
        }

        viewHolder.txt_nama.setText(barang.getNama());
        viewHolder.txt_harga.setText(Converter.doubleToRupiah(barang.getHarga()));
        String jumlah = barang.getJumlah() + " " + barang.getSatuan();
        viewHolder.txt_jumlah.setText(jumlah);
        viewHolder.txt_diskon.setText(Converter.doubleToRupiah(barang.getDiskon()));
        viewHolder.txt_total.setText(Converter.doubleToRupiah(barang.getSubtotal() - barang.getDiskon()));

        viewHolder.img_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(activity, viewHolder.img_edit, Gravity.END);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_item_penjualan, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_hapus){
                            listBarang.remove(viewHolder.getAdapterPosition());
                            AppKeranjangPenjualan.getInstance().hapus_budget(barang.getDiskon());
                            notifyItemRemoved(viewHolder.getAdapterPosition());

                            ((PenjualanNota)activity).initBarang();

                            return true;
                        }
                        else if(item.getItemId() == R.id.action_edit){
                            Gson gson = new Gson();

                            //BarangModel edit = AppKeranjangPenjualan.getInstance().getBarang(viewHolder.getAdapterPosition());

                            Intent i = new Intent(activity, PenjualanDetail.class);
                            i.putExtra(Constant.EXTRA_BARANG, gson.toJson(barang));
                            i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(((PenjualanNota)activity).customer));
                            i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, ((PenjualanNota)activity).JENIS_PENJUALAN);
                            i.putExtra(Constant.EXTRA_EDIT, viewHolder.getAdapterPosition());
                            if(((PenjualanNota)activity).spn_bayar.getSelectedItemPosition() != 0){
                                i.putExtra(Constant.EXTRA_CARA_BAYAR, ((PenjualanNota)activity).spn_bayar.getSelectedItemPosition());
                            }

                            activity.startActivity(i);
                            return true;
                        }

                        return false;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
        /*viewHolder.layout_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(activity, viewHolder.layout_parent, Gravity.END);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_item_penjualan, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.action_hapus){
                            listBarang.remove(viewHolder.getAdapterPosition());
                            AppKeranjangPenjualan.getInstance().hapus_budget(barang.getDiskon());
                            notifyItemRemoved(viewHolder.getAdapterPosition());

                            ((PenjualanNota)activity).initBarang();

                            return true;
                        }
                        else if(item.getItemId() == R.id.action_edit){
                            Gson gson = new Gson();

                            //BarangModel edit = AppKeranjangPenjualan.getInstance().getBarang(viewHolder.getAdapterPosition());

                            Intent i = new Intent(activity, PenjualanDetail.class);
                            i.putExtra(Constant.EXTRA_BARANG, gson.toJson(barang));
                            i.putExtra(Constant.EXTRA_CUSTOMER, gson.toJson(((PenjualanNota)activity).customer));
                            i.putExtra(Constant.EXTRA_JENIS_PENJUALAN, ((PenjualanNota)activity).JENIS_PENJUALAN);
                            i.putExtra(Constant.EXTRA_EDIT, viewHolder.getAdapterPosition());

                            activity.startActivity(i);
                            return true;
                        }

                        return false;
                    }
                });

                popup.show(); //showing popup menu
                return true;
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class PenjualanNotaViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout layout_parent;
        ImageView img_edit;
        TextView txt_nama, txt_harga, txt_diskon, txt_jumlah, txt_total;

        PenjualanNotaViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_diskon = itemView.findViewById(R.id.txt_diskon);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_total = itemView.findViewById(R.id.txt_total);
            layout_parent = itemView.findViewById(R.id.layout_parent);
            img_edit = itemView.findViewById(R.id.img_edit);
        }
    }


}
