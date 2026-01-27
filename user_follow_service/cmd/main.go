package main

import (
	"database/sql"
	"log"
	"net/http"
	"os"
	"user_follow_service/config"
	"user_follow_service/controller"
	"user_follow_service/repository"
	"user_follow_service/routes"

	"github.com/joho/godotenv"

	_ "github.com/go-sql-driver/mysql"
)

func main() {
	err := godotenv.Load()
	if err != nil {
		log.Println("No .env file found, using environment variables")
	}
	if err := godotenv.Load(); err != nil {
		log.Println("No .env file found, using env variables")
	}

	port := os.Getenv("APP_PORT")
	if port == "" {
		port = "8080"
	}

	// DB
	db, err := sql.Open("mysql", config.MySQLDSN())
	if err != nil {
		log.Fatal(err)
	}

	if err := db.Ping(); err != nil {
		log.Fatal("DB connection failed:", err)
	}

	repo := &repository.FollowRepository{DB: db}
	ctrl := &controller.FollowController{Repo: repo}

	routes.Register(ctrl)

	log.Println("API running on :" + port)
	log.Fatal(http.ListenAndServe(":"+port, nil))
}
