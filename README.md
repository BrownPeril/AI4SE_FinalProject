# 🧋 奶茶记录 (MilkTea Record)

个人奶茶消费追踪工具——记录每一杯奶茶，洞察消费习惯。

## 功能

- 📝 记录奶茶：品牌、饮品名、价格、糖度、冰度、杯型、茶底、评分、点评
- 🔍 筛选搜索：按品牌/糖度/价格区间/关键词筛选
- 📊 消费统计：总览卡片 + 每日趋势 + 品牌分布 + 每周汇总
- 🐳 一键部署：Docker Compose 启动

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Vue 3 + Vite + Naive UI + ECharts + Pinia |
| 后端 | Spring Boot 3 + Spring Data JPA + H2 |
| 部署 | Docker + docker-compose + Nginx |

## 快速开始

### 开发模式

```bash
# 启动后端
cd backend && mvn spring-boot:run

# 启动前端（新终端）
cd frontend && npm install && npm run dev
```

访问 http://localhost:5173

### Docker 部署

```bash
docker compose up --build
```

访问 http://localhost

## 端口说明

| 服务 | 开发端口 | Docker 端口 |
|------|---------|------------|
| 前端 | 5173 | 80 |
| 后端 API | 8080 | 8080 (内部) |
| H2 控制台 | 8080/h2-console | - |

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| SPRING_DATASOURCE_URL | jdbc:h2:file:./data/milktea | H2 数据库路径 |

## 目录结构

```
├── backend/          # Spring Boot 后端
├── frontend/         # Vue3 前端
├── docker-compose.yml
├── SPEC.md           # 设计规约
├── PLAN.md           # 实现计划
└── AGENT_LOG.md      # 智能体日志
```

## 测试

```bash
cd backend && mvn test
```
