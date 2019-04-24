package id.net.gmedia.pal.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Adapter.SuratJalanAdapter;
import id.net.gmedia.pal.Model.NotaModel;
import id.net.gmedia.pal.R;
import id.net.gmedia.pal.Util.AppSharedPreferences;
import id.net.gmedia.pal.Util.Constant;

public class SuratJalan extends AppCompatActivity {

    private String search = "";

    private SuratJalanAdapter adapter;
    private List<NotaModel> listNota = new ArrayList<>();
    private LoadMoreScrollListener loadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surat_jalan);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Surat Jalan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv_surat_jalan = findViewById(R.id.rv_surat_jalan);
        rv_surat_jalan.setItemAnimator(new DefaultItemAnimator());
        rv_surat_jalan.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SuratJalanAdapter(this, listNota);
        rv_surat_jalan.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadSuratJalan(false);
            }
        };
        rv_surat_jalan.addOnScrollListener(loadManager);

        /*findViewById(R.id.btn_mutasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printPdf();
            }
        });*/

        loadSuratJalan(true);
    }

    private void loadSuratJalan(final boolean init){
        if(init){
            AppLoading.getInstance().showLoading(this);
            loadManager.initLoad();
        }

        String parameter = String.format(Locale.getDefault(), "?start_date=%s&end_date=%s&start=%d&limit=%d&search=%s",
                "", "", loadManager.getLoaded(), 10, Converter.encodeURL(search));
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_SURAT_JALAN_LIST + parameter,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(AppSharedPreferences.getId(this)),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listNota.clear();
                            adapter.notifyDataSetChanged();
                        }

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listNota.clear();
                            }

                            JSONArray surat_jalan_list = new JSONObject(result).getJSONArray("surat_jalan_list");
                            for(int i = 0; i < surat_jalan_list.length(); i++){
                                JSONObject surat_jalan = surat_jalan_list.getJSONObject(i);
                                listNota.add(new NotaModel(surat_jalan.getString("id"),
                                        surat_jalan.getString("no_bukti"), surat_jalan.getString("tanggal"),
                                        surat_jalan.getDouble("total"), surat_jalan.getString("keterangan")));
                            }

                            loadManager.finishLoad(surat_jalan_list.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            loadManager.finishLoad(0);
                            Toast.makeText(SuratJalan.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(SuratJalan.this, message, Toast.LENGTH_SHORT).show();
                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search = s;
                loadSuratJalan(true);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!searchView.isIconified() && TextUtils.isEmpty(s)) {
                    search = "";
                    loadSuratJalan(true);
                }
                return true;
            }
        });

        return true;
    }

    /*private void printPdf(){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this, "Direktori Eksternal tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }
        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!docsFolder.exists()) {
            if(!docsFolder.mkdir()){
                Toast.makeText(this, "Gagal membuat folder", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        try{
            File pdfFile = new File(docsFolder.getAbsolutePath(),"SuratJalan.pdf");
            Document document = new Document();
            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            // Open to write
            document.open();

            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("Gmedia");
            document.addCreator("Gmedia");

            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            *//*
     * How to USE FONT....
     *//*
            //BaseFont urName = BaseFont.createFont(BaseFont.COURIER);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            // Title Order Details...
            // Adding Title....
            Font mOrderDetailsTitleFont = new Font(Font.FontFamily.COURIER, 36.0f, Font.NORMAL, BaseColor.BLACK);
            // Creating Chunk
            Chunk mOrderDetailsTitleChunk = new Chunk("Surat Jalan", mOrderDetailsTitleFont);
        // Creating Paragraph to add...
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            // Setting Alignment for Heading
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            // Finally Adding that Chunk
            document.add(mOrderDetailsTitleParagraph);

            // Fields of Order Details...
            // Adding Chunks for Title and value
            Font mOrderIdFont = new Font(Font.FontFamily.COURIER, 18.0f, Font.NORMAL, mColorAccent);
            Font tableCellFont = new Font(Font.FontFamily.COURIER, 14.0f, Font.NORMAL, BaseColor.BLACK);
            *//*Chunk mOrderIdChunk = new Chunk("No. Bukti : 0987889", mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);*//*
            document.add(new Paragraph(Chunk.NEWLINE));
            document.add(new Paragraph("No. Bukti : 0987889", mOrderIdFont));
            document.add(new Paragraph("Tanggal   : 9 Juni 2019", mOrderIdFont));
            document.add(new Paragraph("Sales     : Budi Handoko", mOrderIdFont));

            document.add(new Paragraph(Chunk.NEWLINE));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(Chunk.NEWLINE));

            //Membuat tabel
            PdfPTable table = new PdfPTable(5);
            table.setHeaderRows(1);
            PdfPCell cell = new PdfPCell(new Phrase("#", mOrderIdFont));
            cell.setPadding(5);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Kode", mOrderIdFont));
            cell.setPadding(5);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Barang", mOrderIdFont));
            cell.setPadding(5);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Jumlah", mOrderIdFont));
            cell.setPadding(5);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Satuan", mOrderIdFont));
            cell.setPadding(5);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("1",tableCellFont));
            cell.setPadding(5);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("AB.015",tableCellFont));
            cell.setPadding(5);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Bayam Samudra 30 x 100",tableCellFont));
            cell.setPadding(5);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("5",tableCellFont));
            cell.setPadding(5);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Pcs",tableCellFont));
            cell.setPadding(5);
            table.addCell(cell);
            *//*PdfPCell cell = new PdfPCell(new Phrase("Cell with colspan 3"));
            cell.setColspan(3);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Cell with rowspan 2"));
            cell.setRowspan(2);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
            table.addCell("Cell 1.1");
            cell = new PdfPCell();
            cell.addElement(new Phrase("Cell 1.2"));
            table.addCell(cell);
            cell = new PdfPCell(new Phrase("Cell 2.1"));
            cell.setPadding(5);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell = new PdfPCell();
            cell.setPadding(5);
            cell.setUseAscender(true);
            cell.setUseDescender(true);
            Paragraph p = new Paragraph("Cell 2.2");
            p.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(p);
            table.addCell(cell);*//*
            document.add(table);

            document.close();
            openPdf(pdfFile);
        }
        catch (IOException | DocumentException e){
            Toast.makeText(this, "Terjadi kesalahan pembuatan dokumen", Toast.LENGTH_SHORT).show();
            Log.e(Constant.TAG, e.getMessage());
        }
    }

    private void openPdf(File pdf){
        Intent target = new Intent(Intent.ACTION_VIEW);
        if(Build.VERSION.SDK_INT >= 24){
            target.setDataAndType(FileProvider.getUriForFile(this,
                    BuildConfig.APPLICATION_ID + ".provider", pdf),"application/pdf");
        }
        else{
            target.setDataAndType(Uri.fromFile(pdf),"application/pdf");
        }
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent intent = Intent.createChooser(target, "Pilih Aplikasi");
        //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Install aplikasi pembaca PDF terlebih dahulu", Toast.LENGTH_SHORT).show();
        }
    }*/
}
