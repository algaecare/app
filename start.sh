#!/bin/bash

# Source SDKMAN initialization to ensure Java is available
source /home/algaecare/.sdkman/bin/sdkman-init.sh

# Set environment variables for graphical applications (if needed)
export DISPLAY=:0
export XAUTHORITY=/home/algaecare/.Xauthority

# Navigate to the application directory
cd /home/algaecare/app/ || { echo "Failed to change directory to /home/algaecare/app/"; exit 1; }

# Use persistent directory
INSTALL_DIR="/home/algaecare/app/dist"
JAR_FILE="$INSTALL_DIR/algaecare.jar"
SHADED_JAR="$INSTALL_DIR/algaecare-shaded.jar"
LIBS_DIR="$INSTALL_DIR/libs"

# Check if installation exists
if [ ! -f "$JAR_FILE" ] && [ ! -f "$SHADED_JAR" ]; then
    echo "Application not installed. Running installation..."
    ./install.sh
fi

# Start application from persistent location
if [ -f "$SHADED_JAR" ]; then
    echo "Starting application with shaded JAR..."
    java -Xmx1g -XX:+UseG1GC -Dsun.java2d.opengl=True -jar "$SHADED_JAR"
elif [ -f "$JAR_FILE" ] && [ -d "$LIBS_DIR" ]; then
    echo "Starting application with regular JAR..."
    java --module-path "$LIBS_DIR" \
         --add-modules javafx.controls,javafx.media,javafx.base,javafx.graphics \
         --add-exports javafx.base/com.sun.javafx=ALL-UNNAMED \
         --add-exports javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED \
         -Xmx1g -XX:+UseG1GC -Dsun.java2d.opengl=True \
         -jar "$JAR_FILE"
else
    echo "Application not properly installed. Run: ./install.sh"
    exit 1
fi

echo "Application script finished."