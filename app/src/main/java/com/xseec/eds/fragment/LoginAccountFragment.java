package com.xseec.eds.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.xseec.eds.R;
import com.xseec.eds.model.LoginListener;
import com.xseec.eds.model.LoginListener.LoginType;
import com.xseec.eds.model.User;
import com.xseec.eds.util.CodeHelper;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginAccountFragment extends BaseFragment {

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

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
    @InjectView(R.id.image_left)
    ImageView imageLeft;
    @InjectView(R.id.image_right)
    ImageView imageRight;
    @InjectView(R.id.layout_options)
    LinearLayout layoutOptions;

    private LoginListener loginListener;

    public static LoginAccountFragment newInstance() {
        return new LoginAccountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_account, container, false);
        ButterKnife.inject(this, view);

        loginListener = (LoginListener) getActivity();
        getLoginInfo();
        return view;
    }

    @OnClick(R.id.btn_login)
    public void onBtnLoginClicked() {
        //============程序调试区==========================//

        //===============================================//
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        final String authority = CodeHelper.encode(username + ":" + password);
        ViewHelper.startViewAnimator(btnLogin,layoutOptions);
        onLoginThread(authority);
    }

    private void onLoginThread(final String authority) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String deviceName = WAJsonHelper.getUserProjectInfo(WAServiceHelper
                        .getLoginRequest(authority));
                if (!TextUtils.isEmpty(deviceName)) {
                    User user = new User(authority, deviceName);
                    loginListener.onSuccess(user, deviceName, LoginType.ACCOUNT,null);
                    setLoginInfo();
                } else {
                    try {
                        Thread.sleep(ViewHelper.ANIMATION_DURATION);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onFailure();
                        }
                    });
                }
            }
        }).start();
    }

    private void getLoginInfo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity
                ());
        switchRemember.setChecked(preferences.getBoolean(KEY_REMEMBER, true));
        editUsername.setText(preferences.getString(KEY_USERNAME, null));
        editPassword.setText(preferences.getString(KEY_PASSWORD, null));
    }

    private void setLoginInfo() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences
                (getActivity())
                .edit();
        if (switchRemember.isChecked()) {
            editor.putBoolean(KEY_REMEMBER, true);
            editor.putString(KEY_USERNAME, editUsername.getText().toString());
            editor.putString(KEY_PASSWORD, editPassword.getText().toString());
        } else {
            editor.putBoolean(KEY_REMEMBER, false);
            editor.putString(KEY_USERNAME, null);
            editor.putString(KEY_PASSWORD, null);
        }
        editor.apply();
    }

    private void onFailure() {
        Toast.makeText(getContext(), R.string.login_failure, Toast.LENGTH_SHORT).show();
        ViewHelper.resetViewAnimator(btnLogin,layoutOptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    protected void onRefreshViews(String jsonData) {

    }

    @OnClick({R.id.image_left, R.id.image_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_left:
                setLoginInfo();
                loginListener.onReplaceFragment(LoginType.PHONE);
                break;
            case R.id.image_right:
                loginListener.onScan();
                break;
        }
    }
}
