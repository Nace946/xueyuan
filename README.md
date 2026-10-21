# 租房招聘通 — 信息展示小程序 + Java 管理后台

一个「租房信息 + 招聘信息」展示型小程序，配套 Java 后台管理系统。
小程序端只做**信息浏览与排版展示**，不涉及登录、下单、支付；所有内容由后台发布维护。

---

## 一、目录结构

```
.
├── miniprogram/            # 微信小程序（原生开发，无第三方框架）
│   ├── app.js / app.json / app.wxss
│   ├── pages/              # 主包：首页、租房列表、招聘列表、我的
│   ├── pages/detail/       # 分包：房源详情、职位详情、web-view
│   ├── components/         # 房源卡片、招聘卡片、空状态、加载更多
│   ├── services/           # 接口服务层
│   ├── utils/              # 请求封装、格式化、通用工具
│   ├── styles/             # 全局变量与通用样式
│   └── assets/             # tabBar 图标、占位图
│
├── server/                 # Spring Boot 2.7 + MyBatis-Plus 后端（JDK 8 可运行）
│   ├── pom.xml
│   ├── src/main/resources/
│   │   ├── application.yml             # 数据源 / 上传 / JWT 配置
│   │   ├── db/schema.sql               # 建表脚本 + 演示数据
│   │   └── static/admin/               # 管理后台构建产物（npm run build 自动输出到这里）
│   └── src/main/java/com/aixueyuan/rent/
│
├── admin/                  # 管理后台前端（Vue3 + Vite + Element Plus）
│   ├── src/views/          # 登录、概览、房源、招聘、轮播
│   └── vite.config.js      # dev 代理 + 构建输出目录
│
└── tools/gen_icons.py      # 生成 tabBar 图标（纯标准库，无需 Pillow）
```

---

## 二、技术栈与运行环境

| 端 | 技术 | 运行要求 |
|---|---|---|
| 小程序 | 微信原生框架（WXML/WXSS/JS） | 微信开发者工具，基础库建议 2.x 以上 |
| 后端 | Spring Boot 2.7.18 + MyBatis-Plus 3.5.5 + JWT + BCrypt | **JDK 8 及以上**（1.8/11/17 均可），MySQL 5.7 或 8.0 |
| 后台前端 | Vue 3 + Vite 5 + Element Plus | Node.js 18+（仅构建时需要，构建后可随 jar 部署） |

---

## 三、快速开始

### 1. 初始化数据库

```bash
mysql -u root -p < server/src/main/resources/db/schema.sql
```

脚本会创建 `rent_job` 库与 `house / job / banner / sys_user` 四张表，并插入 2 条房源、2 条职位演示数据。

### 2. 启动后端

修改 `server/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://你的地址:3306/rent_job?...&serverTimezone=Asia/Shanghai
    username: 你的账号
    password: 你的密码
upload:
  path: ./uploads/          # 生产环境建议改为绝对路径，如 /data/rent/uploads/
jwt:
  secret: 改成你自己的随机字符串   # 生产环境必须修改
```

启动：

```bash
cd server
mvn spring-boot:run                 # 开发启动
# 或打包部署
mvn clean package -DskipTests
java -jar target/rent-job-server.jar
```

**首次启动会自动创建默认管理员**（仅当 `sys_user` 表为空时），控制台会打印账号密码：
默认账号 `admin`，默认密码 `admin123`（在 `application.yml` 的 `app.admin` 节点配置）。
登录后请尽快修改密码：目前可在数据库中更新 `sys_user.password`（需写入 BCrypt 密文）。

### 3. 启动管理后台

```bash
cd admin
npm install
npm run dev        # 开发：http://localhost:5173 （已配置代理到 8080）
npm run build      # 构建：产物直接输出到 server/src/main/resources/static/admin
```

构建完成后重新打包后端 jar，即可通过 `http://服务器地址:8080/admin/` 访问后台，**前后端一个 jar 部署**。

### 4. 运行小程序

1. 微信开发者工具 → 导入项目 → 目录选择 `miniprogram`，填入你的 AppID。
2. 修改 `miniprogram/utils/config.js` 中的 `baseUrl`：
   - 开发阶段：`http://localhost:8080`，并在开发者工具勾选「不校验合法域名」。
   - 上线前：必须是 **已备案的 https 域名**，且已在小程序后台「开发 → 开发管理 → 服务器域名」配置为 request 合法域名。
3. 上传的图片域名（`/uploads/`）同样需要在 **downloadFile 合法域名** 中配置（与接口同域名时配置一次即可）。

