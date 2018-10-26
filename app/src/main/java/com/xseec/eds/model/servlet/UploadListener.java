package com.xseec.eds.model.servlet;

/**
 * Created by Administrator on 2018/10/18.
 */

public interface UploadListener {
    void onImageUploaded(String response);
    void onImageUploadError(String response);
}
