package com.xseec.eds.model;

import com.xseec.eds.R;
import com.xseec.eds.util.CodeHelper;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.EDSApplication;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QrCode {

    public static final int OVERDUE_MINUTE=5;

    private String deviceName;
    private String authority;
    private QrCodeType qrCodeType;
    private String param;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public QrCodeType getQrCodeType() {
        return qrCodeType;
    }

    public void setQrCodeType(QrCodeType qrCodeTypes) {
        this.qrCodeType = qrCodeTypes;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public enum QrCodeType {LOGIN,DEVICE,WORKORDER}

    @Override
    public String toString() {
        return EDSApplication.getContext().getString(R.string.qr_format, deviceName, authority,qrCodeType.ordinal(),param);
    }

    public static QrCode init(String content){
        //Key经Base64加密后通常末尾有[\n]
        Pattern pattern=Pattern.compile("Node:(\\S+?);Key:([\\S\\n]+?);Type:(\\S+?);Param:(\\S+)");
        Matcher matcher=pattern.matcher(content);
        QrCode qrCode=null;
        if(matcher.find()){
            qrCode=new QrCode();
            qrCode.deviceName =matcher.group(1);
            qrCode.authority =matcher.group(2);
            qrCode.qrCodeType= QrCodeType.values()[Integer.valueOf(matcher.group(3))];
            qrCode.param=matcher.group(4);

            if(qrCode.qrCodeType==QrCodeType.LOGIN){
                //扫码5分钟内才有效
                Date generateTime= DateHelper.getSdfDate(CodeHelper.decode(qrCode.param));
                Calendar now= Calendar.getInstance();
                now.add(Calendar.MINUTE,-OVERDUE_MINUTE);
                Date nowTime= now.getTime();
                if(generateTime==null||nowTime.after(generateTime)){
                    return null;
                }
            }
        }
        return qrCode;
    }
}
