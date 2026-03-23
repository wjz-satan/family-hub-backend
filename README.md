# Family Hub Backend

基于 PRD 实现的家庭管理系统后端 MVP，技术栈为 `Spring Boot 3.3 + Java 17 + Spring Security + JPA + H2`。

## 已实现范围

- 认证：账号密码登录、刷新 Token、重置密码
- 家庭：创建家庭、查看详情、更新家庭、邀请码加入、成员列表、成员角色调整、移除成员
- 任务：列表、详情、创建、更新、删除、状态流转、批量操作
- 日历：事件列表、创建、更新、删除
- 财务：账单列表、创建、更新、删除、月度统计
- Dashboard：今日待办、本月支出、今日日程、成员任务完成情况
- 基础能力：JWT 鉴权、统一响应、全局异常处理、OpenAPI、H2 示例数据

## 启动方式

```bash
cd family-hub-backend
./mvnw spring-boot:run
```

接口文档：

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`

## 默认测试账号

- `13800000001 / Password123!`
- `13800000002 / Password123!`
- `13800000003 / Password123!`

## 鉴权方式

先调用：

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "phone": "13800000001",
  "password": "Password123!"
}
```

然后在业务请求头中带上：

```http
Authorization: Bearer <accessToken>
```
