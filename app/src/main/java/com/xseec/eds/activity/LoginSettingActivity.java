package com.xseec.eds.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.xseec.eds.R;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.util.ApiLevelHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginSettingActivity extends AppCompatActivity {

    @InjectView(R.id.edit_server)
    EditText editServer;
    @InjectView(R.id.edit_project)
    EditText editProject;
    @InjectView(R.id.edit_node)
    EditText editNode;
    @InjectView(R.id.spinner_languages)
    Spinner spinnerLanguages;
    @InjectView(R.id.btn_setting)
    Button btnSetting;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_setting);
        ButterKnife.inject(this);
        editServer.setText(WAServicer.getHostUrl());
        editProject.setText(WAServicer.getProjectName());
        editNode.setText(WAServicer.getNodeName());
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

    @OnClick(R.id.btn_setting)
    public void onViewClicked() {
        String server=editServer.getText().toString();
        String project=editProject.getText().toString();
        String node=editNode.getText().toString();
        if(TextUtils.isEmpty(server)||TextUtils.isEmpty(project)|TextUtils.isEmpty(node)){

        }else {
            WAServicer.setWAServicer(server,project,node);
        }
        dismiss(null);
    }
}
