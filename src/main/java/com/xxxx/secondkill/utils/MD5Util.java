package com.xxxx.secondkill.utils;


import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5Util {
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    public static final String INPUT_PASS_SALT = "1a2b3c4d";

    public static String inputPassToFromPass(String inputPass){
        String str = "" + INPUT_PASS_SALT.charAt(0) + INPUT_PASS_SALT.charAt(2) + inputPass
                + INPUT_PASS_SALT.charAt(5) + INPUT_PASS_SALT.charAt(4);
        return md5(str);
    }

    public static String fromPassToDBPass(String fromPass, String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass, String salt){
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = fromPassToDBPass(fromPass,salt);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassToFromPass("111111"));
        System.out.println(fromPassToDBPass("d018506bc314a32b93eb214102399a63","1a2b3c4d"));
        System.out.println(inputPassToDBPass("111111","1a2b3c4d"));
    }
}
