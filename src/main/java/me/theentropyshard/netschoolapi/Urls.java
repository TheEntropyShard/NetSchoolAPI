package me.theentropyshard.netschoolapi;

/**
 * Класс, содержащий все используемые URLы
 */
public final class Urls {
    private Urls() {}

    /**
     * WebApi urls
     */
    public static final class WebApi {
        private WebApi() {}

        public static final String GET_DATA = "auth/getdata";
        public static final String LOGIN = "login";
        public static final String SCHOOLS_DATABASE = "addresses/schools";
        public static final String SCHOOL_INFO = "schools/%d/card";
        public static final String LOGOUT = "auth/logout";
        public static final String ANNOUNCEMENTS = "announcements";
        public static final String DIARY_INIT = "student/diary/init";
        public static final String DIARY = "student/diary";
        public static final String OVERDUE = "student/diary/pastMandatory";
        public static final String YEARS_CURRENT = "years/current";
        public static final String ACTIVE_SESSIONS = "context/activeSessions";
        public static final String GET_ATTACHMENTS = "student/diary/get-attachments";
        public static final String ATTACHMENTS_DOWNLOAD = "attachments/%d";
    }

    /**
     * Asp urls
     */
    public static final class Asp {
        private Asp() {}

        public static final String GET_MESSAGES = "/asp/ajax/GetMessagesAjax.asp";
    }
}
