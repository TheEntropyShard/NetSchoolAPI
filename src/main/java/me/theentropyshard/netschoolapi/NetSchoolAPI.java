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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.theentropyshard.netschoolapi.exceptions.SchoolNotFoundException;
import me.theentropyshard.netschoolapi.jsonstubs.AuthDataStub;
import me.theentropyshard.netschoolapi.jsonstubs.GetDataStub;
import me.theentropyshard.netschoolapi.jsonstubs.SchoolStub;
import me.theentropyshard.netschoolapi.schemas.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NetSchoolAPI implements Closeable {
    public static final String LOGINDATA = "logindata";
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

    private final String username;
    private final String password;
    private final String schoolName;

    private final HttpClientWrapper client;
    private final ObjectMapper objectMapper;

    private int userId;
    private int yearId;
    private SchoolStub school;

    public NetSchoolAPI(String baseUrl, String username, String password, String schoolName) {
        this.username = username;
        this.password = password;
        this.schoolName = schoolName;

        this.client = new HttpClientWrapper(baseUrl + "/webapi/");
        this.objectMapper = new ObjectMapper();
    }

    private void getSchoolData() {
        try(CloseableHttpResponse response = this.client.get(NetSchoolAPI.SCHOOLS_DATABASE)) {
            SchoolStub[] schoolStubs = this.objectMapper.readValue(response.getEntity().getContent(), SchoolStub[].class);
            for(SchoolStub schoolStub : schoolStubs) {
                if(schoolStub == null) break;
                if(!schoolStub.name.equals(this.schoolName)) continue;
                this.school = schoolStub;
                return;
            }
        } catch (IOException ignored) {
        }
        throw new SchoolNotFoundException("Could not found school \"" + this.schoolName + "\"");
    }

    private AuthDataStub getAuthData() {
        try {
            GetDataStub getDataObject;
            try(CloseableHttpResponse response = this.client.post(NetSchoolAPI.GET_DATA, new StringEntity(""))) {
                getDataObject = objectMapper.readValue(response.getEntity().getContent(), GetDataStub.class);
            }
            String encodedPassword = Utils.md5(this.password.getBytes(Charset.forName("windows-1251")));
            String pw2 = Utils.md5((getDataObject.salt + encodedPassword).getBytes(Charset.forName("UTF-8"))); //for android compatibility
            if(pw2 == null) {
                throw new RuntimeException("Password was not hashed");
            }
            String pw = pw2.substring(0, this.password.length());
            return new AuthDataStub(pw, pw2, getDataObject.lt, getDataObject.ver);
        } catch (IOException e) {
            throw new RuntimeException("Could not get auth data");
        }
    }

    /**
     * Logs into system
     */
    public void login() {
        try {
            this.getSchoolData();

            AuthDataStub ado = this.getAuthData();
            String requestString = this.getRequestBody(ado);
            StringEntity content = new StringEntity(requestString);

            try(CloseableHttpResponse response = this.client.post(NetSchoolAPI.LOGIN, content)) {
                JsonNode node = this.objectMapper.readTree(response.getEntity().getContent());
                this.client.addHeader(new BasicHeader("at", node.get("at").textValue()));
            }

            try(CloseableHttpResponse response = this.client.get(NetSchoolAPI.DIARY_INIT)) {
                JsonNode node = this.objectMapper.readTree(response.getEntity().getContent());
                int currentStudentId = node.get("currentStudentId").intValue();
                this.userId = node.get("students").get(currentStudentId).get("studentId").intValue();
            }

            try(CloseableHttpResponse response = this.client.get(NetSchoolAPI.YEARS_CURRENT)) {
                JsonNode node = this.objectMapper.readTree(response.getEntity().getContent());
                this.yearId = node.get("id").intValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns announcements
     *
     * @return JSON String
     * @throws IOException When an IO exception occurred
     */
    public String getJsonAnnouncements() throws IOException {
        try(CloseableHttpResponse response = this.client.get(NetSchoolAPI.ANNOUNCEMENTS + "?take=-1")) {
            Scanner sc = new Scanner(response.getEntity().getContent());
            StringBuilder sb = new StringBuilder();
            while(sc.hasNextLine()) {
                sb.append(sc.nextLine());
            }
            sc.close();
            return sb.toString();
        }
    }

    public Announcement[] getAnnouncements() throws IOException {
        return this.objectMapper.readValue(this.getJsonAnnouncements(), Announcement[].class);
    }

    /**
     * Returns a JSON diary bounded by week start and week end
     *
     * @param weekStart Start of week in format like 2022-10-17
     * @param weekEnd End of week in format like 2022-10-17
     * @return JSON String
     * @throws IOException When an IO exception occurred
     */
    public String getJsonDiary(String weekStart, String weekEnd) throws IOException {
        if(weekStart == null) weekStart = Utils.getCurrentWeekStart();
        if(weekEnd == null) weekEnd = Utils.getCurrentWeekEnd();

        String query = String.format(
                "?studentId=%d&yearId=%d&weekStart=%s&weekEnd=%s&withLaAssigns=true",
                this.userId,
                this.yearId,
                weekStart,
                weekEnd
        );

        try(CloseableHttpResponse response = this.client.get(NetSchoolAPI.DIARY + query)) {
            return this.objectMapper.readTree(response.getEntity().getContent()).toString();
        }
    }

    /**
     * Returns a diary object bounded by week start and week end
     *
     * @param weekStart Start of week in format like 2022-10-17
     * @param weekEnd End of week in format like 2022-10-17
     * @return Diary object
     * @throws IOException When an IO exception occurred
     */
    public Diary getDiary(String weekStart, String weekEnd) throws IOException {
        return this.objectMapper.readValue(this.getJsonDiary(weekStart, weekEnd), Diary.class);
    }

    /**
     * Converts diary object to a list of attachments
     * @param diary Diary object that represents a study week
     * @return List of the attachments on provided week (diary)
     * @throws IOException When an IO exception occurred
     */
    public List<Attachment> getAttachments(Diary diary) throws IOException {
        List<Integer> assignIds = new ArrayList<>();
        if(!Utils.validateArray(diary.weekDays)) return new ArrayList<>();
        for(Day day : diary.weekDays) {
            if(!Utils.validateArray(day.lessons)) continue;
            for(Lesson lesson : day.lessons) {
                if(!Utils.validateArray(lesson.assignments)) continue;
                for(Assignment assignment : lesson.assignments) {
                    assignIds.add(assignment.id);
                }
            }
        }

        try(CloseableHttpResponse response = this.client.post(
                NetSchoolAPI.GET_ATTACHMENTS + "?studentId=" + this.userId,
                "application/json; charset=UTF-8",
                new StringEntity("{\"assignId\":" + assignIds + "}"))) {
            JsonNode node = this.objectMapper.readTree(response.getEntity().getContent());
            List<Attachment> attachments = new ArrayList<>();
            for(JsonNode part : node) {
                JsonNode attachs = part.get("attachments");
                for(JsonNode attachmentElement : attachs) {
                    attachments.add(this.objectMapper.readValue(attachmentElement.toString(), Attachment.class));
                }
            }
            return attachments;
        }
    }

    public void downloadAttachment(File file, Attachment attachment) {
        if(file == null) file = new File(System.getProperty("user.dir") + "/attachments", attachment.originalFileName);

        try {
            InputStream content = this.client.get(String.format(NetSchoolAPI.ATTACHMENTS_DOWNLOAD, attachment.id)).getEntity().getContent();
            InputStream in = new BufferedInputStream(content);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[2048];
            int n;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(response);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns overdue jobs
     * @param weekStart Start of week in format like 2022-10-17
     * @param weekEnd   End of week in format like 2022-10-17
     * @return JSON String
     * @throws IOException When an IO exception occurred
     */
    public String getJsonOverdue(String weekStart, String weekEnd) throws IOException {
        if(weekStart == null) weekStart = Utils.getCurrentWeekStart();
        if(weekEnd == null) weekEnd = Utils.getCurrentWeekEnd();

        String query = String.format(
                "?studentId=%d&yearId=%d&weekStart=%s&weekEnd=%s",
                this.userId,
                this.yearId,
                weekStart,
                weekEnd
        );

        try(CloseableHttpResponse response = this.client.get(NetSchoolAPI.OVERDUE + query)) {
            return this.objectMapper.readTree(response.getEntity().getContent()).toString();
        }
    }

    //todo make Overdue class and corresponding method

    /**
     * Returns information about school
     *
     * @return JSON String
     * @throws IOException When an IO exception occurred
     */
    public String getJsonSchoolInfo() throws IOException {
        try(CloseableHttpResponse response = this.client.get(String.format(NetSchoolAPI.SCHOOL_INFO, this.school.id))) {
            return this.objectMapper.readTree(response.getEntity().getContent()).toString();
        }
    }

    /**
     * Returns information about school
     *
     * @return SchoolCard object
     * @throws IOException When an IO exception occurred
     */
    public SchoolCard getSchoolInfo() throws IOException {
        return this.objectMapper.readValue(this.getJsonSchoolInfo(), SchoolCard.class);
    }

    /**
     * Returns active sessions
     * @return JSON String
     * @throws IOException When an IO exception occurred
     */
    public String getJsonActiveSessions() throws IOException {
        try(CloseableHttpResponse response = this.client.get(NetSchoolAPI.ACTIVE_SESSIONS)) {
            return this.objectMapper.readTree(response.getEntity().getContent()).toString();
        }
    }

    /**
     * Returns active sessions
     * @return Array of UserSession objects
     * @throws IOException When an IO exception occurred
     */
    public UserSession[] getActiveSessions() throws IOException {
        return this.objectMapper.readValue(this.getJsonActiveSessions(), UserSession[].class);
    }

    /**
     * Logs out from the system
     */
    public void logout() {
        try {
            try(CloseableHttpResponse response = this.client.get(NetSchoolAPI.LOGOUT)) {
                if(response.getStatusLine().getStatusCode() == 401) { //unauthorized
                    //todo add something here
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes this object
     * @throws IOException When an IO exception occurred
     */
    @Override
    public void close() throws IOException {
        this.client.close();
    }

    private String getRequestBody(AuthDataStub ado) {
        return String.format("LoginType=1&cid=%d&sid=%d&pid=%d&cn=%d&sft=%d&scid=%d&UN=%s&PW=%s&lt=%d&pw2=%s&ver=%d",
                this.school.countryId, this.school.stateId, this.school.municipalityDistrictId,
                this.school.cityId, this.school.funcType, this.school.id, Utils.urlEncode(this.username, "UTF-8"),
                ado.pw, ado.lt, ado.pw2, ado.ver);
    }
}