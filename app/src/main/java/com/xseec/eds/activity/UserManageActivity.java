package com.xseec.eds.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.xseec.eds.R;
import com.xseec.eds.model.servlet.Account;
import com.xseec.eds.model.servlet.Phone;
import com.xseec.eds.util.ApiLevelHelper;
import com.xseec.eds.util.ContentHelper;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.PermissionHelper;
import com.xseec.eds.util.ViewHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UserManageActivity extends AppCompatActivity {

    private static final String COMPILE_NUMBER = "\\d{11}$";
    private static final int REQUEST_CONTACT=0;

    private static final int CHECK_PHONE_FORMAT=0;
    private static final int CHECK_PHONE_EXISTED=1;
    private static final int CHECK_CREATE=2;
    private static final int CHECK_UPDATE=3;

    public static final String EXTRA_ACCOUNT = "account";
    public static final String EXTRA_PHONE = "phone";

    @InjectView(R.id.spinner_user_level)
    Spinner spinnerUserLevel;
    @InjectView(R.id.btn_setting)
    Button btnSetting;
    @InjectView(R.id.edit_phone)
    EditText editPhone;
    @InjectView(R.id.image_content)
    ImageView imageContent;

    private Account account;
    private Phone phone;

    public static void start(Activity context, int resultCode, Account account, Phone phone) {
        Intent intent = new Intent( context, UserManageActivity.class );
        intent.putExtra( EXTRA_ACCOUNT, account );
        intent.putExtra( EXTRA_PHONE, phone );
        context.startActivityForResult( intent, resultCode );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_manage );
        ButterKnife.inject( this );
        account = getIntent().getParcelableExtra( EXTRA_ACCOUNT );
        phone = getIntent().getParcelableExtra( EXTRA_PHONE );

        editPhone.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Generator.genPhoneInputFormat( s,start,before,editPhone );
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        initView();
    }

    private void initView() {
        if (isCreatorUser()) {
            btnSetting.setText( R.string.user_creator );
        } else {
            editPhone.setText( phone.getId() );
            btnSetting.setText( R.string.user_update );
        }
    }

    private boolean isCreatorUser() {
        return phone == null ? true : false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.reset( this );
    }

    @OnClick(R.id.btn_setting)
    public void onBtnSettingClicked() {
        String newPhone = Generator.replaceBlank( editPhone.getText().toString() );
        int type=checkEditInput( newPhone );
        String checkInfo;
        switch (type){
            case CHECK_PHONE_FORMAT://输入的手机号格式不正确
                checkInfo=getString( R.string.user_wrong_format );
                ViewHelper.singleAlertDialog( this,checkInfo ,null);
                break;
            case CHECK_PHONE_EXISTED://手机号存在
                checkInfo=getString( R.string.user_created );
                ViewHelper.singleAlertDialog( this,checkInfo,null );
                break;
            case CHECK_CREATE://创建用户确认
                checkInfo = getString( R.string.user_create_confirm, newPhone );
                checkDialog( checkInfo ,type);
                break;
            case CHECK_UPDATE://修改用户确认
                checkInfo = getString( R.string.user_modify_confirm, phone.getId() );
                checkDialog( checkInfo ,type);
                break;
        }
    }

    private int checkEditInput(String inputInfo){
        //nj--判断输入手机号格式是否正确
        if(!Pattern.matches( COMPILE_NUMBER,inputInfo )){
            return CHECK_PHONE_FORMAT;
        }else {
            //判断输入用户是否已被创建
            if (account.isCreatedForPhone( inputInfo )){
                return CHECK_PHONE_EXISTED;
            }else {
                //判断当前执行的类型：创建或修改用户
                return isCreatorUser()?CHECK_CREATE:CHECK_UPDATE;
            }
        }
    }

    public void dismiss(View view) {
        setResult( Activity.RESULT_CANCELED );
        if (ApiLevelHelper.isAtLeast( 21 )) {
            finishAfterTransition();
        }
    }

    @Override
    public void onBackPressed() {
        dismiss( null );
    }

    private void checkDialog(String info,final int type) {
        ViewHelper.checkExit( this, info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPhone = Generator.replaceBlank( editPhone.getText().toString() );
                switch (type){
                    case CHECK_CREATE:
                        account.addPhone( newPhone );
                        break;
                    case CHECK_UPDATE:
                        account.updatePhone( phone.getId(), newPhone );
                        break;
                }
                Intent intent = new Intent();
                intent.putExtra( EXTRA_ACCOUNT, account );
                setResult( RESULT_OK, intent );
                finish();
            }
        } );
    }

    @OnClick(R.id.image_content)
    public void onViewClicked() {
        if (PermissionHelper.checkPermission(this, Manifest.permission.READ_CONTACTS,
                PermissionHelper.CODE_READ_CONTACTS)) {
            ContentHelper.startContact(this, REQUEST_CONTACT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case REQUEST_CONTACT:
                    String result = ContentHelper.getContactInfo(this, data);
                    Pattern pattern=Pattern.compile( COMPILE_NUMBER );
                    Matcher matcher=pattern.matcher( result );
                    String phone =matcher.find()?matcher.group():null ;
                    //nj--手机号码格式化
                    editPhone.setText(phone);
                    break;
                default:
                    break;
            }
        }
    }
}
