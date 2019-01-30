package id.net.gmedia.pal.Util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppRequestCallback implements ApiVolleyManager.RequestCallback {

    private RequestListener listener;

    public AppRequestCallback(RequestListener listener){
        this.listener = listener;
    }

    @Override
    public void onSuccess(String result) {
        System.out.println(result);
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
                if(listener instanceof AdvancedRequestListener){
                    ((AdvancedRequestListener) listener).onEmpty(message);
                }
                else{
                    if(jsonresult.get("response") instanceof JSONObject){
                        listener.onSuccess(jsonresult.getJSONObject("response").toString());
                    }
                    else if(jsonresult.get("response") instanceof JSONArray){
                        listener.onSuccess(jsonresult.getJSONArray("response").toString());
                    }
                }
            }
            else{
                listener.onFail(message);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
            Log.e("VolleyRequest", e.getMessage());
            listener.onFail("Kesalahan parsing JSON");
        }
    }

    @Override
    public void onError(String result) {
        Log.e("VolleyRequest", result);
        listener.onFail("Terjadi kesalahan koneksi");
    }

    public interface RequestListener{
        void onSuccess(String result);
        void onFail(String message);
    }

    public interface AdvancedRequestListener extends RequestListener{
        void onEmpty(String message);
    }
}