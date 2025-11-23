#!/bin/bash

cd "$(dirname "$0")"

echo "=== Weather Client ==="
echo ""

# Build if needed
if [ ! -f "client/target/weather-client-1.0.0.jar" ]; then
    echo "Building client..."
    cd client
    mvn clean package -q
    cd ..
fi

# Check DISPLAY
if [ -z "$DISPLAY" ]; then
    export DISPLAY=:0.0
fi

# Start client
echo "Starting client..."
cd client
java -jar target/weather-client-1.0.0.jar

