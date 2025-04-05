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

### インフラストラクチャ
- **Docker** & **Docker Compose** (コンテナ化)
- **Maven** (ビルドツール)

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
│           └── resources/  # 設定ファイル
└── message-backend/         # バックエンドアプリケーション
    ├── Dockerfile          # バックエンドのコンテナ定義
    ├── pom.xml            # Mavenプロジェクト設定
    └── src/
        └── main/
            ├── java/      # Javaソースコード
            └── resources/
                └── db/
                    └── migration/  # Flywayマイグレーションファイル

```

### コンポーネント詳細

#### message-frontend
- メッセージ送信用Webアプリケーション
- ポート: 8080
- 主な機能:
  - メッセージ送信フォーム提供
  - ActiveMQへのメッセージ送信

#### message-backend
- メッセージ処理アプリケーション
- ポート: 8082
- 主な機能:
  - ActiveMQからのメッセージ受信
  - MySQLへのメッセージ保存
  - 受信メッセージのログ出力
  - Flywayによるデータベースマイグレーション

#### ActiveMQ
- メッセージブローカー
- ポート: 
  - 61616 (メッセージング)
  - 8161 (管理コンソール)
- 機能:
  - メッセージキューの管理
  - フロントエンドとバックエンド間のメッセージ仲介

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
[Frontend (8080)] -> [ActiveMQ (61616)] -> [Backend (8082)] -> [MySQL (3306)]
```

- フロントエンド: メッセージの送信を担当
- ActiveMQ: メッセージキューの管理
- バックエンド: メッセージの受信と保存を担当
- MySQL: メッセージの永続化

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
