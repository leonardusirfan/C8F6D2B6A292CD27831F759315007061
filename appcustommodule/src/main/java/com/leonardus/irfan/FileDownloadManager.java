package com.leonardus.irfan;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloadManager {

    private int FILE_TYPE;
    private Activity activity;
    private boolean cancelable = false;
    private final String TAG = "download_log";

    private final static int DOWNLOAD_REQUEST = 999;

    public static final int TYPE_PDF = 90;
    public static final int TYPE_PNG = 91;

    private ProgressDialog progressDialog;

    public FileDownloadManager(Activity activity, int FILE_TYPE){
        this.activity = activity;
        this.FILE_TYPE = FILE_TYPE;
    }

    public FileDownloadManager(Activity activity, int FILE_TYPE, boolean cancelable){
        this.activity = activity;
        this.FILE_TYPE = FILE_TYPE;
        this.cancelable = cancelable;
    }

    public void download(String url){
        if(writeStorageCheckPermission()){
            DownloadFileFromURL downloader = new DownloadFileFromURL();
            downloader.setListener(new DownloadFileFromURL.DownloadListener() {
                @Override
                public void onStart() {
                    showDialog();
                }

                @Override
                public void onProgress(int progress) {
                    showProgress(progress);
                }

                @Override
                public void onFinished(File f) {
                    finishDialog(f);
                }

                @Override
                public void onError(String message) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity, "Gagal mengunduh", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e(TAG, message);
                }
            });
            downloader.execute(url, getFileName(url.substring(url.lastIndexOf('/') + 1)));
            //new DownloadFile().execute(url);
        }
    }

    public void download(String url, String filename){
        if(writeStorageCheckPermission()){
            DownloadFileFromURL downloader = new DownloadFileFromURL();
            downloader.setListener(new DownloadFileFromURL.DownloadListener() {
                @Override
                public void onStart() {
                    showDialog();
                }

                @Override
                public void onProgress(int progress) {
                    showProgress(progress);
                }

                @Override
                public void onFinished(File f) {
                    finishDialog(f);
                }

                @Override
                public void onError(String message) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity, "Gagal mengunduh", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e(TAG, message);
                }
            });
            downloader.execute(url, filename);
            //new DownloadFile().execute(url);
        }
    }

    //METHOD PERMISSION HANDLING ===================================================================
    private boolean writeStorageCheckPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else{
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, DOWNLOAD_REQUEST);
                return false;
            }
        }
        else{
            return true;
        }
    }

    public static boolean ifFileDownloadManagerPermission(int requestCode, int result){
        if(requestCode == DOWNLOAD_REQUEST){
            return result == PackageManager.PERMISSION_GRANTED;
        }
        else{
            return false;
        }
    }

    //ASYNC DOWNLOAD CLASS =========================================================================
    static class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private File f;
        private String fileName;
        private String folder;
        private DownloadListener listener;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(listener != null){
                listener.onStart();
            }
        }

        private void setListener(DownloadListener listener){
            this.listener = listener;
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                //Mengekstrak nama file dari URL
                //fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());
                /*String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss",
                        Locale.getDefault()).format(new Date());
                fileName = timestamp + "_" + fileName;*/

                // download the file
                folder = Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS) + File.separator + "SEMARGRES/";
                File directory = new File(folder);

                //membuat directory jika belum ada
                if (!directory.exists()) {
                    if(!directory.mkdirs()){
                        return "Tidak bisa membuat folder";
                    }
                }

                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                f = new File(folder + f_url[1]);
                OutputStream output = new FileOutputStream(f);

                byte[] data = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                if(listener != null){
                    listener.onError(e.getMessage());
                }
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            if(listener != null){
                listener.onProgress(Integer.parseInt(progress[0]));
            }
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            if(listener != null){
                listener.onFinished(f);
            }
        }

        public interface DownloadListener{
            void onStart();
            void onProgress(int progress);
            void onFinished(File f);
            void onError(String message);
        }
    }

    //FILE TYPE HANDLING METHOD ====================================================================
    private String getFileName(String name){
        switch (FILE_TYPE){
            case TYPE_PDF : return name + ".pdf";
            case TYPE_PNG : return name + ".png";
        }

        return "";
    }

    private String getFileMimeType(){
        switch (FILE_TYPE){
            case TYPE_PDF : return "application/pdf";
            case TYPE_PNG : return "image/png";
        }

        return "";
    }

    //PROGRESS LISTENER METHOD =====================================================================
    private void showDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Mengunduh file. Tunggu sebentar...");
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(cancelable);
        progressDialog.show();
       /* progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });*/
    }

    private void showProgress(int progress){
        progressDialog.setProgress(progress);
    }

    private void finishDialog(File f){
        // menutup dialog setelah file selesai diunduh
        progressDialog.dismiss();

        // Membuka file yang telah selesai didownload
        try{
            if (f != null) {
                String imagePath = String.valueOf(FileProvider.getUriForFile(activity,
                        activity.getPackageName() + ".provider", f));
                // setting downloaded into image view
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(imagePath), getFileMimeType());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                activity.startActivity(intent);
            }
        }
        catch (Exception e){
            Toast.makeText(activity, "File tidak dapat dibuka", Toast.LENGTH_SHORT).show();
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /*private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        *//**
         * Before starting background thread
         * Show Progress Bar Dialog
         *//*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(activity);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        *//**
         * Downloading file in background thread
         *//*
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "ISIDWTYPER/";

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    //Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        *//**
         * Updating progress bar
         *//*
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();

            // Display File path after downloading
            Toast.makeText(activity,
                    message, Toast.LENGTH_LONG).show();

            if(message.toLowerCase().contains("downloaded at")){

                try {
                    String imagePath = message.replace("Downloaded at: ", "");
                    File file = new File(imagePath);
                    imagePath = String.valueOf(FileProvider.getUriForFile(activity,
                            activity.getPackageName() + ".provider", file));

                    // setting downloaded into image view

                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension
                            (MimeTypeMap.getFileExtensionFromUrl(imagePath));

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(imagePath), mimeType);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    activity.startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }*/
}
