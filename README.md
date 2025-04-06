# メッセージ管理アプリケーション

## 概要
このアプリケーションは、メッセージの作成、更新、削除、一覧表示を行うシステムです。フロントエンドとバックエンドの2つのサービスで構成され、メッセージキュー（ActiveMQ）を使用して非同期通信を行います。

## システム構成

### アーキテクチャ図
```
[Frontend (8080)]
    │
    ├─── [MySQL (3306)] (参照操作)
    │
    └─── [ActiveMQ (61616)] ──> [Backend (8082)] ──> [MySQL (3306)] (更新操作)
```

### データフロー
1. メッセージ一覧表示
   - フロントエンドがMySQLから直接データを取得（MyBatis）
   - ページネーション、ソート、検索はSQLで処理

2. メッセージの作成/更新/削除
   - フロントエンドがActiveMQにメッセージを送信
   - バックエンドがメッセージを受信し処理（MyBatis）
   - 処理結果をMySQLに保存
   - フロントエンドが更新されたデータを表示

### アクセスポイント
- フロントエンド: http://localhost:8080
- バックエンド: http://localhost:8082
- ActiveMQ管理コンソール: http://localhost:8161/admin
  - ユーザー名: admin
  - パスワード: admin
- MySQL: localhost:3306

## 機能詳細

### メッセージ管理
- 最大255文字までのメッセージ入力
- 文字数のリアルタイムカウント
- 作成日時と更新日時の表示
- 削除時の確認ダイアログ

### 一覧表示
- ページネーション
- 動的な表示件数の変更（5, 10, 50, 100, 200件）
- 作成日時/更新日時でのソート（昇順/降順）
- キーワードによる検索
- 一括削除機能

### UI/UX
- レスポンシブデザイン
- モダンなUIコンポーネント
- 相対時間表示（「〇分前」など）
- 操作結果のフィードバック表示
- キーボードショートカット（Ctrl+Enter での送信）

## 技術スタック

### フロントエンドサービス（message-frontend）
- Spring Boot 2.7.0
- Thymeleaf（レイアウトダイアレクト含む）
- MyBatis
- Bootstrap 5.3.0
- Font Awesome 6.4.0
- ActiveMQ（メッセージング）

### バックエンドサービス（message-backend）
- Spring Boot 2.7.0
- MyBatis
- ActiveMQ（メッセージング）
- MySQL 8.0
- Flywayによるマイグレーション管理

## 技術的特徴

### フロントエンド
- Thymeleafレイアウトダイアレクトによる共通レイアウト
- 分離されたJavaScriptファイルによるコード管理
- MyBatisによるデータアクセス
- トランザクション管理

### バックエンド
- MyBatisによるデータアクセス
- メッセージキューによる非同期処理
- 階層化されたトランザクション管理
- 詳細なログ出力

## プロジェクト構造
```
mq-message-app/
├── message-frontend/         # フロントエンドアプリケーション
│   ├── pom.xml             # Mavenプロジェクト設定
│   └── src/
│       └── main/
│           ├── java/       # Javaソースコード
│           │   └── com/example/frontend/
│           │       ├── MessageFrontendApplication.java
│           │       ├── controller/    # 画面制御
│           │       ├── service/      # ビジネスロジック
│           │       ├── mapper/      # MyBatisマッパー
│           │       └── model/       # データモデル
│           └── resources/
│               ├── mybatis/         # MyBatis設定・マッピング
│               ├── static/         # 静的リソース（JS、CSS）
│               └── templates/      # Thymeleafテンプレート
│                   ├── layout/    # 共通レイアウト
│                   └── fragments/ # 再利用可能なフラグメント
└── message-backend/        # バックエンドアプリケーション
    ├── pom.xml           # Mavenプロジェクト設定
    └── src/
        └── main/
            ├── java/    # Javaソースコード
            │   └── com/example/backend/
            │       ├── MessageBackendApplication.java
            │       ├── service/    # ビジネスロジック
            │       ├── mapper/    # MyBatisマッパー
            │       └── model/     # データモデル
            └── resources/
                ├── mybatis/      # MyBatis設定・マッピング
                └── db/migration/ # Flywayマイグレーション
```

