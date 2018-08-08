package com.xseec.eds.util;

import android.content.Context;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.model.Tags.StoredTag;
import com.xseec.eds.model.Tags.Tag;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.Tags.ValidTag;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018/7/9.
 * WebAccess Service Helper
 */


public class WAServiceHelper {

    private static final String TAG = "WAServiceHelper";
    private static final MediaType BODY_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static Request getRequest(String url, String authority, String requestContent) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .addHeader("Authorization", "Basic " + authority);
        if (requestContent != null) {
            RequestBody requestBody = RequestBody.create(BODY_TYPE, requestContent);
            builder.post(requestBody);
        }
        return builder.build();
    }

    private static void sendRequest(String url, String authority, String content, Callback
            callback) {
        Request request = getRequest(url, authority, content);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendLoginRequest(String authority, Callback callback) {
        String url = WAServicer.getLoginUrl();
        sendRequest(url, authority, null, callback);
    }

    public static void sendTagListRequest(String authority, String deviceName, Callback callback) {
        String url = WAServicer.getTagListUrl(deviceName);
        sendRequest(url, authority, null, callback);
    }

    public static void sendGetValueRequest(String authority, List<Tag> tagList, Callback callback) {
        Context context = EDSApplication.getContext();
        String content = context.getString(R.string.was_tag_value_request, tagList.toString());
        String url = WAServicer.getGetValueUrl();
        sendRequest(url, authority, content, callback);
    }

    public static void sendSetValueRequest(String authority, List<ValidTag> tagList, Callback
            callback) {
        Context context = EDSApplication.getContext();
        String content = context.getString(R.string.was_tag_value_request, tagList.toString());
        String url = WAServicer.getSetValueUrl();
        sendRequest(url, authority, content, callback);
    }

    public static void sendTagLogRequest(String authority, String startTime, StoredTag
            .IntervalType intervalType, int interval, int records, List<StoredTag> tagList,
            Callback callback) {
        Context context = EDSApplication.getContext();
        String content = context.getString(R.string.was_tag_log_request, startTime, intervalType
                .name(), interval, records, tagList.toString());
        String url = WAServicer.getTagLogUrl();
        sendRequest(url, authority, content, callback);
    }

    //登录，基本信息，点列表三条数据请求同时处理，不使用callback
    private static Response executeRequest(String url, String authority, String content){
        Request request = getRequest(url, authority, content);
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            return okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Response getLoginRequest(String authority) {
        String url = WAServicer.getLoginUrl();
        return executeRequest(url,authority,null);
    }

    public static Response getTagListRequest(String authority, String deviceName) {
        String url = WAServicer.getTagListUrl(deviceName);
        return executeRequest(url,authority,null);
    }

    public static Response getValueRequest(String authority, List<Tag> tagList) {
        Context context = EDSApplication.getContext();
        String content = context.getString(R.string.was_tag_value_request, tagList.toString());
        String url = WAServicer.getGetValueUrl();
        return executeRequest(url,authority,content);
    }

    public static Response getBasicInfoRequest(String deviceName){
        String url=WAServicer.getBasicInfoUrl(deviceName);
        Request request=new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client=new OkHttpClient();
        try {
            return client.newCall(request).execute();
        }catch (IOException exp){

        }
        return null;
    }
}
