package com.xseec.eds.model;

import android.view.View;

import com.xseec.eds.model.tags.Tag;

/**
 * Created by Administrator on 2018/9/3.
 */

public interface TagListener {
    boolean tagClickEnable(Tag tag,View view);
    void onTagClick(Tag tag,View view);
}
