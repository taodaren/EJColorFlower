package cn.eejing.colorflower.model.request;

import java.util.List;

public class AddrProvincesBean {

    /**
     * code : 1
     * message : 操作成功!
     * data : [{"id":1,"province_id":"110000","province":"北京市"},{"id":2,"province_id":"120000","province":"天津市"},{"id":3,"province_id":"130000","province":"河北省"},{"id":4,"province_id":"140000","province":"山西省"},{"id":5,"province_id":"150000","province":"内蒙古"},{"id":6,"province_id":"210000","province":"辽宁省"},{"id":7,"province_id":"220000","province":"吉林省"},{"id":8,"province_id":"230000","province":"黑龙江"},{"id":9,"province_id":"310000","province":"上海市"},{"id":10,"province_id":"320000","province":"江苏省"},{"id":11,"province_id":"330000","province":"浙江省"},{"id":12,"province_id":"340000","province":"安徽省"},{"id":13,"province_id":"350000","province":"福建省"},{"id":14,"province_id":"360000","province":"江西省"},{"id":15,"province_id":"370000","province":"山东省"},{"id":16,"province_id":"410000","province":"河南省"},{"id":17,"province_id":"420000","province":"湖北省"},{"id":18,"province_id":"430000","province":"湖南省"},{"id":19,"province_id":"440000","province":"广东省"},{"id":20,"province_id":"450000","province":"广西"},{"id":21,"province_id":"460000","province":"海南省"},{"id":22,"province_id":"500000","province":"重庆市"},{"id":23,"province_id":"510000","province":"四川省"},{"id":24,"province_id":"520000","province":"贵州省"},{"id":25,"province_id":"530000","province":"云南省"},{"id":26,"province_id":"540000","province":"西藏"},{"id":27,"province_id":"610000","province":"陕西省"},{"id":28,"province_id":"620000","province":"甘肃省"},{"id":29,"province_id":"630000","province":"青海省"},{"id":30,"province_id":"640000","province":"宁夏"},{"id":31,"province_id":"650000","province":"新疆"},{"id":32,"province_id":"710000","province":"台湾省"},{"id":33,"province_id":"810000","province":"香港"},{"id":34,"province_id":"820000","province":"澳门"}]
     */

    private int code;
    private String message;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * province_id : 110000
         * province : 北京市
         */

        private int id;
        private String province_id;
        private String province;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getProvince_id() {
            return province_id;
        }

        public void setProvince_id(String province_id) {
            this.province_id = province_id;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }
    }
}
