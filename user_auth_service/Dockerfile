FROM golang:1.24-alpine AS builder

WORKDIR /app

RUN apk add --no-cache git

# Copy go mod first for caching
COPY go.mod go.sum ./
RUN go mod download

# Copy the rest
COPY . .
COPY cmd/.env /app/.env

# Build explicitly from cmd/main.go
RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 \
    go build -o app ./cmd/main.go

# ---- Final Image ----
FROM alpine:latest

WORKDIR /app

RUN apk add --no-cache ca-certificates

COPY --from=builder /app/app .

EXPOSE 8500

CMD ["./app"]
