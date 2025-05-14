#!/bin/bash
# Check if pcscd is running
if ! systemctl is-active --quiet pcscd; then
    echo "PCSC daemon not running. Starting..."
    sudo systemctl start pcscd
fi

# Check if NFC reader is detected
if ! pcsc_scan -n | grep -q "Reader"; then
    echo "No NFC reader detected. Please check connection"
    exit 1
fi

echo "NFC system OK"