package me.theentropyshard.netschoolapi;

public final class Constants {
    private Constants() {
        throw new UnsupportedOperationException("Class Constants should not be instantiated");
    }

    public static final String LOGINDATA_URL = "logindata";
    public static final String GET_DATA_URL = "auth/getdata";
    public static final String LOGIN_URL =  "login";
    public static final String SCHOOLS_URL = "addresses/schools";
    public static final String LOGOUT_URL =  "auth/logout";
    public static final String ANNOUNCEMENTS_URL =  "announcements";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36";
}
