# Logo Setup Instructions

## Adding Your KCare Logo

To add your company logo to the QR codes, follow these steps:

### 1. Prepare Your Logo Image
- Save your KCare logo as `kcare-logo.png`
- Recommended dimensions: 100x100 pixels or larger (square format works best)
- Use PNG format with transparent background for best results
- Ensure the logo is simple and recognizable at small sizes

### 2. Place the Logo File
Copy your logo file to: `src/main/resources/static/images/kcare-logo.png`

### 3. Logo Specifications
- **Format**: PNG (preferred) or JPG
- **Size**: Square format recommended (e.g., 100x100, 200x200)
- **Background**: Transparent PNG works best
- **Colors**: High contrast for better visibility
- **Simplicity**: Simple designs work better in QR codes

### 4. Fallback Text
If no logo image is found, the system will automatically display "KCare" text as a fallback.

## API Endpoints with Logo

### PNG with Logo
```
GET /api/v1/qr/qrcode/png/logo?text=https://example.com&width=400&height=400&withLogo=true
```

### PDF with Logo
```
GET /api/v1/qr/qrcode/pdf/logo?text=https://example.com&width=400&height=400&withLogo=true
```

## Logo Positioning
- The logo is automatically centered in the QR code
- Logo size is calculated as 20% of the QR code dimensions
- A white circular background with black border is added behind the logo
- High error correction ensures the QR code remains scannable

## Testing
After adding your logo:
1. Restart the application
2. Test the logo endpoints
3. Verify the QR codes are still scannable with various QR code readers
4. Adjust logo size/contrast if scanning issues occur

## Important Notes
- QR codes with logos require high error correction (Level H)
- The logo should not exceed 30% of the QR code area
- Test scanning with multiple devices to ensure reliability
- Keep logo simple for better scanning performance
