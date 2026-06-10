# 奶茶记录 (MilkTea Record) — 设计文档

> **Spec-Driven, Subagent-Built, Human-Owned.**

---

## 1. 问题陈述

奶茶爱好者在日常消费中缺乏一个轻量工具来追踪自己的奶茶饮用习惯——花了多少钱、偏好哪些品牌和口味、消费频率如何。现有 App 要么功能臃肿（如记账软件），要么无法聚焦奶茶场景（如大众点评偏向发现而非追踪）。

**目标用户**：经常喝奶茶的个人用户，希望在 30 秒内记录一杯奶茶并逐步了解自己的消费模式。

**为什么值得做**：聚焦"奶茶"这一垂直场景，将饮品评价与消费统计合二为一——记录一杯奶茶就是一次消费事件，无需两套入口。

---

## 2. 用户故事

1. **US-1 记录奶茶**：作为用户，我想快速记录一杯奶茶的品牌、饮品名、价格、口味偏好和评价，以便日后回顾。
2. **US-2 浏览与筛选**：作为用户，我想按品牌、糖度、价格区间、关键词筛选我的奶茶记录，以便快速找到某条记录。
3. **US-3 编辑与删除**：作为用户，我想修改或删除之前记录的奶茶信息，以便纠正错误或清理无用记录。
4. **US-4 查看消费总览**：作为用户，我想看到总消费金额、总杯数、均价和最常喝的品牌，以便了解整体消费水平。
5. **US-5 消费趋势**：作为用户，我想看到每日消费趋势折线图和品牌消费分布饼图，以便发现消费规律。
6. **US-6 每周汇总**：作为用户，我想看到每周消费汇总柱状图，以便做周度复盘。

---

## 3. 功能规约

### 3.1 奶茶记录 CRUD

| 项目 | 说明 |
|------|------|
| 输入 | brand(必填), drinkName(必填), price(必填), consumeDate(必填), sugarLevel, iceLevel, cupSize, teaBase, rating(1-5), comment, wouldRecommend |
| 行为 | 创建/读取/更新/删除 MilkTeaRecord；列表支持分页和筛选 |
| 输出 | 单条返回完整 DTO；列表返回分页结构 `{ content, totalElements, totalPages, number, size }` |
| 边界条件 | brand/drinkName/price/consumeDate 不能为空；rating 范围 1-5；price ≥ 0；枚举字段必须是预定义值之一 |
| 错误处理 | 400 参数校验失败；404 记录不存在 |

### 3.2 筛选与搜索

| 项目 | 说明 |
|------|------|
| 输入 | 查询参数：brand, sugarLevel, minPrice, maxPrice, keyword, page, size |
| 行为 | 支持多条件组合筛选；keyword 模糊匹配 drinkName 和 brand |
| 输出 | 分页列表，同 3.1 |
| 边界条件 | 无筛选参数时返回全部记录（分页）；page 从 0 开始 |
| 错误处理 | 非法枚举值返回 400 |

### 3.3 消费统计

| 项目 | 说明 |
|------|------|
| 输入 | 各端点独立，overview 无参数；daily-trend 可选 days 参数(默认30)；brand-distribution 无参数；weekly-summary 可选 weeks 参数(默认4) |
| 行为 | 从 MilkTeaRecord 聚合计算统计数据 |
| 输出 | overview: `{ totalSpent, totalCount, avgPrice, topBrand }`; daily-trend: `{ dates: [], amounts: [] }`; brand-distribution: `{ brands: [], counts: [], amounts: [] }`; weekly-summary: `{ weeks: [], amounts: [], counts: [] }` |
| 边界条件 | 无数据时返回零值，不返回错误 |
| 错误处理 | 参数越界时使用默认值 |

---

## 4. 非功能性需求

| 维度 | 要求 |
|------|------|
| 性能 | 单次 API 响应 < 500ms（本地运行） |
| 安全 | 无用户系统，无需鉴权；CORS 仅允许前端域名 |
| 可用性 | 奶茶色暖色调 UI，操作步骤 ≤ 3 步完成一条记录 |
| 可观测性 | SpringBoot Actuator 健康检查端点 |
| 容器化 | `docker build` + `docker run` 单条命令启动；docker-compose 编排 |

---

## 5. 系统架构

```
┌──────────────────────┐     HTTP/JSON      ┌───────────────────────┐
│  Frontend            │ ◄────────────────► │  Backend              │
│  Vue3 + Vite         │     REST API       │  SpringBoot           │
│  Naive UI (奶茶主题) │                    │  ├ Controller 层      │
│  ECharts             │                    │  ├ Service 层         │
│  Pinia               │                    │  ├ Repository 层(JPA) │
│  Vue Router          │                    │  └ H2 (嵌入式,文件模式)│
│  Axios               │                    │                       │
└──────────────────────┘                    └───────────────────────┘
        │                                            │
   Nginx (生产)                              内嵌于 SpringBoot
   :80 → /api 代理到 :8080                    :8080
```

