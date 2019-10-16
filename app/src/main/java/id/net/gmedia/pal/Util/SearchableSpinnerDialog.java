package id.net.gmedia.pal.Util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leonardus.irfan.SimpleObjectModel;

import java.util.List;

import id.net.gmedia.pal.R;

public class SearchableSpinnerDialog {
    private Dialog dialog;
    private Context context;

    private EditText txt_search;
    private ProgressBar bar_spinner;
    private List<SimpleObjectModel> listItem;
    private SearchableSpinnerListener listener;
    private SearchableSpinnerAdapter adapter;

    public SearchableSpinnerDialog(Context context, SearchableSpinnerListener listener, List<SimpleObjectModel> listItem) {
        this.context = context;
        this.listItem = listItem;
        this.listener = listener;
    }

    public void showDialog(){
        if(dialog == null){
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();

            int device_TotalWidth = metrics.widthPixels;
            int device_TotalHeight = metrics.heightPixels;

            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.popup_searchable_spinner_dialog);
            if(dialog.getWindow() != null){
                dialog.getWindow().setLayout(device_TotalWidth * 80 / 100 , device_TotalHeight * 70 / 100); // set here your value
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialog.setCancelable(false);

            dialog.setOnKeyListener(new Dialog.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dialog.cancel();
                        return true;
                    }
                    return false;
                }
            });

            txt_search = dialog.findViewById(R.id.txt_search);
            bar_spinner = dialog.findViewById(R.id.bar_spinner);
            RecyclerView rv_spinner = dialog.findViewById(R.id.rv_spinner);
            rv_spinner.setLayoutManager(new LinearLayoutManager(context));
            rv_spinner.setItemAnimator(new DefaultItemAnimator());
            adapter = new SearchableSpinnerAdapter();
            rv_spinner.setAdapter(adapter);

            txt_search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listener.onLoad(true, s.toString());
                    setLoading(true);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
        dialog.show();
    }

    public void setLoading(boolean loading){
        if(loading){
            bar_spinner.setVisibility(View.VISIBLE);
        }
        else{
            bar_spinner.setVisibility(View.GONE);
        }
    }

    public RecyclerView.Adapter<RecyclerView.ViewHolder> getAdapter(){
        return adapter;
    }

    public interface SearchableSpinnerListener{
        void onSelected(String id, String value);
        void onLoad(final boolean init, String search);
    }

    class SearchableSpinnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private final int LOAD_MORE = 10;

        @Override
        public int getItemViewType(int position) {
            if(position == listItem.size()){
                return LOAD_MORE;
            }
            else{
                return super.getItemViewType(position);
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            if(i == LOAD_MORE){
                return new LoadMoreViewHolder(LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.item_searchable_spinner_load_more, viewGroup, false));
            }
            else{
                return new SearchableSpinnerViewHolder(LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.item_searchable_spinner, viewGroup, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            if(viewHolder instanceof SearchableSpinnerViewHolder){
                final SimpleObjectModel selected = listItem.get(i);
                ((SearchableSpinnerViewHolder)viewHolder).txt_spinner.setText(selected.getValue());
                ((SearchableSpinnerViewHolder)viewHolder).layout_parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onSelected(selected.getId(), selected.getValue());
                        dialog.dismiss();
                    }
                });
            }
            else if(viewHolder instanceof LoadMoreViewHolder){
                ((LoadMoreViewHolder)viewHolder).txt_load_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onLoad(false, txt_search.getText().toString());
                        setLoading(true);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return listItem.size() + 1;
        }

        class SearchableSpinnerViewHolder extends RecyclerView.ViewHolder{

            LinearLayout layout_parent;
            TextView txt_spinner;

            SearchableSpinnerViewHolder(@NonNull View itemView) {
                super(itemView);
                layout_parent = itemView.findViewById(R.id.layout_parent);
                txt_spinner = itemView.findViewById(R.id.txt_spinner);
            }
        }

        class LoadMoreViewHolder extends RecyclerView.ViewHolder{

            TextView txt_load_more;
            ProgressBar bar_load_more;

            LoadMoreViewHolder(@NonNull View itemView) {
                super(itemView);
                txt_load_more = itemView.findViewById(R.id.txt_load_more);
                bar_load_more = itemView.findViewById(R.id.bar_load_more);
            }
        }
    }
}
