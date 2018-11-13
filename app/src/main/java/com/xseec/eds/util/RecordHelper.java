package com.xseec.eds.util;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Action;

import java.io.IOException;

//nj--记录各种信息 2018/11/7
public class RecordHelper {

    //nj--操作信息记录
    public static void actionLog(String ActionInfo){
        Action action=new Action( );
        action.genId( WAServicer.getUser().getDeviceName() );
        action.setUser( WAServicer.getUser().getUsername() );
        action.setInfo(ActionInfo);
        WAServiceHelper.sendActionUpdateRequest( action, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        } );
    }
}
