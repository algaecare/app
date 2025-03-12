#!/bin/bash
cd "$1"
pkill java

DISPLAY=:0 XAUTHORITY=/home/pi/.Xauthority java \
  --module-path "$HOME/openjfx/sdk/lib" \
  --add-modules javafx.controls,javafx.fxml \
  -Djava.library.path="$HOME/openjfx/sdk/lib" \
  -Dprism.order=sw -Dprism.verbose=true -Dglass.platform=gtk \
  -Dsun.java2d.opengl=True \
  -XX:+UseZGC -Xmx1G -jar "$2".jar

exit 0