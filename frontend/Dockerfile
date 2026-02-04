FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm ci


COPY . .

# 🔥 TESTES UNITÁRIOS (Karma + Puppeteer)
RUN npm run test -- --watch=false

# 🔥 BUILD só se os testes passarem
RUN npm run build -- --configuration production

# =======================
# Runtime (nginx)
# =======================
FROM nginx:alpine

COPY ./nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/elandro-music/browser /usr/share/nginx/html

EXPOSE 4201
