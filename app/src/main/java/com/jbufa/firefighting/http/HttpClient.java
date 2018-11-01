package com.jbufa.firefighting.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.webkit.WebSettings;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jbufa.firefighting.R;
import com.jbufa.firefighting.common.AppContext;
import com.jbufa.firefighting.common.URL;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HttpClient {

    private static final int CONNECT_TIME_OUT = 5;
    private static final int WRITE_TIME_OUT = 30;
    private static final int READ_TIME_OUT = 30;
    private static final int MAX_REQUESTS_PER_HOST = 10;
    private static final String TAG = HttpClient.class.getSimpleName();
    private static final String UTF_8 = "UTF-8";
    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");

    private static OkHttpClient client;

    static {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);
        builder.networkInterceptors().add(new LoggingInterceptor());
        client = builder.build();
        client.dispatcher().setMaxRequestsPerHost(MAX_REQUESTS_PER_HOST);

    }

    static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            Log.i(TAG, String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            Log.i(TAG, String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            return response;
        }
    }

    public static boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) AppContext.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
        } catch (Exception e) {
            Log.v("ConnectivityManager", e.getMessage());
        }
        return false;
    }

    public static void get(String url, Map<String, String> param, String token, final HttpResponseHandler handler) {
        if (!isNetworkAvailable()) {
            Toast.makeText(AppContext.getInstance(), R.string.no_network_connection_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        if (param != null && param.size() > 0) {
            url = url + "?" + mapToQueryString(param);
        }
        if (token == null || token.isEmpty()) {
            Toast.makeText(AppContext.getInstance(), "token获取失败", Toast.LENGTH_SHORT).show();
            return;
        }
        Request request = new Request.Builder().url(url)
                .addHeader("X-Gizwits-Application-Id", "61bb53cb04164c559d20d7be8a26fac6")
                .addHeader("X-Gizwits-User-token", token)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String urlResponse = response.body().string();
                    handler.sendSuccessMessage(urlResponse);
                } catch (Exception e) {
                    handler.sendFailureMessage(call.request(), e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendFailureMessage(call.request(), e);
            }
        });
    }

    public static void getMessage(String url, Map<String, String> param, final HttpResponseHandler handler) {
        if (!isNetworkAvailable()) {
            Toast.makeText(AppContext.getInstance(), R.string.no_network_connection_toast, Toast.LENGTH_SHORT).show();
            return;
        }

        if (param != null && param.size() > 0) {
            url = url + "?" + mapToQueryString(param);
        }
        Request request = new Request.Builder().url(url)
                .addHeader("Authorization", "360b6ecad9b1447f975efc52001f2d42")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    String urlResponse = response.body().string();
                    handler.sendSuccessMessage(urlResponse);
                } catch (Exception e) {
                    handler.sendFailureMessage(call.request(), e);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                handler.sendFailureMessage(call.request(), e);
            }
        });
    }

    public static void post(final String url, JSONObject jsonObject, String token, final HttpResponseHandler handler) {
        if (!isNetworkAvailable()) {
            Toast.makeText(AppContext.getInstance(), "请检测网络是否可用", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestBody body;

        try {
            Request request;
            if (jsonObject != null) {
                jsonObject.put("appKey", "123456");
                jsonObject.put("version", "1.0");
                Log.e("ceshi", "post参数:" + jsonObject.toString());
                body = RequestBody.create(MEDIA_TYPE, jsonObject.toString());
                if (token != null && !token.isEmpty()) {
                    request = new Request.Builder().url(url)
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", token)
                            .post(body)
                            .build();
                } else {
                    request = new Request.Builder().url(url)
                            .addHeader("Content-Type", "application/json")
                            .post(body)
                            .build();
                }
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        try {
                            String string = response.body().string();
                            Log.e("ceshi", "数据: " + string);
                            RestApiResponse apiResponse = getRestApiResponse(string);
//                            Log.e("ceshi", "RestApiResponse 解析后: " + apiResponse.data);
                            if (url.equals(URL.ROOMBINDDEVICE)) {
                                handler.sendSuccessMessage(apiResponse.message);
                            } else {
                                handler.sendSuccessMessage(apiResponse.data);
                            }
                        } catch (Exception e) {
                            handler.sendFailureMessage(call.request(), e);
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        handler.sendFailureMessage(call.request(), e);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static RestApiResponse getRestApiResponse(String responseBody) throws Exception {

        RestApiResponse apiResponse = JSON.parseObject(responseBody, RestApiResponse.class);
        if (apiResponse == null) {
            throw new Exception("服务器数据null (response = " + responseBody + ")");
        }
        if (apiResponse.code != RestApiResponse.STATUS_SUCCESS) {
            throw new Exception(apiResponse.message);
        }
        return apiResponse;
    }

    public static String mapToQueryString(Map<String, String> map) {
        StringBuilder string = new StringBuilder();
        /*if(map.size() > 0) {
            string.append("?");
        }*/
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                string.append(entry.getKey());
                string.append("=");
                string.append(URLEncoder.encode(entry.getValue(), UTF_8));
                string.append("&");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return string.toString();
    }

    //*************************************************************//


    //*************************************************************//

}
