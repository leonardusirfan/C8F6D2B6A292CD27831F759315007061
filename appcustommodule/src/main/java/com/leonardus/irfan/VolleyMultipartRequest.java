package com.leonardus.irfan;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final String twoHyphens = "--";
    private final String lineEnd = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();

    private Response.Listener<NetworkResponse> listener;
    private Response.ErrorListener errorListener;
    private Map<String, String> header;

    public VolleyMultipartRequest(int method, String url,final Map<String, String> header, Response.Listener<NetworkResponse> listener, Response.ErrorListener errorListener){
        super(method, url, errorListener);
        this.listener = listener;
        this.errorListener = errorListener;
        this.header = header;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        for(String key : header.keySet()){
            params.put(key, header.get(key));
        }
        return params;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);

        try{
            //populate text payload
            Map<String, String> params = getParams();
            if(params != null && params.size() > 0){
                textParse(dos, params, getParamsEncoding());
            }

            //populate data byte payload
            Map<String, DataPart> data = getByteData();
            if(data != null && data.size() > 0){
                dataParse(dos, data);
            }

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            return bos.toByteArray();
        }catch (IOException e){
            e.printStackTrace();
            Log.e("MultipartRequest", e.toString());
        }

        return null;
    }

    protected Map<String, DataPart> getByteData() throws AuthFailureError{
        return null;
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        try{
            return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
        }catch (Exception e){
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        errorListener.onErrorResponse(error);
    }

    //parse map string ke stream output berdasarkan kunci dan nilai
    private void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException{
        try{
            for(Map.Entry<String, String> entry : params.entrySet()){
                buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
            }
        }
        catch (UnsupportedEncodingException uee){
            throw  new RuntimeException("Encoding not supported : " + encoding, uee);
        }
    }

    //parsing data kedalam stream output
    private void dataParse(DataOutputStream dataOutputStream, Map<String, DataPart> data) throws IOException{
        for(Map.Entry<String, DataPart> entry : data.entrySet()){
            buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
        }
    }

    private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException{
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
        dataOutputStream.writeBytes(lineEnd);
        dataOutputStream.writeBytes(parameterValue + lineEnd);
    }

    private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataFile, String inputName) throws IOException{
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
        if(dataFile.getType() != null && !dataFile.getType().trim().isEmpty()){
            dataOutputStream.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
        }
        dataOutputStream.writeBytes(lineEnd);

        ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
        int bytesAvailable = fileInputStream.available();

        int maxBufferSize = 1024 * 1024;
        int bufferSize = Math.min(bytesAvailable, maxBufferSize);
        byte[] buffer = new byte[bufferSize];

        int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

        while(bytesRead > 0){
            dataOutputStream.write(buffer, 0, bufferSize);;
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }
        dataOutputStream.writeBytes(lineEnd);
    }

    public class DataPart{
        private String fileName;
        private byte[] content;
        private String type;

        public DataPart(){

        }

        public DataPart(String name, byte[] data){
            this.fileName = name;
            this.content = data;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }
}
