package org.seiko.panc.service;

import android.text.TextUtils;
import android.util.Log;

import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.FlowableProcessor;

import static java.lang.String.format;
import static java.util.Locale.getDefault;

/**
 * Created by Seiko on 2017/6/23/023. Y
 */

public class Utils {

    private static final String TAG = "RxDownload-Panc";
    private static boolean DEBUG = false;

    public static void setDebug(boolean flag) {
        DEBUG = flag;
    }

    public static void log(String message) {
        if (empty(message)) return;
        if (DEBUG) {
            Log.i(TAG, message);
        }
    }

    public static FlowableProcessor<DownloadEvent> createProcessor(
            String missionId, Map<String, FlowableProcessor<DownloadEvent>> processorMap) {

        if (processorMap.get(missionId) == null) {
            FlowableProcessor<DownloadEvent> processor =
                    BehaviorProcessor.<DownloadEvent>create().toSerialized();
            processorMap.put(missionId, processor);
        }
        return processorMap.get(missionId);
    }

    public static void log(Throwable throwable) {
        Log.w(TAG, throwable);
    }

    public static String formatStr(String str, Object... args) {
        return format(getDefault(), str, args);
    }

    public static boolean empty(String string) {
        return TextUtils.isEmpty(string);
    }

    public static void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
