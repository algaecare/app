#!/bin/bash

# Exit on any error
set -e

echo "Starting Algae Care setup on Raspberry Pi..."

# Update system
echo "Updating system packages..."
sudo apt update && sudo apt upgrade -y

# Install required packages
echo "Installing required packages..."
sudo apt install -y \
    temurin-21-jdk \
    maven \
    git \
    xorg \
    unclutter \
    chromium-browser

# Set up environment
echo "Setting up environment..."
cat >> ~/.bashrc << EOL

# Create a directory for the application
mkdir -p ~/algae-care
cd ~/algae-care

# wait for user input to continue

# Algae Care Environment
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-armhf
export MAVEN_OPTS="-Xmx2g -XX:+UseG1GC"
EOL

# Source the updated bashrc
source ~/.bashrc

# Build the application
echo "Building application..."
mvn clean package -P release -DskipTests

# Create systemd service
echo "Creating systemd service..."
sudo tee /etc/systemd/system/algae-care.service << EOL
[Unit]
Description=Algae Care Application
After=graphical.target

[Service]
Type=simple
User=$USER
Environment="DISPLAY=:0"
Environment="JAVA_HOME=/usr/lib/jvm/java-21-openjdk-armhf"
Environment="MAVEN_OPTS=-Xmx2g -XX:+UseG1GC"
WorkingDirectory=/home/$USER/algae-care
ExecStart=/usr/bin/mvn verify -P run-local -DskipTests
Restart=always

[Install]
WantedBy=graphical.target
EOL

# Enable and start service
echo "Enabling and starting service..."
sudo systemctl enable algae-care
sudo systemctl start algae-care

# Set up auto-login and kiosk mode
echo "Setting up auto-login..."
sudo raspi-config nonint do_boot_behaviour B4

# Create autostart configuration
mkdir -p ~/.config/autostart
cat > ~/.config/autostart/algae-care.desktop << EOL
[Desktop Entry]
Type=Application
Name=Algae Care
Exec=/usr/bin/mvn verify -P run-local -DskipTests
Path=/home/$USER/algae-care
EOL

# Hide cursor and prevent screen blanking
cat > ~/.config/autostart/unclutter.desktop << EOL
[Desktop Entry]
Type=Application
Name=Unclutter
Exec=unclutter -idle 0
EOL

echo "Installation complete!"
echo "Please reboot the Raspberry Pi to start the application."
echo "To reboot now, type: sudo reboot"