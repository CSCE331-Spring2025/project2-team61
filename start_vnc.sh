#!/bin/bash

# Install necessary packages
sudo apt-get update
sudo apt-get install -y xvfb x11vnc fluxbox novnc websockify

# Kill any existing instances
pkill -f Xvfb
pkill -f x11vnc
pkill -f websockify

# Find an available display number (starting from 99)
DISPLAY_NUM=99
while [[ -e /tmp/.X$DISPLAY_NUM-lock ]]; do
    ((DISPLAY_NUM++))
done

echo "Using display :$DISPLAY_NUM"

# Start Xvfb (Virtual Display)
Xvfb :$DISPLAY_NUM -screen 0 1024x768x24 -ac -nolisten tcp &
sleep 2

# Export DISPLAY variable
export DISPLAY=:$DISPLAY_NUM

# Start a minimal window manager
fluxbox &

# Find an available VNC port (starting from 5901)
VNC_PORT=5901
while ss -tuln | grep -q ":$VNC_PORT "; do
    ((VNC_PORT++))
done

echo "Using VNC port $VNC_PORT"

# Start x11vnc server
x11vnc -display :$DISPLAY_NUM -rfbport $VNC_PORT -passwd mypassword -forever &
sleep 2

# Find an available WebSocket port (starting from 8080)
WEB_PORT=8080
while ss -tuln | grep -q ":$WEB_PORT "; do
    ((WEB_PORT++))
done

echo "Using WebSocket port $WEB_PORT"

# Start noVNC (Web-based VNC)
websockify --web=/usr/share/novnc $WEB_PORT localhost:$VNC_PORT &

# Print connection details
echo "----------------------------------"
echo "GUI Ready... open in browser:"
echo "Google Cloud Shell Web Preview → Port $WEB_PORT"
echo "----------------------------------"

# Change to the project directory
cd ~/CSCE331/project2-team61 || exit

# Run 'make gui' inside the virtual display
echo "Running 'make gui'..."
make gui

echo "Application should now be running in the VNC session."
