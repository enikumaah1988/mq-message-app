# Message Queue Application

このアプリケーションは、メッセージキューを使用したマイクロサービスアーキテクチャのデモンストレーションです。

## 技術スタック

### バックエンド (message-backend)
- **Java 11**
- **Spring Boot 2.x**
  - Spring JMS (メッセージング)
  - Spring Data JPA (データアクセス)
- **MySQL 8.0** (データベース)
- **ActiveMQ 5.15.9** (メッセージブローカー)
- **Flyway** (データベースマイグレーション)

### フロントエンド (message-frontend)
- **Java 11**
- **Spring Boot 2.x**
  - Spring JMS (メッセージング)
  - Thymeleaf (テンプレートエンジン)
- **Bootstrap 5.3.0** (UIフレームワーク)
- **Font Awesome 6.4.0** (アイコン)

### インフラストラクチャ
- **Docker** & **Docker Compose** (コンテナ化)
- **Maven** (ビルドツール)

## 主な機能

### メッセージ管理
- メッセージの作成、表示、更新、削除（CRUD操作）
- JMSを使用した非同期メッセージング
- データベースでの永続化

### ユーザーインターフェース
- レスポンシブデザイン
- ページネーション機能
  - 可変表示件数（5,10,50,100,200件）
  - ページ間のナビゲーション
- 操作フィードバック
  - 成功/エラーメッセージの表示
  - 削除時の確認ダイアログ
- モダンなUI
  - Bootstrap 5.3.0によるデザイン
  - Font Awesome 6.4.0アイコンの使用

### アーキテクチャ
- Thymeleafレイアウトダイアレクト
  - 共通レイアウト（base.html）
  - 再利用可能なフラグメント（ヘッダー、フッター）
- トランザクション管理
- エラーハンドリング

## システム構成

### コンポーネント構成
```
mq-message-app/
├── docker-compose.yml        # コンテナ構成定義
├── message-frontend/         # フロントエンドアプリケーション
│   ├── Dockerfile           # フロントエンドのコンテナ定義
│   ├── pom.xml             # Mavenプロジェクト設定
│   └── src/
│       └── main/
│           ├── java/       # Javaソースコード
│           │   └── com/example/frontend/
│           │       ├── MessageFrontendApplication.java  # アプリケーションエントリーポイント
│           │       ├── controller/    # コントローラー（画面制御）
│           │       ├── service/      # ビジネスロジック
│           │       ├── repository/   # データアクセス
│           │       └── config/       # アプリケーション設定
│           └── resources/
│               ├── templates/           # Thymeleafテンプレート
│               │   ├── base.html       # 共通レイアウト
│               │   ├── index.html      # メインページ
│               │   └── fragments/      # 再利用可能なフラグメント
│               │       ├── header.html # ヘッダー部分
│               │       └── footer.html # フッター部分
│               └── static/            # 静的リソース
└── message-backend/         # バックエンドアプリケーション
    ├── Dockerfile          # バックエンドのコンテナ定義
    ├── pom.xml            # Mavenプロジェクト設定
    └── src/
        └── main/
            ├── java/      # Javaソースコード
            │   └── com/example/backend/
            │       ├── MessageBackendApplication.java  # アプリケーションエントリーポイント
            │       ├── entity/      # データモデル
            │       ├── repository/  # データアクセス
            │       ├── service/     # ビジネスロジック
            │       └── listener/    # メッセージリスナー
            └── resources/
                └── db/
                    └── migration/  # Flywayマイグレーションファイル

```

### コンポーネント詳細

#### message-frontend
- メッセージ管理用Webアプリケーション
- ポート: 8080
- 主な機能:
  - メッセージの表示（MySQLから直接）
  - メッセージの作成/更新/削除（ActiveMQ経由）
  - ページネーション処理
  - レスポンシブなUI提供
  - 操作フィードバック表示

