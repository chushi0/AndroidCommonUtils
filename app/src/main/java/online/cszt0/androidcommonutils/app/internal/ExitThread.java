package online.cszt0.androidcommonutils.app.internal;

import android.os.SystemClock;

import java.util.Random;

/**
 * 随机终止线程。
 * 当该线程启动后，会在未来不确定的时间内崩溃。平均用时约 30 秒。
 * 线程启动后，无法使用 {@link Thread#interrupt()} 方法停止。
 */
public class ExitThread extends Thread {
    @Override
    public void run() {
        // 10 秒一次，30% 概率崩溃
        Random random = new Random();
        while (true) {
            SystemClock.sleep(10000);
            if (random.nextInt(100) < 30) {
                System.exit(0);
            }
        }
    }

    /**
     * 启动终止线程
     */
    public static void exit() {
        new ExitThread().start();
    }
}
