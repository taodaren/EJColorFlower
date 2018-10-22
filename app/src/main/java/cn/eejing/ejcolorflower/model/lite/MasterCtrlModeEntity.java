package cn.eejing.ejcolorflower.model.lite;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

/**
 * 主控效果实体
 */

public class MasterCtrlModeEntity extends LitePalSupport implements Parcelable {
    private String devId;        // 主控设备ID
    private String groupName;    // 分组名称
    private String type;         // 喷射效果
    private long millis;         // 时间戳

    public MasterCtrlModeEntity() {
    }

    public MasterCtrlModeEntity(String groupName, String type, long millis) {
        this.groupName = groupName;
        this.type = type;
        this.millis = millis;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getMillis() {
        return millis;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.devId);
        dest.writeString(this.groupName);
        dest.writeString(this.type);
        dest.writeLong(this.millis);
    }

    protected MasterCtrlModeEntity(Parcel in) {
        this.devId = in.readString();
        this.groupName = in.readString();
        this.type = in.readString();
        this.millis = in.readLong();
    }

    public static final Creator<MasterCtrlModeEntity> CREATOR = new Creator<MasterCtrlModeEntity>() {
        @Override
        public MasterCtrlModeEntity createFromParcel(Parcel source) {
            return new MasterCtrlModeEntity(source);
        }

        @Override
        public MasterCtrlModeEntity[] newArray(int size) {
            return new MasterCtrlModeEntity[size];
        }
    };
}
