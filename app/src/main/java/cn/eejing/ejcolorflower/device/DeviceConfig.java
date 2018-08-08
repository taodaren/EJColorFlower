package cn.eejing.ejcolorflower.device;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Device configuration
 */

public class DeviceConfig implements Parcelable {
    public long mID;                                                        // 设备ID
    // 进料电机默认速度、刮料电机默认速度、吹风电机默认速度、备用电机默认速度
    public int[] mMotorDefaultSpeed = new int[]{20, 10, 15, 200};
    public int mTemperatureThresholdLow = 500;                              // 温度阈值（低）
    public int mTemperatureThresholdHigh = 510;                             // 温度阈值（高）
    public int mDMXAddress;                                                 // DMX地址
    public int mGualiaoTime;                                                // 刮料时间

    public DeviceConfig() {
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
        dest.writeIntArray(this.mMotorDefaultSpeed);
        dest.writeInt(this.mDMXAddress);
        dest.writeInt(this.mGualiaoTime);
    }

    private DeviceConfig(Parcel in) {
        this.mID = in.readLong();
        this.mTemperatureThresholdLow = in.readInt();
        this.mTemperatureThresholdHigh = in.readInt();
        this.mMotorDefaultSpeed = in.createIntArray();
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
