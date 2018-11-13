package com.xseec.eds.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.adapter.PhotoAdapter;
import com.xseec.eds.model.servlet.UploadListener;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.util.ContentHelper;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.RecordHelper;
import com.xseec.eds.util.PhotoPicker;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


/*
    工单页面：查看、执行、分享、删除工单
    技术要点：FABProgress,PictureSelector
 */

@RuntimePermissions
public class WorkorderActivity extends BaseActivity implements UploadListener,
        FABProgressListener {


    @InjectView(R.id.image_area)
    ImageView imageArea;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.collapsing_layout)
    CollapsingToolbarLayout collapsingLayout;
    @InjectView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @InjectView(R.id.image_state)
    ImageView imageState;
    @InjectView(R.id.text_state)
    TextView textState;
    @InjectView(R.id.layout_state)
    LinearLayout layoutState;
    @InjectView(R.id.edit_log)
    EditText editLog;
    @InjectView(R.id.recycler_image)
    RecyclerView recyclerImage;
    @InjectView(R.id.layout_execute)
    LinearLayout layoutExecute;
    @InjectView(R.id.text_task)
    TextView textTask;
    @InjectView(R.id.text_range)
    TextView textRange;
    @InjectView(R.id.text_location)
    TextView textLocation;
    @InjectView(R.id.text_worker)
    TextView textWorker;
    @InjectView(R.id.text_type)
    TextView textType;
    @InjectView(R.id.text_creator)
    TextView textCreator;
    @InjectView(R.id.text_id)
    TextView textId;
    @InjectView(R.id.fab_execute)
    FloatingActionButton fabExecute;
    @InjectView(R.id.fabProgressCircle)
    FABProgressCircle fabProgressCircle;
    @InjectView(R.id.image_call)
    ImageView imageCall;

    private Workorder workorder;
    private PhotoAdapter adapter;
    private boolean editing = false;
    private List<LocalMedia> sourceImageList;
    private List<String> compressImageList;

    private static final String EXT_WORKORDER = "workorder";
    private static final int REQUEST_WORKORDER = 1;

    public static void start(Context context, Workorder workorder) {
        Intent intent = new Intent(context, WorkorderActivity.class);
        intent.putExtra(EXT_WORKORDER, workorder);
        //context.startActivity(intent);
        //返回后触发工单列表刷新
        ((Activity) context).startActivityForResult(intent, REQUEST_WORKORDER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workorder);
        ButterKnife.inject(this);
        ViewHelper.initToolbar(this, toolbar, R.drawable.ic_arrow_back_white_24dp);
        initRecycler();
        workorder = getIntent().getParcelableExtra(EXT_WORKORDER);
        initViews();
    }

    private void initRecycler() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, ViewHelper.isPort() ? 3 : 6);
        recyclerImage.setLayoutManager(layoutManager);
        adapter = new PhotoAdapter(this, new ArrayList<LocalMedia>());
        recyclerImage.setAdapter(adapter);
    }

    private void initViews() {
        setTitle(workorder.getTitle());
        imageState.setImageResource(workorder.getStateImgRes());
        textState.setText(workorder.getStateTextRes());
        textTask.setText(Workorder.getShowString(workorder.getTask()));
        textRange.setText(workorder.getDateRange());
        textLocation.setText(workorder.getLocation());
        textWorker.setText(workorder.getWorker());
        textType.setText(workorder.getTypeString());
        textCreator.setText(workorder.getCreator());
        textId.setText(workorder.getId());
        sourceImageList = PhotoPicker.getImageMediaList(workorder.getImage());
        if (workorder.getWorkorderState() == Workorder.WorkorderState.DONE) {
            layoutExecute.setVisibility(View.VISIBLE);
            editLog.setText(Workorder.getShowString(workorder.getLog()));
            adapter.addMediaList(sourceImageList);
        }
        //FABProgressListener
        fabProgressCircle.attachListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> medias = PictureSelector.obtainMultipleResult(data);
                    adapter.addMediaList(medias);
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        WorkorderActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode,
                grantResults);
    }


    @OnClick(R.id.fab_execute)
    public void onViewClicked() {
        if (!editing) {
            WorkorderActivityPermissionsDispatcher.prepareEditWithPermissionCheck
                    (WorkorderActivity.this);
        } else {
            editing = false;
            disenableEdit();
            fabProgressCircle.show();
            uploadWorkorder();
        }
        setCheckExit(editing,getString(R.string.workorder_exit_confirm));
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE})
    void prepareEdit() {
        editing = true;
        if (layoutExecute.getVisibility() != View.VISIBLE) {
            layoutExecute.setVisibility(View.VISIBLE);
        }
        //将工单日志中时间删除
        editLog.setText(editLog.getText().toString().replaceAll(DateHelper.dateRegex, ""));
        editLog.setFocusableInTouchMode(true);
        editLog.requestFocus();
        editLog.setSelection(editLog.getText().length());
        fabExecute.setImageResource(R.drawable.ic_cloud_upload_white_24dp);
        adapter.setAddable(true);
    }