**关键设计决策：**
- 开发时 Vite proxy 将 `/api` 请求转发到后端 `:8080`，解决跨域
- 生产时 Nginx 静态资源 + `/api` 反向代理到后端
- H2 使用文件模式 (`spring.datasource.url=jdbc:h2:file:./data/milktea`)，数据持久化
- 无用户系统，所有 API 无需鉴权

---

## 6. 数据模型

### MilkTeaRecord（唯一实体）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | Long | PK, 自增 | 主键 |
| brand | String | NOT NULL | 品牌 |
| drinkName | String | NOT NULL | 饮品名 |
| price | BigDecimal | NOT NULL, ≥ 0 | 价格 |
| consumeDate | LocalDateTime | NOT NULL | 消费时间 |
| sugarLevel | String | 枚举 | 糖度：无糖/微糖/半糖/少糖/全糖 |
| iceLevel | String | 枚举 | 冰度：去冰/少冰/正常冰/多冰/温/热 |
| cupSize | String | 枚举 | 杯型：小杯/中杯/大杯/超大杯 |
| teaBase | String | 可选 | 茶底（如：绿茶、红茶、乌龙茶） |
| rating | Integer | 1-5 | 星级评分 |
| comment | String | 可选 | 文字点评 |
| wouldRecommend | Boolean | 可选 | 是否推荐 |
| createdAt | LocalDateTime | 自动填充 | 创建时间 |
| updatedAt | LocalDateTime | 自动填充 | 更新时间 |

### 枚举定义

| 枚举 | 值 |
|------|----|
| SugarLevel | 无糖, 微糖, 半糖, 少糖, 全糖 |
| IceLevel | 去冰, 少冰, 正常冰, 多冰, 温, 热 |
| CupSize | 小杯, 中杯, 大杯, 超大杯 |

---

## 7. API 设计

### 7.1 奶茶记录 CRUD

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/api/milk-tea-records?page=0&size=10&brand=&sugarLevel=&minPrice=&maxPrice=&keyword=` | 分页+筛选列表 |
| POST | `/api/milk-tea-records` | 新建记录 |
| GET | `/api/milk-tea-records/{id}` | 获取详情 |
| PUT | `/api/milk-tea-records/{id}` | 更新记录 |
| DELETE | `/api/milk-tea-records/{id}` | 删除记录 |

### 7.2 消费统计

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/api/stats/overview` | 总览：totalSpent / totalCount / avgPrice / topBrand |
| GET | `/api/stats/daily-trend?days=30` | 每日消费趋势 |
| GET | `/api/stats/brand-distribution` | 品牌消费分布 |
| GET | `/api/stats/weekly-summary?weeks=4` | 每周消费汇总 |

### 7.3 通用约定

- **统一响应格式**：`{ "code": 200, "message": "success", "data": {...} }`
- **错误响应**：`{ "code": 404, "message": "记录不存在", "data": null }`
- **分页格式**：`{ "content": [], "totalElements": 0, "totalPages": 0, "number": 0, "size": 10 }`
- **日期格式**：`yyyy-MM-dd HH:mm:ss`
- **CORS**：允许前端开发域名和生产品名

---

## 8. 技术选型与理由

| 层级 | 技术 | 理由 |
|------|------|------|
| 前端框架 | Vue 3 + Vite | 组合式 API + 快速构建，课程项目主流选择 |
| UI 组件库 | Naive UI | 轻量美观、主题可定制性强，适合中型项目 |
| 状态管理 | Pinia | Vue 3 官方推荐，比 Vuex 更简洁 |
| 图表 | ECharts | 国内最成熟的图表库，文档丰富 |
| 路由 | Vue Router 4 | Vue 3 官方路由 |
| HTTP 客户端 | Axios | 拦截器机制便于统一处理响应格式 |
| 后端框架 | Spring Boot 3 + Java 17 | 课程主流选择，生态成熟 |
| ORM | Spring Data JPA | 减少样板代码，单表场景足够 |
| 数据库 | H2 (文件模式) | 零安装嵌入式，容器重启数据不丢失 |
| 构建工具 | Maven | Java 项目标准构建工具 |
| 容器化 | Docker + docker-compose | 课程要求，Nginx 静态托管前端 + SpringBoot JAR 后端 |
| CI | GitHub Actions | 课程要求，push 触发测试 + Docker 镜像构建 |

---

## 9. 前端页面设计

