package online.cszt0.androidcommonutils.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import online.cszt0.androidcommonutils.app.internal.ExceptionHandler;
import online.cszt0.androidcommonutils.app.internal.ExitThread;

/**
 * 本类提供了应用程序启动时的全局配置方案。
 * <p>
 * 在使用时，请将该类作为应用程序 <code>Application</code> 使用，或将程序的 <code>Application</code>
 * 继承该 <code>Application</code>，或在自定义 <code>Application</code> 的
 * {@link Application#onCreate()} 方法中调用 {@link ConfigApplication#config(Application)} 方法
 * <p>
 * 要使用启动全局配置方案，请在 AndroidManifest.xml 文件中的 <code>&lt;application&gt;</code> 节点中添加
 * <code>&lt;meta-info&gt;</code>标签：
 * <pre>
 *     &lt;application ...&gt;
 *         &lt;meta-data
 *              android:name="online.cszt0.android_common_utils.boot_config"
 *              android:value="@xml/boot_config" /&gt;
 *     &lt;/application&gt;
 * </pre>
 * 之后，需要在 <code>res/xml</code> 文件夹中创建 <code>boot_config.xml</code> 文件，用于配置启动参数。
 * <pre>
 *     &lt?xml version="1.0" encoding="utf-8"?&gt;
 *     &lt;manifest
 *          application="your application class"
 *          signature="your signature md5"&gt;
 *
 *          &lt;notification
 *              id="notification channel id"
 *              name="notification channel name"
 *              importance="notification channel importance"
 *              description="notification channel description"/&gt;
 *
 *          &lt;exception
 *              target="handler activity class" /&gt;
 *     &lt;/manifest&gt;
 * </pre>
 * <ul>
 *     <li>在 <code>&lt;application&gt;</code> 节点中，两个属性用于程序完整性校验。若其一出错，
 *     则会在未来不确定的某个时间使应用程序崩溃。若未定义，则不作检查。
 *     <b>注意：验证机制不保证不会被破解，若要保证应用程序安全，请尽可能使用多种验证机制。</b></li>
 *
 *     <li>在 <code>&lt;notification&gt;</code> 节点中，定义了一个通知频道。该节点中 id 和 name
 *     是必填项。importance 的取值既可以是 1-5 的数字，也可以是 min, low, default, high, max。
 *     <code>&lt;notification&gt;</code> 节点可以出现多次，定义的所有通知频道均会被注册。</li>
 *
 *     <li>在 <code>&lt;exception&gt;</code> 节点中，定义了全局异常捕获。当应用程序出现异常时，
 *     将启动指定的 Activity，并将异常信息传递。您可以通过
 *     <code>(Throwable) getIntent().getSerializableExtra(ConfigApplication.EXCEPTION_HANDLER_KEY)</code>
 *     来获取。</li>
 * </ul>
 */
public class ConfigApplication extends Application {

    public static final String BOOT_CONFIG_META_DATA = "online.cszt0.android_common_utils.boot_config";
    public static final String EXCEPTION_HANDLER_KEY = "exception";

    /**
     * 程序启动配置
     *
     * @param application Application 实例
     */
    public static void config(Application application) {
        try {
            // 获取 AndroidManifest.xml 中配置的配置文件路径
            PackageManager packageManager = application.getPackageManager();
            String packageName = application.getPackageName();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            String xmlFilePath = applicationInfo.metaData.getString(BOOT_CONFIG_META_DATA);
            if (xmlFilePath == null) return;
            XmlResourceParser xmlPullParser = application.getAssets().openXmlResourceParser(xmlFilePath);
            // 配置用组件
            NotificationManager notificationManager = (NotificationManager) application.getSystemService(NOTIFICATION_SERVICE);
            assert notificationManager != null;
            int status;
            boolean exit = false;
            while ((status = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
                if (status == XmlPullParser.START_TAG) {
                    String tag = xmlPullParser.getName();
                    switch (tag) {
                        case "manifest": {
                            // 校验 application
                            String applicationClass = xmlPullParser.getAttributeValue(null, "application");
                            // 校验应用签名
                            String signature = xmlPullParser.getAttributeValue(null, "signature");
                            if (applicationClass != null) {
                                if (applicationClass.startsWith(".")) {
                                    applicationClass = packageName + applicationClass;
                                }
                                if (!applicationClass.equals(application.getClass().getName())) {
                                    exit = true;
                                }
                                if (application != application.getApplicationContext()) {
                                    exit = true;
                                }
                            }
                            if (signature != null) {
                                Signature[] signatures;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                    PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);
                                    signatures = packageInfo.signingInfo.getApkContentsSigners();
                                } else {
                                    @SuppressLint("PackageManagerGetSignatures") PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                                    signatures = packageInfo.signatures;
                                }
                                try {
                                    MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                                    byte[] res = messageDigest.digest(signatures[0].toByteArray());
                                    StringBuilder builder = new StringBuilder();
                                    for (byte b : res) {
                                        if (b < 16) {
                                            builder.append('0');
                                        }
                                        builder.append(Integer.toHexString(b));
                                    }
                                    if (!signature.equals(builder.toString())) {
                                        exit = true;
                                    }
                                } catch (NoSuchAlgorithmException e) {
                                    exit = true;
                                }
                            }
                            break;
                        }
                        case "notification": {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                String id = xmlPullParser.getAttributeValue(null, "id");
                                String name = xmlPullParser.getAttributeValue(null, "name");
                                String importanceString = xmlPullParser.getAttributeValue(null, "importance");
                                String description = xmlPullParser.getAttributeValue(null, "description");
                                int importance;
                                if (importanceString == null) {
                                    importance = NotificationManager.IMPORTANCE_DEFAULT;
                                } else {
                                    switch (importanceString) {
                                        case "min":
                                        case "1":
                                            importance = NotificationManager.IMPORTANCE_MIN;
                                            break;
                                        case "low":
                                        case "2":
                                            importance = NotificationManager.IMPORTANCE_LOW;
                                            break;
                                        case "default":
                                        case "3":
                                        default:
                                            importance = NotificationManager.IMPORTANCE_DEFAULT;
                                            break;
                                        case "high":
                                        case "4":
                                            importance = NotificationManager.IMPORTANCE_HIGH;
                                            break;
                                        case "max":
                                        case "5":
                                            importance = NotificationManager.IMPORTANCE_MAX;
                                            break;
                                    }
                                }
                                NotificationChannel channel = new NotificationChannel(id, name, importance);
                                if (description != null) {
                                    channel.setDescription(description);
                                }
                                notificationManager.createNotificationChannel(channel);
                            }
                            break;
                        }
                        case "exception": {
                            // 全局异常捕获
                            String componentName = xmlPullParser.getAttributeValue(null, "target");
                            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(application, componentName));
                            break;
                        }
                        default:
                            // ignore
                            break;
                    }
                }
            }
            xmlPullParser.close();
            if (exit) {
                ExitThread.exit();
            }
        } catch (PackageManager.NameNotFoundException | XmlPullParserException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        config(this);
    }
}
