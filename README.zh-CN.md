<p align="center"><img src="res/mipmap-xxxhdpi/ic_launcher.png" width="96" alt="KeepOn 图标"></p>
<h1 align="center">KeepOn 屏幕常亮</h1>
<p align="center">按一下，屏幕不再自动熄灭。</p>
<p align="center">简体中文 | <a href="README.zh-TW.md">繁體中文</a> | <a href="README.md">English</a> | <a href="README.ja.md">日本語</a> | <a href="README.lzh.md">文言</a></p>

只有一个圆形按钮。按下去，任何界面——任何应用、主屏——屏幕都不会再超时熄灭；
再按一下，恢复你原来的屏幕超时设置。

**整个应用在一台安卓手机上完成开发。**没有 Android Studio，没有 SDK，没有
Gradle，也没有电脑：`aapt`、`javac`、`d8`、`apksigner` 全部运行在
[Termux](https://termux.dev) 里；桌面图标由仅用标准库的 Python 脚本绘制；
签名密钥与成品安装包从不离开这台手机。

## 原理

- **主机制**：拿到「修改系统设置」授权后，把 `Settings.System.SCREEN_OFF_TIMEOUT`
  写成 `Integer.MAX_VALUE`——与系统「屏幕超时」设置页写入的是同一个值。没有任何
  后台行为可供查杀，重启后依然有效。关闭时恢复你原来的值。
- **第二重**（可选授权）：一个 1 像素悬浮窗，携带 `FLAG_KEEP_SCREEN_ON`。
- **末路兜底**：已废弃的 `SCREEN_BRIGHT_WAKE_LOCK`。实测（三星折叠屏、
  安卓 16）后台持锁 90 分钟屏幕照样熄灭，故仅在两个授权都没有时使用。

## 安装

1. 在[发布页](../../releases)下载 `keepon.apk`（安卓 10 及以上）
2. 安装时允许「未知来源应用」
3. 打开应用 → 点「允许修改系统设置」并授权 → 返回，点圆形按钮

> 卸载前先在应用内关闭常亮，否则需到 设置 → 显示 → 屏幕超时 手动改回。

## 权限

| 权限 | 用途 |
| --- | --- |
| `WRITE_SETTINGS`（特殊授权） | 写入屏幕超时值——常亮的核心 |
| `SYSTEM_ALERT_WINDOW`（可选） | 1 像素悬浮窗，第二重信号 |
| `WAKE_LOCK` | 无授权时的兜底路径 |
| `FOREGROUND_SERVICE`（含 `SPECIAL_USE`） | 开启期间的开关通知 |
| `POST_NOTIFICATIONS` | 显示这条通知 |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 应用内「允许后台运行」按钮 |

不采集任何数据，不联网，无 `INTERNET` 权限。

## 自行构建

与开发时完全一样——不需要电脑，Termux 即可：

```sh
pkg install aapt openjdk-17 d8 zipalign apksigner python curl
git clone https://github.com/lesliechowsh/keepon
cd keepon && bash build.sh
```

首次构建会自动下载 platform-34 的 `android.jar` 并生成签名密钥；产物
`keepon.apk` 生成在仓库根目录。加 `--publish` 则复制到 `/sdcard/Download/`
并触发媒体扫描。

## 许可证

[MIT](LICENSE)
