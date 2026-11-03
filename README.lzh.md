# 常明記

一按而屏長明，再按而復其故。

是器也，成於掌中一機。無工房（Android Studio），無軟具之倉（SDK），無梯航
（電腦）：aapt、javac、d8、apksigner 四匠，皆役於 Termux 之內；几上圖符，
以程式自繪；簽印與成品，未嘗離機。

## 其理

- **正法**：既得「改系統設置」之權，則書屏幕逾時之數為至大
  （`Integer.MAX_VALUE`）——與機中「屏幕超時」之設同源。無後台之勞，不可殺，
  重啟猶存。閉之，則復其故數。
- **輔法**（權可予可不予）：懸一窗於諸窗之上，方一像素，持
  `FLAG_KEEP_SCREEN_ON`。
- **末策**：舊鎖 `SCREEN_BRIGHT_WAKE_LOCK`，久廢矣。嘗於三星摺機（安卓十六）
  試之，後台持鎖九十分而屏猶自滅，故惟二權皆無時乃用。

## 受之

一、於[發布之頁](../../releases)取 `keepon.apk`（安卓十以上）。
二、許「未知之源」。
三、啟其器，予「允許修改系統設置」之權，乃按其圓鈕。

> 欲棄其器，先閉常明；不然，屏幕超時之數，須手自復之。

## 所索之權

| 權 | 所用 |
| --- | --- |
| `WRITE_SETTINGS`（特權） | 書逾時之數，其本也 |
| `SYSTEM_ALERT_WINDOW`（可選） | 一像素之窗，輔其信 |
| `WAKE_LOCK` | 末策 |
| `FOREGROUND_SERVICE`（並 `SPECIAL_USE`） | 既啟，示通知以為閘 |
| `POST_NOTIFICATIONS` | 示彼通知 |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 器中「許其後台」之鈕 |

不採一數，不聯一網，無 `INTERNET` 之權。

## 自造

與其生時同法，Termux 足矣：

```sh
pkg install aapt openjdk-17 d8 zipalign apksigner python curl
git clone https://github.com/lesliechowsh/keepon
cd keepon && bash build.sh
```

初造，自取 platform-34 之 `android.jar`，自鑄簽印；器成於 `keepon.apk`。
加 `--publish`，則置於 `/sdcard/Download/`，並告媒氏掃之。

## 券

[MIT](LICENSE)
