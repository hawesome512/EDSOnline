package com.xseec.eds.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.model.QrCode;
import com.xseec.eds.util.CodeHelper;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.QrCodeHelper;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class QRCodeActivity extends AppCompatActivity {

    @InjectView(R.id.image_qrcode)
    ImageView imageQrcode;
    @InjectView(R.id.btn_close)
    Button btnClose;
    @InjectView(R.id.text_valid)
    TextView textValid;

    public static void start(Context context) {
        Intent intent = new Intent(context, QRCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        ButterKnife.inject(this);
        refhresh();
    }

    private void refhresh(){
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.MINUTE,QrCode.OVERDUE_MINUTE);
        textValid.setText(getString(R.string.qr_valid,DateHelper.getString(calendar.getTime())));
        String nowTime = CodeHelper.encode(DateHelper.getNowForId());
        imageQrcode.setImageBitmap(QrCodeHelper.getQrCode(QrCode.QrCodeType.LOGIN, nowTime));
    }

    @OnClick({R.id.text_valid, R.id.btn_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_valid:
                refhresh();
                break;
            case R.id.btn_close:
                finish();
                break;
        }
    }
}
