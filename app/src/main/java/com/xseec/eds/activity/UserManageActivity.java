package com.xseec.eds.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.xseec.eds.R;
import com.xseec.eds.model.servlet.Phone;
import com.xseec.eds.util.ApiLevelHelper;
import com.xseec.eds.util.ContentHelper;
import com.xseec.eds.util.PermissionHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UserManageActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACT = 0;

    public static final String EXTRA_PHONE = "phone";

    @InjectView(R.id.spinner_user_level)
    Spinner spinnerUserLevel;
    @InjectView(R.id.btn_setting)
    Button btnSetting;
    @InjectView(R.id.edit_phone)
    EditText editPhone;
    @InjectView(R.id.edit_name)
    EditText editName;
    @InjectView(R.id.image_contact)
    ImageView imageContact;

    private Phone phone;

    public static void start(Activity context, int resultCode, Phone phone) {
        Intent intent = new Intent(context, UserManageActivity.class);
        intent.putExtra(EXTRA_PHONE, phone);
        context.startActivityForResult(intent, resultCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage);
        ButterKnife.inject(this);
        phone = getIntent().getParcelableExtra(EXTRA_PHONE);
        initView();
    }

    private void initView() {
        spinnerUserLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //系统管理员无法在APP上设置
                    Toast.makeText(UserManageActivity.this, R.string.setting_limit_admin, Toast
                            .LENGTH_SHORT).show();
                    spinnerUserLevel.setSelection(1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (phone.getId() == null) {
            //默认创建普通访客
            btnSetting.setText(R.string.user_creator);
            spinnerUserLevel.setSelection(2);
        } else {
            editName.setText(phone.getName());
            editPhone.setText(phone.getId());
            spinnerUserLevel.setSelection(phone.getLevel());
            btnSetting.setText(R.string.user_update);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.btn_setting)
    public void onBtnSettingClicked() {
        String number = editPhone.getText().toString();
        if (Pattern.matches("\\d{11}", number)) {
            phone.setName(editName.getText().toString());
            phone.setId(number);
            phone.setLevel(spinnerUserLevel.getSelectedItemPosition());
            Intent intent = new Intent();
            intent.putExtra(EXTRA_PHONE, phone);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, R.string.user_number_format, Toast.LENGTH_SHORT).show();
        }
    }

    public void dismiss(View view) {
        setResult(Activity.RESULT_CANCELED);
        if (ApiLevelHelper.isAtLeast(21)) {
            finishAfterTransition();
        }
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

    @OnClick(R.id.image_contact)
    public void onViewClicked() {
        if (PermissionHelper.checkPermission(this, Manifest.permission.READ_CONTACTS,
                PermissionHelper.CODE_READ_CONTACTS)) {
            ContentHelper.startContact(this, REQUEST_CONTACT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CONTACT:
                    String result = ContentHelper.getContactInfo(this, data);
                    Pattern pattern = Pattern.compile("(\\w+)\\W*(\\d{11})");
                    Matcher matcher = pattern.matcher(result);
                    if (matcher.find()) {
                        editName.setText(matcher.group(1));
                        editPhone.setText(matcher.group(2));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
