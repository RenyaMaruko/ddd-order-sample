# DDD 学習用サンプル（注文管理 / Java 8 + Spring Boot 2.7）

「注文」を題材に、DDDの基本パーツを最小限で示したプロジェクトです。
**読んで学ぶ用**なので、DBなし（インメモリ）・認証なし・起動確認なしを前提にしています。

## 読む順番（おすすめ）

1. **値オブジェクト** `domain/model/` の `Money` → `Quantity` → `OrderId` / `ProductId`
2. **エンティティ** `OrderLine`
3. **集約ルート** `Order` ← ここが核心。業務ルールは全部ここ
4. **ドメインサービス** `domain/service/DiscountPolicy`, `VolumeDiscountPolicy`
5. **ドメインイベント** `domain/event/OrderConfirmed`, `OrderCancelled`（受信側は `application/eventhandler/*Handler`）
6. **リポジトリ** `domain/repository/OrderRepository`（インターフェース）→ `infrastructure/InMemoryOrderRepository`（実装）
7. **アプリケーションサービス** `application/OrderApplicationService`
8. **プレゼンテーション** `presentation/OrderController`
9. **テスト** `OrderTest` ← Springなしで業務ルールを検証している

## 層構造と依存の向き

```
presentation  ──▶  application  ──▶  domain  ◀──  infrastructure
(Controller)       (UseCase)         (業務の中心)    (Repository実装, 設定, リスナー)
```

- **domain は誰にも依存しない**（Springのアノテーションも付けない）
- infrastructure は domain の `OrderRepository` を「実装する」ことで、domain に依存する（依存性逆転）

## DDDの用語とコードの対応

| DDD用語 | このプロジェクトでの例 | ポイント |
|---|---|---|
| 値オブジェクト | `Money`, `Quantity`, `OrderId`, `ProductId` | イミュータブル。値で等価判定。不変条件を自分で守る |
| エンティティ | `OrderLine` | IDで識別される。集約の内部にあり外から直接触らせない |
| 集約 / 集約ルート | `Order`（+ `OrderLine`） | 一貫性の境界。外部からは `Order` 経由でのみ操作 |
| ドメインサービス | `DiscountPolicy` | 特定のエンティティに置きにくい業務知識 |
| ドメインイベント | `OrderConfirmed` | 「起きた事実」。過去形の名前、変更不可 |
| リポジトリ | `OrderRepository` | 集約単位で保存・取得。IFはdomain、実装はinfrastructure |
| ファクトリ | `Order.create(...)` | 正しい初期状態での生成を保証 |
| アプリケーションサービス | `OrderApplicationService` | 段取りだけ。業務ルールは書かない |

## 注文の状態遷移

```
DRAFT ──confirm──▶ CONFIRMED ──ship──▶ SHIPPED
  │                    │
  └────── cancel ──────┴──▶ CANCELLED   （SHIPPED からはキャンセル不可）
```

## 業務ルール（すべて `Order` / 値オブジェクトの中にある）

- 数量は 1〜100 / 金額はマイナス不可
- 明細の追加・削除は DRAFT のときだけ、明細は最大10件
- 同じ商品を追加したら数量を合算
- 明細が空の注文は確定できない
- 小計 10,000円以上で10%割引（確定時に計算）
- 出荷は確定済みのみ / 出荷済み・キャンセル済みはキャンセル不可

## API（参考：実行する場合）

| メソッド | パス | 内容 |
|---|---|---|
| POST | `/orders` | 注文作成 |
| GET | `/orders/{id}` | 注文取得 |
| POST | `/orders/{id}/items` | 明細追加 `{"productId":"P-001","productName":"ペン","unitPrice":1000,"quantity":10}` |
| DELETE | `/orders/{id}/items/{productId}` | 明細削除 |
| POST | `/orders/{id}/confirm` | 確定 |
| POST | `/orders/{id}/ship` | 出荷 |
| POST | `/orders/{id}/cancel` | キャンセル |

業務ルール違反は `422`、存在しない注文は `404` を返します。

## 学習の次のステップ（アイデア）

- `InMemoryOrderRepository` を Spring Data JPA 実装に差し替えてみる（domain/application が無変更で済むか確認）
- 新しい集約 `Customer` を追加し、`Order` から **IDだけ** で参照する
- 割引ポリシーを追加して差し替える（例：会員ランク別割引）
