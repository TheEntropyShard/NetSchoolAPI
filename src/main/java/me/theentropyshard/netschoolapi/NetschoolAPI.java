package me.theentropyshard.netschoolapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.theentropyshard.netschoolapi.dto.AuthDataObject;
import me.theentropyshard.netschoolapi.dto.GetDataObject;
import me.theentropyshard.netschoolapi.dto.SchoolDataObject;
import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class NetschoolAPI {
    private static final Logger log = Logger.getLogger();

    private final String username;
    private final String password;
    private final String schoolName;

    private final HttpClientWrapper client;

    private final ObjectMapper objectMapper;


    private int cid;
    /**
     * countryId
     */
    private int sid;
    /**
     * stateId
     */
    private int pid;
    /**
     * municipalityDistrictId
     */
    private int cn;
    /**
     * cityId
     */
    private int sft;
    /**
     * funcType (school function type)
     */
    private int scid;

    /**
     * id (school id)
     */

    private int userId;
    private int yearId;

    public NetschoolAPI(String baseUrl, String username, String password, String schoolName) {
        this.username = username;
        this.password = password;
        this.schoolName = schoolName;

        this.client = new HttpClientWrapper(baseUrl + "/webapi/");

        this.objectMapper = new ObjectMapper();

        this.init();
    }

    private void init() {
        log.info("Looking for schools...");
        try(CloseableHttpResponse response = this.client.get(Constants.SCHOOLS_URL)) {
            SchoolDataObject[] schoolDataObjects = this.objectMapper.readValue(response.getEntity().getContent(), SchoolDataObject[].class);
            for(SchoolDataObject schoolDataObject : schoolDataObjects) {
                if(schoolDataObject != null) {
                    if(schoolDataObject.name.equals(this.schoolName)) {
                        log.info("Found school \"" + this.schoolName + "\"");
                        this.cid = schoolDataObject.countryId;
                        this.sid = schoolDataObject.stateId;
                        this.pid = schoolDataObject.municipalityDistrictId;
                        this.cn = schoolDataObject.cityId;
                        this.sft = schoolDataObject.funcType;
                        this.scid = schoolDataObject.id;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not get schools data");
        }
    }

    public void login() {
        log.info("Trying to login...");

        try {
            AuthDataObject ado = this.getAuthData();
            String requestString = this.getRequestString(ado);
            StringEntity content = new StringEntity(requestString);

            try(CloseableHttpResponse response = this.client.post(Constants.LOGIN_URL, content)) {
                JsonNode node = this.objectMapper.readTree(response.getEntity().getContent());
                System.out.println(node);
                this.client.addHeader(new BasicHeader("at", node.get("at").textValue()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.info("Successfully logged in!");
    }

    public void printAnnouncements() {
        try {
            CloseableHttpResponse response = this.client.get(Constants.ANNOUNCEMENTS_URL + "?take=-1");
            Scanner sc = new Scanner(response.getEntity().getContent());
            while(sc.hasNextLine()) {
                System.out.println(sc.nextLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AuthDataObject getAuthData() {
        log.info("Trying to get auth data...");

        AuthDataObject authDataObject;
        try {
            GetDataObject getDataObject;
            try(CloseableHttpResponse response = this.client.post(Constants.GET_DATA_URL, new StringEntity(""))) {
                getDataObject = objectMapper.readValue(response.getEntity().getContent(), GetDataObject.class);
            }
            String encodedPassword = Utils.md5(this.password.getBytes(Charset.forName("windows-1251")));
            String pw2 = Utils.md5((getDataObject.salt + encodedPassword).getBytes(StandardCharsets.UTF_8));
            String pw = pw2.substring(0, this.password.length());
            authDataObject = new AuthDataObject(pw, pw2, getDataObject.lt, getDataObject.ver);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get auth data");
        } catch (NullPointerException e) {
            throw new RuntimeException("Internal error", e);
        }

        log.info("Successfully got auth data");

        return authDataObject;
    }

    public void logout() {
        log.info("Trying to log out...");
        try {
            try(CloseableHttpResponse response = this.client.get(Constants.LOGOUT_URL)) {
                if(response.getStatusLine().getStatusCode() == 401) { //unauthorized
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        log.info("Successfully logged out!");
    }

    private String getRequestString(AuthDataObject ado) {
        return String.format("LoginType=1&cid=%d&sid=%d&pid=%d&cn=%d&sft=%d&scid=%d&UN=%s&PW=%s&lt=%d&pw2=%s&ver=%d",
                this.cid, this.sid, this.pid, this.cn, this.sft, this.scid, Utils.urlEncode(this.username, "UTF-8"),
                ado.pw, ado.lt, ado.pw2, ado.ver);
    }
}