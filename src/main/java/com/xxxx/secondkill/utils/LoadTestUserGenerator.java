package com.xxxx.secondkill.utils;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 一次性压测数据脚本：生成 5000 个用户、插入 t_user、导出 config.txt。
 * config.txt 每行：mobile,userTicket
 */
public class LoadTestUserGenerator {

    private static final int COUNT = 5000;
    private static final long START_MOBILE = 13000000000L;
    private static final String PASSWORD = "111111";
    private static final String SALT = "1a2b3c4d";

    // DB_URL 与 LOGIN_URL 必须属于同一套环境。
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/secondkill?useUnicode=true"
            + "&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai"
            + "&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1a1a1a1a";
    private static final String LOGIN_URL = "http://127.0.0.1:8080/login/doLogin";

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        List<TestUser> users = createUsers();
//        insertUsers(users);
        writeConfigFile(users);
        System.out.println("完成：已生成 " + COUNT + " 个用户，并导出 config.txt");
    }

    private static List<TestUser> createUsers() {
        List<TestUser> users = new ArrayList<TestUser>(COUNT);
        for (int i = 0; i < COUNT; i++) {
            long mobile = START_MOBILE + i;
            String dbPassword = MD5Util.inputPassToDBPass(PASSWORD, SALT);
            users.add(new TestUser(mobile, "user" + i, dbPassword));
        }
        return users;
    }

    private static void insertUsers(List<TestUser> users) throws Exception {
        String sql = "insert into t_user(login_count,nickname,register_date,salt,password,id) values(?,?,?,?,?,?)";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            try {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                for (TestUser user : users) {
                    statement.setInt(1, 0);
                    statement.setString(2, user.nickname);
                    statement.setTimestamp(3, now);
                    statement.setString(4, SALT);
                    statement.setString(5, user.dbPassword);
                    statement.setLong(6, user.mobile);
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        }
    }

    private static void writeConfigFile(List<TestUser> users) throws Exception {
        String formPassword = MD5Util.inputPassToFromPass(PASSWORD);
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("config.txt"), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (TestUser user : users) {
                String ticket = login(user.mobile, formPassword);
                writer.write(user.mobile + "," + ticket);
                writer.newLine();
            }
        }
    }

    private static String login(long mobile, String formPassword) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(LOGIN_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5_000);
        connection.setReadTimeout(5_000);
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");

        String body = "{\"mobile\":\"" + mobile + "\",\"password\":\"" + formPassword + "\"}";
        try (OutputStream output = connection.getOutputStream()) {
            output.write(body.getBytes(StandardCharsets.UTF_8));
        }

        String response = readBody(connection.getResponseCode() >= 400
                ? connection.getErrorStream() : connection.getInputStream());
        if (!response.matches("(?s).*\\\"code\\\"\\s*:\\s*200.*")) {
            throw new IllegalStateException("用户 " + mobile + " 登录失败：" + response);
        }

        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null && "Set-Cookie".equalsIgnoreCase(header.getKey())) {
                for (String cookie : header.getValue()) {
                    String firstPart = cookie.split(";", 2)[0];
                    if (firstPart.startsWith("userTicket=")) {
                        return firstPart.substring("userTicket=".length());
                    }
                }
            }
        }
        throw new IllegalStateException("用户 " + mobile + " 登录成功，但没有取得 userTicket");
    }

    private static String readBody(InputStream input) throws Exception {
        try (InputStream stream = input) {
            byte[] bytes = new byte[1024];
            StringBuilder result = new StringBuilder();
            int length;
            while ((length = stream.read(bytes)) != -1) {
                result.append(new String(bytes, 0, length, StandardCharsets.UTF_8));
            }
            return result.toString();
        }
    }

    private static class TestUser {
        private final long mobile;
        private final String nickname;
        private final String dbPassword;

        private TestUser(long mobile, String nickname, String dbPassword) {
            this.mobile = mobile;
            this.nickname = nickname;
            this.dbPassword = dbPassword;
        }
    }
}
