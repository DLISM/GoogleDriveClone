#!/bin/bash
app_name="google-drive-clone"
app_url="http://localhost:8080"
status_code=$(curl -s -o /dev/null -w "%{http_code}" $app_url)
now=$(date +"%Y-%m-%d %H:%M:%S")
if [ $status_code -ne 200 ]; then
echo "$now - The application is down, restarting..."
  systemctl restart $app_name
fi
