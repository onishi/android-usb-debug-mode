# USB デバッグ切替テストアプリ

このアプリは `Settings.Global.ADB_ENABLED` の更新を試すための検証用です。一般配布向けの安定機能ではありません。

## 前提

1. Android 端末で開発者向けオプションを有効にする
2. USB デバッグを有効にして PC と接続する
3. アプリをインストールする
4. 次のコマンドで権限を付与する

```sh
adb shell pm grant com.onishi.androidusbdebugmode android.permission.WRITE_SECURE_SETTINGS
```

## 使い方

1. アプリ起動後に `WRITE_SECURE_SETTINGS` が `あり` になっていることを確認する
2. `USB デバッグを ON にする` または `USB デバッグを OFF にする` を押す
3. 結果表示と開発者向けオプション画面で挙動を確認する

## 注意

- `ADB_ENABLED` の読取値は端末によって常に `0` になることがあります
- `OFF` にすると現在の adb 接続は切断される可能性があります
- 端末や OEM 実装により書込みが無視される場合があります
- Android の将来バージョンで動作が変わる可能性があります
