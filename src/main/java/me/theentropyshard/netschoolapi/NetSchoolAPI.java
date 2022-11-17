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
import me.theentropyshard.netschoolapi.mail.MailService;
import me.theentropyshard.netschoolapi.mail.schemas.Mail;
import me.theentropyshard.netschoolapi.mail.schemas.MailBox;
import me.theentropyshard.netschoolapi.mail.schemas.Message;
import me.theentropyshard.netschoolapi.mail.schemas.SortingType;
import me.theentropyshard.netschoolapi.reports.schemas.ReportsGroup;
import me.theentropyshard.netschoolapi.reports.schemas.StudentReport;
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
import java.util.*;

public class NetSchoolAPI implements Closeable {
    public static final String REPORTS = "reports/";

    private final String username;
    private final String password;
    private final String schoolName;
    private final String baseUrl;

    private final HttpClientWrapper client;
    private final ObjectMapper objectMapper;

    private final MailService mailService;

    private int yearId;
    private int studentId;
    private String classId;
    private String ver;
    private String at;
    private SchoolStub school;

    public NetSchoolAPI(String baseUrl, String username, String password, String schoolName) {
        this.username = username;
        this.password = password;
        this.schoolName = schoolName;

        if(baseUrl.charAt(baseUrl.length() - 1) == '/') baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        this.baseUrl = baseUrl;

        this.client = new HttpClientWrapper(baseUrl + "/webapi/");
        this.objectMapper = new ObjectMapper();

        this.mailService = new MailService(this);
    }

    private void findSchool() throws IOException {
        try(CloseableHttpResponse response = this.client.get(Urls.WebApi.SCHOOLS_DATABASE)) {
            SchoolStub[] schoolStubs = this.objectMapper.readValue(response.getEntity().getContent(), SchoolStub[].class);
            if(!Utils.validateArray(schoolStubs)) throw new RuntimeException("Школы не найдены");
            for(SchoolStub schoolStub : schoolStubs) {
                if(!schoolStub.name.equals(this.schoolName)) continue;
                if(schoolStub.funcType != 2) {
                    throw new RuntimeException("Поддерживаются только общеобразовательные школы (тип функциональности = 2), у вас " + schoolStub.funcType);
                }
                this.school = schoolStub;
                return;
            }
        }
        throw new RuntimeException("Не удалось найти школу \"" + this.schoolName + "\"");
    }

