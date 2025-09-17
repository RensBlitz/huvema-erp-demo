# Huvsma ERP System

Een complete ERP (Enterprise Resource Planning) applicatie gebouwd met Spring Boot, inclusief REST API, Swagger/OpenAPI documentatie en MCP (Model Context Protocol) server functionaliteit.

## Overzicht

Dit systeem biedt functionaliteit voor:
- **Productbeheer**: Producten, categorieën, voorraad, leveranciers
- **Klantbeheer**: Klantgegevens, factuuradressen
- **Orderbeheer**: Orders, status tracking, voorraadbewegingen
- **Factuurbeheer**: Facturen, betalingsstatus
- **Voorraadbeheer**: Voorraadbewegingen, stock tracking

## Technische Stack

- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Web MVC**
- **Spring Validation**
- **SpringDoc OpenAPI 3** (Swagger UI)
- **Maven**

## Starten van de Applicatie

### Via Maven
```bash
mvn spring-boot:run
```

### Via IntelliJ IDEA
1. Open het project in IntelliJ IDEA
2. Reimport Maven dependencies
3. Run de `HuvsmaErpApplication` main class

### Via JAR
```bash
mvn clean package
java -jar target/huvsma-erp-1.0.0.jar
```

De applicatie start op `http://localhost:8080`

## API Documentatie

### Swagger UI
- **URL**: `http://localhost:8080/swagger-ui.html`
- Toont alle REST endpoints met interactieve documentatie
- Mogelijkheid om API calls direct te testen

### OpenAPI JSON
- **URL**: `http://localhost:8080/api-docs`

## API Endpoints

### Utility Endpoints
- `GET /health` - Health check
- `GET /version` - Applicatie versie informatie
- `POST /_admin/reset` - Reset alle data naar seed state

### Product Endpoints (`/api/v1/products`)
- `GET /products` - Lijst producten (met filtering, paginatie, sortering)
- `GET /products/{id}` - Product details
- `POST /products` - Nieuw product aanmaken
- `PUT /products/{id}` - Product bijwerken
- `DELETE /products/{id}` - Product verwijderen
- `GET /products/{id}/stock` - Voorraad informatie

### Klant Endpoints (`/api/v1/customers`)
- `GET /customers` - Lijst klanten
- `GET /customers/{id}` - Klant details
- `POST /customers` - Nieuwe klant aanmaken
- `PUT /customers/{id}` - Klant bijwerken
- `DELETE /customers/{id}` - Klant verwijderen

### Leverancier Endpoints (`/api/v1/suppliers`)
- `GET /suppliers` - Lijst leveranciers
- `GET /suppliers/{id}` - Leverancier details
- `POST /suppliers` - Nieuwe leverancier aanmaken
- `PUT /suppliers/{id}` - Leverancier bijwerken
- `DELETE /suppliers/{id}` - Leverancier verwijderen

### Order Endpoints (`/api/v1/orders`)
- `GET /orders` - Lijst orders
- `GET /orders/{id}` - Order details
- `POST /orders` - Nieuwe order aanmaken
- `PUT /orders/{id}/status` - Order status bijwerken
- `POST /orders/{id}/recalculate` - Order totalen herberekenen

### Voorraadbeweging Endpoints (`/api/v1/stock-movements`)
- `GET /stock-movements` - Lijst voorraadbewegingen
- `GET /stock-movements/{id}` - Voorraadbeweging details
- `POST /stock-movements` - Nieuwe voorraadbeweging

### Factuur Endpoints (`/api/v1/invoices`)
- `GET /invoices` - Lijst facturen
- `GET /invoices/{id}` - Factuur details
- `POST /invoices` - Nieuwe factuur aanmaken
- `PUT /invoices/{id}/status` - Factuur status bijwerken

## Filtering, Paginatie en Sortering

### Query Parameters
- `page` - Pagina nummer (0-based, default: 0)
- `size` - Pagina grootte (default: 20)
- `sort` - Sorteer veld en richting (bijv. `naam,asc` of `orderDatum,desc`)

