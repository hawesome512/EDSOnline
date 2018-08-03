package com.xseec.eds.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.xseec.eds.model.ComListener;
import com.xseec.eds.model.Tags.Tag;
import com.xseec.eds.model.WAComTask;

import java.util.List;

public class ComService extends Service {

    private static final String TAG="ComService";

    private WAComTask comTask;

    public ComService() {
    }

    private ComBinder comBinder=new ComBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return comBinder;
    }

    public class ComBinder extends Binder{

        public void startCom(ComListener comListener, List<Tag> tagList,String authority){
            comTask=new WAComTask(comListener,tagList);
            comTask.execute(authority);
        }

        public void cancelCom(){
            comTask.cancelCom();
        }
    }
}
