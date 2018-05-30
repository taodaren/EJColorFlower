package cn.eejing.ejcolorflower.model.request;

public class MaterialInfoBean {

    /**
     * code : 1
     * message : 获取信息成功
     * data : {"material_num":"18032090864712","type":"eee","level":"优","color":"白","duration":"16","use_device":"","use_status":0,"use_member":0,"update_time":1527649042,"qr_code":"/www/web/default/public/uploads/qrcode/18032090864712.png"}
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
         * material_num : 18032090864712
         * type : eee
         * level : 优
         * color : 白
         * duration : 16
         * use_device :
         * use_status : 0
         * use_member : 0
         * update_time : 1527649042
         * qr_code : /www/web/default/public/uploads/qrcode/18032090864712.png
         */

        private String material_num;
        private String type;
        private String level;
        private String color;
        private String duration;
        private String use_device;
        private int use_status;
        private int use_member;
        private int update_time;
        private String qr_code;

        public String getMaterial_num() {
            return material_num;
        }

        public void setMaterial_num(String material_num) {
            this.material_num = material_num;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getUse_device() {
            return use_device;
        }

        public void setUse_device(String use_device) {
            this.use_device = use_device;
        }

        public int getUse_status() {
            return use_status;
        }

        public void setUse_status(int use_status) {
            this.use_status = use_status;
        }

        public int getUse_member() {
            return use_member;
        }

        public void setUse_member(int use_member) {
            this.use_member = use_member;
        }

        public int getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(int update_time) {
            this.update_time = update_time;
        }

        public String getQr_code() {
            return qr_code;
        }

        public void setQr_code(String qr_code) {
            this.qr_code = qr_code;
        }
    }
}
