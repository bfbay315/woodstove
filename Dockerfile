# Stage 1: Build frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend

# Build-time argument for Google OAuth client ID
ARG VITE_GOOGLE_CLIENT_ID
ENV VITE_GOOGLE_CLIENT_ID=$VITE_GOOGLE_CLIENT_ID

# Install dependencies
COPY frontend/package*.json ./
RUN npm ci

# Copy frontend source and build
COPY frontend/ ./

# Install adapter-static for static site generation
RUN npm install -D @sveltejs/adapter-static

# Create static folder if missing
RUN mkdir -p static

# Override svelte.config.js to use static adapter
RUN printf '%s\n' \
  "import adapter from '@sveltejs/adapter-static';" \
  "import { vitePreprocess } from '@sveltejs/vite-plugin-svelte';" \
  "" \
  "const config = {" \
  "  preprocess: vitePreprocess()," \
  "  kit: {" \
  "    adapter: adapter({" \
  "      pages: 'build'," \
  "      assets: 'build'," \
  "      fallback: 'index.html'," \
  "      precompress: false," \
  "      strict: false" \
  "    })," \
  "    prerender: {" \
  "      handleHttpError: 'warn'" \
  "    }" \
  "  }" \
  "};" \
  "" \
  "export default config;" > svelte.config.js

# Create layout with prerender config for SPA mode
RUN printf '%s\n' \
  "export const prerender = true;" \
  "export const ssr = false;" > src/routes/+layout.ts

# Build the frontend
RUN npm run build


# Stage 2: Build backend
FROM eclipse-temurin:17-jdk-alpine AS backend-build
WORKDIR /app/backend

# Copy gradle wrapper and build files
COPY backend/gradle gradle
COPY backend/gradlew backend/build.gradle backend/settings.gradle ./

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY backend/src src

# Copy frontend build output to static resources
COPY --from=frontend-build /app/frontend/build/ src/main/resources/static/

# Build the JAR
RUN ./gradlew bootJar --no-daemon


# Stage 3: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built JAR
COPY --from=backend-build /app/backend/build/libs/*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

# Health check using the frontend index page (no auth required)
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
