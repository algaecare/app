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

# Start pcsc_scan in background and redirect output to temp file
temp_file=$(mktemp)
/usr/bin/pcsc_scan > "$temp_file" &
pcsc_pid=$!

# Main script
show_message "~ BITTE STECKE DAS WEISSE USB KABEL HINTER DEM KASTEN AUS UND WIEDER EIN ~"
echo -e "\nWaiting for 20 seconds before showing the scan results..."
echo -e "You can press Ctrl+C to cancel if needed.\n"

# Countdown
countdown 20

# Clear screen and show the output
clear
tail -f "$temp_file"

# Cleanup on script exit
trap "kill $pcsc_pid; rm $temp_file" EXIT