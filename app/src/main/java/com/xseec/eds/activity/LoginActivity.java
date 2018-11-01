package com.xseec.eds.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Basic;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.CodeHelper;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.util.Update.UpdateHelper;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_REMEMBER = "remember";

    @InjectView(R.id.fab_server)
    FloatingActionButton fabServer;
    @InjectView(R.id.edit_username)
    TextInputEditText editUsername;
    @InjectView(R.id.edit_password)
    TextInputEditText editPassword;
    @InjectView(R.id.switch_remember)
    Switch switchRemember;
    @InjectView(R.id.btn_login)
    Button btnLogin;
    @InjectView(R.id.progress_login)
    ProgressBar progressLogin;
    @InjectView(R.id.text_failure)
    TextView textFailure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        getLoginInfo();
        UpdateHelper.checkUpdate(this);
    }

    @OnClick(R.id.btn_login)
    public void onBtnLoginClicked() {

        //==============程序模块调试区域=====================
        //这是一个验证版本分支的语句，无意义
        //=================================================

        ViewHelper.startViewAnimator(btnLogin);
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        final String authority = CodeHelper.encode(username + ":" + password);
        onLoginThread(authority);
    }

    private void onLoginThread(final String authority) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String deviceName = WAJsonHelper.getUserProjectInfo(WAServiceHelper.getLoginRequest(authority));
                if (!TextUtils.isEmpty(deviceName)) {
                    setLoginInfo();
                    final User user = new User(authority, deviceName);
                    WAServicer.setUser(user);
                    final Basic basic=WAJsonHelper.getBasicList(WAServiceHelper.getBaiscQueryRequest(deviceName));
                    final ArrayList<Tag> tagList = (ArrayList<Tag>) WAJsonHelper.getTagList
                            (WAServiceHelper.getTagListRequest(deviceName));
                    TagsFilter.setAllTagList(tagList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.start(LoginActivity.this, basic, tagList);
                            finish();
                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onLoginFailed();
                        }
                    });
                }
            }
        }).start();
    }

    private void onLoginFailed() {
        ViewHelper.resetViewAnimator(btnLogin);
        textFailure.setVisibility(View.VISIBLE);
    }

    private void getLoginInfo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        switchRemember.setChecked(preferences.getBoolean(PREF_REMEMBER, false));
        editUsername.setText(preferences.getString(PREF_USERNAME, null));
        editPassword.setText(preferences.getString(PREF_PASSWORD, null));
    }

    private void setLoginInfo() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this)
                .edit();
        if (switchRemember.isChecked()) {
            editor.putBoolean(PREF_REMEMBER, true);
            editor.putString(PREF_USERNAME, editUsername.getText().toString());
            editor.putString(PREF_PASSWORD, editPassword.getText().toString());
        } else {
            editor.putBoolean(PREF_REMEMBER, false);
            editor.putString(PREF_USERNAME, null);
            editor.putString(PREF_PASSWORD, null);
        }
        editor.apply();
    }

    @OnClick(R.id.fab_server)
    public void onFabServerClicked() {
        LoginSettingActivity.start(this);
    }
}