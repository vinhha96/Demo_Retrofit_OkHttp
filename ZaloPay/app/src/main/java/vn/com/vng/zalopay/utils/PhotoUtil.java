package vn.com.vng.zalopay.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;

import com.zalopay.apploader.internal.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import timber.log.Timber;

/**
 * Created by AnhHieu on 9/19/16.
 * *
 */
public class PhotoUtil {
    private PhotoUtil() {
        // private constructor for utils class
    }

    public static Bitmap getThumbnail(Context context, Uri uri) throws IOException {
        return getThumbnail(context, uri, 256);
    }

    private static Bitmap getThumbnail(Context context, Uri uri, int thumbnailSize) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        if (input == null) {
            return null;
        }

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ?
                onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > thumbnailSize) ? (originalSize / thumbnailSize) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        Timber.d("inSampleSize %s ratio[%s]", bitmapOptions.inSampleSize, ratio);
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional

        input = context.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);

        if (input != null) {
            input.close();
        }
        //  return rotateBitmap(UriUtil.getPath(context, uri), bitmap);
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    public static Bitmap rotateBitmap(String src, Bitmap bitmap) {
        try {
            int orientation = getExifOrientation(src);

            Timber.d("orientation %s", orientation);

            if (orientation == ExifInterface.ORIENTATION_NORMAL) {
                return bitmap;
            }

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                Timber.d("out of memory");
                return bitmap;
            }
        } catch (IOException e) {
            //empty
        }

        return bitmap;
    }

    private static int getExifOrientation(String src) throws IOException {
        int orientation = 1;
        try {
            ExifInterface exif = new ExifInterface(src);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
        } catch (Exception e) {
            //empty
        }
        return orientation;
    }

    private static void printBitmap(Bitmap bitmap) {
        Timber.d("bitmap width [%s] height [%s]", bitmap.getWidth(), bitmap.getHeight());
        int byteCount = 0;
        if (Build.VERSION.SDK_INT >= 19) {
            byteCount = bitmap.getAllocationByteCount();
        } else {
            byteCount = bitmap.getByteCount();
        }
        Timber.d("byteCount %s", byteCount);
    }

    public static byte[] resizeImageByteArray(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = getThumbnail(context, uri, 384);
            return bitmap2BytesWithCompress(bitmap);
        } catch (Exception ex) {
            Timber.w(ex, "reduce image error");
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }
        return null;
    }

    //Note: this function not compress bitmap
    public static byte[] bitmap2byteArray(Bitmap b) {
        int bytes = byteSizeOf(b);
        //or we can calculate bytes this way. Use a different value than 4 if you don't use 32bit images.
        //int bytes = b.getWidth()*b.getHeight()*4;

        ByteBuffer buffer = ByteBuffer.allocate(bytes); //Create a new buffer
        b.copyPixelsToBuffer(buffer); //Move the byte data to the buffer
        byte[] array = buffer.array();
        Timber.d("bytes %s", array.length);
        return array;
    }

    private static byte[] bitmap2BytesWithCompress(Bitmap paramBitmap) {
        if (paramBitmap == null)
            return null;
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        boolean result = paramBitmap.compress(Bitmap.CompressFormat.JPEG, 100, localByteArrayOutputStream);
        if (result) {
            return localByteArrayOutputStream.toByteArray();
        } else {
            return null;
        }
    }


    private static int byteSizeOf(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return data.getByteCount();
        } else {
            return data.getAllocationByteCount();
        }
    }


    public static File createPhotoFile(Context context, String name) {
        File storageDir = new File(context.getCacheDir() + File.separator + "images");
        boolean mkdir = FileUtils.mkdirs(storageDir);
        return new File(storageDir, name);
    }

}
