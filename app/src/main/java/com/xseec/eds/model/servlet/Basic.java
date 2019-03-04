package com.xseec.eds.model.servlet;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.xseec.eds.R;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.tags.EnergyTag;
import com.xseec.eds.util.EDSApplication;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/10/29.
 */

public class Basic extends BaseModel implements Parcelable {

    private static final String SPLIT = ";";
    private static final String ALIAS_SPLIT = "-";

    private String user;
    private String banner;
    private String pricipal;
    private String location;
    private String image;
    private String alias;
    private String energy;

    public Basic() {
        Context context = EDSApplication.getContext();
        this.id = WAServicer.getUser().getDeviceName();
        this.user = context.getString(R.string.app_name);
        this.banner = "xs.jpg";
    }

    public Basic(String id) {
        this.id = id;
    }

    protected Basic(Parcel in) {
        id = in.readString();
        user = in.readString();
        banner = in.readString();
        pricipal = in.readString();
        location = in.readString();
        image = in.readString();
        alias = in.readString();
        energy=in.readString();
    }

    public static final Creator<Basic> CREATOR = new Creator<Basic>() {
        @Override
        public Basic createFromParcel(Parcel in) {
            return new Basic(in);
        }

        @Override
        public Basic[] newArray(int size) {
            return new Basic[size];
        }
    };

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBanner() {
        return banner;
    }

    public String getBannerUrl() {
        return WAServicer.getDownloadImageUrl() + banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getPricipal() {
        return pricipal;
    }

    public void setPricipal(String pricipal) {
        this.pricipal = pricipal;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public static List<EnergyTag> getEnergyTagList(String energy){
        String[] energies=energy.split(SPLIT);
        List<EnergyTag> tags=new ArrayList<>();
        if(energies!=null){
            for(String info:energies){
                String[] infos=EnergyTag.getInfos(info);
                if(infos.length==EnergyTag.VALID_INFOS_LENGTH){
                    tags.add(new EnergyTag(infos));
                }
            }
        }
        return tags;
    }

    @Override
    public String toJson() {
        return EDSApplication.getContext().getString(R.string.svl_basic_request, id, user, banner, pricipal, location, image, alias,energy);
    }

    public LinkedHashMap<String, String> getAliasMap() {
        LinkedHashMap map = new LinkedHashMap();
        if (alias != null) {
            String[] items = alias.split(SPLIT);
            for (String item : items) {
                String[] kv = item.split(ALIAS_SPLIT);
                if (kv.length == 2) {
                    map.put(kv[0], kv[1]);
                }
            }
        }
        return map;
    }

    public void setAlias(LinkedHashMap<String, String> map) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(entry.getKey() + ALIAS_SPLIT + entry.getValue() + SPLIT);
        }
        setAlias(builder.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user);
        dest.writeString(banner);
        dest.writeString(pricipal);
        dest.writeString(location);
        dest.writeString(image);
        dest.writeString(alias);
        dest.writeString(energy);
    }
}
