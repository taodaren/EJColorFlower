package cn.eejing.ejcolorflower.model.lite;

import android.support.annotation.NonNull;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.eejing.ejcolorflower.model.manager.MgrIntervalJet;
import cn.eejing.ejcolorflower.model.manager.MgrOutputJet;
import cn.eejing.ejcolorflower.model.manager.MgrRideJet;
import cn.eejing.ejcolorflower.model.manager.MgrStreamJet;
import cn.eejing.ejcolorflower.model.manager.MgrTogetherJet;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_DELAY;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;
import static cn.eejing.ejcolorflower.app.AppConstant.CURRENT_TIME;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_HIGH;
import static cn.eejing.ejcolorflower.app.AppConstant.INIT_ZERO;
import static cn.eejing.ejcolorflower.app.AppConstant.LOOP_ID;

/**
 * 主控分组信息实体类【数据库保存】
 */

public class MasterGroupLite extends LitePalSupport implements Serializable {
    private String devId;                           // 设备ID
    private String memberId;                        // 会员ID
    private String groupName;                       // 分组名称
    private long groupIdMillis;                     // 分组ID-时间戳
    private int isSelectedGroup;                    // 是否选中分组 1-选中 2-未选中
    private int isSelectedMaster;                   // 是否选中包含主控 1-选中 2-未选中
    private int jetTime;                            // 喷射时间
    private int devNum;                             // 设备数量
    private int startDmx;                           // 起始DMX

    private List<JetModeConfigLite> jetModes;       // 喷射效果列表

    private List<MgrOutputJet> mListMstCtrlMgr;     // 喷射管理列表
    private MgrOutputJet mCurrentManager;           // 当前喷射效果管理

    public MasterGroupLite() {
    }

    public void makeJetMgrs() {
        createMstCtrlMgrList();

        dataOut = new byte[devNum + 2];
        mCurrMasterId = INIT_ZERO;
        mFlagCurrId = INIT_ZERO;
        mCurrentManager = mListMstCtrlMgr.get(INIT_ZERO);
    }

    public int getOutDmxAddrMin() {
        return startDmx;
    }

    public int getOutDmxAddrMax() {
        return startDmx + devNum * 2 - 2; // - ( (isSelectedMaster==1)?2:0 )
    }

    public int mCurrMasterId, mFlagCurrId;               // 当前主控喷射效果 ID 及标志位
    private byte[] dataOut;

