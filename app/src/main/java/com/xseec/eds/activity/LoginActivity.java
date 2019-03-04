package com.xseec.eds.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.xseec.eds.R;
import com.xseec.eds.fragment.AccountLoginFragment;
import com.xseec.eds.fragment.PhoneLoginFragment;
import com.xseec.eds.model.LoginListener;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Basic;
import com.xseec.eds.model.tags.OverviewTag;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.RecordHelper;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.util.Update.UpdateHelper;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.xseec.eds.model.LoginListener.LoginType.ACCOUNT;

public class LoginActivity extends AppCompatActivity implements LoginListener {

    private static final String KEY_LOGIN_TYPE="login_type";

    @InjectView(R.id.fab_server)
    FloatingActionButton fabServer;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        //nj--退回登录界面时优先使用账户登录方式
        intent.putExtra( KEY_LOGIN_TYPE,false );
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        getLoginType();
        int orientation= ViewHelper.isPort()? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        setRequestedOrientation( orientation );
        UpdateHelper.checkUpdate(this);
    }

    @Override
    public void onSuccess(final User user,String deviceName,LoginType loginType) {
        //nj--记录用户登录的方式
        setLoginType( loginType );
        WAServicer.setUser(user);
        final Basic basic = WAJsonHelper.getBasicList(WAServiceHelper
                .getBaiscQueryRequest(deviceName));
        WAServicer.setBasic(basic);
        final ArrayList<Tag> tagList = (ArrayList<Tag>) WAJsonHelper.getTagList
                (WAServiceHelper.getTagListRequest(deviceName));
        TagsFilter.setAllTagList(tagList);
        //Ie数据只需要采集一次
        List<Tag> IeList= WAJsonHelper.refreshTagValue(WAServiceHelper.getValueRequest(TagsFilter.filterDeviceTagList(tagList,"Ie")));
        TagsFilter.refreshTagValue(IeList);

        final ArrayList<OverviewTag> overviewTagList= (ArrayList<OverviewTag>) WAJsonHelper.getOverviewTagList(WAServiceHelper.getOverviewtagQueryRequest(deviceName));

        //nj--添加登录操作信息
        String actionInfo = getString(R.string.action_login);
        RecordHelper.actionLog(actionInfo);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.start(LoginActivity.this, tagList,overviewTagList, basic);
                finish();
            }
        });
    }

    @Override
    public void onReplaceFragment(LoginType  loginType,boolean isAutoLogin) {
        loginTypeChange( loginType,isAutoLogin );
    }

    //NJ--记录上次登录类型，用户或手机号登录
    private void setLoginType(LoginType loginType){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( this )
                .edit();
        editor.putInt( KEY_LOGIN_TYPE,loginType.ordinal() );
        editor.apply();
    }

    private void getLoginType(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );
        int type=preferences.getInt( KEY_LOGIN_TYPE, ACCOUNT.ordinal() );
        Boolean isAutoLogin=getIntent().getBooleanExtra( KEY_LOGIN_TYPE,true );
        LoginType loginType=LoginType.values()[type];
        loginTypeChange( loginType,isAutoLogin );
    }

    private void loginTypeChange(LoginType loginType,boolean isAutoLogin) {
        switch (loginType){
            case ACCOUNT:
                replaceFragment( AccountLoginFragment.newInstance() );
                break;
            case PHONE:
                replaceFragment( PhoneLoginFragment.newInstance(isAutoLogin) );
                break;
        }
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace( R.id.login_content,fragment );
        transaction.commit();
    }

    @OnClick(R.id.fab_server)
    public void onFabServerClicked() {
        LoginSettingActivity.start(this);
    }
}