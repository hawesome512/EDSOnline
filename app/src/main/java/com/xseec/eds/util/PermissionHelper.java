package com.xseec.eds.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.xseec.eds.R;

/**
 * Created by Administrator on 2018/10/12.
 */

public class PermissionHelper {

    public static final int CODE_READ_CONTACTS = 1;

    public static boolean checkPermission(Activity activity, String permission, int requestCode) {
        if (ApiLevelHelper.isAtLeast(23) && ContextCompat.checkSelfPermission(activity,
                permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
            return false;
        } else {
            return true;
        }
    }

    public static void onPermissionRequested(View view, int grantResult) {
        int msgId = grantResult == PackageManager.PERMISSION_GRANTED ? R.string
                .permission_checked : R.string.permission_unchecked;
        Snackbar.make(view, msgId, Snackbar.LENGTH_SHORT).show();
    }
}
