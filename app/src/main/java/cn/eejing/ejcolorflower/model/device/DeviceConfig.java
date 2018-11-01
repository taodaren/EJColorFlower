package cn.eejing.ejcolorflower.model.device;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Device configuration
 */

public class DeviceConfig implements Parcelable {
//    // 进料电机默认速度、刮料电机默认速度、吹风电机默认速度、备用电机默认速度
//    private int[] mMotorDefaultSpeed = new int[]{20, 10, 15, 200};
    private long mID;                                   // 设备ID
    private int feedDefVelocity;                        // 进料电机默认速度
    private int scrapeDefVelocity;                      // 刮料电机默认速度
    private int windDefVelocity;                        // 吹风电机默认速度
    private int backupDefVelocity;                      // 备用电机默认速度
    private int mTemperatureThresholdLow;               // 温度阈值（低）
    private int mTemperatureThresholdHigh;              // 温度阈值（高）
    private int mDMXAddress;                            // DMX地址
    private int mGualiaoTime;                           // 刮料时间

    public DeviceConfig() {
    }

    public long getID() {
        return mID;
    }

    public void setID(long mID) {
        this.mID = mID;
    }

    public int getFeedDefVelocity() {
        return feedDefVelocity;
    }

    public void setFeedDefVelocity(int feedDefVelocity) {
        this.feedDefVelocity = feedDefVelocity;
    }

    public int getScrapeDefVelocity() {
        return scrapeDefVelocity;
    }

    public void setScrapeDefVelocity(int scrapeDefVelocity) {
        this.scrapeDefVelocity = scrapeDefVelocity;
    }

    public int getWindDefVelocity() {
        return windDefVelocity;
    }

    public void setWindDefVelocity(int windDefVelocity) {
        this.windDefVelocity = windDefVelocity;
    }

    public int getBackupDefVelocity() {
        return backupDefVelocity;
    }

    public void setBackupDefVelocity(int backupDefVelocity) {
        this.backupDefVelocity = backupDefVelocity;
    }

    public int getTemperatureThresholdLow() {
        return mTemperatureThresholdLow;
    }

    public void setTemperatureThresholdLow(int temperatureThresholdLow) {
        this.mTemperatureThresholdLow = temperatureThresholdLow;
    }

    public int getTemperatureThresholdHigh() {
        return mTemperatureThresholdHigh;
    }

    public void setTemperatureThresholdHigh(int temperatureThresholdHigh) {
        this.mTemperatureThresholdHigh = temperatureThresholdHigh;
    }

    public int getDMXAddress() {
        return mDMXAddress;
    }

    public void setDMXAddress(int dmxAddress) {
        this.mDMXAddress = dmxAddress;
    }

    public int getGualiaoTime() {
        return mGualiaoTime;
    }

    public void setGualiaoTime(int gualiaoTime) {
        this.mGualiaoTime = gualiaoTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mID);
        dest.writeInt(this.mTemperatureThresholdLow);
        dest.writeInt(this.mTemperatureThresholdHigh);
        dest.writeInt(this.feedDefVelocity);
        dest.writeInt(this.scrapeDefVelocity);
        dest.writeInt(this.windDefVelocity);
        dest.writeInt(this.backupDefVelocity);
        dest.writeInt(this.mDMXAddress);
        dest.writeInt(this.mGualiaoTime);
    }

    private DeviceConfig(Parcel in) {
        this.mID = in.readLong();
        this.mTemperatureThresholdLow = in.readInt();
        this.mTemperatureThresholdHigh = in.readInt();
        this.feedDefVelocity = in.readInt();
        this.scrapeDefVelocity = in.readInt();
        this.windDefVelocity = in.readInt();
        this.backupDefVelocity = in.readInt();
        this.mDMXAddress = in.readInt();
        this.mGualiaoTime = in.readInt();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DeviceConfig> CREATOR = new Parcelable.Creator<DeviceConfig>() {
        @Override
        public DeviceConfig createFromParcel(Parcel source) {
            return new DeviceConfig(source);
        }

        @Override
        public DeviceConfig[] newArray(int size) {
            return new DeviceConfig[size];
        }
    };
}
