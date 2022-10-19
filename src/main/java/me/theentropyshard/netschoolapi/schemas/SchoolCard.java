package me.theentropyshard.netschoolapi.schemas;

import java.util.Arrays;

public class SchoolCard {
    public CommonInfo commonInfo;
    public ManagementInfo managementInfo;
    public ContactInfo contactInfo;
    public OtherInfo otherInfo;
    public BankDetails bankDetails;
    public FoodPayDetails foodPayDetails;
    public InternetConnectionInfo internetConnectionInfo;

    public static class CommonInfo {
        public String legalFormName;
        public String legalFormName83;
        public String typeName;
        public boolean showFormName;
        public String formName;
        public String schoolName;
        public String fullSchoolName;
        public int schoolNumber;
        public String foundingDate;
        public String independ;
        public String additionalName;
        public String mainSchool;
        public String[] founders;
        public String[] ownEducManagements;
        public String status;
        public String smallOrganization;
        public LocationInfo locationInfo;
        public String about;
        public String emId;

        public static class LocationInfo {
            public LocationType locationType;
            public boolean inProvinceCenter;
            public boolean isProvinceSchoolInCity;

            @Override
            public String toString() {
                return "LocationInfo{" +
                        "locationType=" + locationType +
                        ", inProvinceCenter=" + inProvinceCenter +
                        ", isProvinceSchoolInCity=" + isProvinceSchoolInCity +
                        '}';
            }

            public static class LocationType {
                public int id;
                public String key;
                public String name;

                @Override
                public String toString() {
                    return "LocationType{" +
                            "id=" + id +
                            ", key='" + key + '\'' +
                            ", name='" + name + '\'' +
                            '}';
                }
            }
        }

        @Override
        public String toString() {
            return "CommonInfo{" +
                    "legalFormName='" + legalFormName + '\'' +
                    ", legalFormName83='" + legalFormName83 + '\'' +
                    ", typeName='" + typeName + '\'' +
                    ", showFormName=" + showFormName +
                    ", formName='" + formName + '\'' +
                    ", schoolName='" + schoolName + '\'' +
                    ", fullSchoolName='" + fullSchoolName + '\'' +
                    ", schoolNumber=" + schoolNumber +
                    ", foundingDate='" + foundingDate + '\'' +
                    ", independ='" + independ + '\'' +
                    ", additionalName='" + additionalName + '\'' +
                    ", mainSchool='" + mainSchool + '\'' +
                    ", founders=" + Arrays.toString(founders) +
                    ", ownEducManagements=" + Arrays.toString(ownEducManagements) +
                    ", status='" + status + '\'' +
                    ", smallOrganization='" + smallOrganization + '\'' +
                    ", locationInfo=" + locationInfo +
                    ", about='" + about + '\'' +
                    ", emId='" + emId + '\'' +
                    '}';
        }
    }

    public static class ManagementInfo {
        public String director;
        public String principalUVR;
        public String principalAHC;
        public String principalIT;
        public String collegiateManagement;

        @Override
        public String toString() {
            return "ManagementInfo{" +
                    "director='" + director + '\'' +
                    ", principalUVR='" + principalUVR + '\'' +
                    ", principalAHC='" + principalAHC + '\'' +
                    ", principalIT='" + principalIT + '\'' +
                    ", collegiateManagement='" + collegiateManagement + '\'' +
                    '}';
        }
    }

    public static class ContactInfo {
        public String stateProvinceName;
        public String cityName;
        public String districtName;
        public String postAddress;
        public String phones;
        public String fax;
        public String email;
        public String web;
        public String addressesAdditionalBuildings;
        public String juridicalAddress;

        @Override
        public String toString() {
            return "ContactInfo{" +
                    "stateProvinceName='" + stateProvinceName + '\'' +
                    ", cityName='" + cityName + '\'' +
                    ", districtName='" + districtName + '\'' +
                    ", postAddress='" + postAddress + '\'' +
                    ", phones='" + phones + '\'' +
                    ", fax='" + fax + '\'' +
                    ", email='" + email + '\'' +
                    ", web='" + web + '\'' +
                    ", addressesAdditionalBuildings='" + addressesAdditionalBuildings + '\'' +
                    ", juridicalAddress='" + juridicalAddress + '\'' +
                    '}';
        }
    }

