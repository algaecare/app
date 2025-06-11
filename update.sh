#!/bin/bash

# Update script for Algae Care Application
LOG_FILE="update.log"
APP_DIR="/home/algaecare/app"

echo "$(date): Starting update process..." | tee -a "$LOG_FILE"

# Stop the application service if running
if systemctl is-active --quiet algaecare.service; then
    echo "$(date): Stopping algaecare service..." | tee -a "$LOG_FILE"
    sudo systemctl stop algaecare.service
fi

# Pull the latest version from GitHub
echo "$(date): Pulling latest version from GitHub..." | tee -a "$LOG_FILE"
git pull origin main 2>&1 | tee -a "$LOG_FILE"

if [ ${PIPESTATUS[0]} -ne 0 ]; then
    echo "$(date): Git pull failed. Check $LOG_FILE for details." | tee -a "$LOG_FILE"
    exit 1
fi

# Make scripts executable
chmod +x *.sh

# Run the build
echo "$(date): Building application..." | tee -a "$LOG_FILE"
./build.sh 2>&1 | tee -a "$LOG_FILE"

if [ ${PIPESTATUS[0]} -ne 0 ]; then
    echo "$(date): Build failed. Check $LOG_FILE for details." | tee -a "$LOG_FILE"
    exit 1
fi

# Restart the service if it was running
if systemctl is-enabled --quiet algaecare.service; then
    echo "$(date): Restarting algaecare service..." | tee -a "$LOG_FILE"
    sudo systemctl start algaecare.service
    
    # Check if service started successfully
    sleep 3
    if systemctl is-active --quiet algaecare.service; then
        echo "$(date): Service restarted successfully" | tee -a "$LOG_FILE"
    else
        echo "$(date): Service failed to start. Check: systemctl status algaecare.service" | tee -a "$LOG_FILE"
    fi
fi

echo "$(date): Update completed successfully!" | tee -a "$LOG_FILE"
echo "Check logs: $APP_DIR/$LOG_FILE"