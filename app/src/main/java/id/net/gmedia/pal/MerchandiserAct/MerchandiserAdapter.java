package id.net.gmedia.pal.MerchandiserAct;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;

import id.net.gmedia.pal.Model.MerchandiserModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.Constant;

public class MerchandiserAdapter extends RecyclerView.Adapter<MerchandiserAdapter.MerchandiserViewHolder>{

    private Activity activity;
    private List<MerchandiserModel> listMerchandiser;

    public MerchandiserAdapter(Activity activity, List<MerchandiserModel> listMerchandiser){
        this.activity = activity;
        this.listMerchandiser = listMerchandiser;
    }

    @NonNull
    @Override
    public MerchandiserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MerchandiserViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_merchandiser, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MerchandiserViewHolder viewHolder, int i) {
        final MerchandiserModel merchandiser = listMerchandiser.get(i);

        viewHolder.txt_nama.setText(merchandiser.getNama());
        viewHolder.txt_alamat.setText(merchandiser.getAlamat());

        viewHolder.layout_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(activity, MerchandiserTambah.class);
                i.putExtra(Constant.EXTRA_MERCHANDISER, gson.toJson(merchandiser));
                activity.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listMerchandiser.size();
    }

    class MerchandiserViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout layout_parent;
        TextView txt_nama, txt_alamat;

        MerchandiserViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_alamat = itemView.findViewById(R.id.txt_alamat);
            layout_parent = itemView.findViewById(R.id.layout_parent);
        }
    }
}
