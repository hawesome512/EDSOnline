package com.xseec.eds.model;

public interface LoginListener {

    enum LoginType{ACCOUNT,PHONE,SCAN};

    void onSuccess(User user,String deviceName,LoginType loginType,QrCode qrCode);
    void onReplaceFragment(LoginType loginType);
    void onScan();
}
