# BillManager 账单管理系统

> 基于 **Spring Boot 4.1.1 + Java 25 + MyBatis-Plus** 的个人记账后端服务。
> 提供账单与分类的完整增删改查、多条件组合分页查询，统一响应格式与全局异常处理。

当前处于**后端 API 开发阶段**：核心 CRUD 已完成，统计报表、用户体系、接口文档与自动化测试尚未开始。

---

## 技术栈

| 类别 | 选型 | 版本 |
|------|------|------|
| 语言 | Java | 25 |
| 框架 | Spring Boot（Web MVC / Validation / JDBC） | 4.1.1 |
| 持久层 | MyBatis-Plus（`spring-boot4-starter` + `jsqlparser`） | 3.5.17 |
| 数据库 | MySQL（开发环境实测 26.7.0） | — |
| 连接池 | HikariCP（Spring Boot 默认） | — |
| 构建 | Maven（含 Wrapper，无需本地安装） | — |
| 其他 | Lombok、spring-boot-devtools 热重启 | — |

---

## 功能进度

| 模块 | 已完成 | 待完成 |
|------|--------|--------|
| **分类管理** | 列表查询（按类型过滤）、新增、修改、逻辑删除（禁用）、重名校验、禁用分类自动恢复启用 | 分类图标、拖拽排序、删除前的关联账单保护 |
| **账单管理** | 详情查询、多条件分页查询、新增、修改、逻辑删除 | 统计报表（收支汇总 / 分类占比 / 趋势） |
| **基础设施** | 统一响应 `Result<T>`、全局异常处理、JSR-303 参数校验、字段自动填充、分页插件、枚举治理、数据库密码外置 | 用户体系与鉴权、Swagger 接口文档、单元 / 集成测试 |

### 已完成的关键设计

- **逻辑删除**：账单通过 `@TableLogic` 标记 `deleted` 字段，分类通过 `status=0` 禁用，历史数据始终保留，外键关系不被破坏。
- **禁用分类恢复策略**：新增分类时若命中「同名同类型且已禁用」的旧记录，直接恢复启用并更新排序值，规避数据库唯一索引 `uk_category_name_type` 冲突。
- **账单—分类三重业务校验**：新增 / 修改账单时校验分类「存在（404）→ 处于启用状态（400）→ 类型与账单类型一致（400）」，避免脏数据与外键异常退化成 500。
- **规避 N+1 查询**：分页查询先取当前页账单，再提取分类 ID **批量**查询并组装 Map，而非逐条查询分类名。
- **并发竞态兜底**：删除账单时检查受影响行数，行数为 0（已被并发删除）同样返回 404，不制造「删除成功」的假象。
- **枚举治理**：`BillType`、`CategoryStatus`、`ErrorCode` 三个枚举收敛了散落的字符串与数字魔法值；通过 `@EnumValue` + `@JsonValue` 保证数据库存储值与 JSON 报文格式在改造前后完全不变。
- **密码外置**：数据库密码不再硬编码，改为 `${DB_PASSWORD}` 占位符 + 本地 `.env` / 系统环境变量注入。

---

## 接口一览

基础路径：`http://localhost:8080`

### 账单 `/api/bills`

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/bills/{billId}` | 查询账单详情，不存在返回 404 |
| `GET` | `/api/bills/page` | 分页查询账单列表，支持多条件组合过滤 |
| `POST` | `/api/bills` | 新增账单 |
| `PUT` | `/api/bills/{billId}` | 修改账单 |
| `DELETE` | `/api/bills/{billId}` | 删除账单（逻辑删除） |

**分页查询参数**（全部可选，通过 Query String 传递）：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `page` | Integer | `1` | 页码，最小 1 |
| `size` | Integer | `10` | 每页数量，范围 1~100 |
| `billType` | String | — | `INCOME` / `EXPENSE`，非法值返回 400 |
| `categoryId` | Long | — | 按分类过滤 |
| `startDate` | LocalDate | — | 起始日期（含），格式 `yyyy-MM-dd` |
| `endDate` | LocalDate | — | 截止日期（含），格式 `yyyy-MM-dd` |

结果按「账单日期、创建时间」倒序排列。

### 分类 `/api/categories`

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/categories?categoryType=` | 查询**启用状态**的分类列表，可按类型过滤，按类型 + 排序值升序 |
| `POST` | `/api/categories` | 新增分类 |
| `PUT` | `/api/categories/{categoryId}` | 修改分类（重名校验会排除自身） |
| `DELETE` | `/api/categories/{categoryId}` | 删除分类（逻辑删除，置 `status=0`） |

