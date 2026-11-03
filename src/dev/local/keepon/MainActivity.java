package dev.local.keepon;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * One screen: a toggle whose on-state is the system screen-off timeout
 * itself (so it reads true even after our process was killed), the grants
 * the mechanisms depend on, and a button per grant. The write-settings grant
 * is the one that makes the feature ROM-proof, so the screen says so.
 */
public final class MainActivity extends Activity {
  private TextView status;
  private TextView caption;
  private PowerToggleView power;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setGravity(Gravity.CENTER);
    int pad = dp(32);
    root.setPadding(pad, pad, pad, pad);

    status = new TextView(this);
    status.setTextSize(18);
    status.setGravity(Gravity.CENTER);

    power = new PowerToggleView(this);
    power.setListener(() -> {
      if (TimeoutStore.isOn(this)) {
        TimeoutStore.restore(this);
        stopService(new Intent(this, KeepOnService.class));
      } else {
        if (!Settings.System.canWrite(this)) {
          // Half-enabled is worse than not enabled: ask for the grant first.
          openWriteSettings();
          return;
        }
        TimeoutStore.enable(this);
        askForNotifications();
        startForegroundService(new Intent(this, KeepOnService.class));
      }
      render();
    });

    caption = new TextView(this);
    caption.setTextSize(16);
    caption.setGravity(Gravity.CENTER);

    Button write = new Button(this);
    write.setText("允许修改系统设置");
    write.setOnClickListener(v -> openWriteSettings());

    Button overlay = new Button(this);
    overlay.setText("允许显示在其他应用上层");
    overlay.setOnClickListener(v ->
        startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getPackageName()))));

    Button battery = new Button(this);
    battery.setText("允许后台运行");
    battery.setOnClickListener(v -> requestBatteryExemption());

    TextView hint = new TextView(this);
    hint.setTextSize(14);
    hint.setGravity(Gravity.CENTER);
    hint.setPadding(0, dp(24), 0, 0);
    hint.setText("必给：「允许修改系统设置」——常亮靠它，杀不掉。\n"
        + "可选加固：悬浮窗是第二重保险；设置→应用→屏幕常亮→电池→不受限制。");

    root.addView(status);
    stack(root, power, dp(8));
    root.addView(caption);
    stack(root, write, dp(20));
    stack(root, overlay, dp(8));
    stack(root, battery, dp(8));
    root.addView(hint);

    setContentView(root);
  }

  @Override
  protected void onResume() {
    super.onResume();
    render();
  }

  private void render() {
    boolean on = TimeoutStore.isOn(this);
    String notif = Build.VERSION.SDK_INT < 33
        ? "已允许"
        : checkSelfPermission("android.permission.POST_NOTIFICATIONS")
            == PackageManager.PERMISSION_GRANTED ? "已允许" : "未允许";
    status.setText(String.format(
        "屏幕常亮：%s\n修改系统设置：%s\n显示在其他应用上层：%s\n通知：%s\n系统省电限制：%s",
        on ? "开" : "关",
        Settings.System.canWrite(this) ? "已允许" : "未允许",
        Settings.canDrawOverlays(this) ? "已允许" : "未允许",
        notif,
        isBgRestricted() ? "正在限制（去设为无限制）" : "无"));
    boolean onState = TimeoutStore.isOn(this);
    power.setOn(onState);
    caption.setText(onState ? "已开启 · 点按关闭" : "已关闭 · 点按开启");
  }

  private void openWriteSettings() {
    startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
        Uri.parse("package:" + getPackageName())));
  }

  private boolean isBgRestricted() {
    // Context.isBackgroundRestricted() exists on the device (API 28+) but not
    // in the stripped android.jar this toolchain compiles against.
    try {
      Object r = getApplicationContext().getClass()
          .getMethod("isBackgroundRestricted").invoke(getApplicationContext());
      return r instanceof Boolean && (Boolean) r;
    } catch (Exception e) {
      return false;
    }
  }

  /** Android 13+ hides the status notification without this; the service runs either way. */
  private void askForNotifications() {
    if (Build.VERSION.SDK_INT >= 33
        && checkSelfPermission("android.permission.POST_NOTIFICATIONS")
            != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[] {"android.permission.POST_NOTIFICATIONS"}, 1);
    }
  }

  private void requestBatteryExemption() {
    try {
      startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
          Uri.parse("package:" + getPackageName())));
    } catch (Exception e) {
      startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
    }
  }

  private void stack(LinearLayout root, View child, int topMargin) {
    LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    p.topMargin = topMargin;
    child.setLayoutParams(p);
    root.addView(child);
  }

  private int dp(int v) {
    return Math.round(v * getResources().getDisplayMetrics().density);
  }
}
