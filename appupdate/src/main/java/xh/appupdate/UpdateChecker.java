package xh.appupdate;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class UpdateChecker {


    public static void checkForDialog(Context context, String updateURL) {
        if (context != null) {
            AsyncTask<Void, Void, String> task = new CheckUpdateTask(context, updateURL, VersionUpdateConstants.TYPE_DIALOG, true);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else
                task.execute();
            //new CheckUpdateTask(context, VersionUpdateConstants.TYPE_DIALOG, true).execute();
        } else {
            Log.e(VersionUpdateConstants.TAG, "The arg context is null");
        }
    }


    public static void checkForNotification(Context context, String updateURL) {
        if (context != null) {
            new CheckUpdateTask(context, updateURL, VersionUpdateConstants.TYPE_NOTIFICATION, false).execute();
        } else {
            Log.e(VersionUpdateConstants.TAG, "The arg context is null");
        }

    }


}
