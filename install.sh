#!/bin/bash

INSTALL_DIR="/home/algaecare/app/dist"

echo "Installing application to persistent directory..."

# Build first
./build.sh

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