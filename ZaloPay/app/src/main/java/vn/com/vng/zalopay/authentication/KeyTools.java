package vn.com.vng.zalopay.authentication;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.inject.Inject;

import timber.log.Timber;
import vn.com.vng.zalopay.Constants;
import vn.com.vng.zalopay.data.cache.UserConfig;
import vn.com.vng.zalopay.data.util.Utils;

/**
 * Created by hieuvm on 1/3/17.
 * *
 */

public class KeyTools {

    private KeyStore mKeyStore;

    private UserConfig mUserConfig;

    private Cipher mEncryptCipher;

    private Cipher mDecryptCipher;

    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";// KeyStore.getDefaultType();

    @Inject
    public KeyTools(UserConfig userConfig) {
        this.mKeyStore = providesKeyStore();
        this.mUserConfig = userConfig;
    }

    private KeyStore providesKeyStore() {
        try {
            return KeyStore.getInstance(ANDROID_KEY_STORE);
        } catch (KeyStoreException e) {
            Timber.w(e, "Failed to get an instance of KeyStore");
            return null;
        }
    }

    private SecretKey getSecretKey() {
        if (mKeyStore == null) {
            Timber.d("KeyStore is NULL");
            return null;
        }

        try {
            mKeyStore.load(null);

            if (!mKeyStore.containsAlias(Constants.KEY_ALIAS_NAME)) {
                return createKey();
            }

            return (SecretKey) mKeyStore.getKey(Constants.KEY_ALIAS_NAME, null);

        } catch (Exception e) {
            Timber.w(e, "get secret key fail ");
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private SecretKey createKey() {
        Timber.d("create secret key");
        try {
            KeyGenParameterSpec aesSpec = new KeyGenParameterSpec.Builder(Constants.KEY_ALIAS_NAME, KeyProperties.PURPOSE_ENCRYPT
                    | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    //.setKeySize(128)
                    .build();

            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
            keyGenerator.init(aesSpec);
            return keyGenerator.generateKey();

        } catch (Exception e) {
            Timber.w(e, "create secret key fail");
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Cipher getCipher(int mode) {
        if (mKeyStore == null) {
            Timber.w("KeyStore is NULL");
            return null;
        }

        Cipher cipher;

        try {
            mKeyStore.load(null);
            byte[] iv;
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);

            IvParameterSpec ivParams;
            if (mode == Cipher.ENCRYPT_MODE) {
                cipher.init(mode, getSecretKey());
            } else {
                SecretKey secretKey = ((KeyStore.SecretKeyEntry) mKeyStore.getEntry(Constants.KEY_ALIAS_NAME, null)).getSecretKey();
                String keyPasswordIv = mUserConfig.getEncryptedPasswordIV();
                Timber.d("iv : [%s] ", keyPasswordIv);
                iv = Base64.decode(keyPasswordIv, Base64.DEFAULT);
                ivParams = new IvParameterSpec(iv);
                cipher.init(mode, secretKey, ivParams);
            }
            return cipher;
        } catch (Exception e) {
            Timber.d(e, "Get cipher error");
        }
        return null;
    }


    private boolean initEncryptCipher() {
        mEncryptCipher = getCipher(Cipher.ENCRYPT_MODE);
        Timber.d("encrypt cipher: %s", mEncryptCipher);
        return (mEncryptCipher != null);

    }

    boolean initDecryptCipher() {
        mDecryptCipher = getCipher(Cipher.DECRYPT_MODE);
        Timber.d("decrypt cipher: [%s]", mDecryptCipher);
        return (mDecryptCipher != null);
    }

    String decrypt(Cipher cipher) {
        Timber.d("decrypt : [%s]", cipher);
        try {
            String keyPassword = mUserConfig.getEncryptedPassword();
            Timber.d("secret base64: [%s] ", keyPassword);
            byte[] encodedData = Base64.decode(keyPassword, Base64.DEFAULT);
            byte[] decodedData = cipher.doFinal(encodedData);
            String result = new String(decodedData);
            Timber.d("decrypt: %s", result);
            return result;
        } catch (Exception e) {
            Timber.w(e, "Failed to decrypt the data with the generated key.");
        }
        return null;
    }

    public boolean encrypt(String secret) {
        return encrypt(secret, true);
    }

    private boolean encrypt(String secret, boolean isSha256) {
        try {
            if (!isSha256) {
                secret = Utils.sha256Base(secret);
            }

            Timber.d("Password sha256 : [%s]", secret);
            if (!initEncryptCipher()) {
                return false;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, mEncryptCipher);
            cipherOutputStream.write(secret.getBytes());
            cipherOutputStream.flush();
            cipherOutputStream.close();

            IvParameterSpec ivParams = new IvParameterSpec(mEncryptCipher.getIV());
            String iv = Base64.encodeToString(ivParams.getIV(), Base64.DEFAULT);

            byte[] encrypted = outputStream.toByteArray();
            String secretBase64 = Base64.encodeToString(encrypted, Base64.DEFAULT);

            mUserConfig.setEncryptedPassword(secretBase64, iv);

            Timber.d("secret base64 : [%s] secret [%s]", secretBase64, secret);
            Timber.d("iv : [%s]", iv);
            return true;
        } catch (Exception ex) {
            Timber.w(ex, "Failed to encrypt the data with the generated key.");
        }
        return false;
    }

    Cipher getDecryptCipher() {
        return mDecryptCipher;
    }

    void updatePassword(String newPassword) {
        if (shouldUpdatePassword(mUserConfig.getEncryptedPassword(), newPassword)) {
            encrypt(newPassword, true);
        }
    }


    private boolean shouldUpdatePassword(String oldPassword, String newPassword) {
        return !TextUtils.isEmpty(newPassword) && !newPassword.equals(oldPassword);
    }

    public boolean isHavePassword() {
        String password = mUserConfig.getEncryptedPassword();
        return !TextUtils.isEmpty(password);
    }
}
