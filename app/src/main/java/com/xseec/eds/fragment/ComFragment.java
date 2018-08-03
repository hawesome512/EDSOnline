package com.xseec.eds.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.xseec.eds.model.ComListener;
import com.xseec.eds.model.Tags.Tag;
import com.xseec.eds.model.User;
import com.xseec.eds.service.ComService;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2018/7/14.
 */

public abstract class ComFragment extends Fragment implements ComListener {

    private static final String KEY_USER = "user";
    private static final String KEY_TAGS = "tag_list";

    protected static Bundle getBundle(User user, List<Tag> tagList) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_USER, user);
        bundle.putParcelableArray(KEY_TAGS, tagList.toArray(new Tag[tagList.size()]));
        return bundle;
    }

    protected List<Tag> tagList;
    protected User user;

    private ComService.ComBinder comBinder;
    protected ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            comBinder = (ComService.ComBinder) service;
            comBinder.startCom(ComFragment.this, tagList, user.getAuthority());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(KEY_USER);
            Tag[] tags = (Tag[]) getArguments().getParcelableArray(KEY_TAGS);
            tagList = Arrays.asList(tags);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getContext(), ComService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (comBinder != null) {
            comBinder.cancelCom();
        }
        getActivity().unbindService(serviceConnection);
    }


}