---

## 统一响应格式

所有接口返回 `Result<T>` 结构：

```json
{
  "code": 200,
  "message": "查询成功",
  "data": { }
}
```

参数校验失败时，`data` 为「字段名 → 错误提示」的映射：

```json
{
  "code": 400,
  "message": "参数校验失败",
  "data": {
    "billAmount": "账单金额必须大于0",
    "billType": "账单类型只能是INCOME或EXPENSE"
  }
}
```

### 错误码

| code | 枚举 | 含义 |
|------|------|------|
| `200` | — | 成功 |
| `400` | `ErrorCode.BAD_REQUEST` | 参数校验失败、取值非法、业务状态不允许 |
| `404` | `ErrorCode.NOT_FOUND` | 账单 / 分类不存在 |
| `409` | `ErrorCode.CONFLICT` | 数据冲突（如分类重名） |
| `500` | `ErrorCode.INTERNAL_ERROR` | 系统内部错误 |

> 注：业务错误码位于响应体的 `code` 字段，HTTP 状态码仍为 200。

---

## 请求示例

**新增账单**

```http
POST /api/bills
Content-Type: application/json

{
  "billAmount": 68.50,
  "billType": "EXPENSE",
  "categoryId": 1001,
  "billDate": "2026-09-24",
  "remark": "午餐"
}
```

| 字段 | 必填 | 校验规则 |
|------|------|----------|
| `billAmount` | ✅ | ≥ 0.01，整数位 ≤ 13、小数位 ≤ 2（对齐 `DECIMAL(15,2)`） |
| `billType` | ✅ | 只能是 `INCOME` / `EXPENSE` |
| `categoryId` | ✅ | 须为已存在且启用的分类，类型需与 `billType` 一致 |
| `billDate` | ✅ | 格式 `yyyy-MM-dd` |
| `remark` | ❌ | 长度 ≤ 500 |

**新增分类**

```http
POST /api/categories
Content-Type: application/json

{
  "categoryName": "餐饮",
  "categoryType": "EXPENSE",
  "sort": 1
}
```

| 字段 | 必填 | 校验规则 |
|------|------|----------|
| `categoryName` | ✅ | 长度 ≤ 50，同类型下不可重名 |
| `categoryType` | ✅ | 只能是 `INCOME` / `EXPENSE` |
| `sort` | ✅ | ≥ 0，数值越小越靠前 |

**分页查询**

```http
GET /api/bills/page?page=1&size=10&billType=EXPENSE&startDate=2026-09-01&endDate=2026-09-30
```

---

## 数据模型

数据库：`bill_manager`（`utf8mb4` / `utf8mb4_unicode_ci`），建表脚本见 [`src/main/resources/Sql.sql`](src/main/resources/Sql.sql)。

### `category` 分类表

| 字段 | 类型 | 说明 |
|------|------|------|
| `category_id` | BIGINT PK | 分类 ID |
| `category_name` | VARCHAR(50) | 分类名称 |
| `category_type` | VARCHAR(20) | `INCOME` / `EXPENSE` |
| `sort` | INT | 排序值，升序 |
| `status` | TINYINT | 1 启用 / 0 禁用（逻辑删除） |
| `created_time`、`update_time` | DATETIME | 由 `MyMetaObjectHandler` 自动填充 |

索引：`uk_category_name_type (category_name, category_type)` 唯一索引。

### `bill` 账单表

| 字段 | 类型 | 说明 |
|------|------|------|
| `bill_id` | BIGINT PK | 账单 ID，雪花算法生成 |
| `bill_amount` | DECIMAL(15,2) | 金额 |
| `bill_type` | VARCHAR(20) | `INCOME` / `EXPENSE` |
| `category_id` | BIGINT FK | 关联 `category.category_id` |
| `remark` | VARCHAR(500) | 备注，可空 |
| `bill_date` | DATE | 账单日期 |
| `deleted` | TINYINT | 逻辑删除：0 正常 / 1 已删除 |

索引：`idx_bill_date`、`idx_bill_type`、`idx_bill_category_id`。

脚本内预置 **15 个支出分类**（餐饮、娱乐、出行、购物、住房……）与 **7 个收入分类**（工资、奖金、兼职、投资收益……），开箱即可记账。

---

## 快速开始

### 环境要求

