package com.xseec.eds.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.xseec.eds.R;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/17.
 */

public class PhotoPicker {

    public static void addPhotos(final Activity activity, final int maxSelectNum) {
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(maxSelectNum)
                .minSelectNum(1)
                .imageSpanCount(3)
                .compress(true)
                .minimumCompressSize(1000)
                .selectionMode(PictureConfig.MULTIPLE)
                .forResult(PictureConfig.CHOOSE_REQUEST);
//        View bottomView = View.inflate(activity, R.layout.layout_photo_dialog, null);
//        TextView textAlbum = bottomView.findViewById(R.id.text_album);
//        TextView textCamera = (TextView) bottomView.findViewById(R.id.text_camera);
//        TextView textCancel = (TextView) bottomView.findViewById(R.id.text_cancel);
//
//        final PopupWindow pop = new PopupWindow(bottomView, -1, -2);
//        pop.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        pop.setOutsideTouchable(true);
//        pop.setFocusable(true);
//        final Window window = activity.getWindow();
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.alpha = 0.5f;
//        window.setAttributes(lp);
//        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
//
//            @Override
//            public void onDismiss() {
//                WindowManager.LayoutParams lp = window.getAttributes();
//                lp.alpha = 1f;
//                window.setAttributes(lp);
//            }
//        });
//        pop.setAnimationStyle(R.style.photo_picker_dialog_anim);
//        pop.showAtLocation(window.getDecorView(), Gravity.BOTTOM, 0, 0);
//
//        View.OnClickListener clickListener = new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                switch (view.getId()) {
//                    case R.id.text_album:
//                        //相册
//                        PictureSelector.create(activity)
//                                .openGallery(PictureMimeType.ofImage())
//                                .maxSelectNum(maxSelectNum)
//                                .minSelectNum(1)
//                                .imageSpanCount(3)
//                                .compress(true)
//                                .selectionMode(PictureConfig.MULTIPLE)
//                                .forResult(PictureConfig.CHOOSE_REQUEST);
//                        break;
//                    case R.id.text_camera:
//                        //拍照
//                        PictureSelector.create(activity)
//                                .openCamera(PictureMimeType.ofImage())
//                                .forResult(PictureConfig.CHOOSE_REQUEST);
//                        break;
//                    default:
//                        break;
//                }
//                //关闭Dialog
//                if (pop != null && pop.isShowing()) {
//                    pop.dismiss();
//                }
//            }
//        };
//
//        textAlbum.setOnClickListener(clickListener);
//        textCamera.setOnClickListener(clickListener);
//        textCancel.setOnClickListener(clickListener);
    }

    public static void previewSelectedPhotos(Activity activity, List<LocalMedia> selectedList,
            int position) {
        PictureSelector.create(activity).themeStyle(R.style.picture_default_style)
                .openExternalPreview(position, selectedList);
    }

    public static void deleteCache(Context context){
        PictureFileUtils.deleteCacheDirFile(context);
    }

    public static String getFinalPath(LocalMedia media) {
        if (media.isCut() && !media.isCompressed()) {
            // 裁剪过
            return media.getCutPath();
        } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
            // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
            return media.getCompressPath();
        } else {
            // 原图
            return media.getPath();
        }
    }

    public static String getImageName(LocalMedia media, boolean old) {
        String[] tmps = getFinalPath(media).split("/");
        if (tmps != null && tmps.length > 0) {
            if (old) {
                return tmps[tmps.length - 1];
            }else {
                //新上传的图片会加前缀名称：UploadHelper
                //return "1_ADAM3600_"+tmps[tmps.length - 1];
                return WAServicer.getUser().getStorableDirName()+"_"+tmps[tmps.length - 1];
            }
        } else {
            return null;
        }
    }

    public static List<String> getLocalMedisListName(List<LocalMedia> finalList, List<LocalMedia>
            firstList) {
        List<String> target = new ArrayList<>();
        for (LocalMedia media : finalList) {
            target.add(getImageName(media,firstList.contains(media)));
        }
        return target;
    }

    public static List<String> getImageList(String image) {
        if (!TextUtils.isEmpty(image)) {
            List<String> target=new ArrayList<>();
            String[] source=image.split(BaseModel.SPIT);
            for(String s:source){
                target.add(WAServicer.getDownloadImageUrl()+s);
            }
            return target;
        } else {
            return null;
        }
    }

    public static List<LocalMedia> getImageMediaList(String image){
        List<LocalMedia> target=new ArrayList<>();
        if (!TextUtils.isEmpty(image)) {
            String[] source=image.split(BaseModel.SPIT);
            for(String s:source){
                String path=WAServicer.getDownloadImageUrl()+s;
                String imageType= PictureMimeType.createImageType(path);
                int mimeType=PictureMimeType.isPictureType(imageType);
                target.add(new LocalMedia(path,0,mimeType,imageType));
            }
        }
        return target;
    }
}
