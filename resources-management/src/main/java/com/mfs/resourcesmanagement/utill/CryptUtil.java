package com.mfs.resourcesmanagement.utill;

import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.util.Base64Utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public final class CryptUtil {
    private static final String DESKEY = "19980621";
    private CryptUtil(){}

    public static String getMessageDigestByMD5(String text){
        try {
            byte[] digest = MessageDigest.getInstance("md5").digest(text.getBytes());
            return Base64Utils.encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String encryptByDES(String info) {
        try {
            SecretKey secretKey = new SecretKeySpec(DESKEY.getBytes(),"DES");
            SecureRandom secureRandom = new SecureRandom();
            Cipher des = Cipher.getInstance("DES");
            des.init(Cipher.ENCRYPT_MODE,secretKey,secureRandom);
            byte[] bytes = des.doFinal(info.getBytes());
            String s = HexUtils.toHexString(bytes);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String decryptByDES(String encode) {
        try {
            SecretKey secretKey = new SecretKeySpec(DESKEY.getBytes(),"DES");
            SecureRandom secureRandom = new SecureRandom();
            Cipher des = Cipher.getInstance("DES");
            des.init(Cipher.DECRYPT_MODE,secretKey,secureRandom);
            byte[] bytes = des.doFinal(HexUtils.fromHexString(encode));
            String s = new String(bytes);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getAuthorityContent(String authority) {
        String content = null;
        File file = new File("src/main/resources/security/publicKey.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuffer sb = new StringBuffer();
            String str = null;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
            br.close();
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decodeBase64(sb.toString())));
            content = decodeByRSAPublicKey(authority,publicKey);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return content;
    }
    private static String decodeByRSAPublicKey(String code,PublicKey publicKey) {
        String content = null;
        try {
            Cipher rsa = Cipher.getInstance("RSA");
            rsa.init(Cipher.DECRYPT_MODE,publicKey,new SecureRandom());
            byte[] res = rsa.doFinal(Base64.decodeBase64URLSafe(code));
            content = new String(res);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return content;
    }
}