    /**
     * Выполняет вход в систему
     */
    public void login() {
        try {
            String salt;
            String lt;

            try(CloseableHttpResponse response = this.client.post(Urls.WebApi.GET_DATA, new StringEntity(""))) {
                JsonNode getDataNode = this.objectMapper.readTree(response.getEntity().getContent());
                this.ver = getDataNode.get("ver").textValue();
                salt = getDataNode.get("salt").textValue();
                lt = getDataNode.get("lt").textValue();
            }

            String pw2 = Utils.md5((salt + Utils.md5(this.password.getBytes(Charset.forName("windows-1251")))).getBytes(Charset.forName("UTF-8")));
            if(pw2 == null) {
                throw new RuntimeException("Не удалось хэшировать пароль");
            }

            this.findSchool();

            Map<String, Object> data = new HashMap<>();
            data.put("LoginType", 1);
            data.put("cid", this.school.countryId);
            data.put("sid", this.school.stateId);
            data.put("pid", this.school.municipalityDistrictId);
            data.put("cn", this.school.cityId);
            data.put("sft", 2);
            data.put("scid", this.school.id);
            data.put("UN", Utils.urlEncode(this.username, "UTF-8"));
            data.put("PW", pw2.substring(0, this.password.length()));
            data.put("lt", lt);
            data.put("pw2", pw2);
            data.put("ver", this.ver);

            try(CloseableHttpResponse response = this.client.post(Urls.WebApi.LOGIN, new StringEntity(Utils.toFormUrlEncoded(data)))) {
                JsonNode node = this.objectMapper.readTree(response.getEntity().getContent());
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

            this.classId = this.getPeriod().filterSources[1].items[0].title;
        } catch (IOException e) {
            throw new RuntimeException(e);
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

        String query = Utils.toFormUrlEncoded(
                Arrays.asList(
                        "studentId", "yearId",
                        "weekStart", "weekEnd"
                ),
                Arrays.asList(
                        this.studentId, this.yearId,
                        weekStart, weekEnd
                )
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
    public StudentReport getStudentGradesById(String id) throws IOException {
        return null;
    }

    /**
     * Возвращает Period
     *
     * @return Объект StudentReport
     * @throws IOException При IO ошибке
     */
    public StudentReport getPeriod() throws IOException {
        try(CloseableHttpResponse response = this.client.get(Urls.WebApi.REPORTS_STUDENTTOTAL)) {
            return this.objectMapper.readValue(response.getEntity().getContent(), StudentReport.class);
        }
    }

    /**
     * Возвращает HTML, в котором содержатся итоговые оценки
     *
     * @return HTML Строка
     * @throws IOException При IO ошибке
     */
    public String getTotalMarks() throws IOException {
        String data = Utils.toFormUrlEncoded(
                Arrays.asList("AT", "VER", "RPNAME", "RPTID"),
                Arrays.asList(this.at, this.ver, "Итоговые отметки", "StudentTotalMarks")
        );

        this.client.post(this.baseUrl + Urls.Asp.REPORT_STUDENT_TOTAL_MARKS, new StringEntity(data));

        String data2 = Utils.toFormUrlEncoded(
                Arrays.asList("AT", "VER", "LoginType", "RPTID", "SID"),
                Arrays.asList(this.at, this.ver, 0, "StudentTotalMarks", this.studentId)
        );

        try(CloseableHttpResponse response = this.client.post(this.baseUrl + Urls.Asp.STUDENT_TOTAL_MARKS, new StringEntity(data2))) {
            return Utils.readAsOneLine(response.getEntity().getContent());
        }
    }

    /**
     * Возвращает HTML, в котором содержится информационное письмо для родителей
     *
     * @return HTML Строка
     * @throws IOException При IO ошибке
     */
    public String getParentInfoLetter() throws IOException {
        String data = Utils.toFormUrlEncoded(
                Arrays.asList("AT", "VER", "RPNAME", "RPTID"),
                Arrays.asList(this.at, this.ver, "Информационное письмо для родителей", "ParentInfoLetter")
        );

        this.client.post(this.baseUrl + Urls.Asp.REPORT_PARENT_INFO_LETTER, new StringEntity(data));

        String data2 = Utils.toFormUrlEncoded(
                Arrays.asList(
                        "AT", "ver", "LoginType",
                        "RPTID", "SID", "ReportType",
                        "PCLID", "TERMID"
                ),
                Arrays.asList(
                        this.at, this.ver, 0,
                        "ParentInfoLetter", this.studentId,
                        2, this.classId, this.getTermId(1)
                )
        );

        try(CloseableHttpResponse response = this.client.post(this.baseUrl + Urls.Asp.PARENT_INFO_LETTER, new StringEntity(data2))) {
            return Utils.readAsOneLine(response.getEntity().getContent());
        }
    }

    /**
     * Возвращает Id четверти по ее номеру (1, 2, 3, 4)
     *
     * @param term Номер четверти
     * @return Id четветрти
     * @throws IOException При IO ошибке
     */
    public int getTermId(int term) throws IOException {
        if(term < 1) term = 0;

        String data = Utils.toFormUrlEncoded(
                Arrays.asList("AT", "VER", "RPNAME", "RPTID"),
                Arrays.asList(this.at, this.ver, "Информационное письмо для родителей", "ParentInfoLetter")
        );

        try(CloseableHttpResponse response = this.client.post(this.baseUrl + Urls.Asp.REPORT_PARENT_INFO_LETTER, new StringEntity(data))) {
            return HtmlParser.parseTermId(response.getEntity().getContent()).get(term - 1);
        }
    }

    /**
     * Возвращает все письма по данным параметрам
     *
     * @param mailBox      Id почтового ящика, {@link MailBox}
     * @param startIndex Письмо, с которого начинать
     * @param pageSize   Количество писем в одном объекте Mail
     * @param type       Тип сортировки, один вариант
     * @return Объект Mail
     * @throws IOException При IO ошибке
     */
    public Mail getMail(MailBox mailBox, int startIndex, int pageSize, SortingType type) throws IOException {
        return this.mailService.getMail(mailBox, startIndex, pageSize, type);
    }

    /**
     * Отправляет письмо
     *
     * @param message Объект Message
     * @param notify  Отправить ли письмо о прочтении
     * @throws IOException при IO ошибке
     */
    public void sendMessage(Message message, boolean notify) throws IOException {
        this.mailService.sendMessage(message, notify);
    }

    /**
     * Возвращает объект Message
     *
     * @param messageId Id письма
     * @return объект Message
     * @throws IOException при IO ошибке
     */
    public Message readMessage(int messageId) throws IOException {
        return this.mailService.readMessage(messageId);
    }

    /**
     * Удаляет письмо по Id
     *
     * @param mailBox   Почтовый ящик
     * @param messageId Id письма
     * @throws IOException при IO ошибке
     */
    public void deleteMessage(MailBox mailBox, int messageId) throws IOException {
        this.mailService.deleteMessage(mailBox, messageId);
    }

    public List<AssignmentType> getAssignmentTypes(boolean all) throws IOException {
        try(CloseableHttpResponse response = this.client.get(Urls.WebApi.ASSIGNMENT_TYPES + all)) {
            return Arrays.asList(this.objectMapper.readValue(response.getEntity().getContent(), AssignmentType[].class));
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

        String query = "?" + Utils.toFormUrlEncoded(
                Arrays.asList(
                        "studentId", "yearId",
                        "weekStart", "weekEnd"
                ),
                Arrays.asList(
                        this.studentId, this.yearId,
                        weekStart, weekEnd
                )
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

            this.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public HttpClientWrapper getClient() {
        return this.client;
    }

    public int getYearId() {
        return this.yearId;
    }

    public int getStudentId() {
        return this.studentId;
    }

    public String getClassId() {
        return this.classId;
    }

    public String getVer() {
        return this.ver;
    }

    public String getAt() {
        return this.at;
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
}