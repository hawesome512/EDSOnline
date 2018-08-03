package com.xseec.eds.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Response;
import com.xseec.eds.model.Tags.Tag;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/11.
 */

public class WAJsonHelper {

    public static String getUserProjectInfo(Response jsonData) {
        String info=null;
        try {
            JSONObject object = new JSONObject(jsonData.body().string());
            info= object.getJSONObject("UserInfo").getString("Description");
        } catch (Exception exp) {
        }
        return info;
    }

    public static List<Tag> getTagList(Response jsonData){
        List<Tag> tagList=null;
        try {
            JSONArray jsonArray = new JSONObject(jsonData.body().string()).getJSONArray("Tags");
            Gson gson = new Gson();
            tagList = gson.fromJson(jsonArray.toString(), new
                    TypeToken<List<Tag>>() {
                    }.getType());
        }catch (Exception exp){}
        return tagList;

    }

    public static List<Tag> refreshTagValue(Response jsonData) {
        List<Tag> tagList=null;
        try {
            Gson gson = new Gson();
            JSONArray jsonArray = new JSONObject(jsonData.body().string()).getJSONArray("Values");
            tagList = gson.fromJson(jsonArray.toString(), new TypeToken<List<Tag>>() {
            }.getType());
        } catch (Exception exp) {
        }
        return tagList;
    }

    //经WAService修改参数，成功与否不能从response结果获知，故不处理
    private static void getSetTagResult(Response jsonData){}

    public static List<String>[] getTagLog(Response jsonData){
        List<String>[] tagLogs=null;
        try {
            JSONObject jsonObject = new JSONObject(jsonData.body().string());
            JSONArray jsonArray = jsonObject.getJSONArray("DataLog");
            int total=jsonArray.length();
            tagLogs=(List<String>[]) new List[total];
            for(int i=0;i<total;i++){
                JSONArray array= jsonArray.getJSONObject(i).getJSONArray("Values");
                List<String> values=new ArrayList<>();
                for(int j=0;j<array.length();j++){
                    values.add(array.getString(j));
                }
                tagLogs[i]=values;
            }
        } catch (Exception exp) {
        }
        return tagLogs;
    }
}
