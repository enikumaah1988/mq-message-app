# MQ Message Application

ActiveMQを使用したシンプルなメッセージング・アプリケーション

## システム構成

### コンポーネント
- `message-frontend`: メッセージ送信用Webアプリケーション（Spring Boot）
- `message-backend`: メッセージ受信用アプリケーション（Spring Boot）
- `activemq`: メッセージブローカー

### 使用ポート
- フロントエンド: 8080
- バックエンド: 8082
- ActiveMQ: 61616（メッセージング）, 8161（管理コンソール）

## 開発環境のセットアップ

### 必要条件
- Java 11
- Maven
- Docker
- Docker Compose

### ビルドと実行

1. リポジトリのクローン
```bash
git clone https://github.com/enikumaah1988/mq-message-app.git
cd mq-message-app
```

2. アプリケーションの起動
```bash
docker compose up --build
```

これにより、全てのサービス（フロントエンド、バックエンド、ActiveMQ）が起動します。

### 動作確認

1. フロントエンドアクセス
   - URL: http://localhost:8080
   - メッセージ送信フォームが表示されます

2. ActiveMQ管理コンソール
   - URL: http://localhost:8161/admin
   - ユーザー名: admin
   - パスワード: admin

3. メッセージの送受信
   - フロントエンドからメッセージを送信
   - バックエンドのログでメッセージ受信を確認
   - ActiveMQ管理コンソールでキューの状態を確認

## プロジェクト構造

```
mq-message-app/
├── docker-compose.yml    # 全サービスの定義
├── message-frontend/     # フロントエンドアプリケーション
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
└── message-backend/      # バックエンドアプリケーション
    ├── Dockerfile
    ├── pom.xml
    └── src/
```

## 機能
- メッセージの送信（フロントエンド）
- メッセージの受信と記録（バックエンド）
- ActiveMQによるメッセージング
- H2データベースによるメッセージの永続化（フロントエンド） 