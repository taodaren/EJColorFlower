package cn.eejing.ejcolorflower.model.request;

public class ConfirmOrderBean {

    /**
     * code : 1
     * message : 获取成功
     * data : {"goods":{"goods_id":1,"name":"炫彩烟花机","image":"http://60.205.226.109/uploads/goods/20180521/2ee6635624f23464e6f61ca86172da96.jpg","money":0.01,"stock":1,"number":"898","sold":"102","open":1,"postage":3299,"basics_postage":0,"create_time":"1525745128","update_time":"1527039147","content":"                                                                                                &lt;p&gt;&amp;nbsp; &amp;nbsp; &amp;nbsp; &amp;nbsp;本公司自行研制开发制作的机器, 可手机遥控.&amp;nbsp; &amp;nbsp;零污染 绝对环保产品,&lt;/p&gt;&lt;p&gt;其应用场合可为 舞台效果, 大型活动现场.应用广泛,使用方便.手机遥控就可实现你意想不到的视觉盛宴.&lt;/p&gt;&lt;p&gt;&amp;nbsp; &amp;nbsp; &amp;nbsp;&lt;img src=&quot;/uploads/goods_content/20180521/ca2ec0277f8f595c8481243f4e55e565.jpg&quot; alt=&quot;ca2ec0277f8f595c8481243f4e55e565.jpg&quot;&gt;&lt;/p&gt;                                                                                "},"address":{"id":1,"member_id":15,"name":"android","mobile":"18666666666","province_id":110000,"city_id":110100,"area_id":110112,"address":"丁各庄村72号博光润泽有限公司","address_all":"北京市北京市通州区丁各庄村72号博光润泽有限公司","status":1,"add_time":"1525829655"}}
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
         * goods : {"goods_id":1,"name":"炫彩烟花机","image":"http://60.205.226.109/uploads/goods/20180521/2ee6635624f23464e6f61ca86172da96.jpg","money":0.01,"stock":1,"number":"898","sold":"102","open":1,"postage":3299,"basics_postage":0,"create_time":"1525745128","update_time":"1527039147","content":"                                                                                                &lt;p&gt;&amp;nbsp; &amp;nbsp; &amp;nbsp; &amp;nbsp;本公司自行研制开发制作的机器, 可手机遥控.&amp;nbsp; &amp;nbsp;零污染 绝对环保产品,&lt;/p&gt;&lt;p&gt;其应用场合可为 舞台效果, 大型活动现场.应用广泛,使用方便.手机遥控就可实现你意想不到的视觉盛宴.&lt;/p&gt;&lt;p&gt;&amp;nbsp; &amp;nbsp; &amp;nbsp;&lt;img src=&quot;/uploads/goods_content/20180521/ca2ec0277f8f595c8481243f4e55e565.jpg&quot; alt=&quot;ca2ec0277f8f595c8481243f4e55e565.jpg&quot;&gt;&lt;/p&gt;                                                                                "}
         * address : {"id":1,"member_id":15,"name":"android","mobile":"18666666666","province_id":110000,"city_id":110100,"area_id":110112,"address":"丁各庄村72号博光润泽有限公司","address_all":"北京市北京市通州区丁各庄村72号博光润泽有限公司","status":1,"add_time":"1525829655"}
         */

        private GoodsBean goods;
        private AddressBean address;

        public GoodsBean getGoods() {
            return goods;
        }

        public void setGoods(GoodsBean goods) {
            this.goods = goods;
        }

        public AddressBean getAddress() {
            return address;
        }

        public void setAddress(AddressBean address) {
            this.address = address;
        }

