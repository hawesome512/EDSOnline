package com.xseec.eds.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.model.LoginListener;
import com.xseec.eds.model.LoginListener.LoginType;
import com.xseec.eds.model.User;
import com.xseec.eds.util.CodeHelper;
import com.xseec.eds.util.ContentHelper;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AccountLoginFragment extends BaseFragment {

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    @InjectView(R.id.edit_username)
    TextInputEditText editUsername;
    @InjectView(R.id.edit_password)
    TextInputEditText editPassword;
    @InjectView(R.id.text_failure)
    TextView textFailure;
    @InjectView(R.id.switch_remember)
    Switch switchRemember;
    @InjectView(R.id.btn_login)
    Button btnLogin;
    @InjectView(R.id.progress_login)
    ProgressBar progressLogin;
    @InjectView(R.id.tv_change_phone)
    TextView tvChangePhone;

    private LoginListener loginListener;

    public static AccountLoginFragment newInstance(){
        return new AccountLoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_login_account, container, false );
        ButterKnife.inject( this, view );
        //NJ--检查到不为国内SIM卡时，禁止使用手机登录
        int visible=ContentHelper.isChinaSimCard( getActivity() )?View.VISIBLE:View.GONE;
        tvChangePhone.setVisibility( visible );

        loginListener= (LoginListener) getActivity();
        getLoginInfo();
        onEditTextInput( editUsername );
        onEditTextInput( editPassword );
        return view;
    }

    @OnClick(R.id.btn_login)
    public void onBtnLoginClicked() {
        //============程序调试区==========================//

        //===============================================//
        textFailure.setVisibility(View.INVISIBLE);
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        final String authority = CodeHelper.encode( username + ":" + password );
        ViewHelper.startViewAnimator( btnLogin );
        onLoginThread( authority );
    }

    private void onLoginThread(final String authority) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                String deviceName = WAJsonHelper.getUserProjectInfo( WAServiceHelper
                        .getLoginRequest( authority ) );
                if (!TextUtils.isEmpty( deviceName )) {
                    User user=new User( authority,deviceName );
                    loginListener.onSuccess( user, deviceName, LoginType.ACCOUNT );
                    setLoginInfo();
                } else {
                    try {
                        Thread.sleep(ViewHelper.ANIMATION_DURATION);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread( new Runnable() {
                        @Override
                        public void run() {
                            onFailure();
                        }
                    } );
                }
            }
        } ).start();
    }

    private void getLoginInfo() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getActivity() );
        switchRemember.setChecked( preferences.getBoolean( KEY_REMEMBER, false ) );
        editUsername.setText( preferences.getString( KEY_USERNAME, null ) );
        editPassword.setText( preferences.getString( KEY_PASSWORD, null ) );
    }

    private void setLoginInfo() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( getActivity() )
                .edit();
        if (switchRemember.isChecked()) {
            editor.putBoolean( KEY_REMEMBER, true );
            editor.putString( KEY_USERNAME, editUsername.getText().toString() );
            editor.putString( KEY_PASSWORD, editPassword.getText().toString() );
        } else {
            editor.putBoolean( KEY_REMEMBER, false );
            editor.putString( KEY_USERNAME, null );
            editor.putString( KEY_PASSWORD, null );
        }
        editor.apply();
    }

    private void onFailure() {
        textFailure.setVisibility( View.VISIBLE );
        ViewHelper.resetViewAnimator( btnLogin );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset( this );
    }

    @Override
    protected void onRefreshViews(String jsonData) {

    }

    private void onEditTextInput(TextInputEditText editText){
        editText.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textFailure.setVisibility( View.INVISIBLE );
//                ViewHelper.resetViewAnimator( btnLogin );
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );
    }

    @OnClick(R.id.tv_change_phone)
    public void onTvChangePhoneClicked() {
        switchRemember.setChecked( false );
        setLoginInfo();
        loginListener.onReplaceFragment( LoginType.PHONE,false );
    }
}
