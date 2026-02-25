# Smart Mobility Dashboard v1.0

自動化テストのデモを目的とした、車両ダッシュボードシミュレーターです。
制御システム開発者向けに「状態遷移」と「リアルタイム表示」を重視した設計になっています。

## 機能

- **スピードメーター**: 0-180km/h のゲージ表示（Canvas描画）
- **ギアインジケーター**: P / R / N / D の状態表示
- **警告灯**: エンジン警告、シートベルト警告、速度超過警告
- **制御パネル**: 加速/減速ボタン、異常発生スイッチ

## 技術スタック

| レイヤー | 技術 |
|----------|------|
| バックエンド | Quarkus 3.17, Java 21, JAX-RS |
| フロントエンド | Quarkus 3.17, Qute, JavaScript (Canvas API) |
| 単体テスト | JUnit 5, RestAssured |
| GUIテスト | Cucumber 7.x, Playwright for Java |
| コンテナ | Docker, OpenShift |

## プロジェクト構成

```
smart-mobility-dashboard/
├── backend/         # バックエンドAPI（車両状態管理）
├── frontend/        # フロントエンドUI（ダッシュボード画面）
├── e2e-tests/       # GUIテスト（Cucumber + Playwright）
├── k8s/             # Kubernetesマニフェスト
└── docker-compose.yml
```

## ローカル開発

### 前提条件

- Java 21
- Maven 3.9+
- Docker（コンテナ実行時）

### バックエンドの起動

```bash
cd backend
./mvnw quarkus:dev
```

バックエンドは http://localhost:8081 で起動します。

### フロントエンドの起動

```bash
cd frontend
./mvnw quarkus:dev
```

フロントエンドは http://localhost:8080 で起動します。

### Docker Composeで起動

```bash
docker-compose up --build
```

## テストの実行

### 単体テスト

```bash
cd backend
./mvnw test
```

### GUIテスト（Cucumber + Playwright）

```bash
# まずアプリケーションを起動
docker-compose up -d

# テスト実行
cd e2e-tests
./mvnw test

# Playwright ブラウザのインストール（初回のみ）
./mvnw exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
```

## OpenShiftへのデプロイ

```bash
# イメージのビルドとプッシュ
docker build -t <registry>/smart-mobility-backend:latest ./backend
docker build -t <registry>/smart-mobility-frontend:latest ./frontend
docker push <registry>/smart-mobility-backend:latest
docker push <registry>/smart-mobility-frontend:latest

# デプロイ
oc apply -f k8s/backend-deployment.yaml
oc apply -f k8s/frontend-deployment.yaml
```

## API エンドポイント

| メソッド | パス | 説明 |
|----------|------|------|
| GET | /api/vehicle/state | 現在の車両状態を取得 |
| POST | /api/vehicle/accelerate | 加速（+10km/h） |
| POST | /api/vehicle/decelerate | 減速（-10km/h） |
| POST | /api/vehicle/gear/{P\|R\|N\|D} | ギア変更 |
| POST | /api/vehicle/seatbelt/{true\|false} | シートベルト状態変更 |
| POST | /api/vehicle/engine-error/{true\|false} | エンジン異常発生/解除 |
| POST | /api/vehicle/reset | 状態リセット |

## テストシナリオ例（Gherkin）

```gherkin
Feature: スピードメーター表示

  Scenario: 加速ボタンで速度が増加する
    Given ダッシュボードが表示されている
    And 現在のギアが "D" である
    When 加速ボタンを 3 回押す
    Then 速度が 30 km/h になる

  Scenario: 速度超過で警告灯が点灯する
    Given ダッシュボードが表示されている
    And 現在のギアが "D" である
    When 加速ボタンを 13 回押す
    Then 速度警告灯が点灯する
```

## ライセンス

Demo Application
