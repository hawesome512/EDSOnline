package com.xseec.eds.util.Update;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;
import com.xseec.eds.model.WAServicer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/10/30.
 */

public class UpdateHelper {


    private static int getVersionCode(Context context) {
        int code = 0;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            code = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    public static void checkUpdate(Activity activity) {
        final int versionCode = getVersionCode(activity);
        new UpdateAppManager
                .Builder()
                .setActivity(activity)
                .setUpdateUrl(WAServicer.getAppUpdateUrl())
                .setHttpManager(new UpdateHttpManager())
                .build()
                .checkNewApp(new UpdateCallback() {
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            int newVersionCode = jsonObject.getInt("version_code");
                            updateAppBean.setUpdate(newVersionCode > versionCode ? "Yes" : "No")
                                    .setNewVersion(jsonObject.getString("version_name"))
                                    .setApkFileUrl(jsonObject.getString("apk_url"))
                                    .setUpdateLog(jsonObject.getString("update_log"))
                                    .setTargetSize(jsonObject.getString("size"))
                                    .setConstraint(jsonObject.getBoolean("constraint"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return updateAppBean;
                    }
                });
    }
}
