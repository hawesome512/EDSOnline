package com.xseec.eds.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xseec.eds.R;
import com.xseec.eds.model.deviceconfig.Protect;
import com.xseec.eds.model.tags.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018/8/24.
 */

public class TabProtectFragment extends TabBaseFragment {

    private static final String KEY_PROTECTS="protects";
    public static Fragment newInstance(String deviceName,List<Protect> protectList){
        Fragment fragment=new TabProtectFragment();
        List<Tag> tags=new ArrayList<>();
        for(Protect protect:protectList){
            tags.add(new Tag(deviceName+":"+protect.getName()));
        }
        Bundle bundle=getBundle(tags);
        bundle.putParcelableArrayList(KEY_PROTECTS, (ArrayList<? extends Parcelable>) protectList);
        fragment.setArguments(getBundle(tags));
        return fragment;
    }

    @Override
    protected void initLayout() {
        addCard(getString(R.string.device_long), Arrays.asList("Ir","Tr"));
        addCard(getString(R.string.device_short), Arrays.asList("Isd","Tsd"));
        addCard(getString(R.string.device_instant), Arrays.asList("Ii"));
        addCard(getString(R.string.device_ground), Arrays.asList("Ig","Tg"));
    }

    @Override
    public void onClick(View v) {

    }

}
