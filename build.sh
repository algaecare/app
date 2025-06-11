#!/bin/bash

INSTALL_DIR="/home/algaecare/app/dist"

echo "Installing application to persistent directory..."

# Build script for Algae Care Application
LOG_FILE="build.log"

echo "$(date): Starting build process..." | tee -a "$LOG_FILE"

# Set Maven options for better performance
export MAVEN_OPTS="-Xmx2g -XX:+UseG1GC"

# Clean any previous builds
echo "$(date): Cleaning previous builds..." | tee -a "$LOG_FILE"
mvn clean 2>&1 | tee -a "$LOG_FILE"

if [ ${PIPESTATUS[0]} -ne 0 ]; then
    echo "$(date): Clean failed. Check $LOG_FILE for details." | tee -a "$LOG_FILE"
    exit 1
fi

# Build with release profile
echo "$(date): Building application with release profile..." | tee -a "$LOG_FILE"
mvn package -P release -DskipTests 2>&1 | tee -a "$LOG_FILE"

if [ ${PIPESTATUS[0]} -ne 0 ]; then
    echo "$(date): Build failed. Check $LOG_FILE for details." | tee -a "$LOG_FILE"
    exit 1
fi

# Check for both regular JAR and shaded JAR
REGULAR_JAR="target/algaecare.jar"
SHADED_JAR="target/algaecare-shaded.jar"

if [ -f "$SHADED_JAR" ]; then
    echo "$(date): Build successful! Shaded JAR created: $SHADED_JAR" | tee -a "$LOG_FILE"
    MAIN_JAR="$SHADED_JAR"
    JAR_SIZE=$(du -h "$SHADED_JAR" | cut -f1)
    echo "$(date): Shaded JAR size: $JAR_SIZE (includes all dependencies)" | tee -a "$LOG_FILE"
    echo ""
    echo "Build completed successfully!"
    echo "To run locally: java -jar $SHADED_JAR"
    echo "Note: Shaded JAR includes all dependencies - no need for --module-path"
elif [ -f "$REGULAR_JAR" ]; then
    echo "$(date): Build successful! Regular JAR created: $REGULAR_JAR" | tee -a "$LOG_FILE"
    echo "$(date): Dependencies copied to: target/libs/" | tee -a "$LOG_FILE"
    MAIN_JAR="$REGULAR_JAR"
    
    # Show JAR size and dependency count
    JAR_SIZE=$(du -h "$REGULAR_JAR" | cut -f1)
    DEP_COUNT=$(ls target/libs/ 2>/dev/null | wc -l)
    echo "$(date): JAR size: $JAR_SIZE, Dependencies: $DEP_COUNT" | tee -a "$LOG_FILE"
    
    echo ""
    echo "Build completed successfully!"
    echo "To run locally: java --module-path target/libs --add-modules javafx.controls,javafx.media -jar $REGULAR_JAR"
else
    echo "$(date): Build failed - No JAR found!" | tee -a "$LOG_FILE"
    exit 1
fi

# Check if resources are included in the JAR
echo "$(date): Checking for resources in JAR..." | tee -a "$LOG_FILE"
RESOURCE_COUNT=$(jar -tf "$MAIN_JAR" | grep -c "actions/" 2>/dev/null || echo "0")
if [ "$RESOURCE_COUNT" -gt 0 ]; then
    echo "$(date): Found $RESOURCE_COUNT action resource files in JAR" | tee -a "$LOG_FILE"
else
    echo "$(date): WARNING: No action resources found in JAR!" | tee -a "$LOG_FILE"
    echo "$(date): Make sure resources are in src/main/resources/ before building" | tee -a "$LOG_FILE"
fi

echo "$(date): Build script finished." | tee -a "$LOG_FILE"

if [ $? -ne 0 ]; then
    echo "Build failed. Cannot install."
    exit 1
fi

# Create install directory
mkdir -p "$INSTALL_DIR"

# Copy JAR files
if [ -f "target/algaecare-shaded.jar" ]; then
    cp "target/algaecare-shaded.jar" "$INSTALL_DIR/"
    echo "Shaded JAR installed to $INSTALL_DIR/"
fi

if [ -f "target/algaecare.jar" ]; then
    cp "target/algaecare.jar" "$INSTALL_DIR/"
    echo "Regular JAR installed to $INSTALL_DIR/"
fi

# Copy dependencies
if [ -d "target/libs" ]; then
    cp -r "target/libs" "$INSTALL_DIR/"
    echo "Dependencies installed to $INSTALL_DIR/libs/"
fi

echo "Installation complete!"
echo "JAR files are now persistent in: $INSTALL_DIR"