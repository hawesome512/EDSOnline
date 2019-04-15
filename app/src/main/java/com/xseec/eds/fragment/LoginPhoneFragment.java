package com.xseec.eds.fragment;

import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.model.LoginListener;
import com.xseec.eds.model.LoginListener.LoginType;
import com.xseec.eds.model.User;
import com.xseec.eds.model.servlet.Phone;
import com.xseec.eds.model.servlet.ResponseResult;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;
import com.xseec.eds.widget.CaptchaTimeCount;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginPhoneFragment extends BaseFragment {

    private static final String KEY_AUTHORITY = "authority";
    private static final String KEY_LOGIN_INFO = "phone";

    @InjectView(R.id.edit_number)
    TextInputEditText editNumber;
    @InjectView(R.id.edit_code)
    TextInputEditText editCode;
    @InjectView(R.id.btn_code)
    TextView btnCode;
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
    private Phone phone;
    private String authority;
    private ResponseResult result;
    private CaptchaTimeCount captchaTimeCount;

    public static LoginPhoneFragment newInstance() {
        LoginPhoneFragment fragment = new LoginPhoneFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_phone, container, false);
        ButterKnife.inject(this, view);
        captchaTimeCount = new CaptchaTimeCount(60 * 1000, 1000, btnCode);
        loginListener = (LoginListener) getActivity();
        imageLeft.setImageResource(R.drawable.ic_account_white);
        phone = new Phone();
        //nj--判断是否需要自动登录
        checkAutoLogin();
        //此语句应当在chekc之前
        initViews();
        return view;
    }

    private void initViews(){
        editNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                authority=null;
                editCode.setEnabled(true);
                editCode.setText(null);
                btnCode.setEnabled(s.toString().length()==Phone.NUMBER_LENGTH);
            }
        });
    }

    @OnClick(R.id.btn_login)
    public void onBtnLoginClicked() {
        //============程序调试区==========================//

        //===============================================//
        if(phone!=null&&authority!=null){
            onLoginThread(authority,phone);
            return;
        }
        String number = Generator.getPhoneValue(editNumber.getText().toString());
        phone.setId(number);
        String code = editCode.getText().toString();
        if (code.length() != 4) {
            String codeError = getString(R.string.login_phone_code_error);
            onAlertDialog(codeError, 3);
            return;
        }
        phone.setCode(code);
        ViewHelper.startViewAnimator(btnLogin,layoutOptions);
        queryPhone(phone);
    }

    //nj--查询手机信息
    private void queryPhone(Phone phone) {
        WAServiceHelper.sendPhoneQueryRequest(phone, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                result = WAJsonHelper.getServletResult(response);
                refreshViewsInThread(response);
            }
        });
    }

    //nj--设备登入线程
    private void onLoginThread(final String authority, final Phone phone) {
        ViewHelper.startViewAnimator(btnLogin,layoutOptions);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String deviceName = WAJsonHelper.getUserProjectInfo(WAServiceHelper.
                        getLoginRequest(authority));
                if (!TextUtils.isEmpty(deviceName)) {
                    User user = new User(authority, deviceName, phone);
                    loginListener.onSuccess(user, deviceName, LoginType.PHONE,null);
                }
            }
        }).start();
    }

    //nj--获取验证码
    @OnClick(R.id.btn_code)
    public void onBtnCodeClicked() {
        captchaTimeCount.start();
        String number = Generator.getPhoneValue(editNumber.getText().toString());
        phone=new Phone();
        phone.setId(number);
        queryPhone(phone);
        editCode.requestFocus();
    }

    @Override
    protected void onRefreshViews(String jsonData) {
        int resultCode = result.getResultCode();
        switch (resultCode) {
            case 2: //号码未注册
                String unregistered = getString(R.string.login_phone_unregistered);
                onAlertDialog(unregistered, resultCode);
                break;
            case 3: //输入验证码错误
                String codeError = getString(R.string.login_phone_code_error);
                onAlertDialog(codeError, resultCode);
                break;
            case 4: //输入验证码超时
                String codeTimeout = getString(R.string.login_phone_code_timeout);
                onAlertDialog(codeTimeout, resultCode);
                break;
            case 5://验证成功
                final String authority = result.getMessage();
                onLoginThread(authority, phone);
                setLoginInfo(authority);
                break;
            case 6: //验证码已发送手机
                Pattern pattern = Pattern.compile("level:(\\d+);name:(\\S+)");
                Matcher matcher = pattern.matcher(result.getMessage());
                if (matcher.find()) {
                    phone.setLevel(Integer.valueOf(matcher.group(1)));
                    phone.setName(matcher.group(2));
                }
                Toast.makeText(getContext(), R.string.login_message_send, Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void onAlertDialog(String info, final int resultCode) {
        ViewHelper.singleAlertDialog(getActivity(), info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (resultCode) {
                    case 2:
                        captchaTimeCount.onReset();
                        break;
                    case 3:
                    case 4:
                        ViewHelper.resetViewAnimator(btnLogin,layoutOptions);
                        break;
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private void setLoginInfo(String authority) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences
                (getContext()).edit();
        Date loginTime = Calendar.getInstance().getTime();
        phone.setTime(DateHelper.getServletString(loginTime));
        Gson gson = new Gson();
        String phoneToJson = gson.toJson(phone);
        editor.putString(KEY_LOGIN_INFO, phoneToJson);
        editor.putString(KEY_AUTHORITY, authority);
        editor.apply();
    }

    //nj--手机用户一段时间内免短信验证登入
    private void checkAutoLogin() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String phoneToJson = preferences.getString(KEY_LOGIN_INFO, null);
        Phone lastPhone = gson.fromJson(phoneToJson, new TypeToken<Phone>() {
        }.getType());
        if(lastPhone==null){
            return;
        }
        Date calendar = Calendar.getInstance().getTime();
        String currentTime = DateHelper.getServletString(calendar);
        //nj--判断用上次用户信息时自动登录
        if (DateHelper.getBetweenOfSecond(lastPhone.getTime(), currentTime) < 3600 *
                24) {  //nj--自动登录时间段
//            onAutoLoginAnimation();
            authority = preferences.getString(KEY_AUTHORITY, null);
            phone=lastPhone;
            editNumber.setText(lastPhone.getId());
            editCode.setEnabled(false);
            btnCode.setEnabled(false);
            editCode.setText(R.string.login_24hour);

//            onLoginThread(authority, phone);
        }
    }

    @OnClick({R.id.image_left, R.id.image_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.image_left:
                loginListener.onReplaceFragment(LoginType.ACCOUNT);
                break;
            case R.id.image_right:
                loginListener.onScan();
                break;
        }
    }
}
