package com.xseec.eds.model.deviceconfig;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.xseec.eds.model.tags.Tag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2018/8/29.
 */

public class RealZone implements Parcelable {
    @SerializedName("Current")
    private List<String> current;
    @SerializedName("Voltage")
    private List<String> voltage;
    @SerializedName("Grid")
    private List<String> grid;
    @SerializedName("Power")
    private List<String> power;
    @SerializedName("Energy")
    private List<String> energy;
    @SerializedName("Harmonic")
    private List<String> harmonic;

    protected RealZone(Parcel in) {
        current = in.createStringArrayList();
        voltage = in.createStringArrayList();
        grid = in.createStringArrayList();
        power = in.createStringArrayList();
        energy = in.createStringArrayList();
        harmonic = in.createStringArrayList();
    }

    public static final Creator<RealZone> CREATOR = new Creator<RealZone>() {
        @Override
        public RealZone createFromParcel(Parcel in) {
            return new RealZone(in);
        }

        @Override
        public RealZone[] newArray(int size) {
            return new RealZone[size];
        }
    };

    public List<String> getCurrent() {
        return current;
    }

    public void setCurrent(List<String> current) {
        this.current = current;
    }

    public List<String> getVoltage() {
        return voltage;
    }

    public void setVoltage(List<String> voltage) {
        this.voltage = voltage;
    }

    public List<String> getGrid() {
        return grid;
    }

    public void setGrid(List<String> grid) {
        this.grid = grid;
    }

    public List<String> getPower() {
        return power;
    }

    public void setPower(List<String> power) {
        this.power = power;
    }

    public List<String> getEnergy() {
        return energy;
    }

    public void setEnergy(List<String> energy) {
        this.energy = energy;
    }

    public List<String> getHarmonic() {
        return harmonic;
    }

    public void setHarmonic(List<String> harmonic) {
        this.harmonic = harmonic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(current);
        dest.writeStringList(voltage);
        dest.writeStringList(grid);
        dest.writeStringList(power);
        dest.writeStringList(energy);
        dest.writeStringList(harmonic);
    }

    //不包含谐波
    private List<String> getAllList(){
        List<String> target=new ArrayList<>();
        target.addAll(current);
        target.addAll(voltage);
        target.addAll(grid);
        target.addAll(power);
        target.addAll(energy);
        return target;
    }

    public List<Tag> getAllTagList(String deviceSerial){
        List<String> temps=new ArrayList<>();
        temps.addAll(current);
        temps.addAll(voltage);
        temps.addAll(grid);
        temps.addAll(power);
        temps.addAll(energy);
        List<Tag> tagList=new ArrayList<>();
        for(String temp:temps){
            tagList.add(new Tag(deviceSerial+":"+temp));
        }
        return tagList;
    }
}
