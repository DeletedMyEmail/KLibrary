package KLibrary.Utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;


/**
 * This class provides hash methods and encryption/decryption (todo)
 *
 * @version 22.06.2022
 * @author Joshua H. | KaitoKunTatsu#3656
 * */
public class EncryptionUtils {


    private static final String ALGORITHM = "PBKDF2WithHmacSHA512";

private static final char[] CHARACTERS = {'A','B','C','D','E','F','G','H','I','J','K','L','M','O','P','Q','R','S','T','U','V','W','X','Y',
            'Z'};

    private static final int SIZE = 512;

    private static final SecureRandom random = new SecureRandom();;

    private static String getHash(String pString, byte[] pSalt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(pString.toCharArray(), pSalt, 65536, SIZE);
        SecretKeyFactory key_factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash_value = key_factory.generateSecret(spec).getEncoded();
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(hash_value);
    }

    public static String getHash(String pString, int pCost) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] lSalt = new byte[pCost];
        random.nextBytes(lSalt);

        return getHash(pString, lSalt);
    }

    public static String[] getHashAndSalt(String pString, int pCost) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] lSalt = new byte[pCost];
        random.nextBytes(lSalt);

        return new String[] {getHash(pString, lSalt), Arrays.toString(lSalt)};
    }

    public static boolean validate(String pToHash, String pHash, byte[] pSalt)
    {
        try {
            return pHash.equals(getHash(pToHash, pSalt));
        }
        catch (Exception ex) { return false; }
    }

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

    public static void main(String[] args) {
        System.out.println(Arrays.toString(EncryptionUtils.encryptOTP(new char[]{'E', 'F', 'E'}, new char[]{'D', 'D', 'A'})));
        System.out.println(Arrays.toString(EncryptionUtils.decryptOTP(new char[]{'H', 'I', 'E'}, new char[]{'D', 'D', 'A'})));
    }
}