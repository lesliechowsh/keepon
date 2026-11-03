<p align="center"><img src="res/mipmap-xxxhdpi/ic_launcher.png" width="96" alt="Ícone do KeepOn"></p>
<h1 align="center">KeepOn</h1>
<p align="center">Um toque, a tela fica acesa.</p>
<p align="center"><a href="README.zh-CN.md">简体中文</a> | <a href="README.zh-TW.md">繁體中文</a> | <a href="README.lzh.md">文言</a> | <a href="README.wu.md">上海话</a> | <a href="README.md">English</a> | <a href="README.ja.md">日本語</a> | <a href="README.ko.md">한국어</a> | <a href="README.fr.md">Français</a> | <a href="README.de.md">Deutsch</a> | Português | <a href="README.it.md">Italiano</a></p>

Um único botão redondo. Toque nele e a tela nunca mais apaga sozinha — em
qualquer aplicativo, na tela inicial, em qualquer lugar. Toque de novo e a
sua configuração original de tempo limite de tela volta.

**Desenvolvido inteiramente em um celular Android.** Sem Android Studio,
sem SDK, sem Gradle, sem PC: `aapt`, `javac`, `d8` e `apksigner` rodam no
[Termux](https://termux.dev); os ícones são desenhados por um script
Python sem dependências; a chave de assinatura e o APK de publicação
nunca saem do aparelho.

## Como funciona

- **Mecanismo principal:** com a permissão de "modificar configurações do
  sistema", o KeepOn grava `Settings.System.SCREEN_OFF_TIMEOUT` como
  `Integer.MAX_VALUE` — o mesmo valor que a página integrada de tempo
  limite de tela escreve. Não há comportamento em segundo plano para ser
  encerrado: a configuração sobrevive a reinícios e à morte do processo.
  Desligar restaura o valor salvo.
- **Secundário** (permissão opcional): uma janela flutuante de 1 pixel
  com `FLAG_KEEP_SCREEN_ON`.
- **Último recurso:** o obsoleto `SCREEN_BRIGHT_WAKE_LOCK`. Medido em um
  dobrável Samsung com Android 16: 90 minutos mantido em segundo plano e
  a tela apagou do mesmo jeito — por isso só é usado quando nenhuma das
  duas permissões foi concedida.

## Instalação

1. Baixe `keepon.apk` na [página de versões](../../releases)
   (Android 10 ou superior)
2. Permita "instalar apps desconhecidos" quando solicitado
3. Abra o app → toque no botão de modificar as configurações do sistema e
   permita → volte e toque no botão redondo

> Desligue o KeepOn dentro do app antes de desinstalá-lo, senão redefina
> Configurações → Tela → Tempo limite de tela manualmente.

## Permissões

| Permissão | Por quê |
| --- | --- |
| `WRITE_SETTINGS` (acesso especial) | Grava o tempo limite de tela — o cerne |
| `SYSTEM_ALERT_WINDOW` (opcional) | A janela flutuante de 1 pixel, segundo sinal |
| `WAKE_LOCK` | O caminho de último recurso |
| `FOREGROUND_SERVICE` (+`SPECIAL_USE`) | A notificação de alternação enquanto ativo |
| `POST_NOTIFICATIONS` | Mostra essa notificação |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | O botão "permitir em segundo plano" |

Sem coleta de dados, sem acesso à rede, sem permissão de `INTERNET`.

## Compilar você mesmo

Do mesmo jeito que foi construído — sem PC, só o Termux:

```sh
pkg install aapt openjdk-17 d8 zipalign apksigner python curl
git clone https://github.com/lesliechowsh/keepon
cd keepon && bash build.sh
```

O primeiro build baixa o `android.jar` do platform-34 e cria uma chave de
assinatura; o resultado é `keepon.apk` na raiz do repositório. Com
`--publish`, ele é copiado para `/sdcard/Download/` e uma varredura de
mídia é disparada.

## Licença

[MIT](LICENSE)
