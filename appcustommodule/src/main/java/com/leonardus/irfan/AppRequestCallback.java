package com.leonardus.irfan;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppRequestCallback implements ApiVolleyManager.RequestCallback {

    private final String LOG = "api_log";
    private SimpleRequestListener listener;

    public AppRequestCallback(SimpleRequestListener listener){
        this.listener = listener;
    }

    @Override
    public void onSuccess(String result) {
        Log.d(LOG, result);
        try{
            JSONObject jsonresult = new JSONObject(result);
            int status = jsonresult.getJSONObject("metadata").getInt("status");
            String message = jsonresult.getJSONObject("metadata").getString("message");

            if(status == 200){
                if(jsonresult.get("response") instanceof JSONObject){
                    listener.onSuccess(jsonresult.getJSONObject("response").toString());
                }
                else if(jsonresult.get("response") instanceof JSONArray){
                    listener.onSuccess(jsonresult.getJSONArray("response").toString());
                }
                else{
                    listener.onSuccess(message);
                }
            }
            else if(status == 404){
                if(listener instanceof RequestListener){
                    ((RequestListener) listener).onEmpty(message);
                }
                else{
                    if(jsonresult.get("response") instanceof JSONObject){
                        listener.onSuccess(jsonresult.getJSONObject("response").toString());
                    }
                    else if(jsonresult.get("response") instanceof JSONArray){
                        listener.onSuccess(jsonresult.getJSONArray("response").toString());
                    }
                    else{
                        listener.onSuccess(jsonresult.getString("response"));
                    }
                }
            }
            else if(status == 400){
                if(listener instanceof RequestListener){
                    ((RequestListener) listener).onEmpty(message);
                }
                else{
                    if(jsonresult.get("response") instanceof JSONObject){
                        listener.onSuccess(jsonresult.getJSONObject("response").toString());
                    }
                    else if(jsonresult.get("response") instanceof JSONArray){
                        listener.onSuccess(jsonresult.getJSONArray("response").toString());
                    }
                    else{
                        listener.onSuccess(jsonresult.getString("response"));
                    }
                }
            }
            else{
                listener.onFail(message);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e(LOG, e.getMessage());
            listener.onFail("Kesalahan parsing JSON");
        }
    }

    @Override
    public void onError(String result) {
        Log.e(LOG, result);
        listener.onFail("Terjadi kesalahan koneksi");
    }

    public interface SimpleRequestListener {
        void onSuccess(String result);
        void onFail(String message);
    }

    public interface RequestListener extends SimpleRequestListener {
        void onEmpty(String message);
    }
}
