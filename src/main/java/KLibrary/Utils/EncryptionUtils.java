package KLibrary.Utils;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * This class provides hash methods and encryption/decryption<br>
 * A part of the KLibrary (https://github.com/KaitoKunTatsu/KLibrary)
 *
 * @version	v1.0.0 | last edit: 19.08.2022
 * @author Joshua H. | KaitoKunTatsu#3656
 */
public class EncryptionUtils {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

    private static final char[] CHARACTERS = {'A','B','C','D','E','F','G','H','I','J','K','L','M','O','P','Q','R','S','T','U','V','W','X','Y', 'Z'};

    private static final int DEFAULT_RSA_KEY_SIZE = 1024;

    private static final int DEFAULT_HASH_KEY_SIZE = 512;

    private static final int DEFAULT_COST = 16;

    private static final SecureRandom random = new SecureRandom();

    private KeyPair keyPair;

    public EncryptionUtils() { generateKeyPair(); }

    // Hash

    /**
     * @param pString content you want to hash
     * @param pSalt byte array added to the content to make the hash even more secure,<br>can't be empty
     * @return a cryptographically secure hash generated from pString and pSalt
     * */
    public static String getHash(String pString, byte[] pSalt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec lSpec = new PBEKeySpec(pString.toCharArray(), pSalt, 65536, DEFAULT_HASH_KEY_SIZE);
        SecretKeyFactory lKeyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] lHashValue = lKeyFactory.generateSecret(lSpec).getEncoded();
        return Base64.getEncoder().encodeToString(lHashValue);
    }

    /**
     * @param pString content you want to hash
     * @param pCost length of the generated salt (byte array)
     * @return array containing <br>
     * 1. a cryptographically secure hash generated from pString and the generated salt<br>
     * 2. the generated salt with pCost as length
     * */
    public static String[] getHashAndSalt(String pString, int pCost) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] lSalt = new byte[pCost];
        random.nextBytes(lSalt);

        return new String[] {getHash(pString, lSalt), Arrays.toString(lSalt)};
    }

    /**
     * @param pString content you want to hash
     * @return array containing <br>
     * 1. a cryptographically secure hash generated from pString and a generated salt (byte array) with length {@value DEFAULT_COST}<br>
     * 2. the generated salt
     * */
    public static String[] getHashAndSalt(String pString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return getHashAndSalt(pString, DEFAULT_COST);
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

    // RSA

    /**
     *  Generates a new set of key (public and private) used to encrypt/decrypt
     **/
    public void generateKeyPair() { generateKeyPair(DEFAULT_RSA_KEY_SIZE);}

    /**
     *  Generates a new set of key (public and private) used to encrypt/decrypt
     * @param pKeySize size of the generated keys
     **/
    public void generateKeyPair(int pKeySize) {
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
        catch (NoSuchAlgorithmException ignored) { return null; }
        lCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());

        return new String(lCipher.doFinal(pEncryptedMessage));
    }

    public PublicKey getPublicKey() {return keyPair.getPublic();}

}