## セットアップガイド

### 必要なソフトウェア
- Docker 20.10以上
- Docker Compose 2.0以上
- Git

### Dockerコンテナの構成
```
[Frontend Container (8080)]
    │
    ├─── [MySQL Container (3306)]
    │
    └─── [ActiveMQ Container (61616)] ──> [Backend Container (8082)]
```

### アプリケーションのセットアップ

1. プロジェクトのクローン
   ```bash
   git clone https://github.com/yourusername/mq-message-app.git
   cd mq-message-app
   ```

2. 環境変数ファイルの作成
   ```bash
   # .envファイルを作成（以下は例）
   MYSQL_ROOT_PASSWORD=root
   MYSQL_DATABASE=messagedb
   MYSQL_USER=user
   MYSQL_PASSWORD=password
   ACTIVEMQ_ADMIN_LOGIN=admin
   ACTIVEMQ_ADMIN_PASSWORD=admin
   ```

### アプリケーションの起動

1. コンテナのビルドと起動
   ```bash
   docker compose up -d --build
   ```

2. 起動確認
   ```bash
   docker compose ps
   ```

### 動作確認
1. ブラウザで以下のURLにアクセス：
   - フロントエンド: http://localhost:8080
   - ActiveMQ管理コンソール: http://localhost:8161/admin
     - ユーザー名: admin
     - パスワード: admin

2. 以下の機能を確認：
   - メッセージの投稿
   - メッセージの一覧表示
   - メッセージの編集
   - メッセージの削除

### トラブルシューティング

1. ログの確認
   ```bash
   # 全てのコンテナのログを表示
   docker compose logs -f
   
   # 特定のサービスのログを表示
   docker compose logs -f [service_name]
   ```

2. データベースの確認
   ```bash
   # MySQLコンテナに接続
   docker compose exec mysql mysql -u root -p
   ```

3. コンテナの再起動
   ```bash
   # 全てのサービスを再起動
   docker compose restart
   
   # 特定のサービスを再起動
   docker compose restart [service_name]
   ```

4. 環境の再構築
   ```bash
   # コンテナとボリュームを削除して再構築
   docker compose down -v
   docker compose up -d --build
   ```

### 開発環境の設定

1. IDE
   - **推奨: Cursor**
     - AIによる強力なコード補完と提案
     - インテリジェントなコードナビゲーション
     - リアルタイムのエラー検出と修正提案
     - GitとDockerの統合サポート
     
     **拡張機能**
     - Spring Boot Extension Pack
       - Spring Boot開発支援
       - Spring Initializrのサポート
       - Spring Boot設定ファイルの補完
     - Java Extension Pack
       - Java言語サポート
       - デバッガー
       - テストランナー
     - Docker
       - Dockerfileのシンタックスハイライト
       - Docker Composeのサポート
       - コンテナの管理
     - GitLens
       - Gitの履歴表示の強化
       - コードの変更履歴の可視化
     - Thymeleaf
       - HTMLテンプレートのサポート
       - シンタックスハイライト
     - Checkstyle
       - Javaコードスタイルの検証
     - Lombok
       - Lombokアノテーションのサポート
     - XML
       - XMLファイルの編集支援
       - スキーマ検証
     - YAML
       - YAMLファイルの編集支援
       - Docker Compose設定のサポート
     - Japanese Language Pack
       - 日本語UIサポート
     - Error Lens
       - エラー・警告のインライン表示
       - 問題箇所の即時フィードバック
     - Material Icon Theme
       - ファイルタイプに応じたアイコン表示
       - プロジェクト構造の視認性向上

   - 代替: IntelliJ IDEA
     - プロジェクトをMavenプロジェクトとしてインポート
     - JDK 11を設定
     - Lombokプラグインをインストール
     - Docker integrationプラグインをインストール

2. ホットリロードの有効化
   - `docker-compose.yml`の開発用設定を参照

## 今後の改善点
- テストコードの追加
- エラーハンドリングの強化
- パフォーマンスの最適化
- セキュリティ機能の強化
