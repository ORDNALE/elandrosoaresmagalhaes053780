FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./

RUN npm ci


COPY . .

# 🔥 BUILD (Testes pulados para agilidade)
RUN npm run build -- --configuration production

# =======================
# Runtime (nginx)
# =======================
FROM nginx:alpine

COPY ./nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/elandro-music/browser /usr/share/nginx/html

EXPOSE 4201
