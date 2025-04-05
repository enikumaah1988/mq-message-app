# MQ Message Application

ActiveMQを使用したメッセージング・アプリケーション

## プロジェクト構成

- `message-frontend`: フロントエンドアプリケーション（Spring Boot）
- `message-backend`: バックエンドアプリケーション（Spring Boot）

## 開発環境のセットアップ

1. 必要条件
   - Java 11
   - Maven
   - Docker
   - Docker Compose

2. ビルドと実行
   ```bash
   # フロントエンドの起動
   cd message-frontend
   docker compose up --build

   # バックエンドの起動
   cd message-backend
   docker compose up --build
   ```

## 機能

- メッセージの送信
- ActiveMQを使用したメッセージング
- H2データベースによるメッセージの永続化（フロントエンド） 