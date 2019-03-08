package com.xseec.eds.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Alarm;
import com.xseec.eds.model.servlet.ResponseResult;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.util.ContentHelper;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.PermissionHelper;
import com.xseec.eds.util.RecordHelper;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class WorkorderCreatorActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.spinner_type)
    Spinner spinnerType;
    @InjectView(R.id.edit_title)
    EditText editTitle;
    @InjectView(R.id.edit_task)
    EditText editTask;
    @InjectView(R.id.edit_start)
    EditText editStart;
    @InjectView(R.id.edit_end)
    EditText editEnd;
    @InjectView(R.id.edit_location)
    EditText editLocation;
    @InjectView(R.id.edit_worker)
    EditText editWorker;
    @InjectView(R.id.edit_creator)
    EditText editCreator;
    @InjectView(R.id.btn_save)
    Button btnSave;
    @InjectView(R.id.image_worker)
    ImageView imageWorker;
    @InjectView(R.id.image_creator)
    ImageView imageCreator;

    private static final int REQUEST_WORKER = 1;
    private static final int REQUEST_CREATOR = 2;
    private static final String EXT_ALARM = "alarm";
    Workorder workorder;
    //在异常事件中创建异常工单
    Alarm alarm;
    @InjectView(R.id.progress)
    ProgressBar progress;

    public static void start(Activity context, int requestCode) {
        Intent intent = new Intent(context, WorkorderCreatorActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static void start(Activity context, Alarm alarm, int requestCode) {
        Intent intent = new Intent(context, WorkorderCreatorActivity.class);
        intent.putExtra(EXT_ALARM, alarm);
        context.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workorder_creator);
        ButterKnife.inject(this);
        setCheckExit(true, getString(R.string.workorder_save));
        setTitle(R.string.nav_workorder);
        ViewHelper.initToolbar(this, toolbar, R.drawable.ic_arrow_back_white_24dp);
        workorder = new Workorder();
        initIfAlarm();
        initIfSysUse();
    }

    private void initIfAlarm() {
        alarm = getIntent().getParcelableExtra(EXT_ALARM);
        if (alarm != null) {
            Device device = Device.initWith(alarm.getDevice());
            String status = Generator.getAlarmStateText(alarm.getAlarmCode(), device
                    .getStatusItems());
            //异常工单：后期可优化，不直接用1，用枚举
            spinnerType.setSelection(1);
            editTitle.setText(spinnerType.getSelectedItem().toString());
            StringBuilder builder = new StringBuilder();
            builder.append(getString(R.string.alarm_device, device.getDeviceAlias()));
            builder.append("\r\n" + getString(R.string.alarm_time, DateHelper.getString(alarm
                    .getTime())));
            builder.append("\r\n" + getString(R.string.alarm_type, status));
            editTask.setText(builder.toString());
        }
    }

    //nj--为admin系统管理员时初始化Spinner控件
    private void initIfSysUse() {
        if (!WAServicer.getUser().isAdministrator()) {
            String[] types = getResources().getStringArray(R.array.workorder_types);
            List<String> workorderType = new ArrayList<>();
            for (int i = 0; i < types.length; i++) {
                workorderType.add(types[i]);
            }
            workorderType.remove(workorderType.size() - 1);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, workorderType);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerType.setAdapter(adapter);
        }
    }

    @OnClick(R.id.btn_save)
    public void onViewClicked() {
        String title = editTitle.getText().toString();
        String task = editTask.getText().toString();
        String location = editLocation.getText().toString();
        String worker = editWorker.getText().toString();
        String creator = editCreator.getText().toString();
        int type = spinnerType.getSelectedItemPosition();
        if (TextUtils.isEmpty(title) || workorder.getStart() == null || workorder.getEnd() ==
                null) {
            Toast.makeText(this, R.string.workorder_nonull, Toast.LENGTH_SHORT).show();
        } else if (workorder.getStart().after(workorder.getEnd())) {
            Toast.makeText(this, R.string.workorder_range_error, Toast.LENGTH_SHORT).show();
        } else {
            //nj--判断用户类型设置 2018/12/25
            String sysId = getString(R.string.woekorder_sys_id);
            String user = WAServicer.getUser().getDeviceName();
            String id = WAServicer.getUser().isAdministrator() ? sysId : user;

            workorder.genId(id);
            workorder.setType(type);
            workorder.setTitle(title);
            workorder.setTask(Workorder.getServletString(task));
            workorder.setLocation(location);
            workorder.setWorker(worker);
            creator = TextUtils.isEmpty(creator) ? WAServicer.getUser().getUsername() : creator;
            workorder.setCreator(creator);
            ViewHelper.startViewAnimator(btnSave);
            saveWorkorder();
        }
    }

    private void saveWorkorder() {
        WAServiceHelper.sendWorkorderUpdateRequest(workorder, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                onSaveFailed();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                ResponseResult result = WAJsonHelper.getServletResult(response);
                if (result.isSuccess()) {

                    //nj--添加工单创建成功信息
                    String actionInfo = getString(R.string.action_workorder_create, workorder
                            .getTitle());
                    RecordHelper.actionLog(actionInfo);

                    setResult(RESULT_OK);
                    finish();
                    if (alarm != null) {
                        alarm.setConfirm(1);
                        alarm.setReport(workorder.getId());
                        saveAlarm();
                    } else {
                        onFinish();
                    }
                } else {
                    onSaveFailed();
                }
            }
        });
    }

    private void saveAlarm() {
        WAServiceHelper.sendAlarmUpdateRequest(alarm, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                String s = response.body().string();
                onFinish();
            }
        });
    }

    private void onFinish() {
        setResult(RESULT_OK);
        finish();
    }

    @OnClick(R.id.image_worker)
    public void onImageWorkerClicked() {
        pickContack(REQUEST_WORKER);
    }

    @OnClick(R.id.image_creator)
    public void onImageCreatorClicked() {
        pickContack(REQUEST_CREATOR);
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
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String show = (month + 1) + "-" + dayOfMonth;
                String value = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                Date date = DateHelper.getYMDDate(value);
                if (isStart) {
                    editStart.setText(show);
                    workorder.setStart(date);
                } else {
                    editEnd.setText(show);
                    workorder.setEnd(date);
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar
                .DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(calendar.getTime().getTime());
        dialog.show();
    }

    private void pickContack(final int requestCode) {
        if (PermissionHelper.checkPermission(this, Manifest.permission.READ_CONTACTS,
                PermissionHelper.CODE_READ_CONTACTS)) {
            ContentHelper.startContact(this, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        PermissionHelper.onPermissionRequested(btnSave, grantResults[0]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String result = ContentHelper.getContactInfo(this, data);
            switch (requestCode) {
                case REQUEST_WORKER:
                    editWorker.setText(result);
                    break;
                case REQUEST_CREATOR:
                    editCreator.setText(result);
                    break;
                default:
                    break;
            }
        }
    }

    private void onSaveFailed() {
        try {
            Thread.sleep(ViewHelper.ANIMATION_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewHelper.resetViewAnimator(btnSave);
                Snackbar.make(btnSave, R.string.workorder_fail, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
