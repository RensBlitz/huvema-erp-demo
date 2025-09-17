# Render Deployment Guide for Huvsma ERP MCP Server

## Prerequisites
- GitHub repository with your code
- Render.com account

## Deployment Steps

### 1. Push to GitHub
```bash
git add .
git commit -m "Prepare for Render deployment"
git push origin main
```

### 2. Create Render Web Service
1. Go to [Render Dashboard](https://dashboard.render.com)
2. Click "New +" → "Web Service"
3. Connect your GitHub repository
4. Configure the service:
   - **Name**: `huvsma-erp-mcp`
   - **Environment**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/huvsma-erp-1.0.0.jar`
   - **Port**: Leave empty (auto-detected)

### 3. Environment Variables
No additional environment variables needed for basic functionality.

### 4. Deploy
Click "Create Web Service" to start deployment.

## Testing MCP Server

### 1. Get Your Render URL
After deployment, you'll get a URL like: `https://huvsma-erp-mcp.onrender.com`

### 2. Test SSE Connection
```bash
curl -N "https://your-app-url.onrender.com/mcp/sse?sessionId=test-session"
```

### 3. Test MCP Tools List
```bash
curl -X POST "https://your-app-url.onrender.com/mcp/messages" \
  -H "Content-Type: application/json" \
  -H "X-MCP-Session: test-session" \
  -d '{
    "jsonrpc": "2.0",
    "id": "1",
    "method": "tools/list"
  }'
```

### 4. Test MCP Tool Call
```bash
curl -X POST "https://your-app-url.onrender.com/mcp/messages" \
  -H "Content-Type: application/json" \
  -H "X-MCP-Session: test-session" \
  -d '{
    "jsonrpc": "2.0",
    "id": "2",
    "method": "tools/call",
    "params": {
      "name": "huvsma.products.search",
      "arguments": {
        "naam": "draai",
        "page": 0,
        "size": 5
      }
    }
  }'
```

## Available MCP Tools
- `huvsma.products.search` - Search products with filtering
- `huvsma.orders.getById` - Get order by ID
- `huvsma.inventory.adjust` - Adjust inventory stock
- `huvsma.invoices.setStatus` - Set invoice status

## ChatGPT Integration
Once deployed, you can use this MCP server with ChatGPT by:
1. Adding the Render URL as an MCP server endpoint
2. Using the `/mcp/sse` endpoint for real-time communication
3. Sending JSON-RPC messages to `/mcp/messages`

## Troubleshooting
- Check Render logs if deployment fails
- Ensure all dependencies are in `pom.xml`
- Verify the application starts locally with `mvn spring-boot:run`
