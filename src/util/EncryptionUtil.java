/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author vidya
 */
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Properties;

public class EncryptionUtil {

    private static final String CONFIG_FILE = "config.properties";
    private static String MASTER_PASSWORD = null;

    static {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            Properties prop = new Properties();
            prop.load(input);
            MASTER_PASSWORD = prop.getProperty("master_password");

            if (MASTER_PASSWORD == null || MASTER_PASSWORD.isEmpty()) {
                throw new RuntimeException("master_password not found in config.properties");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load config.properties");
        }
    }

    // Generate a new random salt (16 bytes, Base64 encoded)
    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private static SecretKey getSecretKey(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    // Encrypts plain text using master password and salt
    public static String encrypt(String plainText, String saltBase64) {
        try {
            byte[] iv = new byte[16];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            byte[] salt = Base64.getDecoder().decode(saltBase64);
            SecretKey secretKey = getSecretKey(MASTER_PASSWORD.toCharArray(), salt);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));

            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Decrypts encrypted text using master password and salt
    public static String decrypt(String encryptedText, String saltBase64) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            byte[] iv = new byte[16];
            byte[] encrypted = new byte[combined.length - 16];

            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

            IvParameterSpec ivspec = new IvParameterSpec(iv);
            byte[] salt = Base64.getDecoder().decode(saltBase64);
            SecretKey secretKey = getSecretKey(MASTER_PASSWORD.toCharArray(), salt);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new String(cipher.doFinal(encrypted), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

