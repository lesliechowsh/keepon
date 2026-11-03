<p align="center"><img src="res/mipmap-xxxhdpi/ic_launcher.png" width="96" alt="Icona di KeepOn"></p>
<h1 align="center">KeepOn</h1>
<p align="center">Un tocco, lo schermo resta acceso.</p>
<p align="center"><a href="README.zh-CN.md">简体中文</a> | <a href="README.zh-TW.md">繁體中文</a> | <a href="README.lzh.md">文言</a> | <a href="README.wu.md">上海话</a> | <a href="README.md">English</a> | <a href="README.ja.md">日本語</a> | <a href="README.ko.md">한국어</a> | <a href="README.fr.md">Français</a> | <a href="README.de.md">Deutsch</a> | <a href="README.pt.md">Português</a> | Italiano</p>

Un solo pulsante rotondo. Toccalo e lo schermo non si spegne più da solo —
in qualsiasi app, nella schermata home, ovunque. Toccalo di nuovo e
l'impostazione originale del timeout dello schermo torna com'era.

**Sviluppato interamente su uno smartphone Android.** Niente Android
Studio, niente SDK, niente Gradle, niente PC: `aapt`, `javac`, `d8` e
`apksigner` girano in [Termux](https://termux.dev); le icone sono
disegnate da uno script Python che usa solo la libreria standard; la
chiave di firma e l'APK di rilascio non lasciano mai il dispositivo.

## Come funziona

- **Meccanismo principale:** con il permesso di "modifica impostazioni
  di sistema", KeepOn imposta `Settings.System.SCREEN_OFF_TIMEOUT` a
  `Integer.MAX_VALUE` — lo stesso valore che scrive la pagina integrata
  del timeout dello schermo. Non c'è alcun comportamento in background da
  terminare: l'impostazione sopravvive a riavvii e alla morte del
  processo. Lo spegnimento ripristina il valore salvato.
- **Secondario** (permesso facoltativo): una finestra overlay di 1 pixel
  con `FLAG_KEEP_SCREEN_ON`.
- **Ultima risorsa:** il deprecato `SCREEN_BRIGHT_WAKE_LOCK`. Misurato su
  un pieghevole Samsung con Android 16: tenuto 90 minuti in background,
  lo schermo si è spento lo stesso — quindi viene usato solo quando
  nessuno dei due permessi è concesso.

## Installazione

1. Scarica `keepon.apk` dalla [pagina delle release](../../releases)
   (Android 10 o successivo)
2. Consenti l'installazione di "app da origini sconosciute" quando
   richiesto
3. Apri l'app → tocca il pulsante di modifica delle impostazioni di
   sistema e concedi → torna indietro e tocca il pulsante rotondo

> Spegni KeepOn dentro l'app prima di disinstallarla, altrimenti
> reimposta Impostazioni → Display → Timeout schermo a mano.

## Permessi

| Permesso | Perché |
| --- | --- |
| `WRITE_SETTINGS` (accesso speciale) | Scrive il timeout dello schermo — il cuore |
| `SYSTEM_ALERT_WINDOW` (facoltativo) | L'overlay da 1 pixel, un secondo segnale |
| `WAKE_LOCK` | Il percorso di ultima risorsa |
| `FOREGROUND_SERVICE` (+`SPECIAL_USE`) | La notifica-interruttore mentre attivo |
| `POST_NOTIFICATIONS` | Mostra quella notifica |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Il pulsante "consenti in background" |

Nessuna raccolta di dati, nessun accesso di rete, nessun permesso
`INTERNET`.

## Compilarlo da soli

Esattamente com'è stato costruito — senza PC, basta Termux:

```sh
pkg install aapt openjdk-17 d8 zipalign apksigner python curl
git clone https://github.com/lesliechowsh/keepon
cd keepon && bash build.sh
```

La prima build scarica l'`android.jar` di platform-34 e crea una chiave
di firma; il risultato è `keepon.apk` nella radice del repository. Con
`--publish` viene copiato in `/sdcard/Download/` e viene avviata una
scansione dei media.

## Licenza

[MIT](LICENSE)
