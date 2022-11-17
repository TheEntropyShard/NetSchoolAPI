/*      NetSchoolAPI. A simple API client for NetSchool by irTech
 *      Copyright (C) 2022 TheEntropyShard
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.theentropyshard.netschoolapi;

/**
 * Класс, содержащий все используемые URLы
 */
public final class Urls {
    private Urls() {
        throw new UnsupportedOperationException("Class Urls should not be instantiated");
    }

    /**
     * WebApi urls
     */
    public static final class WebApi {
        private WebApi() {
            throw new UnsupportedOperationException("Class WebApi should not be instantiated");
        }

        public static final String LOGIN_DATA = "logindata";
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
        public static final String ASSIGNMENT_TYPES = "grade/assignment/types?all=";
        public static final String UPLOAD_LIMITS = "attachments/uploadLimits";
        public static final String REPORTS_STUDENTTOTAL = "reports/studenttotal";
    }

    /**
     * Asp urls
     */
    public static final class Asp {
        private Asp() {
            throw new UnsupportedOperationException("Class Asp should not be instantiated");
        }

        public static final String GET_MESSAGES = "/asp/ajax/GetMessagesAjax.asp";
        public static final String DELETE_MESSAGES = "/asp/ajax/DeleteMessagesAjax.asp";
        public static final String READ_MESSAGE = "/asp/messages/readmessage.asp";
        public static final String SEND_MESSAGE = "/asp/messages/sendsavemsg.asp";
        public static final String COMPOSE_MESSAGE = "/asp/Messages/composemessage.asp";

        public static final String REPORT_STUDENT_TOTAL_MARKS = "/asp/Reports/ReportStudentTotalMarks.asp";
        public static final String STUDENT_TOTAL_MARKS = "/asp/Reports/StudentTotalMarks.asp";

        public static final String REPORT_STUDENT_AVERAGE_MARK = "/asp/Reports/ReportStudentAverageMark.asp";
        public static final String STUDENT_AVERAGE_MARK = "/asp/Reports/StudentAverageMark.asp";

        public static final String REPORT_STUDENT_AVERAGE_MARK_DYN = "/asp/Reports/ReportStudentAverageMarkDyn.asp";
        public static final String STUDENT_AVERAGE_MARK_DYN = "/asp/Reports/StudentAverageMarkDyn.asp";

        public static final String REPORT_STUDENT_GRADES = "/asp/Reports/ReportStudentGrades.asp";
        public static final String STUDENT_GRADES = "/asp/Reports/StudentGrades.asp";

        public static final String REPORT_STUDENT_TOTAL = "/asp/Reports/ReportStudentTotal.asp";
        public static final String STUDENT_TOTAL = "/asp/Reports/StudentTotal.asp";

        public static final String REPORT_STUDENT_ATTENDANCE_GRADES = "/asp/Reports/ReportStudentAttendanceGrades.asp";
        public static final String STUDENT_ATTENDANCE_GRADES = "/asp/Reports/StudentAttendanceGrades.asp";

        public static final String REPORT_JOURNAL_ACCESS = "/asp/Reports/ReportJournalAccess.asp";
        public static final String JOURNAL_ACCESS = "/asp/Reports/JournalAccess.asp";

        public static final String REPORT_PARENT_INFO_LETTER = "/asp/Reports/ReportParentInfoLetter.asp";
        public static final String PARENT_INFO_LETTER = "/asp/Reports/ParentInfoLetter.asp";

        public static final String REPORT_DOP_EDUC_STUDENT = "/asp/Reports/ReportDopEducStudent.asp";
        public static final String DOP_EDUC_STUDENT = "/asp/Reports/DopEducStudent.asp";
    }
}
