#!/bin/bash

# Source SDKMAN initialization to ensure Java is available
source /home/algaecare/.sdkman/bin/sdkman-init.sh

# Set environment variables for graphical applications (if needed)
export DISPLAY=:0
export XAUTHORITY=/home/algaecare/.Xauthority

# Navigate to the application directory
cd /home/algaecare/app/ || { echo "Failed to change directory to /home/algaecare/app/"; exit 1; }

# --- Build and Run Section ---

# Check if the JAR file exists
JAR_FILE="target/algaecare.jar"
LIBS_DIR="target/libs"

if [ ! -f "$JAR_FILE" ]; then
    echo "JAR file not found: $JAR_FILE"
    echo "Please run the build script first: ./build.sh"
    exit 1
fi

if [ ! -d "$LIBS_DIR" ]; then
    echo "Dependencies directory not found: $LIBS_DIR"
    echo "Please run the build script first: ./build.sh"
    exit 1
fi

# Check if resources are packaged in JAR
echo "Checking if resources are packaged in JAR..."
if ! jar -tf "$JAR_FILE" | grep -q "actions/bike/"; then
    echo "WARNING: Resources not found in JAR. Please ensure:"
    echo "1. Resources are in src/main/resources/"
    echo "2. Run ./build.sh to rebuild with resources"
fi

# Start the application with JavaFX modules
echo "Starting the application with JavaFX runtime..."
java --module-path "$LIBS_DIR" \
     --add-modules javafx.controls,javafx.media,javafx.base,javafx.graphics \
     --add-exports javafx.base/com.sun.javafx=ALL-UNNAMED \
     --add-exports javafx.graphics/com.sun.javafx.tk=ALL-UNNAMED \
     -Xmx1g -XX:+UseG1GC \
     -Dsun.java2d.opengl=True \
     -jar "$JAR_FILE"

echo "Application script finished."