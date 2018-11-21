package cn.eejing.colorflower.model.request;

import com.google.gson.annotations.SerializedName;

public class PayWeiBean {

    /**
     * code : 1
     * message : 操作成功
     * data : {"appid":"wx6a32217e1e3ae4f4","partnerid":"1509160731","prepayid":"wx21163240863241ae009d4c401623462326","package":"Sign=WXPay","timestamp":1542789160,"noncestr":"JBrNP2k2qngrtmYH","sign":"CD958CFE6491E79F99FE91714D50A0D5","Out_trade_no":"YJ181121162040b2QipI"}
     */

    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * appid : wx6a32217e1e3ae4f4
         * partnerid : 1509160731
         * prepayid : wx21163240863241ae009d4c401623462326
         * package : Sign=WXPay
         * timestamp : 1542789160
         * noncestr : JBrNP2k2qngrtmYH
         * sign : CD958CFE6491E79F99FE91714D50A0D5
         * Out_trade_no : YJ181121162040b2QipI
         */

        private String appid;
        private String partnerid;
        private String prepayid;
        @SerializedName("package")
        private String packageX;
        private int timestamp;
        private String noncestr;
        private String sign;
        private String Out_trade_no;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public int getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(int timestamp) {
            this.timestamp = timestamp;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getOut_trade_no() {
            return Out_trade_no;
        }

        public void setOut_trade_no(String Out_trade_no) {
            this.Out_trade_no = Out_trade_no;
        }
    }
}
