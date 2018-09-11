package com.xseec.eds.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.util.ApiLevelHelper;
import com.xseec.eds.widget.VerificationCodeInput;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class VerificationCodeActivity extends AppCompatActivity {

    @InjectView(R.id.code_input)
    VerificationCodeInput codeInput;
    @InjectView(R.id.text_error)
    TextView textError;
    @InjectView(R.id.btn_setting)
    Button btnSetting;

    private static final String EXT_CODE = "code";

    public static void start(Activity activity, int requestCode, String code) {
        Intent intent = new Intent(activity, VerificationCodeActivity.class);
        intent.putExtra(EXT_CODE, code);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
        ButterKnife.inject(this);
        final String code = getIntent().getStringExtra(EXT_CODE);
        codeInput.setOnCompletedListener(new VerificationCodeInput.Listener() {
            @Override
            public void onCompleted(String content) {
                textError.setVisibility(content.equals(code) ? View.INVISIBLE : View.VISIBLE);
                btnSetting.setEnabled(content.equals(code) ? true : false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

    public void dismiss(View view) {
        setResult(RESULT_CANCELED);
        if (ApiLevelHelper.isAtLeast(21)) {
            finishAfterTransition();
        }
        finish();
    }

    @OnClick(R.id.btn_setting)
    public void onViewClicked() {
        setResult(RESULT_OK);
        finish();
    }
}
