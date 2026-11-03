<p align="center"><img src="res/mipmap-xxxhdpi/ic_launcher.png" width="96" alt="KeepOn アイコン"></p>
<h1 align="center">KeepOn</h1>
<p align="center">ワンタップで、画面が消えなくなる。</p>
<p align="center"><a href="README.zh-CN.md">简体中文</a> | <a href="README.zh-TW.md">繁體中文</a> | <a href="README.md">English</a> | 日本語 | <a href="README.lzh.md">文言</a></p>

丸いボタンがひとつ。タップすれば、どのアプリでも、ホーム画面でも、
画面が自動で消えることがなくなります。もう一度タップすれば、
元の画面タイムアウト設定に戻ります。

**このアプリは Android スマホだけで開発されています。**
Android Studio も SDK も Gradle も PC も不要。`aapt`・`javac`・`d8`・
`apksigner` はすべて [Termux](https://termux.dev) 上で動き、ランチャー
アイコンは Python 標準ライブラリだけでラスタライズされ、署名鍵と
リリース APK は端末の外に出ません。

## 仕組み

- **主手段**：「システム設定の変更」許可を得たうえで、
  `Settings.System.SCREEN_OFF_TIMEOUT` を `Integer.MAX_VALUE` に設定します。
  端末の「画面タイムアウト」設定と同じ値です。バックグラウンド動作が
  ないため殺されることも防がれることもなく、再起動後も有効です。
  オフにすると元の値に戻します。
- **補助**（任意の許可）：`FLAG_KEEP_SCREEN_ON` を持つ 1px の
  オーバーレイウィンドウ。
- **最終手段**：非推奨の `SCREEN_BRIGHT_WAKE_LOCK`。Samsung の折りたたみ
  端末（Android 16）での実測では、バックグラウンドで 90 分保持しても
  画面は消えるため、他の許可がない場合にのみ使われます。

## インストール

1. [Releases](../../releases) から `keepon.apk` をダウンロード（Android 10+）
2. 「提供元不明のアプリ」のインストールを許可
3. アプリを開く →「システム設定の変更」ボタンをタップして許可する →
   戻って丸いボタンをタップ

> アンインストールする前に、アプリ内で一度オフにしてください。
> オフにせず消した場合は、設定 → ディスプレイ → 画面タイムアウトを
> 手動で戻してください。

## 権限

| 権限 | 用途 |
|---|---|
| `WRITE_SETTINGS`（特別な許可） | 画面タイムアウトの書き換え（中核） |
| `SYSTEM_ALERT_WINDOW`（任意） | 1px オーバーレイによる補助信号 |
| `WAKE_LOCK` | 最終手段のパス |
| `FOREGROUND_SERVICE`（+`SPECIAL_USE`） | オン中のトグル通知 |
| `POST_NOTIFICATIONS` | その通知の表示 |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 「バックグラウンド許可」ボタン |

データ収集なし、ネットワークアクセスなし、`INTERNET` 権限なし。

## 自分でビルド

開発と同じ方法です。PC は不要、Termux だけで：

```sh
pkg install aapt openjdk-17 d8 zipalign apksigner python curl
git clone https://github.com/lesliechowsh/keepon
cd keepon && bash build.sh
```

初回ビルドで platform-34 の `android.jar` をダウンロードし、署名鍵を
生成します。成果物はリポジトリ直下の `keepon.apk`。
`bash build.sh --publish` は `/sdcard/Download/` へコピーし、
メディアスキャンを実行します。

## ライセンス

[MIT](LICENSE)
