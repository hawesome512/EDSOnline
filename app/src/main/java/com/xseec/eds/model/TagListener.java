package com.xseec.eds.model;

import android.view.View;

import com.xseec.eds.model.tags.Tag;

/**
 * Created by Administrator on 2018/9/3.
 */

public interface TagListener {
    //nj--判断tag能被点击
    boolean tagClickEnable(Tag tag,View view);
    void onTagClick(Tag tag,View view);
}
