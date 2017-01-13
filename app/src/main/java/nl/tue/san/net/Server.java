package nl.tue.san.net;

import android.os.AsyncTask;
import android.util.Log;


import okhttp3.*;

enum RequestTypes {
    GET, POST, DELETE, PATCH;
}

public class Server {
    public static void GET(String uri, Callback cb) {
        new RequestRunner().execute(nl.tue.san.net.IRequest.getInstance(
                RequestTypes.GET,
                uri,
                "",
                cb
        ));
    }

    public static void POST(String uri, Callback cb, String data) {
        new RequestRunner().execute(nl.tue.san.net.IRequest.getInstance(
                RequestTypes.GET,
                uri,
                data,
                cb
        ));
    }
}

class IRequest {
    public RequestTypes type;
    public String endpoint;
    public String data;
    public Callback cb;

    public static IRequest getInstance(RequestTypes type, String endpoint, String data, Callback cb) {
        IRequest r = new IRequest();

        r.cb = cb;
        r.endpoint = endpoint;
        r.data = data;
        r.type = type;

        return r;
    }
}

class RequestRunner extends AsyncTask<IRequest, Void, String> {
    public static String ApiBase = "https://seminar.tuupke.nl/";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    private nl.tue.san.net.IRequest request;

    protected String doInBackground(nl.tue.san.net.IRequest... requests) {
        request = requests[0];

        String endpoint = request.endpoint;
        RequestTypes type = request.type;
        String data = request.data;

        String url = ApiBase + endpoint;

        Request.Builder request = new Request.Builder().url(url);

        switch (type) {
            case POST:
                request = request.post(RequestBody.create(JSON, data));
                break;
            case GET:
            case DELETE:
            case PATCH:
            default:
                // Do nothing
                break;
        }

        try {
            Response response = client.newCall(request.build()).execute();
            return response.body().string();
        } catch (java.io.IOException e) {
            Log.e("Network", "Request error for: " + url + " with data: " + data);
        }

        return null;
    }

    protected void onPostExecute(String result) {
        if (result == null) {
            request.cb.onFailure();
            return;
        }

        request.cb.onSuccess(result);
    }
}