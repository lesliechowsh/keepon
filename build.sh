#!/usr/bin/env bash
# Builds KeepOn — the one-toggle screen-keep-on APK — entirely on the phone.
# No Android SDK, no Gradle, no PC: Termux packages + platform android.jar +
# aapt → javac → d8 → zipalign → apksigner.
set -euo pipefail
cd "$(dirname "$0")"

MIN_SDK=29
TARGET_SDK=34
AJAR=${ANDROID_JAR:-$HOME/.cache/android-platform-34/android.jar}
KS=${PEGAS_KEYSTORE:-$HOME/.keepon/keystore.jks}
KS_PASS=${PEGAS_STOREPASS:-keepon}
ALIAS=${PEGAS_ALIAS:-keepon}
OUT=keepon.apk
PUBLISH=0
[[ "${1:-}" == "--publish" ]] && PUBLISH=1

die() { printf '\033[31m%s\033[0m\n' "$*" >&2; exit 1; }
info() { printf '\033[2m%s\033[0m\n' "$*"; }

for tool in aapt javac d8 zipalign apksigner python3 keytool curl; do
  command -v "$tool" >/dev/null 2>&1 ||
    die "missing $tool — pkg install aapt openjdk-17 d8 zipalign apksigner python curl"
done

if [[ ! -f $AJAR ]]; then
  info "android.jar not cached; downloading platform-34"
  mkdir -p "$(dirname "$AJAR")"
  tmp=$(mktemp -d)
  curl -fsSL -o "$tmp/platform.zip" https://dl.google.com/android/repository/platform-34-ext7_r03.zip ||
    die "could not download platform-34"
  python3 - "$tmp/platform.zip" "$AJAR" <<'PY'
import sys, zipfile
src, dest = sys.argv[1], sys.argv[2]
with zipfile.ZipFile(src) as z:
    name = next(n for n in z.namelist() if n.endswith('/android.jar'))
    with z.open(name) as r, open(dest, 'wb') as w:
        w.write(r.read())
print(f"  android.jar <- {name}")
PY
  rm -rf "$tmp"
fi

if [[ ! -f $KS ]]; then
  info "creating signing key at $KS"
  mkdir -p "$(dirname "$KS")"
  keytool -genkeypair -keystore "$KS" -storetype PKCS12 -storepass "$KS_PASS" \
    -alias "$ALIAS" -keyalg RSA -keysize 4096 -validity 10000 \
    -dname "CN=KeepOn, O=local, C=CN" >/dev/null
  chmod 600 "$KS"
fi

rm -rf build
mkdir -p build/classes build/dex

info "aapt package"
aapt package -f \
  -M AndroidManifest.xml \
  -S res \
  -I "$AJAR" \
  --min-sdk-version "$MIN_SDK" \
  --target-sdk-version "$TARGET_SDK" \
  -F build/base.apk

info "javac"
mapfile -t sources < <(find src -name '*.java')
javac -source 11 -target 11 -encoding UTF-8 -Xlint:-options,-deprecation \
  -classpath "$AJAR" -d build/classes "${sources[@]}"

info "d8"
mapfile -t classes < <(find build/classes -name '*.class')
d8 --release --min-api "$MIN_SDK" --lib "$AJAR" --output build/dex "${classes[@]}"

info "pack dex"
python3 - <<'PY'
import zipfile
with zipfile.ZipFile('build/base.apk', 'a', zipfile.ZIP_DEFLATED) as z:
    z.write('build/dex/classes.dex', 'classes.dex')
PY

info "zipalign"
zipalign -f -p 4 build/base.apk build/aligned.apk

info "apksigner"
# All three schemes on purpose: v2/v3 are what modern Android checks, v1 keeps
# picky system installers from rejecting a hand-built APK over a missing JAR
# signature.
apksigner sign --ks "$KS" --ks-pass "pass:$KS_PASS" --ks-key-alias "$ALIAS" \
  --key-pass "pass:$KS_PASS" \
  --v1-signing-enabled true --v2-signing-enabled true --v3-signing-enabled true \
  --out "$OUT" build/aligned.apk
apksigner verify "$OUT"

printf '\n\033[32m%s\033[0m  %s  (%s)  md5 %s\n' "built" "$OUT" "$(du -h "$OUT" | cut -f1)" \
  "$(md5sum "$OUT" | cut -c1-32)"

# Publish == copy to Download, in place, then rescan: MediaStore keys a row to
# the file it indexed, so never delete the target first (an orphaned row keeps
# the file invisible) and always scan after writing.
if [[ $PUBLISH == 1 ]]; then
  target=${PEGAS_PUBLISH_TO:-/sdcard/Download/KeepOn.apk}
  cp "$OUT" "$target"
  command -v termux-media-scan >/dev/null 2>&1 && termux-media-scan "$target" >/dev/null 2>&1

  expected=$(stat -c%s "$OUT")
  actual=$(stat -c%s "$target" 2>/dev/null || echo 0)
  [[ "$actual" == "$expected" ]] || die "publish failed: $target is $actual bytes, expected $expected"
  [[ "$(md5sum "$OUT" | cut -d' ' -f1)" == "$(md5sum "$target" | cut -d' ' -f1)" ]] ||
    die "publish failed: $target differs from $OUT"

  info "published: $target  $((actual / 1024))KB  md5 $(md5sum "$OUT" | cut -c1-32)"
fi
