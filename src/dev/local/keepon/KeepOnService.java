package dev.local.keepon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

/**
 * The visible half of keep-on: an ongoing notification with a stop action and
 * an optional overlay window (a second keep-awake signal when granted).
 *
 * The keep-on itself is the system timeout set by TimeoutStore; this service
 * holds no wake lock by design, because holding one in the background was
 * measured not to keep the screen on, and the wake-lock path remains only as
 * the last resort when neither the write-settings grant nor the overlay grant
 * exists.
 */
public final class KeepOnService extends Service {
  private static final String CHANNEL = "keep-on";
  private static final int NOTIF_ID = 1;
  private static final String ACTION_STOP = "dev.local.keepon.STOP";

  private WindowManager wm;
  private View overlay;
  private PowerManager.WakeLock lock;

  @Override
  public void onCreate() {
    super.onCreate();
    NotificationManager nm = getSystemService(NotificationManager.class);
    NotificationChannel ch = new NotificationChannel(CHANNEL, "屏幕常亮",
        NotificationManager.IMPORTANCE_LOW);
    nm.createNotificationChannel(ch);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null && ACTION_STOP.equals(intent.getAction())) {
      TimeoutStore.restore(this);
      stopSelf();
      return START_NOT_STICKY;
    }
    startForeground(NOTIF_ID, notification());

    if (TimeoutStore.isOn(this)) {
      if (Settings.canDrawOverlays(this)) {
        addOverlay();
      }
      // The timeout does the work; the overlay, when granted, is a second signal.
    } else if (Settings.canDrawOverlays(this)) {
      addOverlay();
    } else {
      acquireLock();
    }
    return START_STICKY;
  }

  @Override
  public void onDestroy() {
    if (overlay != null) {
      try {
        wm.removeView(overlay);
      } catch (Exception ignored) {
      }
      overlay = null;
    }
    if (lock != null && lock.isHeld()) {
      lock.release();
    }
    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  private void addOverlay() {
    wm = getSystemService(WindowManager.class);
    View v = new View(this);
    WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
        1, 1,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT);
    lp.gravity = Gravity.TOP | Gravity.START;
    wm.addView(v, lp);
    overlay = v;
  }

  private void acquireLock() {
    if (lock == null) {
      PowerManager pm = getSystemService(PowerManager.class);
      lock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "keepon:screen");
      lock.setReferenceCounted(false);
    }
    if (!lock.isHeld()) {
      lock.acquire();
    }
  }

  private Notification notification() {
    PendingIntent open = PendingIntent.getActivity(this, 0,
        new Intent(this, MainActivity.class),
        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    PendingIntent stop = PendingIntent.getService(this, 1,
        new Intent(this, KeepOnService.class).setAction(ACTION_STOP),
        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    return new Notification.Builder(this, CHANNEL)
        .setSmallIcon(android.R.drawable.ic_media_play)
        .setContentTitle("屏幕常亮已开启")
        .setContentText("点这里回到开关")
        .setContentIntent(open)
        .setOngoing(true)
        .addAction(new Notification.Action.Builder(null, "关闭", stop).build())
        .build();
  }
}
