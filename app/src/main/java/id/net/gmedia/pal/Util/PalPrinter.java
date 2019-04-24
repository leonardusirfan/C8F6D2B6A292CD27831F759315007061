package id.net.gmedia.pal.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.leonardus.irfan.bluetoothprinter.NotaPrinter;
import com.leonardus.irfan.bluetoothprinter.PrintFormatter;
import com.leonardus.irfan.bluetoothprinter.RupiahFormatter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import id.net.gmedia.pal.Model.NotaPenjualanModel;
import id.net.gmedia.pal.Model.PelunasanModel;

public class PalPrinter extends NotaPrinter {
    public PalPrinter(Context context, Bitmap bmp) {
        super(context, bmp);
    }

    public void printPelunasan(PelunasanModel pelunasan){
        final int NAMA_MAX = 17;
        final int HARGA_TOTAL_MAX = 13;

        if(getBluetoothDevice() == null){
            Toast.makeText(getContext(), "Sambungkan ke device printer terlebih dahulu!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double jum = 0;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            //PROSES CETAK HEADER
            getOutputStream().write(PrintFormatter.DEFAULT_STYLE);
            getOutputStream().write(PrintFormatter.ALIGN_CENTER);

            if(getHeader() != null){
                getOutputStream().write(getHeader());
                getOutputStream().write(PrintFormatter.NEW_LINE);
            }

            getOutputStream().write(PrintFormatter.ALIGN_LEFT);
            getOutputStream().write(String.format("Outlet      :  %s\n", pelunasan.getOutlet()).getBytes());
            getOutputStream().write(String.format("Area        :  %s\n", pelunasan.getArea()).getBytes());
            getOutputStream().write(String.format("Sales       :  %s\n", pelunasan.getSales()).getBytes());
            getOutputStream().write(String.format("No. Nota    :  %s\n", pelunasan.getNo_nota()).getBytes());

            getOutputStream().write(PrintFormatter.NEW_LINE);

            //PROSES CETAK TRANSAKSI
            getOutputStream().write("--------------------------------\n".getBytes());
            getOutputStream().write(PrintFormatter.ALIGN_LEFT);
            getOutputStream().write("Nomor nota              Terbayar\n".getBytes());
            getOutputStream().write("--------------------------------\n".getBytes());
            getOutputStream().write(PrintFormatter.ALIGN_LEFT);

            List<NotaPenjualanModel> listItem = pelunasan.getListItem();
            for(int i = 0; i < listItem.size(); i++){
                NotaPenjualanModel t =  listItem.get(i);
                String nama = t.getId();
                String dibayar = RupiahFormatter.getRupiah(t.getTerbayar());

                int n = 1;
                if(nama.length() > NAMA_MAX){
                    n = Math.max((int)Math.ceil((double)nama.length()/(double)NAMA_MAX), n);
                }
                if(dibayar.length() > HARGA_TOTAL_MAX){
                    n = Math.max((int)Math.ceil((double)dibayar.length()/(double)HARGA_TOTAL_MAX), n);
                }

                String[] nama_array = leftAligned(nama, NAMA_MAX, n);
                String[] harga_total_array = rightAligned(dibayar, HARGA_TOTAL_MAX, n);

                for(int j = 0; j < n; j++){
                    getOutputStream().write(String.format(Locale.getDefault(), "%s  %s\n",
                            nama_array[j], harga_total_array[j]).getBytes());
                }

                jum += t.getTerbayar();
            }

            //transaksi.setTunai(jum); //tunai selalu sama dengan jumlah
            String jum_string, tunai_string;
            //String kembali_string;
            jum_string = RupiahFormatter.getRupiah(jum);

            getOutputStream().write(PrintFormatter.ALIGN_RIGHT);
            getOutputStream().write("----------".getBytes());
            getOutputStream().write("\nSUBTOTAL :  ".getBytes());
            getOutputStream().write(jum_string.getBytes());

            tunai_string = RupiahFormatter.getRupiah(pelunasan.getTunai());
            StringBuilder builder = new StringBuilder(tunai_string);
            for(int i = 0; i < jum_string.length() - tunai_string.length(); i++){
                builder.insert(0, " ");
            }
            tunai_string = builder.toString();
            //kembali_string = RupiahFormatter.getRupiah(transaksi.getTunai() - jum);

            getOutputStream().write("\nTUNAI    :  ".getBytes());
            getOutputStream().write(tunai_string.getBytes());

            /*outputStream.write("\nKEMBALI : ".getBytes());
            for(int i = 0; i < character_size - kembali_string.length(); i++){
                outputStream.write(" ".getBytes());
            }
            outputStream.write(kembali_string.getBytes());*/

            getOutputStream().write(PrintFormatter.NEW_LINE);
            getOutputStream().write(PrintFormatter.NEW_LINE);

            /*getOutputStream().write(PrintFormatter.ALIGN_LEFT);
            String ppn = "DPP : " + RupiahFormatter.getRupiah(Math.round(pelunasan.getTunai() * 10/ 11)) +
                    "  PPN : " + RupiahFormatter.getRupiah(Math.round(pelunasan.getTunai() / 11));
            getOutputStream().write(ppn.getBytes());

            getOutputStream().write(PrintFormatter.NEW_LINE);
            getOutputStream().write(PrintFormatter.NEW_LINE);*/

            //PROSES CETAK FOOTER
            getOutputStream().write(PrintFormatter.ALIGN_CENTER);
            getOutputStream().write("Terima Kasih\n".getBytes());

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(pelunasan.getTgl_transaksi());

            getOutputStream().write(String.format("%s\n", currentDateandTime).getBytes());
            getOutputStream().write("==============================\n".getBytes());
            /*outputStream.write(PrintFormatter.getSmall());
            outputStream.write("Promo Telkomsel Flash\n".getBytes());
            outputStream.write("Paket internet 24 jam 2GB Rp 20.000\n".getBytes());
            outputStream.write(PrintFormatter.DEFAULT_STYLE);*/
            getOutputStream().write(PrintFormatter.NEW_LINE);
            getOutputStream().write(PrintFormatter.NEW_LINE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
