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

package me.theentropyshard.netschoolapi.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.theentropyshard.netschoolapi.*;
import me.theentropyshard.netschoolapi.mail.schemas.Mail;
import me.theentropyshard.netschoolapi.mail.schemas.MailBox;
import me.theentropyshard.netschoolapi.mail.schemas.Message;
import me.theentropyshard.netschoolapi.mail.schemas.SortingType;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MailService {
    private final NetSchoolAPI api;
    private final HttpClientWrapper client;
    private final ObjectMapper objectMapper;

    public MailService(NetSchoolAPI api) {
        this.api = api;
        this.client = api.getClient();
        this.objectMapper = new ObjectMapper();
    }

    public Mail getMail(MailBox mailBox, int startIndex, int pageSize, SortingType type) throws IOException {
        String query = "?" + Utils.toFormUrlEncoded(
                Arrays.asList("AT", "nBoxId", "jtStartIndex", "jtPageSize", "jtSorting"),
                Arrays.asList(this.api.getAt(), mailBox.boxId, startIndex, pageSize, type.VALUE)
        );

        try(CloseableHttpResponse response = this.client.post(this.api.getBaseUrl() + Urls.Asp.GET_MESSAGES + query, new StringEntity(""))) {
            return this.objectMapper.readValue(response.getEntity().getContent(), Mail.class);
        }
    }

    public void sendMessage(Message message, boolean notify) throws IOException {
        if(message.receiver == null || message.receiver.trim().isEmpty()) {
            throw new IOException("Получатель письма не может быть пустым или null");
        }

        if(message.text == null || message.text.trim().isEmpty()) {
            throw new IOException("Текст письма не может быть пустым или null");
        }

        if(message.text.length() > 65535) {
            throw new IOException("Сообщение слишком большое (длина больше 65535 символов)");
        }

        String antiForgeryToken = "";
        String query = "?" + Utils.toFormUrlEncoded(
                Arrays.asList("ver", "at"),
                Arrays.asList(this.api.getVer(), this.api.getAt())
        );

        try(CloseableHttpResponse response = this.client.get(this.api.getBaseUrl() + Urls.Asp.COMPOSE_MESSAGE + query)) {
            Document doc = Jsoup.parse(response.getEntity().getContent(), "UTF-8", "");
            for(Element input : doc.getElementsByTag("input")) {
                if(input.attr("name").equals("AntiForgeryToken")) {
                    antiForgeryToken = input.attr("value");
                }
            }
        }

        if(antiForgeryToken.isEmpty()) {
            throw new IOException("AntiForgeryToken пуст. Невозможно отправить письмо");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("LoginType", 0);
        data.put("AT", this.api.getAt());
        data.put("VER", this.api.getVer());
        data.put("AntiForgeryToken", antiForgeryToken);
        data.put("MID", "");
        data.put("MBID", 3);
        data.put("LTO", this.api.getStudentId());
        data.put("LCC", "");
        data.put("LBC", "");
        data.put("TA", "");
        data.put("NA", "");
        data.put("PP", "");
        data.put("DMID", "");
        data.put("RT", "");
        data.put("DESTINATION", "");
        data.put("ShortAttach", 1);
        data.put("EDITUSERID", this.api.getStudentId());

        data.put("ATO", Utils.urlEncode(message.receiver.trim(), "UTF-8"));
        data.put("ACC", "");
        data.put("ABC", "");
        data.put("SU", Utils.urlEncode(message.subject.trim(), "UTF-8"));
        data.put("NEEDNOTIFY", notify ? 1 : 0);
        data.put("BO", Utils.urlEncode(message.text.trim(), "UTF-8"));

        try(CloseableHttpResponse response = this.client.post(this.api.getBaseUrl() + Urls.Asp.SEND_MESSAGE, new StringEntity(Utils.toFormUrlEncoded(data)));
            Scanner scanner = new Scanner(response.getEntity().getContent())) {
            if(response.getStatusLine().getStatusCode() != 200) {
                StringBuilder builder = new StringBuilder();
                while(scanner.hasNextLine()) builder.append(scanner.nextLine());
                throw new IOException(builder.toString());
            }
        }
    }

    public Message readMessage(int messageId) throws IOException {
        String query = "?" + Utils.toFormUrlEncoded(
                Arrays.asList("ver", "at", "MID", "MBID"),
                Arrays.asList(this.api.getVer(), this.api.getAt(), messageId, 1)
        );

        StringBuilder builder = new StringBuilder();
        try(CloseableHttpResponse response = this.client.post(this.api.getBaseUrl() + Urls.Asp.READ_MESSAGE + query, new StringEntity(""));
            Scanner scanner = new Scanner(response.getEntity().getContent())) {
            while(scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
            }
        }
        return HtmlParser.parseMessage(builder.toString());
    }

    public void deleteMessage(MailBox mailBox, int messageId) throws IOException {
        String data = Utils.toFormUrlEncoded(
                Arrays.asList("AT", "nBoxId", "deletedMessages", "setWasSaved"),
                Arrays.asList(this.api.getAt(), mailBox.boxId, messageId, true)
        );
        try(CloseableHttpResponse response = this.client.post(this.api.getBaseUrl() + Urls.Asp.DELETE_MESSAGES, new StringEntity(data));
            Scanner scanner = new Scanner(response.getEntity().getContent())) {
            if(response.getStatusLine().getStatusCode() != 200) {
                StringBuilder builder = new StringBuilder();
                while(scanner.hasNextLine()) builder.append(scanner.nextLine());
                throw new IOException(builder.toString());
            }
        }
    }
}
