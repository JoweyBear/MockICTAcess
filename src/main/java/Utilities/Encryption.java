package Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Properties;
import javax.crypto.*;
import javax.crypto.spec.*;

public class Encryption {

    private final Properties outlet = new Properties();

    public Encryption() {
        try (InputStream is = Encryption.class.getResourceAsStream("/Configurations/Outlet.properties")) {
            outlet.load(is);
        } catch (IOException e) {
            System.out.println("Failed to load properties: " + e.getMessage());
        }
    }

    public String encrypt(String strToEncrypt) {
        try {
            byte[] iv = new byte[16]; // all 0s for now
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            String secret = outlet.getProperty("secretKey");
            String salt = outlet.getProperty("saltValue");

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);

            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Encryption error: " + e.getMessage());
            return null;
        }
    }

    public String decrypt(String strToDecrypt) {
        try {
            byte[] iv = new byte[16];
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            String secret = outlet.getProperty("secretKey");
            String salt = outlet.getProperty("saltValue");

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);

            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Decryption error: " + e.getMessage());
            return null;
        }
    }
}
