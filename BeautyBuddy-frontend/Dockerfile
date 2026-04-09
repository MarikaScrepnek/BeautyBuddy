# ---- Stage 1: Build ----
FROM node:22-slim AS build
WORKDIR /app

COPY . .
RUN npm install --legacy-peer-deps
RUN npm run build

# ---- Stage 2: Serve ----
FROM node:22-slim
WORKDIR /app

# Install only the server
RUN npm install -g serve

# Copy only the built dist folder
COPY --from=build /app/dist ./dist

# Expose port
EXPOSE 5173

# Start server
CMD ["serve", "-s", "dist", "-l", "5173"]