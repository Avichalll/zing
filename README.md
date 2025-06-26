# QR Code Generator API

A Spring Boot application that provides REST APIs for generating QR codes in PNG and PDF formats with optional company logo embedding.

## Features

- **PNG QR Code Generation**: Generate QR codes as PNG images with customizable dimensions
- **PDF QR Code Generation**: Generate QR codes embedded in PDF documents
- **Company Logo Embedding**: Add KCare logo to QR codes with automatic fallback to text
- **Industry-Level Code**: Proper error handling, validation, logging, and documentation
- **Size Optimization**: Automatic size validation with minimum 100x100 and maximum 2000x2000 pixels
- **High Quality**: Enhanced error correction and proper encoding settings
- **Logo Safety**: High error correction ensures QR codes remain scannable with logos

## API Endpoints

### Standard QR Codes

#### Generate PNG QR Code
```
GET /api/v1/qr/qrcode/png
```

**Parameters:**
- `text` (required): Text to encode in the QR code
- `width` (optional, default: 300): Width in pixels (100-2000)
- `height` (optional, default: 300): Height in pixels (100-2000)

**Example:**
```
GET /api/v1/qr/qrcode/png?text=https://example.com&width=400&height=400
```

#### Generate PDF QR Code
```
GET /api/v1/qr/qrcode/pdf
```

**Parameters:**
- `text` (required): Text to encode in the QR code
- `width` (optional, default: 300): Width in pixels (100-2000)
- `height` (optional, default: 300): Height in pixels (100-2000)

**Example:**
```
GET /api/v1/qr/qrcode/pdf?text=https://example.com&width=400&height=400
```

### QR Codes with Logo

#### Generate PNG QR Code with Logo
```
GET /api/v1/qr/qrcode/png/logo
```

**Parameters:**
- `text` (required): Text to encode in the QR code
- `width` (optional, default: 300): Width in pixels (100-2000)
- `height` (optional, default: 300): Height in pixels (100-2000)
- `withLogo` (optional, default: true): Whether to include the KCare logo

**Example:**
```
GET /api/v1/qr/qrcode/png/logo?text=https://kcare.com&width=500&height=500&withLogo=true
```

#### Generate PDF QR Code with Logo
```
GET /api/v1/qr/qrcode/pdf/logo
```

**Parameters:**
- `text` (required): Text to encode in the QR code
- `width` (optional, default: 300): Width in pixels (100-2000)
- `height` (optional, default: 300): Height in pixels (100-2000)
- `withLogo` (optional, default: true): Whether to include the KCare logo

**Example:**
```
GET /api/v1/qr/qrcode/pdf/logo?text=https://kcare.com&width=500&height=500&withLogo=true
```

## Logo Setup

### Adding Your Company Logo

1. **Prepare Logo**: Save your KCare logo as PNG format (recommended: 100x100+ pixels, square)
2. **Place File**: Copy to `src/main/resources/static/images/kcare-logo.png`
3. **Restart**: Restart the application to load the new logo
4. **Test**: Use the `/logo` endpoints to generate QR codes with your logo

### Logo Features
- **Automatic Centering**: Logo is automatically positioned in the center
- **Original Colors Preserved**: Your KCare logo maintains its original colors
- **Clean White Background**: White circular background ensures logo visibility
- **Optimal Sizing**: Logo size is 25% of QR code dimensions for best scanability
- **Professional Fallback**: If no logo file found, displays "KCare" in brand blue color
- **High Error Correction**: Ensures QR codes remain scannable despite logo overlay

## Configuration

The application can be configured via `application.properties`:

```properties
# QR Code Configuration
qrcode.defaults.width=300
qrcode.defaults.height=300
qrcode.limits.min-size=100
qrcode.limits.max-size=2000
qrcode.limits.max-text-length=4000
```

## Running the Application

1. **Using Maven:**
   ```bash
   ./mvnw spring-boot:run
   ```

2. **Using JAR:**
   ```bash
   ./mvnw clean package
   java -jar target/zing-0.0.1-SNAPSHOT.jar
   ```

The application will start on `http://localhost:8080`

## Error Handling

The API provides proper error responses:

- **400 Bad Request**: Invalid parameters (empty text, invalid dimensions)
- **500 Internal Server Error**: QR code generation failures

## Dependencies

- Spring Boot 3.5.3
- ZXing Core 3.5.3 (QR code generation)
- ZXing JavaSE 3.5.3 (Image writing)
- iText PDF 5.5.13.2 (PDF generation)
- Lombok (Code generation)

## Quality Features

- Input validation with proper error messages
- Comprehensive logging
- Exception handling
- Size optimization (minimum/maximum constraints)
- High error correction level for better scanning
- Proper HTTP headers and caching
- Industry-standard REST API design
