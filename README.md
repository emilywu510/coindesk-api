# Coindesk Spring Boot Project

## 說明
Spring Boot應用，實作coindesk API匯率查詢、幣別CRUD API、資料轉換與排程更新。

## 功能
- 查詢Coindesk 原始匯率資料 `/coindesk/raw`
- 轉換後資料格式 `/coindesk/converted`
- 幣別維護 API `/currencies` (查詢/新增/修改/刪除)
- 單元測試
- 排程每小時同步匯率

## 加分項
- AOP 印出 request/response log
- Error handling 處理API response
- Swagger UI
- 多語系設計
- design pattern 實作
- 能夠運行在Docker
- 加解密技術應用 (AES)
