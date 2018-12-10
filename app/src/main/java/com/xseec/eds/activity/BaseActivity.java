package com.xseec.eds.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.xseec.eds.R;
import com.xseec.eds.util.ViewHelper;

/**
 * Created by Administrator on 2018/8/17.
 */

public class BaseActivity extends AppCompatActivity {

    private boolean checkExit = false;
    private String checkInfo;

    //增加info,告诉用户具体退出哪个环节：编辑状态、APP
    public void setCheckExit(boolean checkExit,String info) {
        this.checkExit = checkExit;
        this.checkInfo =info;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                confirmExit();
                return false;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
            confirmExit();
    }

    //在Activity中重写onActivityResult方法触发Fragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    protected void confirmExit() {
        if(!checkExit){
            finish();
            return;
        }
        ViewHelper.checkExit(this,getString(R.string.exit_confirm, checkInfo),null);
    }
}
