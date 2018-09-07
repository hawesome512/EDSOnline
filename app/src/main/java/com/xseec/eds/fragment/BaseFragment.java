package com.xseec.eds.fragment;

import android.support.v4.app.Fragment;

import com.squareup.okhttp.Response;

/**
 * Created by Administrator on 2018/8/15.
 */

public abstract class BaseFragment extends Fragment {

    protected boolean refreshViewsInThread(final Response response) {
        //横竖屏切换时，可能造成getActivity==null
        if (getActivity() == null) {
            return false;
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRefreshViews(response);
                }
            });
            return true;
        }
    }

    protected abstract void onRefreshViews(Response response);

}
