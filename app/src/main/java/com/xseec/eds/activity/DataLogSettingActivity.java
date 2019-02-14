package com.xseec.eds.activity;

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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.xseec.eds.R;
import com.xseec.eds.fragment.DataLogFragment;
import com.xseec.eds.model.DataLogFactor;
import com.xseec.eds.model.tags.StoredTag;
import com.xseec.eds.util.ApiLevelHelper;
import com.xseec.eds.util.DateHelper;

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
    Calendar endTime;
    DataLogFactor factor;

    private static final String EXT_FACTOR = "factor";
    @InjectView(R.id.radio_null)
    RadioButton radioNull;
    @InjectView(R.id.radio_min)
    RadioButton radioMin;
    @InjectView(R.id.radio_hour)
    RadioButton radioHour;
    @InjectView(R.id.radio_day)
    RadioButton radioDay;
    @InjectView(R.id.radio_month)
    RadioButton radioMonth;
    @InjectView(R.id.edit_end)
    EditText editEnd;

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
        factor = getIntent().getParcelableExtra(EXT_FACTOR);
        editStart.setText(DateHelper.getString(factor.getStartTime().getTime()));
        editEnd.setText(DateHelper.getString(factor.getEndTime().getTime()));
        editInterval.setText(String.valueOf(factor.getInterval()));
        editRecords.setText(String.valueOf(factor.getRecords()));
        spinnerIntervalType.setSelection(factor.getIntervalType().ordinal());
        spinnerDataType.setSelection(factor.getDataType().ordinal());
        startTime = factor.getStartTime();
        endTime = factor.getEndTime();
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
        pickDate(true);
    }

    @OnClick(R.id.edit_end)
    public void onEditEndClicked() {
        pickDate(false);
    }


    private void pickDate(final boolean isStart) {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (isStart) {
                    startTime.set(year, month, dayOfMonth);
                } else {
                    endTime.set(year, month, dayOfMonth);
                }
                pickTime(calendar, isStart);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar
                .DAY_OF_MONTH)).show();
    }

    private void pickTime(Calendar calendar, final boolean isStart) {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (isStart) {
                    startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startTime.set(Calendar.MINUTE, minute);
                    startTime.set(Calendar.SECOND, 0);
                    factor.setStartTime(startTime);
                    if(radioMonth.isChecked()){
                        factor.setRecords(startTime.getActualMaximum(Calendar.DAY_OF_MONTH));
                    }
                    editStart.setText(DateHelper.getString(startTime.getTime()));
                    endTime=factor.getEndTime();
                    editEnd.setText(DateHelper.getString(endTime.getTime()));
                } else {
                    endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    endTime.set(Calendar.MINUTE, minute);
                    endTime.set(Calendar.SECOND, 0);
                    factor.setEndTime(endTime);
                    editEnd.setText(DateHelper.getString(endTime.getTime()));
                }
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    @OnClick(R.id.btn_setting)
    public void onBtnSettingClicked() {
        if (radioNull.isChecked()&&startTime.after(endTime)) {
            Toast.makeText(this,R.string.workorder_range_error,Toast.LENGTH_SHORT).show();
        } else {
            StoredTag.DataType dataType = StoredTag.DataType.values()[spinnerDataType
                    .getSelectedItemPosition()];
            factor.setDataType(dataType);
            Intent intent = new Intent();
            intent.putExtra(DataLogFragment.KEY_FACOTR, factor);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @OnClick({R.id.radio_null, R.id.radio_min, R.id.radio_hour, R.id.radio_day, R.id.radio_month})
    public void onViewClicked(View view) {
        Calendar start = Calendar.getInstance();
        switch (view.getId()) {
            case R.id.radio_null:
                editEnd.setEnabled(true);
                break;
            case R.id.radio_min:
                factor.setRecords(60);
                factor.setInterval(1);
                factor.setIntervalType(StoredTag.IntervalType.S);
                start.add(Calendar.MINUTE, -1);
                editEnd.setEnabled(false);
                break;
            case R.id.radio_hour:
                factor.setRecords(60);
                factor.setInterval(1);
                factor.setIntervalType(StoredTag.IntervalType.M);
                start.add(Calendar.HOUR_OF_DAY, -1);
                editEnd.setEnabled(false);
                break;
            case R.id.radio_day:
                factor.setRecords(24);
                factor.setInterval(1);
                factor.setIntervalType(StoredTag.IntervalType.H);
                start = DateHelper.getDayStartTime(start);
                editEnd.setEnabled(false);
                break;
            case R.id.radio_month:
                factor.setInterval(1);
                factor.setIntervalType(StoredTag.IntervalType.D);
                factor.setRecords(start.getActualMaximum(Calendar.DAY_OF_MONTH));
                start.set(Calendar.DAY_OF_MONTH, 1);
                start = DateHelper.getDayStartTime(start);
                editEnd.setEnabled(false);
                break;
        }
        factor.setStartTime(start);
        editStart.setText(DateHelper.getString(factor.getStartTime().getTime()));
        editEnd.setText(DateHelper.getString(factor.getEndTime().getTime()));
    }
}