/*
    执行工单后上传：图片压缩上传，其他文本上传，上传动画
 */

    private void uploadWorkorder() {
        //跟sourceImage对比，只上传新增图片
        compressImageList = new ArrayList<>();
        for (LocalMedia media : adapter.getLocalMediaList()) {
            if (!sourceImageList.contains(media)) {
                compressImageList.add(media.getCompressPath());
            }
        }
        if (compressImageList.size() > 0) {
            WAServiceHelper.uploadImage(compressImageList, this);
        } else {
            onImageUploaded(null);
        }
    }

    @Override
    public void onImageUploaded(String response) {
        List<String> imageNames = PhotoPicker.getLocalMedisListName(adapter.getLocalMediaList(),
                sourceImageList);
        //判断数组长度为0时，image="",在Servlet中收到空值，不更新数据库imagelie,故变为：image="null"
        String image =imageNames.size()==0?null: TextUtils.join(Workorder.SPIT, imageNames);
        workorder.setImage(image);
        String log = editLog.getText().toString() + "\n" + DateHelper.getString(new Date());
        workorder.setLog(Workorder.getServletString(log));
        workorder.setStateDone();
        WAServiceHelper.sendWorkorderUpdateRequest(workorder, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

                //nj--工单执行操作记录 18/11/5
                String actionInfo=getString( R.string.action_workorder_execute,workorder.getTitle());
                RecordHelper.actionLog( actionInfo );

                onWorkorderUploaded();
            }
        });
    }

    private void onWorkorderUploaded() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fabProgressCircle.beginFinalAnimation();
            }
        });
    }

    @Override
    public void onFABProgressAnimationEnd() {
        fabExecute.setImageResource(R.drawable.ic_edit_white_24dp);
        imageState.setImageResource(workorder.getStateImgRes());
        textState.setText(workorder.getStateTextRes());
        editLog.setText(Workorder.getShowString(workorder.getLog()));
    }

    @Override
    public void onImageUploadError(String response) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.workorder_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                ContentHelper.shareMessage(this, workorder.toShare());
                break;
            case R.id.menu_delete:
                checkDeleteWorkorder();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkDeleteWorkorder() {
        ViewHelper.checkExit(this, getString(R.string.delete_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nj--添加删除工单记录的名称
                final String actionInfo=getString( R.string.action_workorder_delet,workorder.getTitle());

                workorder.setTitle(null);
                WAServiceHelper.sendWorkorderUpdateRequest(workorder, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {

                        //nj--执行工单删除记录操作 18/11/5
                        RecordHelper.actionLog( actionInfo );
                        finish();
                    }
                });
            }
        });
    }

    private void disenableEdit() {
        adapter.setAddable(false);
        editLog.setFocusable(false);
    }

    @OnClick(R.id.image_call)
    public void onImageViewClicked() {
        ContentHelper.callPhone(this, workorder.getWorker());
    }
}
