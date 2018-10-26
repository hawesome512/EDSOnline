package com.xseec.eds.util;

import android.text.TextUtils;

import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.UploadListener;
import com.xseec.eds.model.servlet.Workorder;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/9.
 */

public class UploadHelper {

    /*
     * 文件上传
     *
     * @param urlStr 接口路径
     * @param filePath 本地图片路径
     * @return
     */
    public static void formUpload(final String urlStr, final List<String> filePaths, final
    UploadListener uploadListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String rsp = "";
                HttpURLConnection conn = null;
                String BOUNDARY = "|"; // request头和上传文件内容分隔符
                List<String> stroedFilenames = new ArrayList<>();

                for (String filePath : filePaths) {
                    try {
                        URL url = new URL(urlStr);
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(5000);
                        conn.setReadTimeout(30000);
                        conn.setDoOutput(true);
                        conn.setDoInput(true);
                        conn.setUseCaches(false);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("User-Agent",
                                "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data; boundary=" + BOUNDARY);
                        OutputStream out = new DataOutputStream(conn.getOutputStream());
                        File file = new File(filePath);
                        String filename = file.getName();
                        //PhotoPicker需与此处对应
                        String storedFilename = WAServicer.getUser().getStorableDirName() + "_" + filename;
                        //String storedFilename = "1_ADAM3600_" + filename;
                        String contentType = "";
                        if (filename.endsWith(".png")) {
                            contentType = "image/png";
                        } else if (filename.endsWith(".jpg")) {
                            contentType = "image/jpg";
                        } else if (filename.endsWith(".gif")) {
                            contentType = "image/gif";
                        } else if (filename.endsWith(".bmp")) {
                            contentType = "image/bmp";
                        }
                        if (contentType == null || contentType.equals("")) {
                            contentType = "application/octet-stream";
                        }
                        StringBuffer strBuf = new StringBuffer();
                        strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                        strBuf.append("Content-Disposition: form-data; name=\"" + filePath
                                + "\"; filename=\"" + storedFilename + "\"\r\n");
//                        strBuf.append("Content-Disposition: form-data; name=\"" + filePath
//                                + "\"; filename=\"" + filename + "\"\r\n");
                        strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                        out.write(strBuf.toString().getBytes());
                        DataInputStream in = new DataInputStream(new FileInputStream(file));
                        int bytes = 0;
                        byte[] bufferOut = new byte[1024];
                        while ((bytes = in.read(bufferOut)) != -1) {
                            out.write(bufferOut, 0, bytes);
                        }
                        in.close();
                        byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
                        out.write(endData);
                        out.flush();
                        out.close();

                        // 读取返回数据
                        StringBuffer buffer = new StringBuffer();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn
                                .getInputStream
                                        (), "utf-8"));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line).append("\n");
                        }
                        rsp = buffer.toString();
                        reader.close();
                        reader = null;
                        stroedFilenames.add(storedFilename);
                    } catch (Exception e) {
                        e.printStackTrace();
                        uploadListener.onImageUploadError(e.getMessage());
                    } finally {
                        if (conn != null) {
                            conn.disconnect();
                            conn = null;
                        }
                    }
                }
                uploadListener.onImageUploaded(TextUtils.join(Workorder.SPIT, stroedFilenames));
            }
        }).start();
    }
}
