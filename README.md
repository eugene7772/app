## Запуск

### 1) Скачайте проект
### 2) Запустите докер

Есть два docker-compose
один в корне - запустит auth, core db, второй в dialog запустит - dialog, citus 

start
```bash
docker compose up --build -d
```
stop
```bash
docker compose down -v
```
В папке postman лежит коллекция с запросами (лучше выполнять запросы по ней потому что они немного отличаются от стандартной коллекции)