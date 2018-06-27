package cn.eejing.ejcolorflower.model.session;

public class AddrSession {
    private String consignee;
    private String phone;
    private String address;

    public AddrSession(String consignee, String phone, String address) {
        this.consignee = consignee;
        this.phone = phone;
        this.address = address;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
