package cn.eejing.colorflower.model.request;

import java.util.List;

public class AreaSelectBean {

    /**
     * code : 1
     * message : 数据获取成功
     * data : [{"id":1,"name":"北京市"},{"id":338,"name":"天津市"},{"id":636,"name":"河北省"},{"id":3102,"name":"山西"},{"id":4670,"name":"内蒙古自治区"},{"id":5827,"name":"辽宁省"},{"id":7531,"name":"吉林省"},{"id":8558,"name":"黑龙江省"},{"id":10543,"name":"上海市"},{"id":10808,"name":"江苏省"},{"id":12596,"name":"浙江省"},{"id":14234,"name":"安徽省"},{"id":16068,"name":"福建省"},{"id":17359,"name":"江西省"},{"id":19280,"name":"山东省"},{"id":21387,"name":"河南省"},{"id":24022,"name":"湖北省"},{"id":25579,"name":"湖南省"},{"id":28240,"name":"广东省"},{"id":30164,"name":"广西壮族自治区"},{"id":31563,"name":"海南省"},{"id":31929,"name":"重庆市"},{"id":33007,"name":"四川省"},{"id":37906,"name":"贵州省"},{"id":39556,"name":"云南省"},{"id":41103,"name":"西藏自治区"},{"id":41877,"name":"陕西省"},{"id":43776,"name":"甘肃省"},{"id":45286,"name":"青海省"},{"id":45753,"name":"宁夏回族自治区"},{"id":46047,"name":"新疆维吾尔自治区"},{"id":47493,"name":"台湾省"},{"id":47494,"name":"香港特别行政区"},{"id":47495,"name":"澳门特别行政区"}]
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
         * name : 北京市
         */

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
