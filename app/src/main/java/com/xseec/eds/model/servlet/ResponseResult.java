package com.xseec.eds.model.servlet;

/**
 * Created by Administrator on 2018/10/12.
 */

public class ResponseResult {


    //更新Servlet返回值:-1→更新失败，0→数据库异常操作，1→更新成功
    private int resultCode;
    private String message;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess(){
        return resultCode==1;
    }
}