        public static class GoodsBean {
            /**
             * goods_id : 1
             * name : 炫彩烟花机
             * image : http://60.205.226.109/uploads/goods/20180521/2ee6635624f23464e6f61ca86172da96.jpg
             * money : 0.01
             * stock : 1
             * number : 898
             * sold : 102
             * open : 1
             * postage : 3299
             * basics_postage : 0
             * create_time : 1525745128
             * update_time : 1527039147
             * content :                                                                                                 &lt;p&gt;&amp;nbsp; &amp;nbsp; &amp;nbsp; &amp;nbsp;本公司自行研制开发制作的机器, 可手机遥控.&amp;nbsp; &amp;nbsp;零污染 绝对环保产品,&lt;/p&gt;&lt;p&gt;其应用场合可为 舞台效果, 大型活动现场.应用广泛,使用方便.手机遥控就可实现你意想不到的视觉盛宴.&lt;/p&gt;&lt;p&gt;&amp;nbsp; &amp;nbsp; &amp;nbsp;&lt;img src=&quot;/uploads/goods_content/20180521/ca2ec0277f8f595c8481243f4e55e565.jpg&quot; alt=&quot;ca2ec0277f8f595c8481243f4e55e565.jpg&quot;&gt;&lt;/p&gt;
             */

            private int goods_id;
            private String name;
            private String image;
            private double money;
            private int stock;
            private String number;
            private String sold;
            private int open;
            private int postage;
            private int basics_postage;
            private String create_time;
            private String update_time;
            private String content;

            public int getGoods_id() {
                return goods_id;
            }

            public void setGoods_id(int goods_id) {
                this.goods_id = goods_id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getImage() {
                return image;
            }

            public void setImage(String image) {
                this.image = image;
            }

            public double getMoney() {
                return money;
            }

            public void setMoney(double money) {
                this.money = money;
            }

            public int getStock() {
                return stock;
            }

            public void setStock(int stock) {
                this.stock = stock;
            }

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public String getSold() {
                return sold;
            }

            public void setSold(String sold) {
                this.sold = sold;
            }

            public int getOpen() {
                return open;
            }

            public void setOpen(int open) {
                this.open = open;
            }

            public int getPostage() {
                return postage;
            }

            public void setPostage(int postage) {
                this.postage = postage;
            }

            public int getBasics_postage() {
                return basics_postage;
            }

            public void setBasics_postage(int basics_postage) {
                this.basics_postage = basics_postage;
            }

            public String getCreate_time() {
                return create_time;
            }

            public void setCreate_time(String create_time) {
                this.create_time = create_time;
            }

            public String getUpdate_time() {
                return update_time;
            }

            public void setUpdate_time(String update_time) {
                this.update_time = update_time;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }

        public static class AddressBean {
            /**
             * id : 1
             * member_id : 15
             * name : android
             * mobile : 18666666666
             * province_id : 110000
             * city_id : 110100
             * area_id : 110112
             * address : 丁各庄村72号博光润泽有限公司
             * address_all : 北京市北京市通州区丁各庄村72号博光润泽有限公司
             * status : 1
             * add_time : 1525829655
             */

            private int id;
            private int member_id;
            private String name;
            private String mobile;
            private int province_id;
            private int city_id;
            private int area_id;
            private String address;
            private String address_all;
            private int status;
            private String add_time;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getMember_id() {
                return member_id;
            }

            public void setMember_id(int member_id) {
                this.member_id = member_id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMobile() {
                return mobile;
            }

            public void setMobile(String mobile) {
                this.mobile = mobile;
            }

            public int getProvince_id() {
                return province_id;
            }

            public void setProvince_id(int province_id) {
                this.province_id = province_id;
            }

            public int getCity_id() {
                return city_id;
            }

            public void setCity_id(int city_id) {
                this.city_id = city_id;
            }

            public int getArea_id() {
                return area_id;
            }

            public void setArea_id(int area_id) {
                this.area_id = area_id;
            }

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
            }

            public String getAddress_all() {
                return address_all;
            }

            public void setAddress_all(String address_all) {
                this.address_all = address_all;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getAdd_time() {
                return add_time;
            }

            public void setAdd_time(String add_time) {
                this.add_time = add_time;
            }
        }
    }
}
