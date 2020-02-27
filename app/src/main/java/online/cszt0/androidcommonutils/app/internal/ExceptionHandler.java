package online.cszt0.androidcommonutils.app.internal;

import android.app.Application;
import android.content.Intent;
import android.os.Process;

import androidx.annotation.NonNull;

import online.cszt0.androidcommonutils.app.ConfigApplication;

/**
 * 全局异常捕获器
 *
 * @hide
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Application application;
    private Class<?> activityClass;

    public ExceptionHandler(Application application, String componentName) {
        this.application = application;
        if (componentName.startsWith(".")) {
            componentName = application.getPackageName() + componentName;
        }
        try {
            activityClass = Class.forName(componentName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        Process.killProcess(Process.myPid());
        Intent intent = new Intent(application, activityClass);
        intent.putExtra(ConfigApplication.EXCEPTION_HANDLER_KEY, e);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        application.startActivity(intent);
    }
}