### Filter Voorbeelden
```
GET /api/v1/products?naam=draai&categorie=Machines&verkoopPrijsMin=1000&page=0&size=10&sort=naam,asc
GET /api/v1/orders?status=IN_BEHANDELING&datumVan=2024-01-01&datumTot=2024-12-31
GET /api/v1/customers?bedrijfsNaam=metaal&btwNummer=NL123456789B01
```

## MCP/SSE Server

De applicatie ondersteunt Model Context Protocol (MCP) via Server-Sent Events voor real-time communicatie.

### MCP Endpoints
- `GET /mcp/sse` - SSE verbinding opzetten
- `POST /mcp/messages` - MCP berichten versturen

### MCP/SSE Gebruiken

#### Stap 1: Open SSE Verbinding
```bash
curl -N "http://localhost:8080/mcp/sse?sessionId=my-session-123"
```

#### Stap 2: Verstuur JSON-RPC Berichten
```bash
curl -X POST "http://localhost:8080/mcp/messages" \
  -H "Content-Type: application/json" \
  -H "X-MCP-Session: my-session-123" \
  -d '{
    "jsonrpc": "2.0",
    "id": "1",
    "method": "tools/list"
  }'
```

#### Stap 3: Gebruik Sessie Parameter (alternatief)
```bash
curl -X POST "http://localhost:8080/mcp/messages?session=my-session-123" \
  -H "Content-Type: application/json" \
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

#### Stap 4: Voorbeeld tools/list Call
```json
{
  "jsonrpc": "2.0",
  "id": "3",
  "method": "tools/list"
}
```

#### Stap 5: Voorbeeld tools/call Call
```json
{
  "jsonrpc": "2.0",
  "id": "4",
  "method": "tools/call",
  "params": {
    "name": "huvsma.orders.getById",
    "arguments": {
      "id": "ORD-1001"
    }
  }
}
```

### Beschikbare MCP Tools
- `huvsma.products.search` - Producten zoeken met filtering
- `huvsma.orders.getById` - Order ophalen op ID
- `huvsma.inventory.adjust` - Voorraad aanpassen
- `huvsma.invoices.setStatus` - Factuur status wijzigen

## Data Seeding

De applicatie wordt automatisch gestart met dummy data:
- 8 producten (machines en onderdelen)
- 5 klanten
- 3 leveranciers
- 4 orders met verschillende statussen
- 6 voorraadbewegingen
- 3 facturen

### Data Resetten
```bash
curl -X POST "http://localhost:8080/_admin/reset"
```

## CORS Configuratie

CORS is geconfigureerd voor:
- `/api/v1/**` - Alle REST API endpoints
- `/mcp/**` - MCP/SSE endpoints

Alle origins zijn toegestaan voor development doeleinden.

## Validatie

Alle Request DTO's hebben Bean Validation annotaties:
- `@NotBlank` - Verplichte velden
- `@Email` - Email validatie
- `@Positive` - Positieve getallen
- `@Size` - String lengte validatie

Foutmeldingen worden in het Nederlands getoond.

## Error Handling

De applicatie heeft een globale error handler die:
- Validatiefouten netjes weergeeft
- Consistente error responses levert
- Passende HTTP status codes gebruikt

## Build en Deployment

### Maven Commands
```bash
# Clean en compile
mvn clean compile

# Run tests
mvn test

# Package applicatie
mvn clean package

# Run applicatie
mvn spring-boot:run
```

### Docker (optioneel)
```bash
# Build Docker image
docker build -t huvsma-erp .

# Run container
docker run -p 8080:8080 huvsma-erp
```

## Ontwikkeling

### Project Structuur
```
src/main/java/nl/huvema/huvsmaerp/
├── config/          # Configuratie klassen
├── controller/       # REST controllers
├── dto/             # Data Transfer Objects
├── storage/         # In-memory repositories
├── util/            # Utility klassen
└── HuvsmaErpApplication.java
```

### Nieuwe Features Toevoegen
1. Maak DTO's in `dto/` package
2. Voeg repository toe in `storage/` package
3. Implementeer controller in `controller/` package
4. Voeg MCP tool toe aan `McpController` indien nodig

## Licentie

Dit project is ontwikkeld voor demonstratie doeleinden.
