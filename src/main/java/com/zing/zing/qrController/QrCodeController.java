package com.zing.zing.qrController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for QR Code generation in various formats
 * Provides endpoints for generating QR codes as PNG images and PDF documents
 */
@RestController
@RequestMapping("/api/v1/qr")
@RequiredArgsConstructor
@Slf4j
public class QrCodeController {

    private final QRCodeService qrCodeService;

    /**
     * Generate QR Code as PNG image
     * Generates a QR code in PNG format with specified dimensions.
     * Minimum size is 100x100, maximum is 2000x2000 for optimal quality.
     * 
     * @param text   Text to encode in QR code (required)
     * @param width  Width of QR code in pixels (100-2000, default: 300)
     * @param height Height of QR code in pixels (100-2000, default: 300)
     * @return ResponseEntity containing PNG image bytes
     */
    @GetMapping(value = "/qrcode/png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCodePNG(
            @RequestParam String text,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height) {

        try {
            log.info("Received request to generate PNG QR code: text length={}, dimensions={}x{}",
                    text.length(), width, height);

            byte[] qrCodeBytes = qrCodeService.generateQRCodeImage(text, width, height);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            headers.set("Content-Disposition", "inline; filename=qrcode.png");

            log.info("Successfully generated PNG QR code with {} bytes", qrCodeBytes.length);

            return new ResponseEntity<>(qrCodeBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for PNG QR code generation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (WriterException | IOException e) {
            log.error("Error generating PNG QR code", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate QR Code as PDF document
     * Generates a QR code embedded in a PDF document with specified dimensions.
     * 
     * @param text   Text to encode in QR code (required)
     * @param width  Width of QR code in pixels (100-2000, default: 300)
     * @param height Height of QR code in pixels (100-2000, default: 300)
     * @return ResponseEntity containing PDF document bytes
     */
    @GetMapping(value = "/qrcode/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateQRCodePDF(
            @RequestParam String text,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height) {

        try {
            log.info("Received request to generate PDF QR code: text length={}, dimensions={}x{}",
                    text.length(), width, height);

            byte[] qrCodeBytes = qrCodeService.generateQRCodePDF(text, width, height);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qrcode.pdf");
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            log.info("Successfully generated PDF QR code with {} bytes", qrCodeBytes.length);

            return new ResponseEntity<>(qrCodeBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for PDF QR code generation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (WriterException | DocumentException | IOException e) {
            log.error("Error generating PDF QR code", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate QR Code as PNG image with company logo
     * Generates a QR code in PNG format with KCare logo embedded in the center.
     * 
     * @param text     Text to encode in QR code (required)
     * @param width    Width of QR code in pixels (100-2000, default: 300)
     * @param height   Height of QR code in pixels (100-2000, default: 300)
     * @param withLogo Whether to include company logo (default: true)
     * @return ResponseEntity containing PNG image bytes with logo
     */
    @GetMapping(value = "/qrcode/png/logo", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQRCodePNGWithLogo(
            @RequestParam String text,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height,
            @RequestParam(defaultValue = "true") boolean withLogo) {

        try {
            log.info("Received request to generate PNG QR code with logo: text length={}, dimensions={}x{}, logo={}",
                    text.length(), width, height, withLogo);

            byte[] qrCodeBytes = qrCodeService.generateQRCodeImageWithLogo(text, width, height, withLogo);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);
            headers.set("Content-Disposition", "inline; filename=qrcode-with-logo.png");

            log.info("Successfully generated PNG QR code with logo, size: {} bytes", qrCodeBytes.length);

            return new ResponseEntity<>(qrCodeBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for PNG QR code with logo generation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (WriterException | IOException e) {
            log.error("Error generating PNG QR code with logo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate QR Code as PDF document with company logo
     * Generates a QR code embedded in a PDF document with KCare logo.
     * 
     * @param text     Text to encode in QR code (required)
     * @param width    Width of QR code in pixels (100-2000, default: 300)
     * @param height   Height of QR code in pixels (100-2000, default: 300)
     * @param withLogo Whether to include company logo (default: true)
     * @return ResponseEntity containing PDF document bytes with logo
     */
    @GetMapping(value = "/qrcode/pdf/logo", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateQRCodePDFWithLogo(
            @RequestParam String text,
            @RequestParam(defaultValue = "300") int width,
            @RequestParam(defaultValue = "300") int height,
            @RequestParam(defaultValue = "true") boolean withLogo) {

        try {
            log.info("Received request to generate PDF QR code with logo: text length={}, dimensions={}x{}, logo={}",
                    text.length(), width, height, withLogo);

            byte[] qrCodeBytes = qrCodeService.generateQRCodePDFWithLogo(text, width, height, withLogo);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=qrcode-with-logo.pdf");
            headers.setCacheControl("no-cache, no-store, must-revalidate");
            headers.setPragma("no-cache");
            headers.setExpires(0);

            log.info("Successfully generated PDF QR code with logo, size: {} bytes", qrCodeBytes.length);

            return new ResponseEntity<>(qrCodeBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid parameters for PDF QR code with logo generation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (WriterException | DocumentException | IOException e) {
            log.error("Error generating PDF QR code with logo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Test endpoint to debug logo functionality
     * Returns information about logo loading status
     */
    @GetMapping(value = "/qrcode/test/logo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> testLogoFunctionality() {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("Testing logo functionality...");

            // Test logo loading
            byte[] testQr = qrCodeService.generateQRCodeImageWithLogo("TEST", 300, 300, true);

            response.put("status", "success");
            response.put("message", "Logo test completed successfully");
            response.put("qrCodeSize", testQr.length);
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Logo test failed", e);
            response.put("status", "error");
            response.put("message", e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Debug endpoint to check logo loading status
     */
    @GetMapping(value = "/qrcode/debug/logo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> debugLogoStatus() {
        try {
            Map<String, Object> debugInfo = qrCodeService.debugLogoStatus();
            return ResponseEntity.ok(debugInfo);
        } catch (Exception e) {
            Map<String, Object> errorInfo = new HashMap<>();
            errorInfo.put("error", e.getMessage());
            errorInfo.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
        }
    }

    /**
     * Test endpoint with forced visible logo for debugging
     */
    @GetMapping(value = "/qrcode/test/visible-logo", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateTestQRCodeWithVisibleLogo(
            @RequestParam(defaultValue = "TEST LOGO VISIBILITY") String text,
            @RequestParam(defaultValue = "400") int width,
            @RequestParam(defaultValue = "400") int height) {

        try {
            log.info("GENERATING TEST QR CODE WITH FORCED VISIBLE LOGO");

            byte[] qrCodeBytes = qrCodeService.generateTestQRCodeWithVisibleLogo(text, width, height);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.set("Content-Disposition", "inline; filename=test-qr-visible-logo.png");

            return new ResponseEntity<>(qrCodeBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error generating test QR code with visible logo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Test endpoint specifically for color preservation
     */
    @GetMapping(value = "/qrcode/test/colors", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateColorTestQRCode(
            @RequestParam(defaultValue = "KCare Color Test") String text,
            @RequestParam(defaultValue = "400") int width,
            @RequestParam(defaultValue = "400") int height) {

        try {
            log.info("GENERATING COLOR PRESERVATION TEST QR CODE");

            // Generate QR with logo and original colors
            byte[] qrCodeBytes = qrCodeService.generateQRCodeImageWithLogo(text, width, height, true);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.set("Content-Disposition", "inline; filename=kcare-color-test.png");

            return new ResponseEntity<>(qrCodeBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error generating color test QR code", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Global exception handler for unexpected errors
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        log.error("Unexpected error in QR code controller", e);

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal server error");
        errorResponse.put("message", "An unexpected error occurred while processing your request");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
