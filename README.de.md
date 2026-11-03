<p align="center"><img src="res/mipmap-xxxhdpi/ic_launcher.png" width="96" alt="KeepOn-Symbol"></p>
<h1 align="center">KeepOn</h1>
<p align="center">Ein Tipp, der Bildschirm bleibt an.</p>
<p align="center"><a href="README.zh-CN.md">简体中文</a> | <a href="README.zh-TW.md">繁體中文</a> | <a href="README.lzh.md">文言</a> | <a href="README.wu.md">上海话</a> | <a href="README.md">English</a> | <a href="README.ja.md">日本語</a> | <a href="README.ko.md">한국어</a> | <a href="README.fr.md">Français</a> | Deutsch | <a href="README.pt.md">Português</a> | <a href="README.it.md">Italiano</a></p>

Ein einzelner runder Knopf. Antippen — und der Bildschirm schaltet sich
nirgends mehr von selbst ab: in keiner App, nicht auf dem
Startbildschirm. Erneut antippen, und deine ursprüngliche Einstellung für
den Bildschirm-Timeout ist zurück.

**Vollständig auf einem Android-Smartphone entwickelt.** Kein Android
Studio, kein SDK, kein Gradle, kein PC: `aapt`, `javac`, `d8` und
`apksigner` laufen in [Termux](https://termux.dev); die Launcher-Icons
zeichnet ein Python-Skript, das nur die Standardbibliothek benutzt; der
Signaturschlüssel und das Release-APK verlassen das Gerät nie.

## Funktionsweise

- **Hauptmechanismus:** Mit der Berechtigung „Systemeinstellungen ändern"
  schreibt KeepOn `Settings.System.SCREEN_OFF_TIMEOUT` auf
  `Integer.MAX_VALUE` — derselbe Wert, den die eingebaute Seite für den
  Bildschirm-Timeout schreibt. Es gibt kein Hintergrundverhalten, das man
  beenden könnte: Die Einstellung übersteht Neustarts und Prozessende.
  Ausschalten stellt den gespeicherten Wert wieder her.
- **Sekundär** ( optionale Berechtigung): ein 1-Pixel-Overlay-Fenster mit
  `FLAG_KEEP_SCREEN_ON`.
- **Letzter Ausweg:** der veraltete `SCREEN_BRIGHT_WAKE_LOCK`. Auf einem
  Samsung-Foldable mit Android 16 gemessen: 90 Minuten im Hintergrund
  gehalten, der Bildschirm ging trotzdem aus — wird also nur benutzt, wenn
  keine der beiden Berechtigungen erteilt ist.

## Installation

1. Lade `keepon.apk` von der [Releases-Seite](../../releases) herunter
   (Android 10 oder höher)
2. Erlaube beim Installieren „Apps aus unbekannten Quellen"
3. App öffnen → dem Button zum Ändern der Systemeinstellungen folgen und
   erlauben → zurückkommen und den runden Knopf antippen

> Schalte KeepOn in der App aus, bevor du sie deinstallierst — sonst musst
> du Einstellungen → Anzeige → Bildschirm-Timeout von Hand zurücksetzen.

## Berechtigungen

| Berechtigung | Warum |
| --- | --- |
| `WRITE_SETTINGS` (Sonderzugriff) | Schreibt den Bildschirm-Timeout — der Kern davon |
| `SYSTEM_ALERT_WINDOW` (optional) | Das 1-Pixel-Overlay als zweites Signal |
| `WAKE_LOCK` | Der letzte Ausweg |
| `FOREGROUND_SERVICE` (+`SPECIAL_USE`) | Die Umschalt-Benachrichtigung, solange an |
| `POST_NOTIFICATIONS` | Zeigt diese Benachrichtigung |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Der Button „Hintergrund erlauben" |

Keine Datenerhebung, kein Netzwerkzugriff, keine `INTERNET`-Berechtigung.

## Selbst bauen

Genau so, wie es gebaut wurde — kein PC nötig, Termux genügt:

```sh
pkg install aapt openjdk-17 d8 zipalign apksigner python curl
git clone https://github.com/lesliechowsh/keepon
cd keepon && bash build.sh
```

Der erste Build lädt das `android.jar` von platform-34 herunter und legt
einen Signaturschlüssel an; das Ergebnis liegt als `keepon.apk` im
Wurzelverzeichnis. Mit `--publish` wird es nach `/sdcard/Download/`
kopiert und ein Medienscan wird ausgelöst.

## Lizenz

[MIT](LICENSE)
