<p align="center"><img src="res/mipmap-xxxhdpi/ic_launcher.png" width="96" alt="KeepOn icon"></p>
<h1 align="center">KeepOn</h1>
<p align="center">One tap, the screen stays on.</p>
<p align="center">简体中文 | <a href="README.md">English</a> | <a href="README.ja.md">日本語</a></p>

A single round button. Tap it and the screen never times out — in any app,
on the home screen, anywhere. Tap again and your original screen-timeout
setting comes back.

**Built entirely on an Android phone.** No Android Studio, no SDK, no Gradle,
no PC. `aapt`, `javac`, `d8` and `apksigner` run in [Termux](https://termux.dev);
the launcher icons are rasterised by a stdlib-only Python script; the signing
key and the release APK never leave the device.

## 原理

- **主机制**：拿到「修改系统设置」授权后，把 `Settings.System.SCREEN_OFF_TIMEOUT`
  写成 `Integer.MAX_VALUE`——和系统设置里「屏幕超时」同一个值。没有任何后台行为，
  杀不掉、防不住，重启也有效。关闭时恢复你原来的值。
- **第二重**（可选授权）：1px 悬浮窗挂 `FLAG_KEEP_SCREEN_ON`。
- **末路兜底**：已废弃的 `SCREEN_BRIGHT_WAKE_LOCK`。实测（三星 Fold / Android 16）
  后台持锁 90 分钟屏幕照熄，这台机器上它无效——所以它只在没有前两个授权时才用。

## 安装

1. 从 [Releases](../../releases) 下载 `keepon.apk`（Android 10+）
2. 安装时允许「未知来源」
3. 打开 App → 点「允许修改系统设置」→ 回来点圆形按钮

> 卸载前先在 App 里关一次常亮，否则需要去 设置→显示→屏幕超时 手动改回。

## 权限

| 权限 | 用途 |
|---|---|
| `WRITE_SETTINGS`（特殊授权） | 写屏幕超时值，常亮的核心 |
| `SYSTEM_ALERT_WINDOW`（可选） | 1px 悬浮窗作第二重信号 |
| `WAKE_LOCK` | 无授权时的末路兜底 |
| `FOREGROUND_SERVICE`（+SPECIAL_USE） | 常亮期间的开关通知 |
| `POST_NOTIFICATIONS` | 显示那条通知 |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | App 里「允许后台运行」按钮 |

零数据采集，零联网，无 `INTERNET` 权限。

## 自己构建

同样不需要 PC——Termux 里：

```sh
pkg install aapt openjdk-17 d8 zipalign apksigner python curl
git clone https://github.com/lesliechowsh/keepon
cd keepon && bash build.sh
```

首次构建会自动下载 platform-34 的 `android.jar`、生成签名密钥；
产物 `keepon.apk` 出在当前目录。`bash build.sh --publish` 会复制到
`/sdcard/Download/` 并触发媒体扫描。

## 许可

[MIT](LICENSE)
