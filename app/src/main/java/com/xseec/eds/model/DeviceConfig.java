package com.xseec.eds.model;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/8/23.
 */

public class DeviceConfig {

    @SerializedName("Type")
    private String deviceType;
    @SerializedName("Name")
    private String deviceName;
    @SerializedName("Real")
    private RealZone realZone;
    @SerializedName("Protect")
    private List<Protect> protectZone;
    @SerializedName("Control")
    private String farControl;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public RealZone getRealZone() {
        return realZone;
    }

    public void setRealZone(RealZone realZone) {
        this.realZone = realZone;
    }

    public List<Protect> getProtectZone() {
        return protectZone;
    }

    public void setProtectZone(List<Protect> protectZone) {
        this.protectZone = protectZone;
    }

    public String getFarControl() {
        return farControl;
    }

    public void setFarControl(String farControl) {
        this.farControl = farControl;
    }

    public class RealZone {
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
    }

    public class Protect {

        @SerializedName("Name")
        private String name;
        @SerializedName("Items")
        private List<String> items;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getItems() {
            return items;
        }

        public void setItems(List<String> items) {
            this.items = items;
        }
    }
}
