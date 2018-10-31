package cn.eejing.ejcolorflower.device;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * DeviceStatus information
 */

public class DeviceStatus implements Parcelable {
    //    private int[] mMotorSpeed = new int[]{20, 10, 15, 200};  // 电机1速度，电机2速度，电机3速度，电机4速度
    private int mTemperature = -40;              // 温度
    private float mSupplyVoltage = 12;           // 直流供电电压
    private int mMotorSpeed1;                    // 电机1速度
    private int mMotorSpeed2;                    // 电机2速度
    private int mMotorSpeed3;                    // 电机3速度
    private int mMotorSpeed4;                    // 电机4速度
    private int mPitch = 0;                      // 倾斜角
    private int mUltrasonicDistance = 60;        // 超声波距离
    private int mInfraredDistance = 60;          // 红外距离
    private int mRestTime = -1;                  // 剩余时间

    public DeviceStatus() {
    }

    public int getTemperature() {
        return mTemperature;
    }

    public void setTemperature(int temperature) {
        this.mTemperature = temperature;
    }

    public float getSupplyVoltage() {
        return mSupplyVoltage;
    }

    public void setSupplyVoltage(float supplyVoltage) {
        this.mSupplyVoltage = supplyVoltage;
    }

    public int getMotorSpeed1() {
        return mMotorSpeed1;
    }

    public void setMotorSpeed1(int motorSpeed1) {
        this.mMotorSpeed1 = motorSpeed1;
    }

    public int getMotorSpeed2() {
        return mMotorSpeed2;
    }

    public void setMotorSpeed2(int motorSpeed2) {
        this.mMotorSpeed2 = motorSpeed2;
    }

    public int getMotorSpeed3() {
        return mMotorSpeed3;
    }

    public void setMotorSpeed3(int motorSpeed3) {
        this.mMotorSpeed3 = motorSpeed3;
    }

    public int getMotorSpeed4() {
        return mMotorSpeed4;
    }

    public void setMotorSpeed4(int motorSpeed4) {
        this.mMotorSpeed4 = motorSpeed4;
    }

    public int getPitch() {
        return mPitch;
    }

    public void setPitch(int pitch) {
        this.mPitch = pitch;
    }

    public int getUltrasonicDistance() {
        return mUltrasonicDistance;
    }

    public void setUltrasonicDistance(int ultrasonicDistance) {
        this.mUltrasonicDistance = ultrasonicDistance;
    }

    public int getInfraredDistance() {
        return mInfraredDistance;
    }

    public void setInfraredDistance(int infraredDistance) {
        this.mInfraredDistance = infraredDistance;
    }

    public int getRestTime() {
        return mRestTime;
    }

    public void setRestTime(int restTime) {
        this.mRestTime = restTime;
    }

    public static final Creator<DeviceStatus> CREATOR = new Creator<DeviceStatus>() {
        @Override
        public DeviceStatus createFromParcel(Parcel in) {
            return new DeviceStatus(in);
        }

        @Override
        public DeviceStatus[] newArray(int size) {
            return new DeviceStatus[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mTemperature);
        dest.writeFloat(this.mSupplyVoltage);
        dest.writeInt(this.mMotorSpeed1);
        dest.writeInt(this.mMotorSpeed2);
        dest.writeInt(this.mMotorSpeed3);
        dest.writeInt(this.mMotorSpeed4);
        dest.writeInt(this.mPitch);
        dest.writeInt(this.mUltrasonicDistance);
        dest.writeInt(this.mInfraredDistance);
        dest.writeInt(this.mRestTime);
    }

    public String strOfRestTime() {
        int sec = mRestTime % 60;
        int min = (mRestTime / 60) % 60;
        int hour = (mRestTime / 3600) % 60;
        StringBuilder sb = new StringBuilder();
        if (hour > 0) {
            sb.append(String.valueOf(hour));
            sb.append(" 小时 ");
        }
        if (min > 0) {
            sb.append(String.valueOf(min));
            sb.append(" 分 ");
        }
        sb.append(String.valueOf(sec));
        sb.append(" 秒");
        return sb.toString();
    }

    private DeviceStatus(Parcel in) {
        this.mTemperature = in.readInt();
        this.mSupplyVoltage = in.readFloat();
        this.mMotorSpeed1 = in.readInt();
        this.mMotorSpeed2 = in.readInt();
        this.mMotorSpeed3 = in.readInt();
        this.mMotorSpeed4 = in.readInt();
        this.mPitch = in.readInt();
        this.mUltrasonicDistance = in.readInt();
        this.mInfraredDistance = in.readInt();
        this.mRestTime = in.readInt();
    }
}