- **JDK 25** 或以上
- **MySQL** 服务（开发环境实测 26.7.0）
- Maven 无需安装，仓库自带 Wrapper

### 1. 初始化数据库

执行建表脚本，会创建 `bill_manager` 库、两张表并写入预置分类：

```bash
mysql -u root -p < src/main/resources/Sql.sql
```

### 2. 配置数据库密码

密码不入库，通过项目根目录的 `.env` 文件提供。复制模板后填入真实值：

```bash
# Windows
copy .env.example .env

# macOS / Linux
cp .env.example .env
```

编辑 `.env`：

```properties
DB_PASSWORD=你的数据库密码
```

`.env` 已被 `.gitignore` 排除，不会被提交。

> 生产 / CI 环境无需 `.env`，直接设置系统环境变量 `DB_PASSWORD` 即可 —— 其优先级高于 `.env` 文件。
> 若两处都未提供，应用启动时会因占位符无法解析而快速失败，不会静默使用空密码。

数据库连接地址与用户名如需修改，编辑 [`src/main/resources/application.yml`](src/main/resources/application.yml) 的 `spring.datasource` 配置。

### 3. 启动应用

```bash
# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

启动后访问 `http://localhost:8080/api/categories` 应返回预置分类列表。

### 4. 运行测试

```bash
# Windows
mvnw.cmd test

# macOS / Linux
./mvnw test
```

---

## 项目结构

```
src/main/java/com/example/billmanager/
├── BillManagerApplication.java      # 启动类
├── config/                          # MyBatisPlusConfig（分页插件）、MyMetaObjectHandler（字段自动填充）
├── controller/                      # BillController、CategoryController
├── dto/                             # 请求参数对象
│   ├── bill/                        #   BillCreatedDTO、BillUpdateDTO、BillQueryDTO
│   └── category/                    #   CategoryCreateDTO、CategoryUpdateDTO
├── entity/                          # Bill、Category
├── enums/                           # BillType、CategoryStatus、ErrorCode
├── exception/                       # BusinessException、GlobalExceptionHandler
├── mapper/                          # BillMapper、CategoryMapper
├── service/                         # 业务接口
│   └── impl/                        # BillServiceImpl、CategoryServiceImpl
└── vo/                              # Result（统一响应）、BillPageVO（分页返回对象）
```

---

## 已知问题

- **自动化测试严重不足**：目前仅有一个 `contextLoads()` 上下文加载测试。由于 HikariCP 懒初始化，该测试**不会真正建立数据库连接**，因此它通过并不代表数据源配置正确，也未覆盖任何业务逻辑。补齐 Service / Controller 层测试是当前优先级最高的工程化任务。
- **全局异常处理未兜底**：`GlobalExceptionHandler` 尚未覆盖 `Exception`、`HttpMessageNotReadableException`、`MissingServletRequestParameterException` 等，异常堆栈有直接暴露给前端的风险。
- **枚举治理未完全收敛**：`Bill` 实体与 `BillPageVO` 已使用 `BillType` 枚举，但 `Category.categoryType` 与各 DTO 仍为 `String`，由 `BillServiceImpl.parseBillType()` 转换并兜底 400。
- **分页 VO 手动逐字段拷贝**：`BillServiceImpl` 中账单实体到 `BillPageVO` 的映射为手写赋值，后续可引入 MapStruct 简化。
- **生产环境需关闭调试配置**：`mybatis-plus.configuration.log-impl: StdOutImpl`（SQL 控制台日志）与 devtools 热重启应在生产 profile 中关闭。

完整技术债清单与后续规划见 [`docs/未来开发规划.md`](docs/未来开发规划.md)。

---

## 开发文档

| 文档 | 内容 |
|------|------|
| [`docs/开发日志.md`](docs/开发日志.md) | 按时间倒序记录各阶段完成的功能、技术决策与踩坑过程 |
| [`docs/未来开发规划.md`](docs/未来开发规划.md) | 短期 / 中期 / 长期功能规划、技术债清单、里程碑视图 |

---

## 后续路线图

**中期（P1）** — 账单统计接口（收支汇总、分类占比、日月趋势）、分类删除的关联保护、SpringDoc OpenAPI 接口文档、单元与集成测试（目标核心业务覆盖率 ≥ 70%）。

**长期（P2）** — 用户模块与多租户数据隔离（Spring Security + JWT）、预算管理、周期性账单自动生成、Excel / CSV 导入导出、分类缓存（Caffeine / Redis）与深翻页优化、配套前端（Vue3 / React + ECharts）。