### 页面1：奶茶记录列表 `/records`
- 左侧：筛选面板（品牌下拉、糖度下拉、价格区间、关键词搜索）
- 右侧：卡片列表 + 分页 + "+ 记一杯奶茶"按钮
- 卡片显示：品牌·饮品名、口味详情、价格、星级评分、消费时间、编辑/删除操作

### 页面2：记录表单 `/records/new` 和 `/records/:id/edit`
- 三栏布局：基本信息（品牌/饮品名/价格/消费时间）| 饮品详情（糖度/冰度/杯型/茶底）| 主观评价（星级/点评/是否推荐）
- 保存/取消按钮

### 页面3：消费统计 `/statistics`
- 顶部：4个概览卡片（总消费/总杯数/均价/最常喝品牌）
- 下方左：每日消费趋势折线图（近30天）
- 下方右：品牌消费分布饼图
- 底部：每周消费汇总柱状图

### 导航
- 顶部导航栏，2个 Tab：记录列表 / 消费统计
- 表单页面通过"+ 记一杯奶茶"按钮进入，为独立路由页面，非 Tab

---

## 10. 项目目录结构

```
milk-tea-record/
├── backend/
│   ├── src/main/java/com/milktea/
│   │   ├── MilkTeaApplication.java
│   │   ├── controller/
│   │   │   ├── MilkTeaRecordController.java
│   │   │   └── StatsController.java
│   │   ├── service/
│   │   │   ├── MilkTeaRecordService.java
│   │   │   └── StatsService.java
│   │   ├── repository/
│   │   │   └── MilkTeaRecordRepository.java
│   │   ├── model/
│   │   │   ├── MilkTeaRecord.java
│   │   │   └── enums/
│   │   │       ├── SugarLevel.java
│   │   │       ├── IceLevel.java
│   │   │       └── CupSize.java
│   │   ├── dto/
│   │   │   ├── MilkTeaRecordRequest.java
│   │   │   ├── MilkTeaRecordResponse.java
│   │   │   ├── StatsOverviewResponse.java
│   │   │   └── ApiResponse.java
│   │   └── config/
│   │       ├── CorsConfig.java
│   │       └── WebConfig.java
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── data.sql
│   ├── src/test/java/com/milktea/
│   │   ├── controller/
│   │   ├── service/
│   │   └── repository/
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── App.vue
│   │   ├── main.js
│   │   ├── router/index.js
│   │   ├── stores/milkTea.js
│   │   ├── views/
│   │   │   ├── RecordListView.vue
│   │   │   ├── RecordFormView.vue
│   │   │   └── StatisticsView.vue
│   │   ├── components/
│   │   │   ├── RecordCard.vue
│   │   │   ├── FilterBar.vue
│   │   │   ├── RecordForm.vue
│   │   │   ├── OverviewCards.vue
│   │   │   └── ChartPanel.vue
│   │   ├── api/milkTea.js
│   │   └── assets/styles/theme.js
│   ├── index.html
│   ├── vite.config.js
│   ├── Dockerfile
│   └── nginx.conf
├── docker-compose.yml
├── .github/workflows/ci.yml
├── SPEC.md
├── PLAN.md
├── AGENT_LOG.md
└── README.md
```

---

## 11. 验收标准

| 功能 | 完成标准 |
|------|----------|
| 记录 CRUD | 能新建/读取/更新/删除奶茶记录，所有字段正确持久化 |
| 分页 | 列表默认每页 10 条，翻页正常工作 |
| 筛选 | 品牌/糖度/价格区间/关键词组合筛选结果正确 |
| 统计总览 | 总消费/总杯数/均价/最常喝品牌计算正确 |
| 每日趋势 | 折线图数据点与数据库一致 |
| 品牌分布 | 饼图比例与实际消费数据一致 |
| 每周汇总 | 柱状图数据正确 |
| 容器化 | `docker-compose up` 一键启动，前端可访问，API 正常 |
| CI | GitHub Actions push 触发测试 + Docker 镜像构建 |
| 测试 | 后端单元测试覆盖率 ≥ 70%；`mvn test` 一键通过 |

---

## 12. 风险与未决问题

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| H2 文件模式并发写入 | 数据损坏 | 单用户场景无并发，可接受 |
| ECharts 包体积大 | 前端加载慢 | 按需引入 ECharts 组件 |
| 枚举字段用 String 存储 | 数据库无约束 | 后端 Java Enum + @Valid 校验 |
| Vite proxy 与 Nginx 配置不一致 | 开发/生产行为差异 | 统一 `/api` 前缀，两套配置保持一致 |
| 无用户系统导致数据无隔离 | 多人共用会混乱 | 明确为单用户工具，不做多用户支持 |
