package KLibrary.Utils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * This class provides hash methods and encryption/decryption<br>
 * A part of the KLibrary (https://github.com/KaitoKunTatsu/KLibrary)
 *
 * @version	v1.1.0 | last edit: 27.08.2022
 * @author Joshua H. | KaitoKunTatsu#3656
 */
public class EncryptionUtils {

    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA512";

    private static final char[] CHARACTERS = {'A','B','C','D','E','F','G','H','I','J','K','L','M','O','P','Q','R','S','T','U','V','W','X','Y', 'Z'};

    private static final int DEFAULT_RSA_KEY_SIZE = 1024;

    private static final int DEFAULT_HASH_KEY_SIZE = 512;

    private static final int DEFAULT_AES_KEY_SIZE = 128;

    private static final int DEFAULT_COST = 16;

    private static final SecureRandom random = new SecureRandom();

    private KeyPair keyPair;

    public EncryptionUtils() { generateRSAKeyPair(); }

    // Hash

    /**
     * @param pString content you want to hash
     * @param pSalt byte array added to the content to make the hash even more secure,<br>can't be empty
     * @return a cryptographically secure hash generated from pString and pSalt
     * */
    public static String getHash(String pString, byte[] pSalt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec lSpec = new PBEKeySpec(pString.toCharArray(), pSalt, 65536, DEFAULT_HASH_KEY_SIZE);
        SecretKeyFactory lKeyFactory = SecretKeyFactory.getInstance(HASH_ALGORITHM);
        byte[] lHashValue = lKeyFactory.generateSecret(lSpec).getEncoded();
        return Base64.getEncoder().encodeToString(lHashValue);
    }

    /**
     * @param pCost length of the generated byte array
     * @return a new random byte array generated by {@link SecureRandom} with pCost as length
     * */
    public static byte[] generateSalt(int pCost)
    {
        byte[] lSalt = new byte[pCost];
        random.nextBytes(lSalt);
        return lSalt;
    }

    /**
     * @return a new random byte array generated by {@link SecureRandom} with length {@value DEFAULT_COST}
     * */
    public static byte[] generateSalt()
    {
        return generateSalt(DEFAULT_COST);
    }

    /**
     * Compares a string with a hash value by hashing the string
     *
     * @param pToValidate string you want to compare
     * @param pHash hash value you want to compare the pToValidate to
     * @param pSalt the salt used to generate pHash
     * @return true if hash value of pToValidate and pSalt equals pHash
     * */
    public static boolean validate(String pToValidate, String pHash, byte[] pSalt)
    {
        try {
            return pHash.equals(getHash(pToValidate, pSalt));
        }
        catch (Exception ex) { return false; }
    }

    // OTP

    /**
     * Uses the OTP (One-Time-Pad) to encrypt a char array
     *
     * @param pToEncrypt the char array you want to encrypt
     * @param pKey a random char array with the SAME LENGTH as pToEncrypt
     * @return encrypted version of pToEncrypt
     **/
    public static char[] encryptOTP(char[] pToEncrypt, char[] pKey) throws IllegalArgumentException
    {
        if (pToEncrypt.length != pKey.length) throw new IllegalArgumentException("Message length must be equal to key length");

        char[] lEncrypted = new char[pToEncrypt.length];
        for (int i = 0; i<lEncrypted.length; i++)
        {
            int[] lIndices = searchCharacterIndex(pKey[i], pToEncrypt[i]);
            lEncrypted[i] = CHARACTERS[(lIndices[0]+lIndices[1])%26];
        }
        return lEncrypted;
    }

    /**
     * Uses the OTP (One-Time-Pad) to decrypt a char array
     *
     * @param pToDecrypt the char array you want to decrypt
     * @param pKey the same array used to encrypt the message
     * @return decrypted version of pToDecrypt
     **/
    public static char[] decryptOTP(char[] pToDecrypt, char[] pKey) throws IllegalArgumentException
    {
        if (pToDecrypt.length != pKey.length) throw new IllegalArgumentException("Message length must be equal to key length");

        char[] lDecrypted = new char[pToDecrypt.length];
        for (int i = 0; i<lDecrypted.length; i++)
        {
            int[] lIndices = searchCharacterIndex(pKey[i], pToDecrypt[i]);
            int lIndicesSum = lIndices[1]-lIndices[0];
            if (lIndicesSum < 0) lIndicesSum += CHARACTERS.length;
            lDecrypted[i] = CHARACTERS[lIndicesSum%26];
        }
        return lDecrypted;
    }

    private static int[] searchCharacterIndex(char... pChar)
    {
        int[] lIndices = new int[pChar.length];
        for (int l = 0; l< pChar.length; l++)
        {
            for (int i = 0; i< CHARACTERS.length; i++)
            {
                if (CHARACTERS[i] == pChar[l]) lIndices[l] = i;
            }
        }
        return lIndices;
    }

    // AES

    public static SecretKey generateSymmetricKey() { return generateSymmetricKey(DEFAULT_AES_KEY_SIZE);}

    public static SecretKey generateSymmetricKey(int pKeySize)
    {
        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException ignored) {
            // Won't happen
            return null;
        }

        keyGenerator.init(DEFAULT_AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

    public static String encryptAES(String pToDecrypt, SecretKey pKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        Cipher lCipher;
        try {
         lCipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ignored) {
            // Won't happen
            return null;
        }
        lCipher.init(Cipher.ENCRYPT_MODE, pKey);
        return bytesToString(lCipher.doFinal(pToDecrypt.getBytes()));
    }

    public static String decryptAES(String pToDecrypt, SecretKey pKey) throws InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher lCipher;
        try {
            lCipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ignored) {
            // Won't happen
            return null;
        }
        lCipher.init(Cipher.DECRYPT_MODE, pKey);
        byte[] decryptedBytes = lCipher.doFinal(stringToBytes(pToDecrypt));
        return new String(decryptedBytes);
    }

    public static SecretKey decodeAESKey(byte[] pKey) {
        return new SecretKeySpec(pKey, 0, pKey.length, "AES");
    }

    public static SecretKey decodeAESKey(String pKey){return decodeAESKey(stringToBytes(pKey)); }

    // RSA

    /**
     *  Generates a new set of key (public and private) used to encrypt/decrypt
     **/
    public void generateRSAKeyPair() { generateRSAKeyPair(DEFAULT_RSA_KEY_SIZE);}

    /**
     *  Generates a new set of key (public and private) used to encrypt/decrypt
     * @param pKeySize size of the generated keys
     **/
    public void generateRSAKeyPair(int pKeySize) {
        try
        {
            KeyPairGenerator lGen = KeyPairGenerator.getInstance("RSA");
            lGen.initialize(pKeySize);
            keyPair = lGen.generateKeyPair();
        }
        catch (NoSuchAlgorithmException ignored) {}
    }


    /**
     * Uses the asymmetric RSA algorithm to encrypt a char array
     *
     * @param pMessage the string you want to encrypt
     * @param pPublicKey the public key of the person you want to send something to
     * @return encrypt byte array version of pMessage
     **/
    public byte[] encryptRSA(String pMessage, PublicKey pPublicKey) throws NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException
    { return encryptRSA(pMessage.getBytes(), pPublicKey);}

    /**
     * Uses the asymmetric RSA algorithm to encrypt a char array
     *
     * @param pMessage the char array you want to encrypt
     * @param pPublicKey the public key of the person you want to send something to
     * @return encrypt byte array version of pMessage
     **/
    public byte[] encryptRSA(byte[] pMessage, PublicKey pPublicKey) throws NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher lCipher;
        try {
            lCipher = Cipher.getInstance("RSA");
        }
        catch (NoSuchAlgorithmException ignored) { return null; }
        lCipher.init(Cipher.ENCRYPT_MODE, pPublicKey);

        return lCipher.doFinal(pMessage);
    }

    /**
     * Uses the asymmetric RSA algorithm to decrypt a message encrypted by using your public key
     *
     * @param pEncryptedMessage the string you want to encrypt
     * @return encrypt string version of pEncryptedMessage
     **/
    public String decryptRSA(byte[] pEncryptedMessage) throws NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher lCipher;
        try {
            lCipher = Cipher.getInstance("RSA");
        }
        catch (NoSuchAlgorithmException e) {
            // won't happen
            return null;
        }

        lCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        return new String(lCipher.doFinal(pEncryptedMessage));
    }

    public PublicKey getPublicKey() {return keyPair.getPublic();}

    public static PublicKey decodeRSAKey(byte[] pPublicKeyBytes) throws InvalidKeySpecException{
        try
        {
            KeyFactory lFactory = KeyFactory.getInstance("RSA");
            return lFactory.generatePublic(new X509EncodedKeySpec(pPublicKeyBytes));
        }
        catch (NoSuchAlgorithmException e) {
            // won't happen
            return null;
        }
    }

    public static PublicKey decodeRSAKey(String pPublicKeyString) throws InvalidKeySpecException {
        return decodeRSAKey(stringToBytes(pPublicKeyString));
    }

    // YEET

    public static String encodeKey(Key pKey) { return bytesToString(pKey.getEncoded()); }

    public static byte[] stringToBytes(String pStr)
    {
        return Base64.getDecoder().decode(pStr);
    }

    public static String bytesToString(byte[] pBytes)
    {
        return Base64.getEncoder().encodeToString(pBytes);
    }

}
