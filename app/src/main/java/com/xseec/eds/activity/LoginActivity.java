package com.xseec.eds.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.qrcode.encoder.QRCode;
import com.xseec.eds.R;
import com.xseec.eds.fragment.LoginAccountFragment;
import com.xseec.eds.fragment.LoginPhoneFragment;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.LoginListener;
import com.xseec.eds.model.QrCode;
import com.xseec.eds.model.User;
import com.xseec.eds.model.UserType;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Basic;
import com.xseec.eds.model.tags.OverviewTag;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.QrCodeHelper;
import com.xseec.eds.util.RecordHelper;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.xseec.eds.model.LoginListener.LoginType.PHONE;

@RuntimePermissions
public class LoginActivity extends AppCompatActivity implements LoginListener {

    @InjectView(R.id.fab_server)
    FloatingActionButton fabServer;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

//        int orientation = ViewHelper.isPort() ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
//                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//        setRequestedOrientation(orientation);

        onReplaceFragment(LoginType.PHONE);
    }

    @Override
    public void onSuccess(final User user, String deviceName, final LoginType loginType,final QrCode qrCode) {
        WAServicer.setUser(user);
        final Basic basic = WAJsonHelper.getBasicList(WAServiceHelper
                .getBaiscQueryRequest(deviceName));
        WAServicer.setBasic(basic);

        final ArrayList<Tag> tagList = (ArrayList<Tag>) WAJsonHelper.getTagList
                (WAServiceHelper.getTagListRequest(deviceName));
        TagsFilter.setAllTagList(tagList);
        //Ie数据只需要采集一次
        List<Tag> IeList = WAJsonHelper.refreshTagValue(WAServiceHelper.getValueRequest
                (TagsFilter.filterDeviceTagList(tagList, "Ie")));
        TagsFilter.refreshTagValue(IeList);

        final ArrayList<OverviewTag> overviewTagList = (ArrayList<OverviewTag>) WAJsonHelper
                .getOverviewTagList(WAServiceHelper.getOverviewtagQueryRequest(deviceName));


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (qrCode != null && qrCode.getQrCodeType() == QrCode.QrCodeType.DEVICE) {
                    DeviceActivity.start(LoginActivity.this, qrCode.getParam());
                } else {
                    //nj--记录用户登录的方式
                    RecordHelper.actionLog(getString(R.string.action_login));

                    MainActivity.start(LoginActivity.this, tagList, overviewTagList, basic);
                    finish();
                }
            }
        });
    }

    @Override
    public void onReplaceFragment(LoginType loginType) {
        Fragment fragment=loginType==PHONE?LoginPhoneFragment.newInstance():LoginAccountFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.login_content, fragment);
        transaction.commit();
    }

    @OnClick(R.id.fab_server)
    public void onFabServerClicked() {
        LoginSettingActivity.start(this);
    }

    @Override
    public void onScan() {
        LoginActivityPermissionsDispatcher.prepareScanWithPermissionCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LoginActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
                grantResults);
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    void prepareScan() {
        QrCodeHelper.scan(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_CANCELED){
            return;
        }
        String result = QrCodeHelper.getScanResult(requestCode, resultCode, data);
        final QrCode qrCode = QrCode.init(result);
        if (qrCode == null) {
            Toast.makeText(this, R.string.qr_invalid, Toast.LENGTH_SHORT).show();
            return;
        }
        final User user = new User(qrCode.getAuthority(), qrCode.getDeviceName());
        user.setUserType(UserType.TEL_TEMP);
        WAServicer.setUser(user);
        switch (qrCode.getQrCodeType()) {
            case WORKORDER:
                WorkorderActivity.start(this, qrCode.getParam());
                break;
            case LOGIN:
            case DEVICE:
                Toast.makeText(this, R.string.qr_go, Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess(user, qrCode.getDeviceName(), LoginType.SCAN,qrCode);
                    }
                }).start();
                break;
        }

    }

}