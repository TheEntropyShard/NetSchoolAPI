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
import me.theentropyshard.netschoolapi.HtmlParser;
import me.theentropyshard.netschoolapi.NetSchoolAPI;
import me.theentropyshard.netschoolapi.Urls;
import me.theentropyshard.netschoolapi.Utils;
import me.theentropyshard.netschoolapi.http.ContentType;
import me.theentropyshard.netschoolapi.http.HttpClientWrapper;
import me.theentropyshard.netschoolapi.mail.models.Mail;
import me.theentropyshard.netschoolapi.mail.models.MailBox;
import me.theentropyshard.netschoolapi.mail.models.Message;
import me.theentropyshard.netschoolapi.mail.models.SortingType;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

        try(Response response = this.client.post(this.api.getBaseUrl() + Urls.Asp.GET_MESSAGES + query, "", ContentType.FORM_URLENCODED)) {
            return this.objectMapper.readValue(Objects.requireNonNull(response.body()).byteStream(), Mail.class);
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

        try(Response response = this.client.get(this.api.getBaseUrl() + Urls.Asp.COMPOSE_MESSAGE + query)) {
            Document doc = Jsoup.parse(Objects.requireNonNull(response.body()).byteStream(), "UTF-8", "");
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

        try(
                Response response = this.client.post(
                        this.api.getBaseUrl() + Urls.Asp.SEND_MESSAGE,
                        Utils.toFormUrlEncoded(data),
                        ContentType.FORM_URLENCODED
                )
        ) {
            if(response.code() != 200) throw Utils.getError(Objects.requireNonNull(response.body()).byteStream());
        }
    }

    public Message readMessage(int messageId) throws IOException {
        String query = "?" + Utils.toFormUrlEncoded(
                Arrays.asList("ver", "at", "MID", "MBID"),
                Arrays.asList(this.api.getVer(), this.api.getAt(), messageId, 1)
        );

        StringBuilder builder = new StringBuilder();
        try(
                Response response = this.client.post(this.api.getBaseUrl() + Urls.Asp.READ_MESSAGE + query, "", ContentType.FORM_URLENCODED);
                InputStream is = Objects.requireNonNull(response.body()).byteStream();
                Scanner scanner = new Scanner(is)
        ) {
            if(response.code() != 200) throw Utils.getError(is);
            while(scanner.hasNextLine()) builder.append(scanner.nextLine());
        }
        return HtmlParser.parseMessage(builder.toString());
    }

    public void deleteMessage(MailBox mailBox, int messageId) throws IOException {
        String data = Utils.toFormUrlEncoded(
                Arrays.asList("AT", "nBoxId", "deletedMessages", "setWasSaved"),
                Arrays.asList(this.api.getAt(), mailBox.boxId, messageId, true)
        );
        try(Response response = this.client.post(this.api.getBaseUrl() + Urls.Asp.READ_MESSAGE, data, ContentType.FORM_URLENCODED)) {
            if(response.code() != 200) throw Utils.getError(Objects.requireNonNull(response.body()).byteStream());
        }
    }
}
