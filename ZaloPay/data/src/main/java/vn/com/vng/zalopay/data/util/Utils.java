package vn.com.vng.zalopay.data.util;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

/**
 * Created by AnhHieu on 8/26/16.
 * SHA Utils
 */
public class Utils {

    public static String sha256Base(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            Timber.e(ex, "sha256Base");
            return "";
        }
    }


    /*
    * join with char |
    * */
    public static String sha256(String... params) {
        String content = Strings.joinWithDelimiter("|", params);

        Timber.d("pre-sha256:  %s", content);

        return sha256Base(content);
    }
}
