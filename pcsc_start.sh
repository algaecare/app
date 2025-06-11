#!/bin/bash

# Function to clear screen and show message
show_message() {
    clear
    echo -e "\n\n"
    echo "=============================================="
    echo ""
    echo "$1"
    echo ""
    echo "=============================================="
}

# Function for countdown
countdown() {
    local seconds=$1
    while [ $seconds -gt 0 ]; do
        echo -ne "\rStarting scan in: $seconds seconds..."
        sleep 1
        ((seconds--))
    done
    echo -e "\rStarting scan now..."
}

# Main script
show_message "~ BITTE STECKE DAS WEISSE USB KABEL HINTER DEM KASTEN AUS UND WIEDER EIN ~"
echo -e "\nWaiting for 10 seconds before starting the scan..."
echo -e "You can press Ctrl+C to cancel if needed.\n"

# Countdown
countdown 10

# Clear screen before starting pcsc_scan
clear

# start the start.sh script in the background
exec /home/algaecare/app/start.sh &

# Start the actual pcsc_scan in the same terminal
exec /usr/bin/pcsc_scan