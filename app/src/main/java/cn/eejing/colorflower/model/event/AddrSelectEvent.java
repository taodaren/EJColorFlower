package cn.eejing.colorflower.model.event;

public class AddrSelectEvent {
    private String province;
    private String city;
    private String district;
    private String provinceId;
    private String cityId;
    private String districtId;

    public AddrSelectEvent(String province, String city, String district, String provinceId, String cityId, String districtId) {
        this.province = province;
        this.city = city;
        this.district = district;
        this.provinceId = provinceId;
        this.cityId = cityId;
        this.districtId = districtId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }
}
