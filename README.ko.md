<p align="center"><img src="res/mipmap-xxxhdpi/ic_launcher.png" width="96" alt="KeepOn 아이콘"></p>
<h1 align="center">KeepOn</h1>
<p align="center">한 번 누르면, 화면이 꺼지지 않습니다.</p>
<p align="center"><a href="README.zh-CN.md">简体中文</a> | <a href="README.zh-TW.md">繁體中文</a> | <a href="README.lzh.md">文言</a> | <a href="README.wu.md">上海话</a> | <a href="README.md">English</a> | <a href="README.ja.md">日本語</a> | 한국어 | <a href="README.fr.md">Français</a> | <a href="README.de.md">Deutsch</a> | <a href="README.pt.md">Português</a> | <a href="README.it.md">Italiano</a></p>

버튼 하나. 누르면 어떤 앱에서든, 홈 화면에서든 화면이 자동으로 꺼지지
않습니다. 다시 누르면 원래 화면 시간 제한 설정으로 돌아갑니다.

**이 앱은 안드로이드 폰 한 대에서만 개발되었습니다.** 안드로이드 스튜디오도,
SDK도, Gradle도, PC도 없이 `aapt`, `javac`, `d8`, `apksigner`가 모두
[Termux](https://termux.dev) 안에서 돌고, 런처 아이콘은 표준 라이브러리만
쓰는 Python 스크립트가 그리며, 서명 키와 릴리스 APK는 단말 밖으로 나간
적이 없습니다.

## 작동 원리

- **주 방식**: '시스템 설정 수정' 권한을 받으면
  `Settings.System.SCREEN_OFF_TIMEOUT`을 `Integer.MAX_VALUE`로 씁니다 —
  기기의 '화면 시간 제한' 설정 페이지가 쓰는 것과 같은 값입니다. 죽일 수
  있는 백그라운드 동작이 없고, 재부팅과 프로세스 종료 뒤에도 유지됩니다.
  끄면 저장해 둔 원래 값이 복원됩니다.
- **보조**(선택 권한): `FLAG_KEEP_SCREEN_ON`을 가진 1픽셀 오버레이 창.
- **마지막 수단**: 폐기된 `SCREEN_BRIGHT_WAKE_LOCK`. 안드로이드 16을
  돌리는 삼성 폴더블에서 실측한 결과, 백그라운드에서 90분을 유지해도
  화면은 꺼졌습니다 — 따라서 두 권한이 모두 없을 때만 쓰입니다.

## 설치

1. [릴리스 페이지](../../releases)에서 `keepon.apk`를 내려받습니다
   (안드로이드 10 이상)
2. 설치할 때 '출처를 알 수 없는 앱' 설치를 허용합니다
3. 앱을 열고 → 시스템 설정 수정 버튼을 눌러 허용하고 → 돌아와서 둥근
   버튼을 누릅니다

> 앱을 지우기 전에 먼저 상시 켜짐을 끄세요. 그러지 않으면 설정 →
> 디스플레이 → 화면 시간 제한을 직접 되돌려야 합니다.

## 권한

| 권한 | 용도 |
| --- | --- |
| `WRITE_SETTINGS` (특수 권한) | 화면 시간 제한 값 기록 — 핵심 기능 |
| `SYSTEM_ALERT_WINDOW` (선택) | 1픽셀 오버레이, 두 번째 신호 |
| `WAKE_LOCK` | 권한이 없을 때의 마지막 수단 |
| `FOREGROUND_SERVICE` (+`SPECIAL_USE`) | 켜져 있는 동안의 토글 알림 |
| `POST_NOTIFICATIONS` | 그 알림을 표시 |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 앱 안의 '백그라운드 허용' 버튼 |

데이터 수집 없음, 네트워크 접근 없음, `INTERNET` 권한 없음.

## 직접 빌드하기

개발할 때와 똑같은 방법입니다 — PC는 필요 없고 Termux면 충분합니다:

```sh
pkg install aapt openjdk-17 d8 zipalign apksigner python curl
git clone https://github.com/lesliechowsh/keepon
cd keepon && bash build.sh
```

첫 빌드에서 platform-34 `android.jar`를 내려받고 서명 키를 만듭니다.
결과물은 저장소 루트의 `keepon.apk`입니다. `--publish`를 붙이면
`/sdcard/Download/`로 복사하고 미디어 스캔을 실행합니다.

## 라이선스

[MIT](LICENSE)
