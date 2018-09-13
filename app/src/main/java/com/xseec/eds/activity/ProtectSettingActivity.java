package com.xseec.eds.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.fragment.TabProtectFragment;
import com.xseec.eds.model.deviceconfig.Protect;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.ApiLevelHelper;
import com.xseec.eds.util.Generator;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class ProtectSettingActivity extends AppCompatActivity implements SeekBar
        .OnSeekBarChangeListener {

    @InjectView(R.id.text_name)
    TextView textName;
    @InjectView(R.id.seek_bar)
    SeekBar seekBar;
    @InjectView(R.id.btn_setting)
    Button btnSetting;
    @InjectView(R.id.text_first)
    TextView textFirst;
    @InjectView(R.id.text_last)
    TextView textLast;

    private Tag tag;
    private Protect protect;

    private static final String EXT_TAG = "tag";
    private static final String EXT_PROTECT = "protect";

    public static void start(Activity context, int requestCode, Tag tag,
            Protect protect) {
        Intent intent = new Intent(context, ProtectSettingActivity.class);
        intent.putExtra(EXT_TAG, tag);
        intent.putExtra(EXT_PROTECT,protect);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protect_setting);
        ButterKnife.inject(this);
        tag = getIntent().getParcelableExtra(EXT_TAG);
        protect = getIntent().getParcelableExtra(EXT_PROTECT);
        List<String> items=protect.getItems();
        seekBar.setMax(items.size() - 1);
        seekBar.incrementProgressBy(1);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(Generator.getNearestIndex(tag.getTagValue(),items));
        //seek.progress初始值与index一样时，不会触发onProgressChanged
        changeShowValueWithProgress(seekBar.getProgress());
        textFirst.setText(items.get(0));
        textLast.setText(items.get(items.size()-1));
    }

    public void dismiss(View view) {
        setResult(Activity.RESULT_CANCELED);
        if (ApiLevelHelper.isAtLeast(21)) {
            finishAfterTransition();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        changeShowValueWithProgress(progress);
    }

    private void changeShowValueWithProgress(int progress) {
        String value=protect.getItems().get(progress);
        String unit=value.equals(getString(R.string.device_protect_off))?"":protect.getUnit();
        unit=unit.replace(Protect.IE,Protect.IN);
        textName.setText(tag.getTagShortName() + ":" + value+unit);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @OnClick(R.id.btn_setting)
    public void onViewClicked() {
        Intent data = new Intent();
        data.putExtra(TabProtectFragment.RESULT_TAG, textName.getText());
        setResult(RESULT_OK, data);
        finish();
    }
}
