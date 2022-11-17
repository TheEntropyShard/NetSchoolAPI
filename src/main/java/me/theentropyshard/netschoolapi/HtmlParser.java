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

import me.theentropyshard.netschoolapi.mail.schemas.Message;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class HtmlParser {
    private HtmlParser() {
        throw new UnsupportedOperationException("Class HtmlParser should not be instantiated");
    }

    public static List<Integer> parseTermId(InputStream is) throws IOException {
        List<Integer> terms = new ArrayList<>();

        Document doc = Jsoup.parse(is, "UTF-8", "");
        for(Element select : doc.getElementsByTag("select")) {
            if(select.hasClass("form-control") && select.attr("name").equals("TERMID")) {
                for(Element option : select.getElementsByTag("option")) {
                    terms.add(Integer.valueOf(option.attr("value")));
                }
            }
        }
        return terms;
    }

    public static Message parseMessage(String html) throws IOException {
        Document doc = Jsoup.parse(html);
        Element messageHeaders = doc.getElementById("message_headers");
        Element messageBody = doc.getElementById("message_body");

        if(messageHeaders == null) {
            throw new IOException("Не удалось найти заголовки сообщения");
        }

        if(messageBody == null) {
            throw new IOException("Не удалось найти текст сообщения");
        }

        Message message = new Message();

        for(Element formGroup : messageHeaders.getElementsByClass("form-group")) {
            Element label = formGroup.getElementsByClass("control-label col-md-4 col-lg-3 col-sm-4").get(0);
            Element input = formGroup.getElementsByTag("input").get(0);
            switch(label.text()) {
                case "От кого":
                    message.sender = input.attr("value");
                    break;
                case "Кому":
                    message.receiver = input.attr("value");
                    break;
                case "Отправлено":
                    message.sentDate = input.attr("value");
                    break;
            }
        }

        for(Element formGroup : messageBody.getElementsByClass("form-group")) {
            Element label = formGroup.getElementsByClass("control-label ").get(0);
            switch(label.text()) {
                case "Тема":
                    Element input = formGroup.getElementsByTag("input").get(0);
                    message.subject = input.attr("value");
                    break;
                case "Текст":
                    message.text = formGroup.text().substring(6);
                    break;
            }
        }

        return message;
    }
}
