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
import me.theentropyshard.netschoolapi.jsonstubs.AuthDataStub;
import me.theentropyshard.netschoolapi.jsonstubs.GetDataStub;
import me.theentropyshard.netschoolapi.jsonstubs.SchoolStub;
import me.theentropyshard.netschoolapi.mail.schemas.Mail;
import me.theentropyshard.netschoolapi.mail.schemas.MailBoxIds;
import me.theentropyshard.netschoolapi.mail.schemas.SortingType;
import me.theentropyshard.netschoolapi.reports.schemas.ReportsGroup;
import me.theentropyshard.netschoolapi.reports.schemas.StudentGrades;
import me.theentropyshard.netschoolapi.schemas.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class NetSchoolAPI implements Closeable {
    public static final String REPORTS = "reports/";

    private final String username;
    private final String password;
    private final String schoolName;
    private final String baseUrl;

    private final HttpClientWrapper client;
    private final ObjectMapper objectMapper;

    private int studentId;
    private int yearId;
    private String ver;
    private String at;
    private SchoolStub school;

    public NetSchoolAPI(String baseUrl, String username, String password, String schoolName) {
        this.username = username;
        this.password = password;
        this.schoolName = schoolName;

        this.baseUrl = baseUrl;

        if(baseUrl.charAt(baseUrl.length() - 1) == '/') baseUrl = baseUrl.substring(0, baseUrl.length() - 1);

        this.client = new HttpClientWrapper(baseUrl + "/webapi/");
        this.objectMapper = new ObjectMapper();
    }

    private void getSchoolData() {
        try(CloseableHttpResponse response = this.client.get(Urls.WebApi.SCHOOLS_DATABASE)) {
            SchoolStub[] schoolStubs = this.objectMapper.readValue(response.getEntity().getContent(), SchoolStub[].class);
            for(SchoolStub schoolStub : schoolStubs) {
                if(schoolStub == null) break;
                if(!schoolStub.name.equals(this.schoolName)) continue;
                if(schoolStub.funcType != 2) {
                    throw new RuntimeException("Поддерживаются только общеобразовательные школы (тип функциональности = 2), у вас " + schoolStub.funcType);
                }
                this.school = schoolStub;
                return;
            }
        } catch (IOException ignored) {
        }
        throw new RuntimeException("Не удалось найти школу \"" + this.schoolName + "\"");
    }

    private AuthDataStub getAuthData() {
        try {
            GetDataStub getDataObject;
            try(CloseableHttpResponse response = this.client.post(Urls.WebApi.GET_DATA, new StringEntity(""))) {
                getDataObject = objectMapper.readValue(response.getEntity().getContent(), GetDataStub.class);
            }
            String encodedPassword = Utils.md5(this.password.getBytes(Charset.forName("windows-1251")));
            String pw2 = Utils.md5((getDataObject.salt + encodedPassword).getBytes(Charset.forName("UTF-8"))); //for android compatibility
            if(pw2 == null) {
                throw new RuntimeException("Не удалось хэшировать пароль");
            }
            String pw = pw2.substring(0, this.password.length());
            return new AuthDataStub(pw, pw2, getDataObject.lt, getDataObject.ver);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось получить данные для авторизации");
        }
    }

    /**
     * Выполняет вход в систему
     */
    public void login() {
        try {
            this.getSchoolData();

            AuthDataStub ado = this.getAuthData();
            this.ver = String.valueOf(ado.ver);
            String requestString = this.getRequestBody(ado);
            StringEntity content = new StringEntity(requestString);

            try(CloseableHttpResponse response = this.client.post(Urls.WebApi.LOGIN, content)) {
                JsonNode node = this.objectMapper.readTree(response.getEntity().getContent());
                System.out.println(node);
                String at = node.get("at").textValue();
                this.at = at;
                this.client.addHeader(new BasicHeader("at", at));
            }

            try(CloseableHttpResponse response = this.client.get(Urls.WebApi.DIARY_INIT)) {
                JsonNode node = this.objectMapper.readTree(response.getEntity().getContent());
                int currentStudentId = node.get("currentStudentId").intValue();
                this.studentId = node.get("students").get(currentStudentId).get("studentId").intValue();
            }

            try(CloseableHttpResponse response = this.client.get(Urls.WebApi.YEARS_CURRENT)) {
                JsonNode node = this.objectMapper.readTree(response.getEntity().getContent());
                this.yearId = node.get("id").intValue();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Возвращает объявления в виде массива объявлений
     *
     * @return Список объявлений
     * @throws IOException При IO ошибке
     */
    public List<Announcement> getAnnouncements() throws IOException {
        try(CloseableHttpResponse response = this.client.get(Urls.WebApi.ANNOUNCEMENTS + "?take=-1")) {
            return Arrays.asList(this.objectMapper.readValue(response.getEntity().getContent(), Announcement[].class));
        }
    }

    /**
     * Возвращает объект дневника от и до определенной даты (включительно)
     *
     * @param weekStart Начало недели в формате 2022-10-17
     * @param weekEnd   Конец недели в формате 2022-10-17
     * @return Объект дневника
     * @throws IOException При IO ошибке
     */
    public Diary getDiary(String weekStart, String weekEnd) throws IOException {
        if(weekStart == null) weekStart = Utils.getCurrentWeekStart();
        if(weekEnd == null) weekEnd = Utils.getCurrentWeekEnd();

        String query = String.format(
                "?studentId=%d&yearId=%d&weekStart=%s&weekEnd=%s&withLaAssigns=true",
                this.studentId,
                this.yearId,
                weekStart,
                weekEnd
        );

        try(CloseableHttpResponse response = this.client.get(Urls.WebApi.DIARY + query)) {
            return this.objectMapper.readValue(response.getEntity().getContent(), Diary.class);
        }
    }

    /**
     * Возвращает все доступные отчеты
     *
     * @return Объект ReportsGroup
     * @throws IOException При IO ошибке
     */
    public List<ReportsGroup> getAvailableReports() throws IOException {
        try(CloseableHttpResponse response = this.client.get(NetSchoolAPI.REPORTS)) {
            return Arrays.asList(this.objectMapper.readValue(response.getEntity().getContent(), ReportsGroup[].class));
        }
    }

    /**
     * Возвращает объект оценок ученика
     *
     * @param id Идентификатор отчета из {@code getAvailableReports()}
     * @return Объект StudentGrades
     * @throws IOException При IO ошибке
     */
    public StudentGrades getStudentGradesById(String id) throws IOException {
        return null;
    }

    //TODO implement receiving reports

    /**
     * Возвращает все письма по данным параметрам
     * @param boxId Id почтового ящика, {@link MailBoxIds}
     * @param startIndex Письмо, с которого начинать
     * @param pageSize Количество писем в одном объекте Mail
     * @param type Тип сортировки, один вариант
     * @return Объект Mail
     * @throws IOException При IO ошибке
     */
    public Mail getMail(int boxId, int startIndex, int pageSize, SortingType type) throws IOException {
        String query = "?";
        query = query + "AT=" + this.at + "&";
        query = query + "nBoxID=" + boxId + "&";
        query = query + "jtStartIndex=" + startIndex + "&";
        query = query + "jtPageSize=" + pageSize + "&";
        query = query + "jtSorting=" + type.VALUE;

        try(CloseableHttpResponse response = this.client.post(this.baseUrl + Urls.Asp.GET_MESSAGES + query, new StringEntity(""))) {
            return this.objectMapper.readValue(response.getEntity().getContent(), Mail.class);
        }
    }

    public void sendMail() {

    }

    /**
     * Удаляет письмо по Id
     * @param boxId Id почтового ящика, {@link MailBoxIds}
     * @param messageId Id письма
     * @throws IOException При IO ошибке
     */
    public void deleteMail(int boxId, int messageId) throws IOException {
        String data = "AT=" + this.at + "&nBoxId=" + boxId + "&deletedMessages=" + messageId + "&setWasSaved=true";
        StringEntity content = new StringEntity(data);
        try(CloseableHttpResponse response = this.client.post(this.baseUrl + Urls.Asp.DELETE_MESSAGES, content);
            Scanner scanner = new Scanner(response.getEntity().getContent())) {
            if(response.getStatusLine().getStatusCode() != 200) {
                StringBuilder builder = new StringBuilder();
                while(scanner.hasNextLine()) builder.append(scanner.nextLine());
                throw new IOException(builder.toString());
            }
        }
    }

    /**
     * Достает все прикрепленные файлы из объекта дневника
     *
     * @param diary Объект дневника, представляющий неделю
     * @return Список всех прикрепленных файлов на предоставленной неделе (дневник)
     * @throws IOException При IO ошибке
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
                Urls.WebApi.GET_ATTACHMENTS + "?studentId=" + this.studentId,
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

    /**
     * Скачивает прикрепленный файл
     *
     * @param file       Файл, в который сохранить скачанный файл
     * @param attachment Прикрепленный файл, который нужно скачать
     */
    public void downloadAttachment(File file, Attachment attachment) throws IOException {
        if(file == null) file = new File(System.getProperty("user.dir") + "/attachments", attachment.originalFileName);

        InputStream content = this.client.get(String.format(Urls.WebApi.ATTACHMENTS_DOWNLOAD, attachment.id)).getEntity().getContent();
        Files.copy(content, file.toPath());
    }

    public String getGradesForSubject(String startDate, String endDate, int subjectId) throws IOException {

        try(CloseableHttpResponse response = this.client.get("")) {

        }

        return "";
    }

    /**
     * Возвращает пропущенные задания
     *
     * @param weekStart Начало недели в формате 2022-10-17
     * @param weekEnd   Конец недели в формате 2022-10-17
     * @return Список заданий
     * @throws IOException При IO ошибке
     */
    public List<Assignment> getOverdueJobs(String weekStart, String weekEnd) throws IOException {
        if(weekStart == null) weekStart = Utils.getCurrentWeekStart();
        if(weekEnd == null) weekEnd = Utils.getCurrentWeekEnd();

        String query = String.format(
                "?studentId=%d&yearId=%d&weekStart=%s&weekEnd=%s",
                this.studentId,
                this.yearId,
                weekStart,
                weekEnd
        );

        try(CloseableHttpResponse response = this.client.get(Urls.WebApi.OVERDUE + query)) {
            return Arrays.asList(this.objectMapper.readValue(response.getEntity().getContent(), Assignment[].class));
        }
    }

    /**
     * Возвращает информацию о школе
     *
     * @return Объект SchoolCard
     * @throws IOException При IO ошибке
     */
    public SchoolCard getSchoolInfo() throws IOException {
        try(CloseableHttpResponse response = this.client.get(String.format(Urls.WebApi.SCHOOL_INFO, this.school.id))) {
            return this.objectMapper.readValue(response.getEntity().getContent(), SchoolCard.class);
        }
    }

    /**
     * Возвращает активные сессии
     *
     * @return Список объектов UserSession
     * @throws IOException При IO ошибке
     */
    public List<UserSession> getActiveSessions() throws IOException {
        try(CloseableHttpResponse response = this.client.get(Urls.WebApi.ACTIVE_SESSIONS)) {
            return Arrays.asList(this.objectMapper.readValue(response.getEntity().getContent(), UserSession[].class));
        }
    }

    /**
     * Выполняет выход из системы
     */
    public void logout() {
        try {
            try(CloseableHttpResponse response = this.client.get(Urls.WebApi.LOGOUT)) {
                if(response.getStatusLine().getStatusCode() == 401) { //unauthorized
                    System.out.println("Unauthorized or already logged out");
                    return;
                }
                Scanner sc = new Scanner(response.getEntity().getContent());
                while(sc.hasNextLine()) {
                    System.out.println(sc.nextLine());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes this object
     *
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