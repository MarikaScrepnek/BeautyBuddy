# Use Node LTS
FROM node:22-slim

WORKDIR /app

COPY package*.json ./
RUN npm install --legacy-peer-deps

COPY . .

# Build the frontend
ARG VITE_API_BASE_URL
ENV VITE_API_BASE_URL=${VITE_API_BASE_URL}

RUN npm run build

# Serve static files
RUN npm install -g serve
CMD ["serve", "-s", "dist", "-l", "5173"]