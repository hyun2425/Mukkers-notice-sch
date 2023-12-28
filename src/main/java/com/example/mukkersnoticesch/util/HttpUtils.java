package com.example.mukkersnoticesch.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    public static String sendHttpGet(String url) {
        StringBuilder content = new StringBuilder();

        try {
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

            // GET 메서드 설정
            connection.setRequestMethod("GET");

            // 응답 내용 읽기
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), getCharsetFromContentType(connection)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }

            // 연결 종료
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    public static String sendHttpGetToCookies(String url) {
        StringBuilder content = new StringBuilder();
        String setCookieHeader = null;

        try {
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

            // GET 메서드 설정
            connection.setRequestMethod("GET");

            // 응답 내용 및 헤더 읽기
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), getCharsetFromContentType(connection)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }

            // 쿠키 헤더 읽기
            Map<String, List<String>> headerFields = connection.getHeaderFields();
            List<String> cookies = headerFields.get("Set-Cookie");
            if (cookies != null && !cookies.isEmpty()) {
                setCookieHeader = String.join("; ", cookies);
            }

            // 연결 종료
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return setCookieHeader;
    }

    private static String getCharsetFromContentType(HttpURLConnection connection) {
        // Content-Type 헤더에서 문자 인코딩 추출
        String contentType = connection.getHeaderField("Content-Type");
        String charset = StandardCharsets.UTF_8.name();  // 기본은 UTF-8

        if (contentType != null) {
            String[] values = contentType.split(";");

            for (String value : values) {
                value = value.trim();
                if (value.toLowerCase().startsWith("charset=")) {
                    charset = value.substring("charset=".length());
                    break;
                }
            }
        }

        return charset;
    }

    public static String sendHttpPost(String url, String postData, String cookies) {
        StringBuilder content = new StringBuilder();

        try {
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();

            // POST 메서드 설정
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // 쿠키 추가
            if (null != cookies) {
                connection.setRequestProperty("Cookie", cookies);
            }

            // 데이터 전송 및 문자 인코딩 설정
            try (OutputStream os = connection.getOutputStream()) {
                byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
                os.write(postDataBytes);
            }

            // 응답 내용 읽기 및 문자 인코딩 설정
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
            }

            // 연결 종료
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

}
