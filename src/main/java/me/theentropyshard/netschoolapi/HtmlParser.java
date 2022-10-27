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


}
