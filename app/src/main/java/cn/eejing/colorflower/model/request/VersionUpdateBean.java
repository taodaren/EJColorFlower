package cn.eejing.colorflower.model.request;

public class VersionUpdateBean {

    /**
     * code : 1
     * message : 版本升级信息获取成功
     * data : {"versionData":{"is_upload":1,"apk_url":"itms-apps://itunes.apple.com/cn/app/id1309448515?","upgrade_point":"跟更能鞥呢更能哥哥哥","version_code":"2.5"}}
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
         * versionData : {"is_upload":1,"apk_url":"itms-apps://itunes.apple.com/cn/app/id1309448515?","upgrade_point":"跟更能鞥呢更能哥哥哥","version_code":"2.5"}
         */

        private VersionDataBean versionData;

        public VersionDataBean getVersionData() {
            return versionData;
        }

        public void setVersionData(VersionDataBean versionData) {
            this.versionData = versionData;
        }

        public static class VersionDataBean {
            /**
             * is_upload : 1
             * apk_url : itms-apps://itunes.apple.com/cn/app/id1309448515?
             * upgrade_point : 跟更能鞥呢更能哥哥哥
             * version_code : 2.5
             */

            private int is_upload;
            private String apk_url;
            private String upgrade_point;
            private String version_code;

            public int getIs_upload() {
                return is_upload;
            }

            public void setIs_upload(int is_upload) {
                this.is_upload = is_upload;
            }

            public String getApk_url() {
                return apk_url;
            }

            public void setApk_url(String apk_url) {
                this.apk_url = apk_url;
            }

            public String getUpgrade_point() {
                return upgrade_point;
            }

            public void setUpgrade_point(String upgrade_point) {
                this.upgrade_point = upgrade_point;
            }

            public String getVersion_code() {
                return version_code;
            }

            public void setVersion_code(String version_code) {
                this.version_code = version_code;
            }
        }
    }
}
