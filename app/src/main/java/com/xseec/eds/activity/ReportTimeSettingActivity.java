package com.xseec.eds.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.xseec.eds.R;
import com.xseec.eds.util.ApiLevelHelper;
import com.xseec.eds.util.DateHelper;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

//nj--Create a activity for setting the report time on 2018/11/20
public class ReportTimeSettingActivity extends BaseActivity {

    public static String KEY_START_TIME="startTime";
    public static String KEY_END_TIME="endTime";

    @InjectView(R.id.edit_start)
    EditText editStart;
    @InjectView(R.id.edit_end)
    EditText editEnd;
    @InjectView(R.id.btn_setting)
    Button btnSetting;

    private String start;
    private String end;
    private Calendar calendar;
    private Calendar startTime;
    private Calendar endTime;

    public static void start(Activity context,int requestCode){
        Intent intent=new Intent( context,ReportTimeSettingActivity.class );
        context.startActivityForResult( intent,requestCode );
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_report_time_setting );
        ButterKnife.inject( this );
        initView();
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

    private void initView(){
        startTime=DateHelper.getDayStartTime(Calendar.getInstance());
        endTime= (Calendar) startTime.clone();
        startTime.add( Calendar.DAY_OF_MONTH,-7 );
        end=DateHelper.getYMDString( endTime.getTime() );
        start=DateHelper.getYMDString( startTime.getTime());
        editStart.setText( start );
        editEnd.setText( end );
    }

    private void pickDate(final boolean isStart){
        calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog
                .OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(year,month,dayOfMonth);
                calendar=DateHelper.getDayStartTime(calendar);
                if (isStart) {
                    start=DateHelper.getYMDString( calendar.getTime());
                    startTime=calendar;
                    editStart.setText(start);
                } else {
                    end=DateHelper.getYMDString( calendar.getTime() );
                    endTime=calendar;
                    editEnd.setText( end );
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate( calendar.getTime().getTime() );
        dialog.show();
    }

    @OnClick(R.id.edit_start)
    public void onStartClicked() { pickDate( true );}

    @OnClick(R.id.edit_end)
    public void onEndClicked() { pickDate( false );}

    @OnClick(R.id.btn_setting)
    public void onBtnSettingClicked() {
        start=DateHelper.getServletString( startTime.getTime() );
        end=DateHelper.getServletString( endTime.getTime() );
        if(startTime.before( endTime )){
            Intent intent=new Intent(  );
            intent.putExtra( KEY_START_TIME, start);
            intent.putExtra( KEY_END_TIME,end );
            setResult( RESULT_OK,intent );
            finish();
        }else{
            Toast.makeText( this, "请重新选择时间", Toast.LENGTH_SHORT ).show();
        }
    }
}
