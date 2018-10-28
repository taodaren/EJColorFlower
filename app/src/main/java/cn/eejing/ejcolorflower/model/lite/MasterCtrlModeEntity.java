package cn.eejing.ejcolorflower.model.lite;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

/**
 * 主控效果实体
 */

public class MasterCtrlModeEntity extends LitePalSupport implements Parcelable {
    private String devId;              // 主控设备ID
    private String groupName;          // 分组名称
    private String type;               // 喷射效果 有效果 无效果
    private long groupIdMillis;        // 时间戳-当分组ID使用

    public MasterCtrlModeEntity() {
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

    public long getGroupIdMillis() {
        return groupIdMillis;
    }

    public void setGroupIdMillis(long groupIdMillis) {
        this.groupIdMillis = groupIdMillis;
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
        dest.writeLong(this.groupIdMillis);
    }

    protected MasterCtrlModeEntity(Parcel in) {
        this.devId = in.readString();
        this.groupName = in.readString();
        this.type = in.readString();
        this.groupIdMillis = in.readLong();
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
