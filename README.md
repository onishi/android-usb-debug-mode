# Android USB Debug Mode

Android の `USB デバッグ` をアプリとクイック設定タイルから切り替えるための検証用アプリです。  
`Settings.Global.ADB_ENABLED` を更新するため、`WRITE_SECURE_SETTINGS` が付与されたデバッグ環境を前提にしています。

一般ユーザー向けの安定機能ではなく、端末差分や OEM 実装差を確認するための検証用途を想定しています。

## 現在の仕様

- アプリ画面の `USB デバッグ` スイッチで ON/OFF を切り替える
- `WRITE_SECURE_SETTINGS` の付与状態を表示する
- `ADB_ENABLED` の読取値を表示する
- `開発者向けオプション` を開く
- クイック設定タイルから ON/OFF を切り替える
- クイック設定タイルの長押しまたは設定導線からアプリを開く
- 操作結果は画面内固定表示ではなくトーストで通知する

## 対応環境

- `minSdk`: 26
- `targetSdk`: 36
- Kotlin / Android View ベースのシンプルな単一 Activity 構成

## 前提

1. Android 端末で開発者向けオプションを有効化する
2. いったん USB デバッグを ON にして PC と接続する
3. アプリをインストールする
4. 次のコマンドで `WRITE_SECURE_SETTINGS` を付与する

```sh
adb shell pm grant com.onishi.androidusbdebugmode android.permission.WRITE_SECURE_SETTINGS
```

権限がない場合、アプリのスイッチは無効化され、クイック設定タイルは `権限なし` の unavailable 状態になります。

## 画面構成

- `操作` カード
- `USB デバッグ` スイッチ
- `開発者向けオプション` ボタン
- `現在の状態` カード
- 状態チップ
- `WRITE_SECURE_SETTINGS` 表示
- `ADB_ENABLED` 表示
- `再読込` ボタン

状態チップは現在 `操作可能` または `権限付与待ち` を表示します。

## クイック設定タイル

- タイル名は `USB デバッグ`
- タップで `ADB_ENABLED` の現在値から次状態を計算して切り替える
- `ADB_ENABLED == 1` のとき `ACTIVE`、それ以外は `INACTIVE`
- 権限未付与時は `UNAVAILABLE`
- subtitle は `ON` / `OFF` / `権限なし`
- manifest では `TOGGLEABLE_TILE` と `CATEGORY_CONNECTIVITY` を設定済み

端末によってはタイル metadata の反映が遅く、見た目やカテゴリが変わらない場合があります。その場合は一度タイルを外して再追加してください。

## 使い方

### アプリ画面

1. アプリを起動する
2. `WRITE_SECURE_SETTINGS` が `付与済み` になっていることを確認する
3. `USB デバッグ` スイッチを切り替える
4. 必要なら `開発者向けオプション` を開いて実機側の見え方を確認する
5. 表示を更新したい場合は `再読込` を押す

### クイック設定タイル

1. クイック設定の編集画面を開く
2. `USB デバッグ` タイルを追加する
3. タイルをタップして ON/OFF を切り替える
4. 必要に応じて長押ししてアプリを開く

## 動作上の注意

- `ADB_ENABLED` の読取値は端末によって `0` 固定や取得失敗になることがあります
- `USB デバッグ OFF` にすると現在の adb 接続は切断される可能性があります
- `USB デバッグ ON` に戻しても即時に接続が復帰するとは限りません
- `Settings.Global.putInt()` が成功しても、OEM 実装により実際の USB デバッグ状態へ反映されない場合があります
- `WRITE_SECURE_SETTINGS` は通常アプリには付与できず、adb での手動付与が前提です
- Android バージョンや OEM 実装により今後挙動が変わる可能性があります

## 開発

### ビルド

```sh
env GRADLE_USER_HOME=$PWD/.gradle ./gradlew assembleDebug --console=plain
```

### テスト

```sh
env GRADLE_USER_HOME=$PWD/.gradle ./gradlew test --console=plain
```

### 実機インストール

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 関連ファイル

- `app/src/main/java/com/onishi/androidusbdebugmode/MainActivity.kt`
- `app/src/main/java/com/onishi/androidusbdebugmode/UsbDebugController.kt`
- `app/src/main/java/com/onishi/androidusbdebugmode/UsbDebugTileService.kt`
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/AndroidManifest.xml`
- `docs/usb-debug-toggle.md`
