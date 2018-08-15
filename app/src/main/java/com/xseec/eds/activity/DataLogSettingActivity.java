package com.xseec.eds.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.xseec.eds.R;
import com.xseec.eds.fragment.DataLogFragment;
import com.xseec.eds.model.DataLogFactor;
import com.xseec.eds.model.Tags.StoredTag;
import com.xseec.eds.util.ApiLevelHelper;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.EDSApplication;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DataLogSettingActivity extends AppCompatActivity {

    @InjectView(R.id.edit_start)
    EditText editStart;
    @InjectView(R.id.edit_interval)
    EditText editInterval;
    @InjectView(R.id.spinner_interval_type)
    Spinner spinnerIntervalType;
    @InjectView(R.id.edit_records)
    EditText editRecords;
    @InjectView(R.id.spinner_data_type)
    Spinner spinnerDataType;
    @InjectView(R.id.btn_setting)
    Button btnSetting;

    Calendar startTime;

    private static final String EXT_FACTOR = "factor";

    public static void start(Activity context, int requestCode, DataLogFactor defaultFactor) {
        Intent intent = new Intent(context, DataLogSettingActivity.class);
        intent.putExtra(EXT_FACTOR, defaultFactor);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_log_setting);
        ButterKnife.inject(this);
        DataLogFactor factor = getIntent().getParcelableExtra(EXT_FACTOR);
        editStart.setText(DateHelper.getString(factor.getStartTime().getTime()));
        editInterval.setText(String.valueOf(factor.getInterval()));
        editRecords.setText(String.valueOf(factor.getRecords()));
        spinnerIntervalType.setSelection(factor.getIntervalType().ordinal());
        spinnerDataType.setSelection(factor.getDataType().ordinal());
        startTime=factor.getStartTime();
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

    @OnClick(R.id.edit_start)
    public void onEditStartClicked() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startTime.set(year, month, dayOfMonth);
                pickTime(calendar);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar
                .DAY_OF_MONTH)).show();
    }

    private void pickTime(Calendar calendar) {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTime.set(Calendar.MINUTE, minute);
                startTime.set(Calendar.SECOND, 0);
                editStart.setText(DateHelper.getString(startTime.getTime()));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    @OnClick(R.id.btn_setting)
    public void onBtnSettingClicked() {
        String strInterval = editInterval.getText().toString();
        String strRecourds = editRecords.getText().toString();
        String strStartTime = editStart.getText().toString();
        StoredTag.IntervalType intervalType = StoredTag.IntervalType.values()[spinnerIntervalType
                .getSelectedItemPosition()];
        StoredTag.DataType dataType = StoredTag.DataType.values()[spinnerDataType
                .getSelectedItemPosition()];
        if (!TextUtils.isEmpty(strInterval) && !TextUtils.isEmpty(strRecourds)) {
            int interval = Integer.parseInt(strInterval);
            int records = Integer.parseInt(strRecourds);
            DataLogFactor factor = new DataLogFactor(startTime, intervalType, interval, records,
                    dataType);

            Intent intent = new Intent();
            intent.putExtra(DataLogFragment.KEY_FACOTR, factor);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
