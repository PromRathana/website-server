package com.cambofreelance.websiteservice.logger.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static final String module = StringUtils.class.getName();
    private static final char[] hexChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
        'b', 'c', 'd', 'e',
        'f'};

    private StringUtils() {
    }

    public static boolean isStringNotNull(String stringValue) {
        return stringValue != null && !stringValue.equalsIgnoreCase("null") && !stringValue.trim()
            .isEmpty();
    }

    public static boolean isStringNull(String stringValue) {
        return stringValue == null || stringValue.equalsIgnoreCase("null") || stringValue.trim()
            .isEmpty();
    }

    public static String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return sdf.format(timestamp);
    }

    public static String replaceString(String mainString, String oldString, String newString) {
        if (mainString == null) {
            return null;
        }
        if ((oldString == null) || (oldString.isEmpty())) {
            return mainString;
        }
        if (newString == null) {
            newString = "";
        }

        int i = mainString.lastIndexOf(oldString);

        if (i < 0) {
            return mainString;
        }

        StringBuilder mainSb = new StringBuilder(mainString);

        while (i >= 0) {
            mainSb.replace(i, i + oldString.length(), newString);
            i = mainString.lastIndexOf(oldString, i - 1);
        }
        return mainSb.toString();
    }

    public static List<String> split(String str, String delim) {
        List<String> splitList = null;
        StringTokenizer st;

        if (str == null) {
            return null;
        }
        if (delim != null) {
            st = new StringTokenizer(str, delim);
        } else {
            st = new StringTokenizer(str);
        }
        if (st.hasMoreTokens()) {
            splitList = new ArrayList<>();

            while (st.hasMoreTokens()) {
                splitList.add(st.nextToken());
            }
        }
        return splitList;
    }

    public static Map<String, String> strToMap(String str, boolean trim) {
        if (str == null) {
            return null;
        }
        Map<String, String> decodedMap = new HashMap<>();
        List<String> elements = split(str, "|");

        for (String s : elements) {
            List<String> e = split(s, "=");

            if (e.size() != 2) {
                continue;
            }
            String name = e.get(0);
            String value = e.get(1);
            if (trim) {
                if (name != null) {
                    name = name.trim();
                }
                if (value != null) {
                    value = value.trim();
                }
            }
            if (name != null && value != null) {
                decodedMap.put(URLDecoder.decode(name, StandardCharsets.UTF_8),
                    URLDecoder.decode(value, StandardCharsets.UTF_8));
            }
        }
        return decodedMap;
    }

    public static String cleanHexString(String str) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            if ((str.charAt(i) != ' ') && (str.charAt(i) != ':')) {
                buf.append(str.charAt(i));
            }
        }
        return buf.toString();
    }

    public static char[] encodeInt(int i, int j, char[] digestChars) {
        if (i < 16) {
            digestChars[j] = '0';
        }
        ++j;
        do {
            digestChars[(j--)] = hexChar[(i & 0xF)];
            i >>>= 4;
        } while (i != 0);
        return digestChars;
    }

    private static String removeRegex(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.replaceAll("");
    }

    public static String htmlSpecialChars(String html, boolean doubleQuotes, boolean singleQuotes,
        boolean insertBR) {
        html = replaceString(html, "&", "&amps;");
        html = replaceString(html, "<", "&lt;");
        html = replaceString(html, ">", "&gt;");
        if (doubleQuotes) {
            html = replaceString(html, "\"", "&quot;");
        }
        if (singleQuotes) {
            html = replaceString(html, "'", "&#039");
        }
        if (insertBR) {
            html = replaceString(html, "\n", "<br>");
        }

        return html;
    }
}
