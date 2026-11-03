<p align="center"><img src="res/mipmap-xxxhdpi/ic_launcher.png" width="96" alt="KeepOn 圖示"></p>
<h1 align="center">KeepOn 螢幕常亮</h1>
<p align="center">按一下，螢幕不再自動熄滅。</p>
<p align="center"><a href="README.zh-CN.md">简体中文</a> | 繁體中文 | <a href="README.lzh.md">文言</a> | <a href="README.wu.md">上海话</a> | <a href="README.md">English</a> | <a href="README.ja.md">日本語</a> | <a href="README.ko.md">한국어</a> | <a href="README.fr.md">Français</a> | <a href="README.de.md">Deutsch</a> | <a href="README.pt.md">Português</a> | <a href="README.it.md">Italiano</a></p>

只有一個圓形按鈕。按下去，任何畫面——任何應用程式、主畫面——螢幕都不會再
逾時熄滅；再按一下，恢復你原本的螢幕逾時設定。

**整個應用程式在一台 Android 手機上完成開發**。沒有 Android Studio、沒有
SDK、沒有 Gradle，也沒有電腦：`aapt`、`javac`、`d8`、`apksigner` 全部運作於
[Termux](https://termux.dev)；桌面圖示由僅用標準函式庫的 Python 腳本繪製；
簽署金鑰與成品安裝包從不離開這支手機。

## 原理

- **主機制**：取得「修改系統設定」授權後，將 `Settings.System.SCREEN_OFF_TIMEOUT`
  寫為 `Integer.MAX_VALUE`——與系統「螢幕逾時」設定頁寫入的是同一個值。沒有
  任何背景行為可供系統終止，重新開機後依然有效。關閉時恢復你原本的數值。
- **第二重**（可選授權）：一個 1 像素懸浮視窗，攜帶 `FLAG_KEEP_SCREEN_ON`。
- **最後備案**：已棄用的 `SCREEN_BRIGHT_WAKE_LOCK`。實測（三星摺疊機、
  Android 16）背景持鎖 90 分鐘螢幕照樣熄滅，因此僅在兩項授權皆未取得時使用。

## 安裝

1. 在[發布頁](../../releases)下載 `keepon.apk`（Android 10 及以上）
2. 安裝時允許「安裝未知應用程式」
3. 開啟應用程式 → 點「允許修改系統設定」並授權 → 返回，點圓形按鈕

> 解除安裝前先在應用程式內關閉常亮，否則須到 設定 → 顯示 → 螢幕逾時
> 手動改回。

## 權限

| 權限 | 用途 |
| --- | --- |
| `WRITE_SETTINGS`（特殊授權） | 寫入螢幕逾時值——常亮的核心 |
| `SYSTEM_ALERT_WINDOW`（可選） | 1 像素懸浮視窗，第二重信號 |
| `WAKE_LOCK` | 無授權時的備案路徑 |
| `FOREGROUND_SERVICE`（含 `SPECIAL_USE`） | 開啟期間的開關通知 |
| `POST_NOTIFICATIONS` | 顯示這則通知 |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 應用程式內「允許背景執行」按鈕 |

不蒐集任何資料，不連網，無 `INTERNET` 權限。

## 自行建置

與開發時完全相同——不需要電腦，Termux 即可：

```sh
pkg install aapt openjdk-17 d8 zipalign apksigner python curl
git clone https://github.com/lesliechowsh/keepon
cd keepon && bash build.sh
```

首次建置會自動下載 platform-34 的 `android.jar` 並產生簽署金鑰；產物
`keepon.apk` 產生在儲存庫根目錄。加 `--publish` 則複製到
`/sdcard/Download/` 並觸發媒體掃描。

## 授權條款

[MIT](LICENSE)
