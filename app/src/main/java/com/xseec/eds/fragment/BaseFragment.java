package com.xseec.eds.fragment;

import android.support.v4.app.Fragment;

import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;

/**
 * Created by Administrator on 2018/8/15.
 */

public abstract class BaseFragment extends Fragment {

    protected boolean refreshViewsInThread(final Response response) {
        final String jsonData = getString(response);
        //横竖屏切换时，可能造成getActivity==null
        if (getActivity() == null) {
            return false;
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRefreshViews(jsonData);
                }
            });
            return true;
        }
    }

    protected abstract void onRefreshViews(String jsonData);


    /*
     *在主线程onRefreshViews再处理response.body().string()大概率出现调用异常
     */
    private String getString(Response response) {
        try {
            return response.body().string();
        } catch (Exception e) {
            return null;
        }
    }
}
