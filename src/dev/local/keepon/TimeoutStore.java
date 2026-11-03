package dev.local.keepon;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;

/**
 * The primary keep-on mechanism: set the system screen-off timeout to a
 * sentinel "never" value and restore the user's original on the way out.
 *
 * It is not a background behaviour a ROM can hunt — it is the setting the
 * system itself obeys, the same one the built-in screen-timeout page writes.
 * It survives the process being killed and reboots, which is also why "on"
 * is derived from the system value rather than from a flag in our process.
 */
final class TimeoutStore {
  /** Integer.MAX_VALUE ms ≈ 24 days: the conventional "never sleep" value. */
  static final long SENTINEL = 2147483647L;
  private static final String PREFS = "keepon";
  private static final String ORIG = "orig_timeout";

  private TimeoutStore() {
  }

  static long current(Context c) {
    try {
      return Settings.System.getLong(c.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
    } catch (Settings.SettingNotFoundException e) {
      return -1;
    }
  }

  static boolean isOn(Context c) {
    return current(c) == SENTINEL;
  }

  static void enable(Context c) {
    SharedPreferences p = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    long now = current(c);
    if (now != SENTINEL) {
      p.edit().putLong(ORIG, now).apply();
    }
    Settings.System.putLong(c.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, SENTINEL);
  }

  static void restore(Context c) {
    if (!isOn(c)) {
      return;
    }
    long orig = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getLong(ORIG, 60_000L);
    Settings.System.putLong(c.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, orig);
  }
}
