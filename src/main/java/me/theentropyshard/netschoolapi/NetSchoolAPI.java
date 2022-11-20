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
import me.theentropyshard.netschoolapi.diary.DiaryService;
import me.theentropyshard.netschoolapi.diary.schemas.*;
import me.theentropyshard.netschoolapi.http.ContentType;
import me.theentropyshard.netschoolapi.http.HttpClientWrapper;
import me.theentropyshard.netschoolapi.mail.MailService;
import me.theentropyshard.netschoolapi.mail.schemas.Mail;
import me.theentropyshard.netschoolapi.mail.schemas.MailBox;
import me.theentropyshard.netschoolapi.mail.schemas.Message;
import me.theentropyshard.netschoolapi.mail.schemas.SortingType;
import me.theentropyshard.netschoolapi.reports.ReportType;
import me.theentropyshard.netschoolapi.reports.ReportsService;
import me.theentropyshard.netschoolapi.reports.schemas.ReportsGroup;
import okhttp3.Headers;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

public class NetSchoolAPI {
    public static final String REPORTS = "reports/";

    private final String username;
    private final String password;
    private final String schoolName;
    private final String baseUrl;

    private final HttpClientWrapper client;
    private final ObjectMapper objectMapper;

    private final MailService mailService;
    private final ReportsService reportsService;
    private final DiaryService diaryService;

