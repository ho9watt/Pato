package com.example.pato.customclass;

import java.security.MessageDigest;


public class SHA256 {
    public static String encrypt(String data){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
            messageDigest.update(data.getBytes());

            byte byteData[] = messageDigest.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<byteData.length;i++) {
                String hex=Integer.toHexString(0xff & byteData[i]);
                if(hex.length()==1){
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
