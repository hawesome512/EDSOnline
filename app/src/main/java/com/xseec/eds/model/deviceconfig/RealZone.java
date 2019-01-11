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
    @SerializedName("Source1")
    private List<String> source1;
    @SerializedName("Source2")
    private List<String> source2;

    protected RealZone(Parcel in) {
        current = in.createStringArrayList();
        voltage = in.createStringArrayList();
        grid = in.createStringArrayList();
        power = in.createStringArrayList();
        energy = in.createStringArrayList();
        harmonic = in.createStringArrayList();
        source1=in.createStringArrayList();
        source2=in.createStringArrayList();
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

    public List<String> getSource1() {
        return source1;
    }

    public void setSource1(List<String> source1) {
        this.source1 = source1;
    }

    public List<String> getSource2() {
        return source2;
    }

    public void setSource2(List<String> source2) {
        this.source2 = source2;
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
        dest.writeStringList(source1);
        dest.writeStringList(source2);
    }

    public List<Tag> getAllTagList(String deviceSerial){
        List<String> temps=new ArrayList<>();
        temps.addAll(current);
        temps.addAll(voltage);
        temps.addAll(grid);
        temps.addAll(power);
        temps.addAll(energy);
        temps.addAll(source1);
        temps.addAll(source2);
        List<Tag> tagList=new ArrayList<>();
        for(String temp:temps){
            tagList.add(new Tag(deviceSerial+":"+temp));
        }
        return tagList;
    }
}
