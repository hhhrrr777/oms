# WayF 设计规范（Design Tokens）

> 来源：登录页 [vue2/src/views/login.vue](../vue2/src/views/login.vue) 定稿方案。
> 用途：主页、表单页、弹窗等后续页面统一取用本文件中的色彩、圆角、阴影、字号规范，保持视觉一致性。
> 技术栈：Vue 2.7 + Element UI 2.15 + SCSS。

## 1. 品牌主色

| Token | 值 | 用途 |
|---|---|---|
| `--brand-primary` | `#307dff` | 主按钮、链接、聚焦态、品牌强调 |
| `--brand-primary-hover` | `#4a8dff` | 主按钮 hover |
| `--brand-primary-deep` | `#2f7bff` | 渐变按钮深色端（hover） |
| `--brand-primary-light` | `#4a9bff` | 渐变按钮浅色端 |
| `--brand-primary-lighter` | `#5aa9ff` | 渐变浅色端、徽章渐变 |

主按钮渐变：`linear-gradient(90deg, #307dff, #4a9bff)`，hover 时 `linear-gradient(90deg, #2f7bff, #5aa9ff)`。
徽章/Logo 渐变：`linear-gradient(135deg, #307dff, #5aa9ff)`。

## 2. 品牌背景渐变（大背景/品牌区）

```
linear-gradient(150deg, #16235e 0%, #1d39c4 32%, #307dff 68%, #5aa9ff 100%)
```

四段：深靛 `#16235e` → 宝蓝 `#1d39c4` → 主蓝 `#307dff` → 亮蓝 `#5aa9ff`。
适用：登录页背景、品牌横幅、空状态插画底色等需要品牌氛围的大面积区域。

### 背景装饰色（低透明度叠加）

| 元素 | 值 | 说明 |
|---|---|---|
| 光晕 orb-1 | `rgba(105,177,255,.55)` | 浅蓝光晕，blur 70px |
| 光晕 orb-2 | `rgba(123,92,255,.45)` | 紫色光晕，blur 70px |
| 光晕 orb-3 | `rgba(22,194,163,.35)` | 青色光晕，blur 70px |
| 圆环描边 | `rgba(255,255,255,.18)` / `.12` | 同心圆装饰 |
| 光束 | `rgba(255,255,255,.14)` → 透明 | 斜向 18° 渐变光束 |
| 网点纹理 | `rgba(255,255,255,.16)` 1px 点，间距 24px | 全屏细点纹 |

强调辅助色：成功/正向 `#16c2a3`（青绿），用于上涨指标、健康度等。

## 3. 中性色（文字 / 边框 / 底色）

| Token | 值 | 用途 |
|---|---|---|
| 主文字 | `#1f2329` | 标题、输入内容 |
| 次要文字 | `#8a8f99` | 副标题、说明 |
| 弱提示文字 | `#9aa0aa` | 图标、slogan |
| 占位/链接弱色 | `#b6bcc7` | 底部链接、分隔文案 |
| 分隔点 | `#d5dae2` | 链接间圆点 |
| 输入框边框 | `#e4e7ed` | 默认边框 |
| 卡片边框 | `#eef1f6` | 浅色卡片描边 |
| 分割线 | `#eceff4` | 分隔线 |
| 页面浅灰底 | `#f6f8fb` | 表单区底色（如需） |
| 纯白 | `#fff` | 卡片、面板 |

## 4. 聚焦态

- 边框：`#307dff`
- 光晕：`box-shadow: 0 0 0 3px rgba(48,125,255,.12)`
- 过渡：`transition: border-color .2s, box-shadow .2s`

## 5. 圆角

| 场景 | 值 |
|---|---|
| 输入框 / 按钮 | `8px` |
| 小徽章 / 图标块 | `10px` ~ `12px` |
| 卡片 / 面板 | `18px` |
| 圆形图标按钮 | `50%` |

## 6. 阴影

| Token | 值 | 用途 |
|---|---|---|
| 悬浮卡片（彩色背景上） | `0 32px 80px rgba(8,22,70,.35)` | 登录卡、弹窗 |
| 徽章投影 | `0 8px 18px rgba(48,125,255,.35)` | Logo 徽章 |
| 按钮投影 | `0 8px 18px rgba(48,125,255,.3)` | 主按钮 |
| 图标悬浮 | `0 6px 14px rgba(48,125,255,.18)` | 可点图标 hover |

## 7. 字号 / 字重

| 场景 | 字号 | 字重 |
|---|---|---|
| 品牌大标题（hero） | `34~36px` | 700 |
| 卡片标题（欢迎登录） | `25~26px` | 700 |
| 表单副标题 | `14px` | 400 |
| 输入框 / 按钮 | `14~15px` | 400 / 500 |
| 辅助说明 / 底部链接 | `12px` | 400 |

字体族：`"Helvetica Neue", Arial, "PingFang SC", "Microsoft YaHei", sans-serif`
主按钮字距：`letter-spacing: 6px`（"登 录"类短文案专用）。

## 8. 尺寸

| 元素 | 值 |
|---|---|
| 输入框高度 | `46px` |
| 主按钮高度 | `46px` |
| 输入框左内边距（带图标） | `40px` |
| 前缀图标宽度 / 左距 | `16px` / `12px` |
| 登录卡宽度 | `380px` |
| 登录卡内边距 | `40px 38px 28px` |

## 9. 响应式断点

`900px`：以下收起品牌区/装饰，仅保留表单卡片居中。

## 10. 动效

- 悬浮卡片漂浮：`translateY(0 → -10px)`，`6s ease-in-out infinite`
- 可点元素 hover：`translateY(-2px)` + 投影加深，`transition: all .2s`
- 输入/按钮交互过渡统一 `.2s`

---

### Element UI 覆盖参考

覆盖 Element 默认样式时的关键选择器（见 login.vue `<style>`）：

```scss
.el-input input {
  height: 46px; border-color: #e4e7ed; border-radius: 8px;
  &:focus { border-color: #307dff; box-shadow: 0 0 0 3px rgba(48,125,255,.12); }
}
.el-button--primary {
  background: linear-gradient(90deg, #307dff, #4a9bff);
  border: none; border-radius: 8px;
}
```

> 注意：登录页 `<style>` 未加 `scoped`，覆盖样式会影响全局；在主页/表单页复用时建议加 `scoped` 或使用深度选择器 `::v-deep` 限定作用域。
