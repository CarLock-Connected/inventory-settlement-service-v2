#!/bin/sh
DELAY=$(( RANDOM % 10 ))
sleep "$DELAY"
FAIL=$(( RANDOM % 20 ))
if [ "$FAIL" -eq 0 ]; then
  BODY='{"error":"service failure"}'
  printf "HTTP/1.1 500 Internal Server Error\r\nContent-Length: %d\r\nConnection: close\r\n\r\n%s" ${#BODY} "$BODY"
else
  BODY='{"taxRate": 0.0825}'
  printf "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: %d\r\nConnection: close\r\n\r\n%s" ${#BODY} "$BODY"
fi
