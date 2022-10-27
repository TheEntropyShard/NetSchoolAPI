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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Various utils used around the code
 */
public final class Utils {
    private Utils() {
        throw new UnsupportedOperationException("Class Utils should not be instantiated");
    }

    /**
     * Hashes input using md5 algorithm
     * @param input byte array to be hashed
     * @return Hashed string
     */
    public static String md5(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(input);
            StringBuilder sb = new StringBuilder();
            for(byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    /**
     * Wrappers {@code URLEncoder.encode(s, enc)} method with exception handling
     * @param s String to be encoded
     * @param enc Encoding to be used
     * @return URL-Encoded string
     */
    public static String urlEncode(String s, String enc) {
        try {
            return URLEncoder.encode(s, enc);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return The start date of the current week in format 2022-10-17 (fullyear-month-day)
     */
    public static String getCurrentWeekStart() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return monday.toString();
    }

    /**
     * @return The end date of the current week in format 2022-10-17 (fullyear-month-day)
     */
    public static String getCurrentWeekEnd() {
        LocalDate today = LocalDate.now();
        LocalDate saturday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        return saturday.toString();
    }

    /**
     * Validates array
     * @param array The array to be validated
     * @return True, if array is not null and not empty. False otherwise
     */
    public static boolean validateArray(Object[] array) {
        return array != null && array.length != 0;
    }

    public static String toFormUrlEncoded(List<String> params, List<Object> values) {
        StringBuilder builder = new StringBuilder();
        if(params.size() > values.size()) {
            throw new IllegalArgumentException("Not enough values: params size is " + params.size() + ", while values size is " + values.size());
        } else if(params.size() < values.size()) {
            throw new IllegalArgumentException("Not enough params: params size is " + params.size() + ", while values size is " + values.size());
        }

        for(int i = 0; i < params.size(); i++) {
            String param = params.get(i);
            Object value = values.get(i);

            builder.append(param);
            builder.append("=");
            builder.append(value);
            builder.append("&");
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    public static List<String> readAllLines(InputStream is) {
        List<String> lines = new ArrayList<>();
        Scanner sc = new Scanner(is);
        while(sc.hasNextLine()) {
            lines.add(sc.nextLine());
        }
        sc.close();
        return lines;
    }

    public static String readAsOneLine(InputStream is) {
        StringBuilder builder = new StringBuilder();
        Scanner sc = new Scanner(is);
        while(sc.hasNextLine()) {
            builder.append(sc.nextLine());
        }
        sc.close();
        return builder.toString();
    }
}
