# deps: isolated so `npm ci` is only re-run when the lockfile actually changes.
FROM node:22-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci

# builder: compiles the standalone Next.js server output.
FROM node:22-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

# runner: just the standalone server output + static assets, no source or node_modules.
FROM node:22-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
COPY --from=builder /app/public ./public
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
EXPOSE 3000
ENV PORT=3000
# Docker auto-injects HOSTNAME=<container id>; Next's standalone server binds to that
# literal value instead of all interfaces, and this app has no route handlers building
# absolute URLs from request.url, so overriding it to 0.0.0.0 has no other side effect here.
ENV HOSTNAME=0.0.0.0
CMD ["node", "server.js"]
