package me.theentropyshard.netschoolapi.dto;

import java.util.Objects;

public class SchoolDataObject {
    public int countryId;
    public int stateId;
    public int cityId;
    public int parentCityId;
    public int provinceId;
    public int municipalityDistrictId;
    public int funcType;
    public int cityDistrictId;
    public int id;
    public String name;
    public String addressString;

    public SchoolDataObject() {
    }

    public SchoolDataObject(int countryId, int stateId, int cityId,
                            int parentCityId, int provinceId,
                            int municipalityDistrictId, int funcType,
                            int cityDistrictId, int id, String name, String addressString) {
        this.countryId = countryId;
        this.stateId = stateId;
        this.cityId = cityId;
        this.parentCityId = parentCityId;
        this.provinceId = provinceId;
        this.municipalityDistrictId = municipalityDistrictId;
        this.funcType = funcType;
        this.cityDistrictId = cityDistrictId;
        this.id = id;
        this.name = name;
        this.addressString = addressString;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        SchoolDataObject that = (SchoolDataObject) o;
        return countryId == that.countryId && stateId == that.stateId && cityId == that.cityId && parentCityId == that.parentCityId && provinceId == that.provinceId && municipalityDistrictId == that.municipalityDistrictId && funcType == that.funcType && cityDistrictId == that.cityDistrictId && id == that.id && Objects.equals(name, that.name) && Objects.equals(addressString, that.addressString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryId, stateId, cityId, parentCityId, provinceId, municipalityDistrictId, funcType, cityDistrictId, id, name);
    }

    @Override
    public String toString() {
        return "SchoolDataObject{" +
                "countryId=" + countryId +
                ", stateId=" + stateId +
                ", cityId=" + cityId +
                ", parentCityId=" + parentCityId +
                ", provinceId=" + provinceId +
                ", municipalityDistrictId=" + municipalityDistrictId +
                ", funcType=" + funcType +
                ", cityDistrictId=" + cityDistrictId +
                ", id=" + id +
                ", name=" + name +
                ", addressString=" + addressString +
                '}';
    }
}
