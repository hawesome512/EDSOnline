package com.xseec.eds.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.model.BasicInfo;
import com.xseec.eds.model.Tags.Tag;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.util.CodeHelper;
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
    }

    @OnClick(R.id.btn_login)
    public void onBtnLoginClicked() {
        startLoginAnimator();
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        final String authority = CodeHelper.encode(username + ":" + password);
        onLoginThread(authority);
    }

    private void onLoginThread(final String authority) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String deviceName = WAJsonHelper.getUserProjectInfo(WAServiceHelper
                        .getLoginRequest(authority));
                if (!TextUtils.isEmpty(deviceName)) {
                    setLoginInfo();
                    final User user = new User(authority, deviceName);
                    final BasicInfo basicInfo = WAJsonHelper.getBasicInfo(WAServiceHelper
                            .getBasicInfoRequest(deviceName));
                    final ArrayList<Tag> tagList = (ArrayList<Tag>) WAJsonHelper.getTagList
                            (WAServiceHelper.getTagListRequest(authority, deviceName));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            WAServicer.setUser(user);
                            MainActivity.start(LoginActivity.this, user, basicInfo, tagList);
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
        resetLoginAnimator();
        textFailure.setVisibility(View.VISIBLE);
    }

    private void startLoginAnimator() {
        ViewPropertyAnimator animator = btnLogin.animate();
        float scaleValue = btnLogin.getHeight() * 1.0f / btnLogin.getWidth();
        animator.scaleX(scaleValue);
        animator.alpha(0f);
        animator.setDuration(500);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.start();
    }

    private void resetLoginAnimator() {
        btnLogin.setScaleX(1f);
        btnLogin.setAlpha(1f);
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