    public static class OtherInfo {
        public String inn;
        public String kpp;
        public String ogrn;
        public String okpo;
        public String okato;
        public String okogu;
        public String okopf;
        public String okfs;
        public String okved;
        public String specialization;
        public String maxOccupancy;
        public String maxOccupancyOnShift;
        public String numberShifts;
        public String referenceToCharter;
        public String presenceOfPool;
        public String barrierFreeEnvironment;
        public String videoSurveillance;
        public String linkToScanCopyLicenseEducation;
        public String socialPartnerShip;
        public String timetable;
        public String conditionsEducation;
        public String projectTypeForSchool;

        @Override
        public String toString() {
            return "OtherInfo{" +
                    "inn='" + inn + '\'' +
                    ", kpp='" + kpp + '\'' +
                    ", ogrn='" + ogrn + '\'' +
                    ", okpo='" + okpo + '\'' +
                    ", okato='" + okato + '\'' +
                    ", okogu='" + okogu + '\'' +
                    ", okopf='" + okopf + '\'' +
                    ", okfs='" + okfs + '\'' +
                    ", okved='" + okved + '\'' +
                    ", specialization='" + specialization + '\'' +
                    ", maxOccupancy='" + maxOccupancy + '\'' +
                    ", maxOccupancyOnShift='" + maxOccupancyOnShift + '\'' +
                    ", numberShifts='" + numberShifts + '\'' +
                    ", referenceToCharter='" + referenceToCharter + '\'' +
                    ", presenceOfPool='" + presenceOfPool + '\'' +
                    ", barrierFreeEnvironment='" + barrierFreeEnvironment + '\'' +
                    ", videoSurveillance='" + videoSurveillance + '\'' +
                    ", linkToScanCopyLicenseEducation='" + linkToScanCopyLicenseEducation + '\'' +
                    ", socialPartnerShip='" + socialPartnerShip + '\'' +
                    ", timetable='" + timetable + '\'' +
                    ", conditionsEducation='" + conditionsEducation + '\'' +
                    ", projectTypeForSchool='" + projectTypeForSchool + '\'' +
                    '}';
        }
    }

    public static class BankDetails {
        public String bankScore;
        public String corrScore;
        public String personalAccount;
        public String bik;
        public String note;
        public String bankName;
        public String bankKpp;

        @Override
        public String toString() {
            return "BankDetails{" +
                    "bankScore='" + bankScore + '\'' +
                    ", corrScore='" + corrScore + '\'' +
                    ", personalAccount='" + personalAccount + '\'' +
                    ", bik='" + bik + '\'' +
                    ", note='" + note + '\'' +
                    ", bankName='" + bankName + '\'' +
                    ", bankKpp='" + bankKpp + '\'' +
                    '}';
        }
    }

    public static class FoodPayDetails {
        public String foodPayOrgName;
        public String foodPayInn;
        public String foodPayKpp;
        public String foodPayBankName;
        public String foodPayBankScore;
        public String foodPayBankCorrScore;
        public String foodPayBankBik;
        public String foodPayBankKpp;

        @Override
        public String toString() {
            return "FoodPayDetails{" +
                    "foodPayOrgName='" + foodPayOrgName + '\'' +
                    ", foodPayInn='" + foodPayInn + '\'' +
                    ", foodPayKpp='" + foodPayKpp + '\'' +
                    ", foodPayBankName='" + foodPayBankName + '\'' +
                    ", foodPayBankScore='" + foodPayBankScore + '\'' +
                    ", foodPayBankCorrScore='" + foodPayBankCorrScore + '\'' +
                    ", foodPayBankBik='" + foodPayBankBik + '\'' +
                    ", foodPayBankKpp='" + foodPayBankKpp + '\'' +
                    '}';
        }
    }

    public static class InternetConnectionInfo {
        public int computersCount;
        public String contentFilteringName;
        public int internetSpeedUnderContract;
        public int internetSpeedInFact;
        public String internetProviderName;
        public String internetAccessTechnology;

        @Override
        public String toString() {
            return "InternetConnectionInfo{" +
                    "computersCount=" + computersCount +
                    ", contentFilteringName='" + contentFilteringName + '\'' +
                    ", internetSpeedUnderContract=" + internetSpeedUnderContract +
                    ", internetSpeedInFact=" + internetSpeedInFact +
                    ", internetProviderName='" + internetProviderName + '\'' +
                    ", internetAccessTechnology='" + internetAccessTechnology + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SchoolCard{" +
                "commonInfo=" + commonInfo +
                ", managementInfo=" + managementInfo +
                ", contactInfo=" + contactInfo +
                ", otherInfo=" + otherInfo +
                ", bankDetails=" + bankDetails +
                ", foodPayDetails=" + foodPayDetails +
                ", internetConnectionInfo=" + internetConnectionInfo +
                '}';
    }
}
