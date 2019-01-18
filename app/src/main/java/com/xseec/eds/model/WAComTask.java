package com.xseec.eds.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.squareup.okhttp.Response;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.EDSApplication;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.util.List;

/**
 * Created by Administrator on 2018/7/12.
 */

public class WAComTask extends AsyncTask<String, Void, Boolean> {

    private static final String INTERVAL = "interval";

    private ComListener comListener;
    private List<Tag> tagList;
    private Boolean isCanceled = false;
    private int interval;

    public WAComTask(ComListener comListener, List<Tag> totalList) {
        this.comListener=comListener;
        this.tagList = totalList;
        initInterval();
    }

    private void initInterval() {
        Context context = EDSApplication.getContext();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        interval = preferences.getInt(INTERVAL, 3);
    }

    public void cancelCom() {
        this.isCanceled = true;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String authority = strings[0];
        while (!isCanceled) {
            try {
                Response response = WAServiceHelper.getValueRequest(tagList);
                List<Tag> validTagList = WAJsonHelper.refreshTagValue(response);
                comListener.onRefreshed(validTagList);
                Thread.sleep(1000 * interval);
            } catch (Exception exp) {
            }
        }
        return null;
    }
}
