package com.xseec.eds.util;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Action;

import java.io.IOException;

//nj--记录各种信息 2018/11/7
public class RecordHelper {

    //nj--操作信息记录
    public static void actionLog(String actionInfo){
        Action action=new Action( );
        User user=WAServicer.getUser();
        action.genId( user.getDeviceName() );

        //nj--判断是否能读取到本机号码 2018/11/14
        if (action.getTelephony().isEmpty()||" ".equals( action.getTelephony() )){
            action.setUser(user.getUsername());
        }else {
            action.setUser(action.getTelephony());
        }

        action.setInfo(actionInfo);
        WAServiceHelper.sendActionUpdateRequest(action, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });
    }
}
