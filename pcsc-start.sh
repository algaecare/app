#!/bin/bash

# Function to clear screen and show message
show_message() {
    clear
    echo -e "\n\n"
    echo "=============================================="
    echo "$1"
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
    echo -e "\rStarting scan now...                    "
}

# Main script
show_message "Please connect your PCSC Scanner now!"
echo -e "\nWaiting for 15 seconds before starting the scan..."
echo -e "You can press Ctrl+C to cancel if needed.\n"

# Countdown
countdown 15

# Clear screen before starting pcsc_scan
clear

# Start the actual pcsc_scan in the same terminal
exec /usr/bin/pcsc_scan