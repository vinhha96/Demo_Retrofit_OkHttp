package vn.com.vng.zalopay.tracker;

import android.text.TextUtils;

import com.zalopay.apploader.internal.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import timber.log.Timber;
import vn.com.vng.zalopay.data.filelog.FileLogStore;
import vn.com.vng.zalopay.data.util.ObservableHelper;

/**
 * Created by hieuvm on 4/21/17.
 * Static helper for filelog implementations
 */

public class FileLogHelper {

    private static final String ZIP_SUFFIX = ".zip";

    public static Observable<String[]> listFileLogs() {
        return listFileLogs(FileLog.Instance.getRootDirectory(), FileLog.Instance.getCurrentFileLog());
    }

    private static Observable<String[]> listFileLogs(File directory, File exclude) {
        return ObservableHelper.makeObservable(() -> {
            if (!directory.exists() || !directory.isDirectory()) {
                return new String[]{};
            }

            File[] files = directory.listFiles();
            List<String> ret = new ArrayList<>();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        continue;
                    }

                    if (file.equals(exclude)) {
                        continue;
                    }

                    String fileName = file.getName();

                    if (fileName.endsWith(ZIP_SUFFIX)) {
                        continue;
                    }

                    ret.add(file.getAbsolutePath());
                }
            }

            Timber.d("filelogs should upload size [%s]", ret.size());

            return ret.toArray(new String[ret.size()]);
        });
    }

    public static Observable<String> zipFileLog(String filePath) {
        return ObservableHelper.makeObservable(() -> {
            File file = new File(filePath);
            if (!file.exists()) {
                return "";
            }

            String zipFilePath = file.getAbsolutePath().replace(".txt", ZIP_SUFFIX);
            File zipFile = new File(zipFilePath);

            if (zipFile.exists()) {
                return zipFilePath;
            }

            try {
                FileUtils.zip(new String[]{filePath}, zipFilePath);
                return zipFilePath;
            } catch (IOException e) {
                Timber.d(e, "Zip file error");
                return "";
            }
        });
    }

    public static Observable<String> uploadFileLog(String filePath, FileLogStore.Repository fileLogRepository) {
        return FileLogHelper.zipFileLog(filePath) // Zip file
                .filter(s -> !TextUtils.isEmpty(s))
                .flatMap(fileLogRepository::uploadFileLog) // Upload file
                .doOnNext(s -> FileUtils.deleteFileAtPathSilently(filePath)) // Remove .txt
                .doOnNext(FileUtils::deleteFileAtPathSilently) // Remove .zip
                ;
    }

    private static Observable<String> uploadFileLogIgnoreError(String path, FileLogStore.Repository fileLogRepository) {
        return FileLogHelper.uploadFileLog(path, fileLogRepository)
                .onErrorResumeNext(Observable.empty());
    }

    public static Observable<Boolean> uploadFileLogs(FileLogStore.Repository fileLogRepository) {
        return FileLogHelper.listFileLogs()
                .flatMap(Observable::from)
                .flatMap(path -> uploadFileLogIgnoreError(path, fileLogRepository))
                .map(s -> Boolean.TRUE);
    }

    public static Observable<Boolean> cleanupLogs() {
        return ObservableHelper.makeObservable(() -> {
            FileLog.Instance.cleanupLogs();
            return true;
        });
    }
}