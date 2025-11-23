#!/bin/bash

cd "$(dirname "$0")"

echo "=== Weather Server ==="
echo ""

# Load .env file from project root (priority)
if [ -f ".env" ]; then
    echo "📝 Loading API key from .env file (root)..."
    # Load only OPENWEATHER_API_KEY, ignore other vars and comments
    while IFS='=' read -r key value; do
        # Skip comments and empty lines
        [[ "$key" =~ ^#.*$ ]] && continue
        [[ -z "$key" ]] && continue
        # Remove quotes from value
        value=$(echo "$value" | sed -e 's/^["'\'']//' -e 's/["'\'']$//')
        # Only export OPENWEATHER_API_KEY
        if [ "$key" = "OPENWEATHER_API_KEY" ]; then
            export OPENWEATHER_API_KEY="$value"
            # Check if it's a placeholder
            if [ "$value" = "your-api-key" ] || [ "$value" = "your-api-key-here" ] || [ -z "$value" ]; then
                echo "⚠️  WARNING: .env contains placeholder value!"
                echo "   Please update .env with your actual API key"
                unset OPENWEATHER_API_KEY
            fi
        fi
    done < .env
elif [ -f "client/.env" ]; then
    echo "📝 Loading API key from client/.env file..."
    while IFS='=' read -r key value; do
        [[ "$key" =~ ^#.*$ ]] && continue
        [[ -z "$key" ]] && continue
        value=$(echo "$value" | sed -e 's/^["'\'']//' -e 's/["'\'']$//')
        if [ "$key" = "OPENWEATHER_API_KEY" ]; then
            export OPENWEATHER_API_KEY="$value"
            if [ "$value" = "your-api-key" ] || [ "$value" = "your-api-key-here" ] || [ -z "$value" ]; then
                echo "⚠️  WARNING: .env contains placeholder value!"
                unset OPENWEATHER_API_KEY
            fi
        fi
    done < client/.env
fi

# Kill old processes
pkill -f "weather-server" 2>/dev/null
if [ -f server.pid ]; then
    kill $(cat server.pid) 2>/dev/null
    rm -f server.pid
fi

sleep 1

# Build if needed
if [ ! -f "server/target/weather-server-1.0.0.jar" ]; then
    echo "Building server..."
    cd server
    mvn clean package -q
    cd ..
fi

# Check API key
if [ -z "$OPENWEATHER_API_KEY" ]; then
    echo "⚠️  WARNING: OPENWEATHER_API_KEY not set!"
    echo "   Create .env file with: OPENWEATHER_API_KEY=your-key"
    echo ""
else
    # Mask API key for display
    MASKED_KEY="${OPENWEATHER_API_KEY:0:4}...${OPENWEATHER_API_KEY: -4}"
    echo "✅ API Key loaded: $MASKED_KEY"
    echo ""
fi

# Start server
echo "Starting server on port 8888..."
echo "Server sẽ chạy trong terminal này."
echo "Để dừng server, nhấn Ctrl+C"
echo ""
echo "----------------------------------------"
echo ""

cd server
java -jar target/weather-server-1.0.0.jar
