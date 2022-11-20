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

package me.theentropyshard.netschoolapi.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.theentropyshard.netschoolapi.NetSchoolAPI;
import me.theentropyshard.netschoolapi.http.HttpClientWrapper;
import okhttp3.Request;
import okhttp3.WebSocket;

import java.io.IOException;

public class ReportsService {
    private final NetSchoolAPI api;
    private final HttpClientWrapper client;
    private final ObjectMapper objectMapper;

    public ReportsService(NetSchoolAPI api) {
        this.api = api;
        this.client = api.getClient();
        this.objectMapper = new ObjectMapper();
    }

    public String getReport(ReportType reportType) throws IOException {
        Request request = new Request.Builder()
                .url("wss://giseo.rkomi.ru/WebApi/signalr/connect")
                .build();
        WebSocketListenerImpl webSocketListener = new WebSocketListenerImpl();
        WebSocket ws = this.client.getClient().newWebSocket(request, webSocketListener);

        //TODO

        return "Empty";
    }

    public String getStudentTotalMarks() throws IOException {
        /*String data = Utils.toFormUrlEncoded(
                Arrays.asList("AT", "VER", "RPNAME", "RPTID"),
                Arrays.asList(this.api.getAt(), this.api.getVer(), "Итоговые отметки", "StudentTotalMarks")
        );

        this.client.post(this.api.getBaseUrl() + Urls.Asp.REPORT_STUDENT_TOTAL_MARKS, new StringEntity(data));

        String data2 = Utils.toFormUrlEncoded(
                Arrays.asList("AT", "VER", "LoginType", "RPTID", "SID"),
                Arrays.asList(this.api.getAt(), this.api.getVer(), 0, "StudentTotalMarks", this.api.getStudentId())
        );

        try(CloseableHttpResponse response = this.client.post(this.api.getBaseUrl() + Urls.Asp.STUDENT_TOTAL_MARKS, new StringEntity(data2))) {
            return Utils.readAsOneLine(response.getEntity().getContent());
        }*/
        return "Empty";
    }

    public String getStudentAverageMark() throws IOException {
        /*try(CloseableHttpResponse response = this.client.get(this.api.getBaseUrl() + "/webapi/integration/poo/reports/" + "StudentTotalMarks")) {
            return Utils.readAsOneLine(response.getEntity().getContent());
        }*/
        return "Empty";
    }
}
