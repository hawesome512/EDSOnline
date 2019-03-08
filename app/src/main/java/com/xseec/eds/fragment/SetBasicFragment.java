package com.xseec.eds.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.adapter.AliasAdapter;
import com.xseec.eds.adapter.PhotoAdapter;
import com.xseec.eds.model.Custom;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.BaseModel;
import com.xseec.eds.model.servlet.Basic;
import com.xseec.eds.model.servlet.UploadListener;
import com.xseec.eds.util.PhotoPicker;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SetBasicFragment extends BaseFragment implements UploadListener {

    private final static String KEY_CUSTOM_TYPE = "customType";
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.edit_area)
    EditText editArea;
    @InjectView(R.id.edit_engineer)
    EditText editEngineer;
    @InjectView(R.id.edit_location)
    EditText editLocation;
    @InjectView(R.id.recycler_image)
    RecyclerView recyclerImage;
    @InjectView(R.id.recycler_device)
    RecyclerView recyclerDevice;
    @InjectView(R.id.btn_save)
    Button btnSave;
    @InjectView(R.id.progress)
    ProgressBar progress;
    @InjectView( R.id.layout_area )
    View layoutArea;

    private PhotoAdapter photoAdapter;
    private AliasAdapter aliasAdapter;
    private List<LocalMedia> sourceImageList;
    private Custom custom;
    private Basic basic;

    public static Fragment newInstance(Custom custom) {
        Bundle bundle = new Bundle();
        Fragment fragment = new SetBasicFragment();
        bundle.putParcelable( KEY_CUSTOM_TYPE, custom );
        fragment.setArguments( bundle );
        return fragment;
    }

    public SetBasicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_set_basic, container, false );
        ButterKnife.inject( this, view );
        custom=getArguments().getParcelable( KEY_CUSTOM_TYPE );
        ViewHelper.initToolbar( (AppCompatActivity) getActivity(), toolbar, R.drawable.ic_arrow_back_white_24dp );
        getActivity().setTitle( custom.getNameRes() );

        basic = WAServicer.getBasic();
        editArea.setText( basic.getUser() );
        editEngineer.setText( basic.getPricipal() );
        editLocation.setText( basic.getLocation() );
        GridLayoutManager layoutManager = new GridLayoutManager( getContext(), ViewHelper.isPort()
                ? 3 : 6 );
        recyclerImage.setLayoutManager( layoutManager );
        sourceImageList = PhotoPicker.getImageMediaList( basic.getImage() );
        photoAdapter = new PhotoAdapter( getActivity(), new ArrayList<LocalMedia>() );
        photoAdapter.addMediaList( sourceImageList );
        photoAdapter.setAddable( true );
        recyclerImage.setAdapter( photoAdapter );

        aliasAdapter = new AliasAdapter( getContext(), basic.getAliasMap() );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getContext() );
        recyclerDevice.setLayoutManager( linearLayoutManager );
        recyclerDevice.setAdapter( aliasAdapter );
        initLayout();
        return view;
    }

    private void initLayout(){
        layoutArea.setVisibility( View.GONE );
        recyclerDevice.setVisibility( View.GONE );
        switch (custom.getCustomType()){
            case AREA:
                layoutArea.setVisibility( View.VISIBLE );
                break;
            case ALIAS:
                recyclerDevice.setVisibility( View.VISIBLE );
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> medias = PictureSelector.obtainMultipleResult( data );
                    photoAdapter.addMediaList( medias );
                    break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset( this );
    }

    @OnClick(R.id.btn_save)
    public void onBtnSaveClicked() {
        ViewHelper.startViewAnimator( btnSave );
        uploadBasic();
    }

    private void uploadBasic() {
        //跟sourceImage对比，只上传新增图片
        List<String> compressImageList = new ArrayList<>();
        for (LocalMedia media : photoAdapter.getLocalMediaList()) {
            if (!sourceImageList.contains( media )) {
                compressImageList.add( media.getCompressPath() );
            }
        }
        if (compressImageList.size() > 0) {
            WAServiceHelper.uploadImage( compressImageList, this );
        } else {
            onImageUploaded( null );
        }
    }

    @Override
    public void onImageUploaded(String response) {
        basic.setUser( editArea.getText().toString() );
        basic.setLocation( editLocation.getText().toString() );
        basic.setPricipal( editEngineer.getText().toString() );
        List<String> imageNames = PhotoPicker.getLocalMedisListName( photoAdapter
                        .getLocalMediaList(),
                sourceImageList );
        //判断数组长度为0时，image="",在Servlet中收到空值，不更新数据库imagelie,故变为：image="null"
        String image = imageNames.size() == 0 ? null : TextUtils.join( BaseModel.SPIT, imageNames );
        basic.setImage( image );
        basic.setAlias( aliasAdapter.getAliasMap() );
        WAServiceHelper.sendBasicUpdateRequest( basic, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                refreshViewsInThread( response );
            }
        } );
    }

    @Override
    public void onImageUploadError(String response) {

    }

    @Override
    protected void onRefreshViews(String jsonData) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                String deviceName = WAServicer.getUser().getDeviceName();
                final Basic basic = WAJsonHelper.getBasicList( WAServiceHelper
                        .getBaiscQueryRequest( deviceName ) );
                WAServicer.setBasic( basic );
                getActivity().finish();
            }
        } ).start();
    }
}
