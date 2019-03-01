package com.xseec.eds.model;

public interface LoginListener {

    enum LoginType{ACCOUNT,PHONE};

    void onSuccess(User user,String deviceName,LoginType loginType);
    void onReplaceFragment(LoginType loginType,boolean isAutoLogin);
}
