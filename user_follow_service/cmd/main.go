package main

import (
	"database/sql"
	"fmt"
	"log"
	"net/http"
	"os"
	"time"
	"user_follow_service/client"
	"user_follow_service/handler"
	"user_follow_service/repository"
	"user_follow_service/service"

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
		"http://localhost:8081", // users service
		httpClient,
	)

	repo := repository.NewFollowRepository(db)
	svc := service.NewFollowService(repo, userClient)
	ctrl := handler.NewFollowController(svc)

	http.HandleFunc("/follow", ctrl.Follow)
	http.HandleFunc("/unfollow", ctrl.Unfollow)
	http.HandleFunc("/get-followers", ctrl.GetFollowers)
	http.HandleFunc("/get-following", ctrl.GetFollowing)

	log.Println("Follow service running on :8080")
	log.Fatal(http.ListenAndServe(":8080", nil))
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
