package com.xseec.eds.model;

import com.xseec.eds.model.tags.Tag;

import java.util.List;

/**
 * Created by Administrator on 2018/7/12.
 */

public interface ComListener {

    void onRefreshed(List<Tag> validTagList);
}
