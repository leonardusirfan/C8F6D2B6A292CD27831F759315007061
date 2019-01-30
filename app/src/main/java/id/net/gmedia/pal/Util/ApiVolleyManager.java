package id.net.gmedia.pal.Util;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ApiVolleyManager {
    public static int METHOD_GET = Request.Method.GET;
    public static int METHOD_POST = Request.Method.POST;
    private final String REQ_TAG = "tag";

    private final static ApiVolleyManager ourInstance = new ApiVolleyManager();
    private RequestQueue requestQueue;

    public static ApiVolleyManager getInstance() {
        return ourInstance;
    }

    private ApiVolleyManager() {
    }

    public void addRequest(final Context context, String url, int method, final Map<String, String> header, final RequestCallback callback){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }

        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Jika hasil respon kosong
                if(response == null || response.equals("null")) {
                    callback.onError("Autentifikasi gagal/respons kosong");
                }

                try {
                    callback.onSuccess(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                for(String key : header.keySet()){
                    params.put(key, header.get(key));
                }
                return params;
            }
        };

        request.setTag(REQ_TAG);
        requestQueue.add(request);
    }

    public void addRequest(final Context context, String url, int method, final Map<String, String> header, final JSONObject body, final RequestCallback callback){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }

        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Jika hasil respon kosong
                if(response == null || response.equals("null")) {
                    callback.onError("Akses tidak valid/respon kosong");
                }

                try {
                    callback.onSuccess(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        }){
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
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return body == null ? null : body.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    Log.e("ApiVolley BODY : ","Unsupported Encoding");
                    return null;
                }
            }
        };

        request.setTag(REQ_TAG);
        requestQueue.add(request);
    }

    public void addMultipartRequest(Context context, String url, final Map<String, String> header, final byte[] upload_data, final RequestCallback callback){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
        }

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, header,new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    if(response == null || new String(response.data).equals("null")){
                        callback.onError("Akses tidak valid/respon kosong");
                    }
                    else{
                        callback.onSuccess(new String(response.data));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onError(e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error.toString());
            }
        }){
            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData(){
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();
                long imageName = System.currentTimeMillis();
                params.put("pic", new VolleyMultipartRequest.DataPart(imageName + ".jpg", upload_data));
                return params;
            }
        };

        volleyMultipartRequest.setTag(REQ_TAG);
        requestQueue.add(volleyMultipartRequest);
    }

    public interface RequestCallback{
        void onSuccess(String result);
        void onError(String result);
    }

    public void cancelRequest(){
        requestQueue.cancelAll(REQ_TAG);
    }
}