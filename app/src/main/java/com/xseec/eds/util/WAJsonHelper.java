package com.xseec.eds.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Response;
import com.xseec.eds.model.BasicInfo;
import com.xseec.eds.model.servlet.Action;
import com.xseec.eds.model.servlet.Alarm;
import com.xseec.eds.model.servlet.Basic;
import com.xseec.eds.model.servlet.ResponseResult;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.model.tags.Tag;

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

    public static BasicInfo getBasicInfo(Response jsonData){
        Gson gson=new Gson();
        try {
            return  gson.fromJson(jsonData.body().string(),BasicInfo.class);
        }catch (Exception exp){}
        return null;
    }

    /*
        Servlet Json处理区域
     */
    public static List<Workorder> getWorkorderList(Response response){
        try {
            String json=response.body().string();
            json=filterServletJson(json);
            return getServletDateFormatGson().fromJson(filterServletJson(json),new TypeToken<List<Workorder>>(){}.getType());
        }catch (Exception exp){
            String s=exp.getMessage();
        }
        return null;
    }

    public static List<Alarm> getAlarmList(Response response){
        try {
            String json=response.body().string();
            return getServletDateFormatGson().fromJson(filterServletJson(json),new TypeToken<List<Alarm>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Action> getActionList(Response response){
        try {
            String json=response.body().string();
            return getServletDateFormatGson().fromJson(filterServletJson(json),new TypeToken<List<Action>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Basic getBasicList(Response response){
        try {
            String json=response.body().string();
            List<Basic> basics= getServletDateFormatGson().fromJson(filterServletJson(json),new TypeToken<List<Basic>>(){}.getType());
            if(basics!=null&&basics.size()>0){
                return basics.get(0);
            }else {
                return new Basic();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String filterServletJson(String json){
        //从Servlet返回的数据包含：【……，"key":""null""……】应该过滤，否则gson转换格式会出错
        return json.replaceAll(",\"\\w+\":\"+null\"+","");
    }

    private static Gson getServletDateFormatGson(){
        //使用此语句：Gson有能力将“2018-09-10”格式字符串转换为Date类型变量
        return new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public static ResponseResult getServletResult(Response response){
        Gson gson=new Gson();
        try {
            return gson.fromJson(response.body().string(),new TypeToken<ResponseResult>(){}.getType());
        } catch (Exception e) {
            return null;
        }
    }
}
