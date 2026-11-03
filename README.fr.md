<p align="center"><img src="res/mipmap-xxxhdpi/ic_launcher.png" width="96" alt="Icône KeepOn"></p>
<h1 align="center">KeepOn</h1>
<p align="center">Un appui, l'écran reste allumé.</p>
<p align="center"><a href="README.zh-CN.md">简体中文</a> | <a href="README.zh-TW.md">繁體中文</a> | <a href="README.lzh.md">文言</a> | <a href="README.wu.md">上海话</a> | <a href="README.md">English</a> | <a href="README.ja.md">日本語</a> | <a href="README.ko.md">한국어</a> | Français | <a href="README.de.md">Deutsch</a> | <a href="README.pt.md">Português</a> | <a href="README.it.md">Italiano</a></p>

Un seul bouton rond. Appuyez dessus et l'écran ne s'éteint plus jamais tout
seul — dans toutes les applications, sur l'écran d'accueil, partout. Appuyez
à nouveau et votre délai d'extinction d'origine revient.

**Développé entièrement sur un téléphone Android.** Pas d'Android Studio,
pas de SDK, pas de Gradle, pas de PC : `aapt`, `javac`, `d8` et `apksigner`
tournent dans [Termux](https://termux.dev) ; les icônes sont dessinées par
un script Python sans dépendances ; la clé de signature et l'APK de
publication ne quittent jamais l'appareil.

## Fonctionnement

- **Mécanisme principal** : avec l'autorisation « modifier les paramètres
  système », KeepOn écrit `Settings.System.SCREEN_OFF_TIMEOUT` à
  `Integer.MAX_VALUE` — la même valeur que la page intégrée de délai
  d'extinction de l'écran. Aucun comportement en arrière-plan à tuer :
  le réglage survit aux redémarrages comme à la mort du processus. Le
  désactiver restaure la valeur enregistrée.
- **Secondaire** (autorisation facultative) : une fenêtre flottante d'un
  pixel portant `FLAG_KEEP_SCREEN_ON`.
- **Dernier recours** : le `SCREEN_BRIGHT_WAKE_LOCK` obsolète. Mesuré sur un
  pliable Samsung sous Android 16 : maintenu 90 minutes en arrière-plan, il
  n'a pas empêché l'écran de s'éteindre — il ne sert donc que si aucune des
  deux autorisations n'est accordée.

## Installation

1. Téléchargez `keepon.apk` depuis la [page des versions](../../releases)
   (Android 10 ou plus)
2. Autorisez « installer des applications inconnues » quand on vous le demande
3. Ouvrez l'appli → accordez l'autorisation de modifier les paramètres
   système → revenez et appuyez sur le bouton rond

> Désactivez KeepOn dans l'appli avant de la désinstaller, sinon remettez
> Paramètres → Affichage → Délai avant extinction à la main.

## Autorisations

| Autorisation | Pourquoi |
| --- | --- |
| `WRITE_SETTINGS` (accès spécial) | Écrit le délai d'extinction — le cœur du mécanisme |
| `SYSTEM_ALERT_WINDOW` (facultatif) | La fenêtre flottante d'un pixel, second signal |
| `WAKE_LOCK` | Le chemin de dernier recours |
| `FOREGROUND_SERVICE` (+`SPECIAL_USE`) | La notification-interrupteur pendant l'activation |
| `POST_NOTIFICATIONS` | Affiche cette notification |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Le bouton « autoriser l'arrière-plan » |

Aucune collecte de données, aucun accès réseau, pas d'autorisation `INTERNET`.

## Compiler soi-même

Comme le projet a été construit — aucun PC requis, Termux suffit :

```sh
pkg install aapt openjdk-17 d8 zipalign apksigner python curl
git clone https://github.com/lesliechowsh/keepon
cd keepon && bash build.sh
```

Le premier build télécharge le `android.jar` de platform-34 et crée une clé
de signature ; le résultat est `keepon.apk` à la racine du dépôt.
`bash build.sh --publish` le copie dans `/sdcard/Download/` et déclenche un
scan média.

## Licence

[MIT](LICENSE)
