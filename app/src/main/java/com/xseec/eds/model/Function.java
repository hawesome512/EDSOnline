package com.xseec.eds.model;

import android.support.v4.app.Fragment;

import com.xseec.eds.fragment.BaseFragment;

/**
 * Created by Administrator on 2018/12/11.
 */

public class Function {
    private int imageRes;
    private int nameRes;
    private int menuId;
    private Fragment fragment;

    public Function(int imageRes, int nameRes, int menuId, Fragment fragment) {
        this.imageRes = imageRes;
        this.nameRes = nameRes;
        this.menuId = menuId;
        this.fragment = fragment;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public int getNameRes() {
        return nameRes;
    }

    public void setNameRes(int nameRes) {
        this.nameRes = nameRes;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}
