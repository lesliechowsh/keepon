<p align="center"><img src="res/mipmap-xxxhdpi/ic_launcher.png" width="96" alt="KeepOn icon"></p>
<h1 align="center">KeepOn</h1>
<p align="center">One tap, the screen stays on.</p>
<p align="center"><a href="README.zh-CN.md">简体中文</a> | <a href="README.zh-TW.md">繁體中文</a> | English | <a href="README.ja.md">日本語</a> | <a href="README.lzh.md">文言</a></p>

A single round button. Tap it and the screen never times out — in any app,
on the home screen, anywhere. Tap again and your original screen-timeout
setting comes back.

**Built entirely on an Android phone.** No Android Studio, no SDK, no Gradle,
no PC. `aapt`, `javac`, `d8` and `apksigner` run in [Termux](https://termux.dev);
the launcher icons are rasterised by a stdlib-only Python script; the signing
key and the release APK never leave the device.

## How it works

- **Primary.** With the "modify system settings" grant, KeepOn sets
  `Settings.System.SCREEN_OFF_TIMEOUT` to `Integer.MAX_VALUE` — the same
  setting the built-in screen-timeout page writes. There is no background
  behaviour to kill: it survives reboots and process death. Turning it off
  restores your saved value.
- **Secondary** (optional grant): a 1-pixel overlay window carrying
  `FLAG_KEEP_SCREEN_ON`.
- **Last resort:** the deprecated `SCREEN_BRIGHT_WAKE_LOCK`. Measured on a
  Samsung foldable running Android 16: 90 minutes held in the background did
  not keep the screen on — so it is used only when neither grant exists.

## Install

1. Download `keepon.apk` from [Releases](../../releases) (Android 10+)
2. Allow "install unknown apps" when prompted
3. Open the app → tap the modify-system-settings button and allow →
   come back and tap the round button

> Turn keep-on off inside the app before uninstalling, or reset
> Settings → Display → Screen timeout by hand.

## Permissions

| Permission | Why |
|---|---|
| `WRITE_SETTINGS` (special grant) | Writes the screen timeout — the core of it |
| `SYSTEM_ALERT_WINDOW` (optional) | The 1px overlay, a second signal |
| `WAKE_LOCK` | The last-resort path |
| `FOREGROUND_SERVICE` (+`SPECIAL_USE`) | The toggle notification while on |
| `POST_NOTIFICATIONS` | Shows that notification |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | The "allow background" button |

No data collection, no network access, no `INTERNET` permission.

## Build it yourself

Same way it was built — no PC needed, just Termux:

```sh
pkg install aapt openjdk-17 d8 zipalign apksigner python curl
git clone https://github.com/lesliechowsh/keepon
cd keepon && bash build.sh
```

The first build downloads the platform-34 `android.jar` and creates a signing
key; the output is `keepon.apk` in the repo root. `bash build.sh --publish`
copies it to `/sdcard/Download/` and triggers a media scan.

## License

[MIT](LICENSE)
