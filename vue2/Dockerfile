# syntax=docker/dockerfile:1.7
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
# 默认走国内镜像源（与 README 保持一致）；其他网络可用 --build-arg NPM_REGISTRY=https://registry.npmjs.org 覆盖
ARG NPM_REGISTRY=https://registry.npmmirror.com
RUN --mount=type=cache,target=/root/.npm \
    npm ci --registry=${NPM_REGISTRY} --no-audit --no-fund

COPY . .

# .env.production → VUE_APP_BASE_API=/prod-api
RUN npm run build:prod

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
# nginx.conf 运行时由 compose 挂载（deploy/nginx/nginx.dev.conf / nginx.prod.conf），
# 证书仅在 prod 由 compose 挂载 —— 都不打进镜像，切换环境无需重建
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
