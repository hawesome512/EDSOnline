package com.xseec.eds.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.xseec.eds.R;
import com.xseec.eds.model.QrCode;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;
import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.bean.ZxingConfig;
import com.yzq.zxinglibrary.common.Constant;
import com.yzq.zxinglibrary.encode.CodeCreator;

import static android.app.Activity.RESULT_OK;

public class QrCodeHelper {

    public static final int REQUEST_CODE_SCAN=1;

    public static void scan(Activity activity){
        Intent intent=new Intent(activity, CaptureActivity.class);
        activity.startActivityForResult(intent,REQUEST_CODE_SCAN);
    }

    public static void customScan(Activity activity){
        Intent intent = new Intent(activity, CaptureActivity.class);
        /*ZxingConfig是配置类
         *可以设置是否显示底部布局，闪光灯，相册，
         * 是否播放提示音  震动
         * 设置扫描框颜色等
         * 也可以不传这个参数
         * */
        ZxingConfig config = new ZxingConfig();
        config.setPlayBeep(true);//是否播放扫描声音 默认为true
        config.setShake(true);//是否震动  默认为true
        config.setDecodeBarCode(true);//是否扫描条形码 默认为true
        config.setReactColor(R.color.colorAccent);//设置扫描框四个角的颜色 默认为白色
        config.setFrameLineColor(R.color.colorAccent);//设置扫描框边框颜色 默认无色
        config.setScanLineColor(R.color.colorAccent);//设置扫描线的颜色 默认白色
        config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
        intent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
        activity.startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    public static String getScanResult(int requestCode, int resultCode, Intent data){
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                return data.getStringExtra(Constant.CODED_CONTENT);
            }
        }
        return null;
    }

    public static Bitmap getQrCode(QrCode.QrCodeType qrCodeTypes,String param){
        Context context=EDSApplication.getContext();
        User user= WAServicer.getUser();
        String content=context.getString(R.string.qr_format,user.getDeviceName(),user.getAuthority(), qrCodeTypes.ordinal(),param);
        Bitmap logo = drawableToBitmap(context.getResources().getDrawable(R.mipmap.ic_launcher));
        return CodeCreator.createQRCode(content, 400, 400, logo);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
