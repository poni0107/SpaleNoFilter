package view

import (
	"encoding/json"
	"net/http"
)

func Success(w http.ResponseWriter, msg string) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(map[string]string{
		"status":  "ok",
		"message": msg,
	})
}

func Error(w http.ResponseWriter, msg string, status string, code int) {
	w.WriteHeader(code)
	json.NewEncoder(w).Encode(map[string]string{
		"status":  status,
		"message": msg,
	})
}
