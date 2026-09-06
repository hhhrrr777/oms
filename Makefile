COMPOSE_DEV  = docker compose -f docker-compose.yml -f docker-compose.dev.yml
COMPOSE_PROD = docker compose -f docker-compose.yml -f docker-compose.prod.yml

.PHONY: dev infra prod logs ps down clean

dev:            ## 全容器化 dev（web:88 api:8086 mysql:3306 redis:6379）
	$(COMPOSE_DEV) up -d --build

infra:          ## 轻量本地开发：容器里只跑 mysql + redis
	$(COMPOSE_DEV) up -d mysql redis

prod:           ## 生产（web:80/443，TLS，不暴露 db 端口）
	$(COMPOSE_PROD) up -d --build

logs:
	$(COMPOSE_DEV) logs -f

ps:
	$(COMPOSE_DEV) ps

down:
	$(COMPOSE_DEV) down

clean:          ## 会删库！连 volume 一起移除
	$(COMPOSE_DEV) down -v
