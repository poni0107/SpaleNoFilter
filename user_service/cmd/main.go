package main

import (
	"database/sql"
	"fmt"
	"log"
	"net/http"
	"os"

	"user_service/handler"
	"user_service/middleware"
	"user_service/repository"
	"user_service/service"

	_ "github.com/go-sql-driver/mysql"
	"github.com/joho/godotenv"
)

func main() {
	if err := godotenv.Load(); err != nil {
		log.Fatal("Error loading .env file")
	}

	dsn := MySQLDSN()
	log.Println("Connecting to MySQL...")
	db, err := sql.Open("mysql", dsn)
	if err != nil {
		log.Fatal(err)
	}

	if err := db.Ping(); err != nil {
		log.Fatal("MySQL not reachable:", err)
	}

	repo := repository.NewUserRepository(db)
	svc := service.NewUserService(repo)
	ctrl := handler.NewUserController(svc)

	mux := http.NewServeMux()
	mux.Handle("/data",
		middleware.JWTMiddleware(
			http.HandlerFunc(middleware.ProtectedHandler),
			[]byte(os.Getenv("JWT_SECRET")),
		),
	)

	mux.Handle("/users/get", middleware.JWTMiddleware(
		http.HandlerFunc(ctrl.GetUser),
		[]byte(os.Getenv("JWT_SECRET")),
	),
	)
	mux.HandleFunc("/users", ctrl.CreateUser)
	mux.HandleFunc("/users/delete", ctrl.DeleteUser)
	mux.HandleFunc("/users/exists", ctrl.Exists)
	mux.HandleFunc("/users/auth", ctrl.GetUserByCredentials)

	port := os.Getenv("APP_PORT")
	if port == "" {
		log.Println("User service running can't read port from env \n exiting...")
		return
	}

	log.Println("User service running on :" + port)
	log.Fatal(http.ListenAndServe(":"+port, mux))
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
	db := os.Getenv("MYSQL_USER_SERVICE_DATABASE")

	return fmt.Sprintf("%s:%s@tcp(%s:%s)/%s?parseTime=true", user, pass, host, port, db)
}
