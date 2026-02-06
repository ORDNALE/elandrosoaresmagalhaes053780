FROM node:20-alpine AS build

WORKDIR /app

# Forçar invalidação de cache a cada build se necessário (opcional, mas útil para debug)
# ARG CACHEBUST=1

COPY package*.json ./

# Limpar cache do npm antes de instalar para garantir frescor
RUN npm cache clean --force && npm ci

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