    public int getCurJettiingDevCnt(){
        if ( mCurrentManager!=null ){
            return mCurrentManager.getDevCount();
        }
        return 0;
    }
    public byte[] updateWithDataOut() {
        // 如果如果当前运行的 ID 大于或等于主控管理集合的最大数量，喷射停止
        if (mCurrMasterId >= mListMstCtrlMgr.size()) {
            return null;
        }

        // 如果当前运行的 ID 与 flag 不同，让其相等
        if (mFlagCurrId != mCurrMasterId) {
            mFlagCurrId = mCurrMasterId;
            mCurrentManager = mListMstCtrlMgr.get(mCurrMasterId);
        }

        // 发送进入在线实时控制模式命令
        // 调用方法判断当前组是否完成喷射
        boolean isFinish = false;
        if (mCurrentManager != null) {
            isFinish = mCurrentManager.updateWithDataOut(dataOut);
        } else {
            mCurrentManager = mListMstCtrlMgr.get(0);
            isFinish = mCurrentManager.updateWithDataOut(dataOut);
        }

        if (isFinish) {
            // 当前组喷射完成，进入到下一组，继续执行下一组
            mCurrMasterId++;
            if (mCurrMasterId >= mListMstCtrlMgr.size()) {
                //Log.i(JET, "终于喷完了！！！");

//                // 设置不可编辑状态
//                imgMasterMode.setClickable(false);
//                etStarDmx.setFocusable(false);
//                etDevNum.setFocusable(false);
//                etStarDmx.setFocusableInTouchMode(false);
//                etDevNum.setFocusableInTouchMode(false);
            }
        }
        return dataOut;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public long getGroupIdMillis() {
        return groupIdMillis;
    }

    public void setGroupIdMillis(long groupIdMillis) {
        this.groupIdMillis = groupIdMillis;
    }

    public int getIsSelectedGroup() {
        return isSelectedGroup;
    }

    public void setIsSelectedGroup(int isSelectedGroup) {
        this.isSelectedGroup = isSelectedGroup;
    }

    public int getIsSelectedMaster() {
        return isSelectedMaster;
    }

    public void setIsSelectedMaster(int isSelectedMaster) {
        this.isSelectedMaster = isSelectedMaster;
    }

    public int getJetTime() {
        return jetTime;
    }

    public void setJetTime(int jetTime) {
        this.jetTime = jetTime;
    }

    public int getDevNum() {
        return devNum;
    }

    public void setDevNum(int devNum) {
        this.devNum = devNum;
    }

    public int getStartDmx() {
        return startDmx;
    }

    public void setStartDmx(int startDmx) {
        this.startDmx = startDmx;
    }

    public List<JetModeConfigLite> getJetModes() {
        return jetModes;
    }

    public void setJetModes(List<JetModeConfigLite> jetModes) {
        if (jetModes == null) {
            this.jetModes = new ArrayList<>();
        } else {
            this.jetModes = jetModes;
        }
    }

    /** 创建主控管理列表集合 */
    private void createMstCtrlMgrList() {
        mListMstCtrlMgr = new ArrayList<>();

        // 给主控管理集合添加数据
        for (int i = 0; i < jetModes.size(); i++) {
            switch (jetModes.get(i).getJetType()) {
                case CONFIG_STREAM:
                    mListMstCtrlMgr.add(setDataWithStream(jetModes.get(i)));
                    break;
                case CONFIG_RIDE:
                    mListMstCtrlMgr.add(setDataWithRide(jetModes.get(i)));
                    break;
                case CONFIG_INTERVAL:
                    mListMstCtrlMgr.add(setDataWithInterval(jetModes.get(i)));
                    break;
                case CONFIG_TOGETHER:
                    mListMstCtrlMgr.add(setDataWithTogether(jetModes.get(i)));
                    break;
                case CONFIG_DELAY:
                    mListMstCtrlMgr.add(setDataWithTogether(jetModes.get(i)));
                    break;
                default:
                    break;
            }
        }
    }

    @NonNull
    private MgrOutputJet setDataWithStream(JetModeConfigLite lite) {
        MgrStreamJet mgrStream = new MgrStreamJet();
        mgrStream.setType(CONFIG_STREAM);
        mgrStream.setDevCount(devNum + ((isSelectedMaster == 1) ? 1 : 0));
        mgrStream.setCurrentTime(CURRENT_TIME);
        mgrStream.setLoopId(LOOP_ID);
        mgrStream.setHigh((byte) Integer.parseInt(DEFAULT_HIGH));
        mgrStream.setDirection(Integer.parseInt(lite.getDirection()));
        mgrStream.setGap((int) (Float.parseFloat(lite.getGap()) * 10));
        mgrStream.setDuration((int) (Float.parseFloat(lite.getDuration()) * 10));
        mgrStream.setGapBig((int) (Float.parseFloat(lite.getBigGap()) * 10));
        mgrStream.setLoop(Integer.parseInt(lite.getJetRound()));
        return mgrStream;
    }

    @NonNull
    private MgrOutputJet setDataWithRide(JetModeConfigLite lite) {
        MgrRideJet mgrRide = new MgrRideJet();
        mgrRide.setType(CONFIG_RIDE);
        mgrRide.setDevCount(devNum + ((isSelectedMaster == 1) ? 1 : 0));
        mgrRide.setCurrentTime(CURRENT_TIME);
        mgrRide.setLoopId(LOOP_ID);
        mgrRide.setHigh((byte) Integer.parseInt(DEFAULT_HIGH));
        mgrRide.setDirection(Integer.parseInt(lite.getDirection()));
        mgrRide.setGap((int) (Float.parseFloat(lite.getGap()) * 10));
        mgrRide.setDuration((int) (Float.parseFloat(lite.getDuration()) * 10));
        mgrRide.setGapBig((int) (Float.parseFloat(lite.getBigGap()) * 10));
        mgrRide.setLoop(Integer.parseInt(lite.getJetRound()));
        return mgrRide;
    }

    @NonNull
    private MgrOutputJet setDataWithInterval(JetModeConfigLite lite) {
        MgrIntervalJet mgrInterval = new MgrIntervalJet();
        mgrInterval.setType(CONFIG_INTERVAL);
        mgrInterval.setDevCount(devNum + ((isSelectedMaster == 1) ? 1 : 0));
        mgrInterval.setCurrentTime(CURRENT_TIME);
        mgrInterval.setLoopId(LOOP_ID);
        mgrInterval.setGapBig((int) (Float.parseFloat(lite.getGap()) * 10));
        mgrInterval.setDuration((int) (Float.parseFloat(lite.getDuration()) * 10));
        mgrInterval.setLoop(Integer.parseInt(lite.getJetRound()));
        return mgrInterval;
    }

    @NonNull
    private MgrOutputJet setDataWithTogether(JetModeConfigLite lite) {
        MgrTogetherJet mgrTogether = new MgrTogetherJet();
        mgrTogether.setType(CONFIG_TOGETHER);
        mgrTogether.setDevCount(devNum + ((isSelectedMaster == 1) ? 1 : 0));
        mgrTogether.setCurrentTime(CURRENT_TIME);
        mgrTogether.setDuration((int) (Float.parseFloat(lite.getDuration()) * 10));
        mgrTogether.setHigh((byte) Integer.parseInt(lite.getHigh()));
        return mgrTogether;
    }

}