#### message-backend
- メッセージ処理アプリケーション
- ポート: 8082
- 主な機能:
  - ActiveMQからの更新メッセージ受信
  - MySQLへのデータ操作（CRUD）
  - トランザクション管理
  - Flywayによるデータベースマイグレーション

#### ActiveMQ
- メッセージブローカー
- ポート: 
  - 61616 (メッセージング)
  - 8161 (管理コンソール)
- 機能:
  - メッセージキューの管理
  - 更新操作のメッセージ仲介
  - 非同期処理の実現

#### MySQL
- データベースサーバー
- ポート: 3306
- 機能:
  - メッセージの永続化
  - データベース名: messagedb
- マイグレーション管理:
  - Flywayによるバージョン管理
  - マイグレーション履歴テーブル: flyway_schema_history

## アーキテクチャ

```
[Frontend (8080)]
    │
    ├─── [MySQL (3306)] (Read)
    │
    └─── [ActiveMQ (61616)] ──> [Backend (8082)] ──> [MySQL (3306)] (Write)
```

### データフロー
- フロントエンド:
  - 表示: MySQLから直接データを読み取り
  - 更新: ActiveMQを介してバックエンドにメッセージを送信
- ActiveMQ: 
  - フロントエンドからバックエンドへの更新メッセージ仲介
  - 作成/更新/削除操作のメッセージング
- バックエンド:
  - メッセージの受信と処理
  - データベースへの書き込み操作
- MySQL:
  - フロントエンド: 読み取り専用アクセス（表示用）
  - バックエンド: 書き込みアクセス（CRUD操作）

### 処理フロー
1. メッセージ表示
   - フロントエンドがMySQLから直接データを取得
   - ページネーションとソートはフロントエンドで処理

2. メッセージ作成/更新/削除
   - フロントエンドがActiveMQにメッセージを送信
   - バックエンドがメッセージを受信し処理
   - 処理結果をMySQLに保存
   - フロントエンドが更新されたデータを表示

## 起動方法

```bash
cd mq-message-app
docker compose up --build
```

## データベース管理

### マイグレーション
データベースのスキーマ管理にはFlywayを使用しています。マイグレーションファイルは以下の場所に配置されています：
```
message-backend/src/main/resources/db/migration/
```

マイグレーションファイルの命名規則：
- `V{バージョン}__{説明}.sql`
- 例: `V1__create_message_table.sql`

### テーブル構造
#### messageテーブル
```sql
CREATE TABLE message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### データベース接続
```bash
# MySQLコンテナに接続
docker exec -it mq-message-app-mysql-1 mysql -uroot -proot

# データベース選択
use messagedb;

# テーブル一覧表示
show tables;

# メッセージテーブルの内容確認
select * from message;

# マイグレーション履歴確認
select * from flyway_schema_history;
```

### データベース情報
- データベース名: messagedb
- ユーザー名: root
- パスワード: root
- 主要テーブル:
  - message: メッセージ保存用テーブル
  - flyway_schema_history: マイグレーション履歴

## アクセスポイント

- フロントエンド: http://localhost:8080
- バックエンド: http://localhost:8082
- ActiveMQ管理コンソール: http://localhost:8161/admin
  - ユーザー名: admin
  - パスワード: admin
- MySQL: localhost:3306

## メッセージキュー管理

### ActiveMQ管理コンソール
1. http://localhost:8161/admin にアクセス
2. ユーザー名: admin、パスワード: admin でログイン
3. 「Queues」タブを選択
4. 使用中のキュー:
   - `messageQueue`: フロントエンドからバックエンドへのメッセージ送信用
5. 不要なキューの削除:
   - 削除したいキューの「Delete」をクリック

### キューの監視
管理コンソールでは以下の情報を確認できます：
- Number of Pending Messages: 未処理メッセージ数
- Number of Consumers: コンシューマー数
- Messages Enqueued: キューに追加されたメッセージの総数
- Messages Dequeued: 処理されたメッセージの総数
