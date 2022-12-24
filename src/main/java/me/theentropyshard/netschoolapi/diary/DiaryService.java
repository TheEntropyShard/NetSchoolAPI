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

package me.theentropyshard.netschoolapi.diary;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.theentropyshard.netschoolapi.NetSchoolAPI;
import me.theentropyshard.netschoolapi.Urls;
import me.theentropyshard.netschoolapi.Utils;
import me.theentropyshard.netschoolapi.diary.models.Assignment;
import me.theentropyshard.netschoolapi.diary.models.DetailedAssignment;
import me.theentropyshard.netschoolapi.diary.models.Diary;
import me.theentropyshard.netschoolapi.http.HttpClientWrapper;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;

public class DiaryService {
    private final NetSchoolAPI api;
    private final HttpClientWrapper client;
    private final ObjectMapper objectMapper;

    public DiaryService(NetSchoolAPI api) {
        this.api = api;
        this.client = api.getClient();
        this.objectMapper = new ObjectMapper();
    }

    public Diary getDiary(String weekStart, String weekEnd) throws IOException {
        if(weekStart == null) weekStart = Utils.getCurrentWeekStart();
        if(weekEnd == null) weekEnd = Utils.getCurrentWeekEnd();

        Map<String, Object> data = new HashMap<>();
        data.put("studentId", this.api.getStudentId());
        data.put("vers", this.api.getVer());
        data.put("weekEnd", weekEnd);
        data.put("weekStart", weekStart);
        data.put("withLaAssigns", true);
        data.put("yearId", this.api.getYearId());

        String query = "?" + Utils.toFormUrlEncoded(data);

        try(Response response = this.client.get(Urls.WebApi.DIARY + query)) {
            return this.objectMapper.readValue(Objects.requireNonNull(response.body()).byteStream(), Diary.class);
        }
    }

    public List<Assignment> getOverdueJobs(String weekStart, String weekEnd) throws IOException {
        if(weekStart == null) weekStart = Utils.getCurrentWeekStart();
        if(weekEnd == null) weekEnd = Utils.getCurrentWeekEnd();

        String query = "?" + Utils.toFormUrlEncoded(
                Arrays.asList(
                        "studentId", "yearId",
                        "weekStart", "weekEnd"
                ),
                Arrays.asList(
                        this.api.getStudentId(), this.api.getYearId(),
                        weekStart, weekEnd
                )
        );

        try(Response response = this.client.get(Urls.WebApi.OVERDUE + query)) {
            return Arrays.asList(this.objectMapper.readValue(Objects.requireNonNull(response.body()).byteStream(), Assignment[].class));
        }
    }

    public DetailedAssignment getDetailedAssignment(int assignmentId) throws IOException {
        String query = "?studentId=" + this.api.getStudentId();
        try(Response response = this.client.get(Urls.WebApi.ASSIGNS + "/" + assignmentId + query)) {
            return this.objectMapper.readValue(Objects.requireNonNull(response.body()).byteStream(), DetailedAssignment.class);
        }
    }
}
