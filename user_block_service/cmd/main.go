package main

import (
	"database/sql"
	"fmt"
	"log"
	"net/http"
	"os"
	"time"
	"user_block_service/client"
	"user_block_service/handler"
	"user_block_service/repository"
	"user_block_service/service"

	_ "github.com/go-sql-driver/mysql"
	"github.com/joho/godotenv"
)

func main() {
	if err := godotenv.Load(); err != nil {
		log.Fatal("Error loading .env file")
	}
	fmt.Print(MySQLDSN())
	db, err := sql.Open("mysql", MySQLDSN())
	if err != nil {
		log.Fatal(err)
	}

	httpClient := &http.Client{
		Timeout: 3 * time.Second,
	}

	userClient := client.NewUserClient(
		"http://localhost:8081",
		httpClient,
	)

	repo := repository.NewBlockedRepository(db)
	svc := service.NewBlockedService(repo, userClient)
	ctrl := handler.NewBlockedController(svc)

	http.HandleFunc("/block", ctrl.Block)
	http.HandleFunc("/unblock", ctrl.Unblock)
	http.HandleFunc("/blocked", ctrl.GetBlocked)

	log.Println("Blocked service running on :9001")
	log.Fatal(http.ListenAndServe(":9001", nil))
}

func MySQLDSN() string {
	host := os.Getenv("MYSQL_HOST")
	if host == "" {
		host = "127.0.0.1"
	}

	port := os.Getenv("MYSQL_PORT")
	if port == "" {
		port = "3306"
	}

	user := os.Getenv("MYSQL_USER")
	pass := os.Getenv("MYSQL_PASSWORD")
	db := os.Getenv("MYSQL_USER_FOLLOWING_SERVICE_DATABASE")

	return fmt.Sprintf("%s:%s@tcp(%s:%s)/%s?parseTime=true", user, pass, host, port, db)
}
