package com.xseec.eds.model.servlet;

import android.text.TextUtils;

import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.xseec.eds.model.WAServicer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/29.
 */

public abstract class BaseModel {
    //使用英文“;”作为分隔符
    public static final String SPIT = ";";
    protected String id;
    protected String image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getImageList() {
        if (!TextUtils.isEmpty(image)) {
            List<String> target=new ArrayList<>();
            String[] source=image.split(SPIT);
            for(String s:source){
                target.add(WAServicer.getDownloadImageUrl()+s);
            }
            return target;
        } else {
            return null;
        }
    }

    public List<LocalMedia> getImageMediaList(){
        List<LocalMedia> target=new ArrayList<>();
        if (!TextUtils.isEmpty(image)) {
            String[] source=image.split(SPIT);
            for(String s:source){
                String path=WAServicer.getDownloadImageUrl()+s;
                String imageType= PictureMimeType.createImageType(path);
                int mimeType=PictureMimeType.isPictureType(imageType);
                target.add(new LocalMedia(path,0,mimeType,imageType));
            }
        }
        return target;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (TextUtils.isEmpty(id)) {
            return null;
        } else {
            stringBuilder.append("id=" + id);
        }
        return stringBuilder.toString();
    }

    public abstract String toJson();
}