---

## 四、接口清单

### 小程序端（无需登录，`/api/open/**`）

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/open/home/index` | 首页聚合：轮播 + 推荐房源 + 最新职位 + 数量统计 |
| GET | `/api/open/house/list` | 房源分页，支持 keyword / district / minPrice / maxPrice / sort |
| GET | `/api/open/house/{id}` | 房源详情（自动累加浏览量） |
| GET | `/api/open/job/list` | 职位分页，支持 keyword / district / jobType / sort |
| GET | `/api/open/job/{id}` | 职位详情 |

统一返回结构：`{ "code": 0, "msg": "ok", "data": {...} }`，`code != 0` 为业务错误。

### 管理端（需 JWT，`/api/admin/**`）

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/admin/login` | 登录，返回 token |
| GET | `/api/admin/profile` | 校验登录态 |
| GET/POST/PUT/DELETE | `/api/admin/house/...` | 房源分页、详情、新增、修改、删除 |
| POST | `/api/admin/house/{id}/status` | 上/下架 |
| GET/POST/PUT/DELETE | `/api/admin/job/...` | 招聘管理（同上） |
| GET/POST/PUT/DELETE | `/api/admin/banner/...` | 首页轮播管理 |
| POST | `/api/admin/upload/image` | 图片上传，返回 `{ url: "/uploads/2026/10/18/xxx.jpg" }` |

除 `/api/admin/login` 外，其余管理端接口需在请求头携带 `Authorization: Bearer <token>`。

---

## 五、部署建议（生产）

1. **JDK 8 起步**：服务器装 JRE/JDK 8 即可运行，无需更高版本。
2. **上传目录持久化**：`upload.path` 指向容器外挂载目录，避免重启丢失图片。
3. **HTTPS 与域名**：小程序要求 https，用 Nginx 反向代理到 8080，或直接在 Spring Boot 配置 SSL。
4. **定时备份**：`rent_job` 数据库 + `uploads` 目录。
5. **修改默认口令**：`jwt.secret` 与管理员密码必须替换。

```nginx
server {
    listen 443 ssl;
    server_name api.example.com;   # 需备案 + 小程序后台配置合法域名
    ssl_certificate     /path/fullchain.pem;
    ssl_certificate_key /path/privkey.pem;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    client_max_body_size 20m;
}
```

---

## 六、⚠️ 提审合规提示（以微信官方最新规则为准）

本小程序虽只做展示，但**内容类目仍需资质**，提审前请在微信公众平台「设置 → 基本设置 → 类目」中核对：

1. **招聘类目**：发布招聘/求职信息通常属于人力资源相关类目，可能需提供人力资源服务相关资质。若无资质，建议仅做"信息展示"且信息由自有渠道产生，并提前在提审备注中说明；最终以微信官方类目资质要求为准。
2. **房产/租房类目**：房屋租赁信息发布可能涉及房产相关资质要求，同样以官方类目页面为准。
3. **个人主体限制**：个人主体无法选择部分商业类目，且不支持 `web-view` 组件；如需外链跳转请使用企业/组织主体。
4. **信息真实性**：后台发布的信息请自行核实，小程序「我的」页已内置免责声明，建议保留。
5. **隐私合规**：本项目未调用任何微信隐私接口（不获取位置、手机号、用户信息），因此无需配置隐私授权弹窗；后续若新增此类能力，需先在小程序后台配置《用户隐私保护指引》并在 `app.json` 声明 `requiredPrivateInfos`。

官方文档入口：
- 小程序运营规范：https://developers.weixin.qq.com/miniprogram/product/reject.html
- 类目资质查询：https://developers.weixin.qq.com/miniprogram/product/material/category.html
- 服务器域名配置：https://developers.weixin.qq.com/miniprogram/dev/framework/ability/network.html

---

## 七、常见问题

| 问题 | 排查 |
|---|---|
| 小程序请求失败 | 检查 `config.js` 的 baseUrl；开发阶段勾选「不校验合法域名」；确认后端已启动 |
| 图片在真机不显示 | 图片域名需配置 downloadFile 合法域名；确认 `/uploads/` 能被访问 |
| 后台登录后立即掉线 | 检查服务器时间是否准确（JWT 有效期依赖系统时间） |
| 上传图片失败 | 检查 `upload.path` 目录写权限；默认单图限制 10MB |
| 启动时报数据库连接失败 | 检查 MySQL 是否允许远程连接、`serverTimezone` 参数、账号权限 |