    private int yearId;
    private int studentId;
    private String classId;
    private String className;
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
        this.reportsService = new ReportsService(this);
        this.diaryService = new DiaryService(this);
    }

    private void findSchool() throws IOException {
        try(Response response = this.client.get(Urls.WebApi.SCHOOLS_DATABASE)) {
            SchoolStub[] schoolStubs = this.objectMapper.readValue(Objects.requireNonNull(response.body()).byteStream(), SchoolStub[].class);
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
    public final void login() {
        try {
            String salt;
            String lt;

            try(Response response = this.client.post(Urls.WebApi.GET_DATA, "")) {
                JsonNode getDataNode = this.objectMapper.readTree(Objects.requireNonNull(response.body()).byteStream());
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

            try(Response response = this.client.post(Urls.WebApi.LOGIN, Utils.toFormUrlEncoded(data))) {
                JsonNode node = this.objectMapper.readTree(Objects.requireNonNull(response.body()).byteStream());
                String at = node.get("at").textValue();
                this.at = at;
                this.client.addHeader(Headers.of("at", at));
            }

            try(Response response = this.client.get(Urls.WebApi.DIARY_INIT)) {
                JsonNode node = this.objectMapper.readTree(Objects.requireNonNull(response.body()).byteStream());
                int currentStudentId = node.get("currentStudentId").intValue();
                this.studentId = node.get("students").get(currentStudentId).get("studentId").intValue();
            }

            try(Response response = this.client.get(Urls.WebApi.YEARS_CURRENT)) {
                JsonNode node = this.objectMapper.readTree(Objects.requireNonNull(response.body()).byteStream());
                this.yearId = node.get("id").intValue();
            }

            try(Response response = this.client.get(Urls.WebApi.REPORTS_STUDENTTOTAL)) {
                JsonNode node = this.objectMapper.readTree(Objects.requireNonNull(response.body()).byteStream());
                this.classId = node
                        .get("filterSources").get(1)
                        .get("defaultValue").textValue();
                this.className = node
                        .get("filterSources").get(1)
                        .get("items").get(0)
                        .get("title").textValue();
            }
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
        try(Response response = this.client.get(Urls.WebApi.ANNOUNCEMENTS + "?take=-1")) {
            return Arrays.asList(this.objectMapper.readValue(Objects.requireNonNull(response.body()).byteStream(), Announcement[].class));
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
        return this.diaryService.getDiary(weekStart, weekEnd);
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
        return this.diaryService.getOverdueJobs(weekStart, weekEnd);
    }

    /**
     * Возвращает подробную информацию об уроке
     *
     * @param assignmentId Id урока
     * @return Объект DetailedAssignment
     * @throws IOException При IO ошибке
     */
    public DetailedAssignment getDetailedAssignment(int assignmentId) throws IOException {
        return this.diaryService.getDetailedAssignment(assignmentId);
    }

    /**
     * Возвращает все доступные отчеты
     *
     * @return Объект ReportsGroup
     * @throws IOException При IO ошибке
     */
    public List<ReportsGroup> getAvailableReports() throws IOException {
        try(Response response = this.client.get(NetSchoolAPI.REPORTS)) {
            return Arrays.asList(this.objectMapper.readValue(Objects.requireNonNull(response.body()).byteStream(), ReportsGroup[].class));
        }
    }

    /**
     * Возвращает Period
     *
     * @return Объект StudentReport
     * @throws IOException При IO ошибке
     */
    /*public StudentReport getPeriod() throws IOException {
        try(CloseableHttpResponse response = this.client.get(Urls.WebApi.REPORTS_STUDENTTOTAL)) {
            return this.objectMapper.readValue(response.getEntity().getContent(), StudentReport.class);
        }
    }*/

    public void dos() throws IOException {
        this.reportsService.getReport(ReportType.STUDENT_TOTAL_MARKS);
    }

    /**
     * Возвращает Id четверти по ее номеру (1, 2, 3, 4)
     *
     * @param term Номер четверти по {@link Term}
     * @return Id четветрти
     * @throws IOException При IO ошибке
     */
    public int getTermId(Term term) throws IOException {
        String data = Utils.toFormUrlEncoded(
                Arrays.asList("AT", "VER", "RPNAME", "RPTID"),
                Arrays.asList(this.at, this.ver, "Информационное письмо для родителей", "ParentInfoLetter")
        );

        try(Response response = this.client.post(this.baseUrl + Urls.Asp.REPORT_PARENT_INFO_LETTER, data)) {
            return HtmlParser.parseTermId(Objects.requireNonNull(response.body()).byteStream()).get(term.number);
        }
    }

    /**
     * Возвращает все письма по данным параметрам
     *
     * @param mailBox    Id почтового ящика, {@link MailBox}
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

    /**
     * Возвращает типы оценок
     *
     * @param all Все типы или нет
     * @return Список типов оценок
     * @throws IOException При IO ошибке
     */
    public List<AssignmentType> getAssignmentTypes(boolean all) throws IOException {
        try(Response response = this.client.get(Urls.WebApi.ASSIGNMENT_TYPES + all)) {
            return Arrays.asList(this.objectMapper.readValue(Objects.requireNonNull(response.body()).byteStream(), AssignmentType[].class));
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

        try(Response response = this.client.post(
                Urls.WebApi.GET_ATTACHMENTS + "?studentId=" + this.studentId,
                "{\"assignId\":" + assignIds + "}",
                ContentType.JSON)) {
            JsonNode node = this.objectMapper.readTree(Objects.requireNonNull(response.body()).byteStream());
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
        String fileName = attachment.name != null ? attachment.name : attachment.originalFileName;
        if(file == null) file = new File(System.getProperty("user.dir") + "/attachments", fileName);

        Files.copy(Objects.requireNonNull(this.client.get(String.format(Urls.WebApi.ATTACHMENTS_DOWNLOAD, attachment.id)).body()).byteStream(), file.toPath());
    }

    /**
     * Возвращает информацию о школе
     *
     * @return Объект SchoolCard
     * @throws IOException При IO ошибке
     */
    public SchoolCard getSchoolInfo() throws IOException {
        try(Response response = this.client.get(String.format(Urls.WebApi.SCHOOL_INFO, this.school.id))) {
            return this.objectMapper.readValue(Objects.requireNonNull(response.body()).byteStream(), SchoolCard.class);
        }
    }

    /**
     * Возвращает активные сессии
     *
     * @return Список объектов UserSession
     * @throws IOException При IO ошибке
     */
    public List<UserSession> getActiveSessions() throws IOException {
        try(Response response = this.client.get(Urls.WebApi.ACTIVE_SESSIONS)) {
            return Arrays.asList(this.objectMapper.readValue(Objects.requireNonNull(response.body()).byteStream(), UserSession[].class));
        }
    }

    /**
     * Выполняет выход из системы
     */
    public final void logout() {
        try {
            try(Response response = this.client.get(Urls.WebApi.LOGOUT)) {
                if(response.code() == 401) {
                    System.out.println("Unauthorized or already logged out");
                    return;
                }
                Scanner sc = new Scanner(Objects.requireNonNull(response.body()).byteStream());
                while(sc.hasNextLine()) {
                    System.out.println(sc.nextLine());
                }
                sc.close();
            }
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

    public String getClassName() {
        return this.className;
    }

    public String getVer() {
        return this.ver;
    }

    public String getAt() {
        return this.at;
    }
}