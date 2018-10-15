package com.xseec.eds.util;

import android.content.Context;
import android.text.TextUtils;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.model.servlet.Action;
import com.xseec.eds.model.servlet.Alarm;
import com.xseec.eds.model.DataLogFactor;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.model.tags.StoredTag;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.tags.ValidTag;

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
                .addHeader("Content-Type", "application/json;charset=utf-8");
        if(!TextUtils.isEmpty(authority))     {
                builder.addHeader("Authorization", "Basic " + authority);}
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

    public static void sendTagListRequest(String deviceName, Callback callback) {
        String url = WAServicer.getTagListUrl(deviceName);
        String authority=WAServicer.getUser().getAuthority();
        sendRequest(url, authority, null, callback);
    }

    public static void sendGetValueRequest(List<Tag> tagList, Callback callback) {
        Context context = EDSApplication.getContext();
        String content = context.getString(R.string.was_tag_value_request, tagList.toString());
        String url = WAServicer.getGetValueUrl();
        String authority=WAServicer.getUser().getAuthority();
        sendRequest(url, authority, content, callback);
    }

    public static void sendSetValueRequest(List<ValidTag> tagList, Callback
            callback) {
        Context context = EDSApplication.getContext();
        String content = context.getString(R.string.was_tag_value_request, tagList.toString());
        String url = WAServicer.getSetValueUrl();
        String authority=WAServicer.getUser().getAuthority();
        sendRequest(url, authority, content, callback);
    }

    public static void sendTagLogRequest(DataLogFactor factor,List<StoredTag> tagList,Callback callback){
        sendTagLogRequest(factor.getStartTimeString(),factor.getIntervalType(),factor.getInterval(),factor.getRecords(),tagList,callback);
    }

    public static void sendTagLogRequest( String startTime, StoredTag
            .IntervalType intervalType, int interval, int records, List<StoredTag> tagList,
            Callback callback) {
        Context context = EDSApplication.getContext();
        String content = context.getString(R.string.was_tag_log_request, startTime, intervalType
                .name(), interval, records, tagList.toString());
        String url = WAServicer.getTagLogUrl();
        String authority=WAServicer.getUser().getAuthority();
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

    public static Response getTagListRequest(String deviceName) {
        String url = WAServicer.getTagListUrl(deviceName);
        String authority=WAServicer.getUser().getAuthority();
        return executeRequest(url,authority,null);
    }

    public static Response getValueRequest(List<Tag> tagList) {
        Context context = EDSApplication.getContext();
        String content = context.getString(R.string.was_tag_value_request, tagList.toString());
        String url = WAServicer.getGetValueUrl();
        String authority=WAServicer.getUser().getAuthority();
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

    public static void sendWorkorderQueryRequest(Workorder workorder,String startTime,String endTime,Callback callback){
        String url=WAServicer.getWorkorderQueryUrl(workorder,startTime,endTime);
        sendRequest(url,null,null,callback);
    }

    public static void sendWorkorderUpdateRequest(Workorder workorder,Callback callback){
        String url=WAServicer.getWorkorderUpdateUrl();
        String content=workorder.toJson();
        sendRequest(url,null,content,callback);
    }

    public static void sendAlarmUpdateRequest(Alarm alarm,String startTime,String endTime,Callback callback){
        String url=WAServicer.getAlarmUpdateUrl(alarm,startTime,endTime);
        sendRequest(url,null,null,callback);
    }

    public static void sendActionUpdateRequest(Action action,String startTime,String endTime,Callback callback){
        String url=WAServicer.getActionUpdateUrl(action,startTime,endTime);
        sendRequest(url,null,null,callback);
    }

    public static String uploadImage(String imageUrl){
        return UploadHelper.formUpload(WAServicer.getUploadImageUrl(),imageUrl);
    }
}
