<p align="center"><img src="res/mipmap-xxxhdpi/ic_launcher.png" width="96" alt="KeepOn 图标"></p>
<h1 align="center">KeepOn 屏幕常亮</h1>
<p align="center">揿一记，屏就勿会自家熄脱。</p>
<p align="center"><a href="README.zh-CN.md">简体中文</a> | <a href="README.zh-TW.md">繁體中文</a> | <a href="README.lzh.md">文言</a> | 上海话 | <a href="README.md">English</a> | <a href="README.ja.md">日本語</a> | <a href="README.ko.md">한국어</a> | <a href="README.fr.md">Français</a> | <a href="README.de.md">Deutsch</a> | <a href="README.pt.md">Português</a> | <a href="README.it.md">Italiano</a></p>

就一个圆钮钮。揿下去，随便啥界面——啥个应用、主屏——屏侪勿会超时熄脱；
再揿一记，侬原来个屏超时设定就转来哉。

**搿只应用是光光勒一部安卓手机浪向开发完成个**。呒没 Android Studio，
呒没 SDK，呒没 Gradle，也呒没电脑：`aapt`、`javac`、`d8`、`apksigner`
全部跑勒 [Termux](https://termux.dev) 里向；桌面图标是一个光用标准库个
Python 脚本画个；签名钥匙搭成品安装包从来呒没离开过搿部手机。

## 道理

- **主机制**：拿到「修改系统设置」授权以后，拿
  `Settings.System.SCREEN_OFF_TIMEOUT` 写成功 `Integer.MAX_VALUE`——搭
  系统「屏幕超时」设定页写进去个是同一个值。一眼眼后台行为也呒没，杀勿
  脱，重启也有效。关脱个辰光恢复侬原来个值。
- **第二重**（可选授权）：一个 1 像素个悬浮窗，带 `FLAG_KEEP_SCREEN_ON`。
- **压箱底个**：已经废弃个 `SCREEN_BRIGHT_WAKE_LOCK`。实测（三星折叠屏、
  安卓 16）后台握牢 90 分钟屏照样熄脱，所以只有两个授权侪呒没个辰光再用。

## 装法

1. 勒[发布页](../../releases)下载 `keepon.apk`（安卓 10 搭以上）
2. 安装个辰光允许「未知来源应用」
3. 打开应用 → 揿「允许修改系统设置」并授权 → 转来，揿圆钮钮

> 卸载之前先勒应用里向拿常亮关脱，否则要到 设置 → 显示 → 屏幕超时
> 手动改转来。

## 权限

| 权限 | 用场 |
| --- | --- |
| `WRITE_SETTINGS`（特殊授权） | 写屏超时个值——常亮个核心 |
| `SYSTEM_ALERT_WINDOW`（可选） | 1 像素悬浮窗，第二重信号 |
| `WAKE_LOCK` | 呒没授权辰光个兜底 |
| `FOREGROUND_SERVICE`（含 `SPECIAL_USE`） | 开勒海辰光个开关通知 |
| `POST_NOTIFICATIONS` | 显搿条通知 |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 应用里「允许后台运行」个钮钮 |

勿采集任何数据，勿联网，呒没 `INTERNET` 权限。

## 自家构建

搭开发辰光完全一样——勿需要电脑，Termux 就好：

```sh
pkg install aapt openjdk-17 d8 zipalign apksigner python curl
git clone https://github.com/lesliechowsh/keepon
cd keepon && bash build.sh
```

头一趟构建会自家下载 platform-34 个 `android.jar` 并生成签名钥匙；成品
`keepon.apk` 出勒仓库根目录。加 `--publish` 就复制到 `/sdcard/Download/`
并触发媒体扫描。

## 许可证

[MIT](LICENSE)
