# Android USB Debug Mode

Android の `USB デバッグ` をアプリから切り替えるための検証用アプリです。  
`Settings.Global.ADB_ENABLED` を `WRITE_SECURE_SETTINGS` 付きで更新する前提で動作します。

一般配布向けの安定機能ではなく、端末や OEM 実装依存の挙動確認が主目的です。

## 前提

1. Android 端末で開発者向けオプションを有効化する
2. USB デバッグを ON にして PC と接続する
3. アプリをインストールする
4. 次のコマンドで `WRITE_SECURE_SETTINGS` を付与する

```sh
adb shell pm grant com.onishi.androidusbdebugmode android.permission.WRITE_SECURE_SETTINGS
```

## できること

- アプリ画面から `USB デバッグ` の ON/OFF を切り替える
- `WRITE_SECURE_SETTINGS` の付与状態を表示する
- `ADB_ENABLED` の読取値を表示する
- `開発者向けオプション` を開く
- クイック設定タイルから ON/OFF を切り替える
- クイック設定タイル長押しでアプリを開く

## 画面仕様

- `操作` カード
  - `USB デバッグ` のトグル
  - `開発者向けオプション` ボタン
- `現在の状態` カード
  - 状態チップ
  - `WRITE_SECURE_SETTINGS`
  - `ADB_ENABLED`
  - `再読込` ボタン
- `実行結果` カード
  - 直近の操作結果メッセージ

## クイック設定タイル

- タイル名: `USB デバッグ`
- タップ: `USB デバッグ` の ON/OFF 切替
- 長押し: アプリを開く
- タイルは `toggleable` として manifest に登録済み
- カテゴリは `connectivity` として登録済み

注意:
端末によっては metadata の反映に差があり、見た目を変えるには一度タイルを外して再追加した方がよい場合があります。

## 使い方

### アプリ画面

1. アプリを起動する
2. `WRITE_SECURE_SETTINGS` が `付与済み` になっていることを確認する
3. トグルで `USB デバッグ` を切り替える
4. 必要なら `開発者向けオプション` を開いて状態を確認する
5. `現在の状態` カード下部の `再読込` で表示を更新する

### クイック設定タイル

1. クイック設定の編集画面を開く
2. `USB デバッグ` タイルを追加する
3. タイルをタップして ON/OFF を切り替える
4. タイルを長押ししてアプリを開く

## 動作上の注意

- `ADB_ENABLED` の読取値は端末によって信頼できない場合があります
- `USB デバッグ OFF` 時は現在の adb 接続が切断される可能性があります
- `USB デバッグ ON` に戻しても即時復帰は端末依存です
- 設定書き込み自体が無視される端末もあります
- Android バージョンや OEM 実装で今後挙動が変わる可能性があります

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
- `app/src/main/AndroidManifest.xml`
- `docs/usb-debug-toggle.md`
