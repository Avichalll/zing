package com.zing.zing.qrController;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QRCodeService {

    private static final int MIN_SIZE = 100;
    private static final int MAX_SIZE = 2000;

    /**
     * Generates QR code image in PNG format with enhanced quality and proper sizing
     * 
     * @param text   The text to encode in QR code
     * @param width  The width of QR code (minimum 100, maximum 2000)
     * @param height The height of QR code (minimum 100, maximum 2000)
     * @return byte array of PNG image
     * @throws WriterException          if QR code generation fails
     * @throws IOException              if image writing fails
     * @throws IllegalArgumentException if parameters are invalid
     */
    public byte[] generateQRCodeImage(String text, int width, int height)
            throws WriterException, IOException, IllegalArgumentException {

        log.info("Generating QR code PNG for text length: {}, dimensions: {}x{}",
                text != null ? text.length() : 0, width, height);

        // Validate inputs
        validateQRCodeInput(text, width, height);

        // Ensure minimum size for readability
        width = Math.max(width, MIN_SIZE);
        height = Math.max(height, MIN_SIZE);

        // Ensure maximum size for performance
        width = Math.min(width, MAX_SIZE);
        height = Math.min(height, MAX_SIZE);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // Configure encoding hints for better quality
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // High error correction
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 2); // Reduced margin for larger QR code

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);

        byte[] result = pngOutputStream.toByteArray();
        log.info("Successfully generated QR code PNG with size: {} bytes", result.length);

        return result;
    }

    /**
     * Validates input parameters for QR code generation
     */
    private void validateQRCodeInput(String text, int width, int height) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }

        if (text.length() > 4000) {
            throw new IllegalArgumentException("Text length cannot exceed 4000 characters");
        }

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive integers");
        }

        if (width > MAX_SIZE || height > MAX_SIZE) {
            throw new IllegalArgumentException("Width and height cannot exceed " + MAX_SIZE + " pixels");
        }
    }

    /**
     * Generates QR code in PDF format with enhanced quality
     * 
     * @param text   The text to encode in QR code
     * @param width  The width of QR code
     * @param height The height of QR code
     * @return byte array of PDF document
     * @throws WriterException          if QR code generation fails
     * @throws DocumentException        if PDF creation fails
     * @throws IOException              if image processing fails
     * @throws IllegalArgumentException if parameters are invalid
     */
    public byte[] generateQRCodePDF(String text, int width, int height)
            throws WriterException, DocumentException, IOException, IllegalArgumentException {

        log.info("Generating QR code PDF for text length: {}, dimensions: {}x{}",
                text != null ? text.length() : 0, width, height);

        // Generate high-quality PNG first
        byte[] pngImageData = generateQRCodeImage(text, width, height);

        // Create PDF document
        Document document = new Document();
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, pdfOutputStream);
            document.open();

            // Create image from PNG data
            Image qrCodeImage = Image.getInstance(pngImageData);

            // Scale image to fit page while maintaining aspect ratio
            qrCodeImage.scaleToFit(width, height);
            qrCodeImage.setAlignment(Image.ALIGN_CENTER);

            // Add some margin around the QR code
            document.add(qrCodeImage);

            log.info("Successfully generated QR code PDF");

        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return pdfOutputStream.toByteArray();
    }

    /**
     * Generates QR code image with company logo embedded
     * 
     * @param text     The text to encode in QR code
     * @param width    The width of QR code (minimum 100, maximum 2000)
     * @param height   The height of QR code (minimum 100, maximum 2000)
     * @param withLogo Whether to include the company logo
     * @return byte array of PNG image with logo
     * @throws WriterException          if QR code generation fails
     * @throws IOException              if image writing fails
     * @throws IllegalArgumentException if parameters are invalid
     */
    public byte[] generateQRCodeImageWithLogo(String text, int width, int height, boolean withLogo)
            throws WriterException, IOException, IllegalArgumentException {

        log.info("Generating QR code PNG with logo: {}, text length: {}, dimensions: {}x{}",
                withLogo, text != null ? text.length() : 0, width, height);

        if (!withLogo) {
            return generateQRCodeImage(text, width, height);
        }

        // Validate inputs
        validateQRCodeInput(text, width, height);

        // Ensure minimum size for readability
        width = Math.max(width, MIN_SIZE);
        height = Math.max(height, MIN_SIZE);

        // Ensure maximum size for performance
        width = Math.min(width, MAX_SIZE);
        height = Math.min(height, MAX_SIZE);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // Configure encoding hints for better quality - Higher error correction for
        // logo overlay
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // High error correction essential for logo
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 2);

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        // Convert to BufferedImage with color support for logo overlay
        BufferedImage qrImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D qrGraphics = qrImage.createGraphics();

        try {
            // Fill with white background
            qrGraphics.setColor(Color.WHITE);
            qrGraphics.fillRect(0, 0, width, height);

            // Draw QR code pattern in black
            qrGraphics.setColor(Color.BLACK);
            for (int x = 0; x < bitMatrix.getWidth(); x++) {
                for (int y = 0; y < bitMatrix.getHeight(); y++) {
                    if (bitMatrix.get(x, y)) {
                        qrGraphics.fillRect(x, y, 1, 1);
                    }
                }
            }
        } finally {
            qrGraphics.dispose();
        }

        // Add logo overlay
        BufferedImage finalImage = addLogoToQRCode(qrImage, width, height);

        // Convert to byte array
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        ImageIO.write(finalImage, "PNG", pngOutputStream);

        byte[] result = pngOutputStream.toByteArray();
        log.info("Successfully generated QR code PNG with logo, size: {} bytes", result.length);

        return result;
    }

    /**
     * Adds company logo to the center of QR code
     */
    private BufferedImage addLogoToQRCode(BufferedImage qrImage, int width, int height) throws IOException {
        log.info("Adding logo to QR code, QR dimensions: {}x{}", width, height);

        Graphics2D g2d = qrImage.createGraphics();

        try {
            // Enable anti-aliasing for better quality
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // Calculate logo size (optimal size for scanability)
            int logoSize = Math.min(width, height) / 4; // 25% of QR code size
            int logoX = (width - logoSize) / 2;
            int logoY = (height - logoSize) / 2;

            log.info("Logo positioning: size={}, x={}, y={}", logoSize, logoX, logoY);

            // Create clean white background for logo
            int padding = logoSize / 10; // Small padding around logo
            int backgroundSize = logoSize + (padding * 2);
            int backgroundX = logoX - padding;
            int backgroundY = logoY - padding;

            // Draw white background circle for clean logo display
            g2d.setColor(Color.WHITE);
            g2d.fillOval(backgroundX, backgroundY, backgroundSize, backgroundSize);

            // Try to load logo image from resources
            BufferedImage logoImage = loadLogoImage();
            if (logoImage != null) {
                log.info("Using original KCare logo with preserved colors, original size: {}x{}",
                        logoImage.getWidth(), logoImage.getHeight());

                // Ensure we're working with a color-capable image
                BufferedImage colorLogo = ensureColorImage(logoImage);

                // Scale logo while preserving all original colors
                BufferedImage scaledLogo = scaleImage(colorLogo, logoSize, logoSize);

                // Draw logo with full opacity to preserve all original colors
                g2d.setComposite(AlphaComposite.SrcOver);
                g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                g2d.drawImage(scaledLogo, logoX, logoY, null);

                log.info("Successfully drew KCare logo with original colors preserved");
            } else {
                log.info("Logo image not found, using KCare text fallback");
                // Fallback: Draw "KCare" text in brand colors
                drawLogoText(g2d, logoX, logoY, logoSize);
            }

        } finally {
            g2d.dispose();
        }

        log.info("Logo overlay completed with original colors preserved");
        return qrImage;
    }

    /**
     * Attempts to load logo image from resources with color preservation
     */
    private BufferedImage loadLogoImage() {
        try {
            log.info("Attempting to load KCare logo with color preservation");

            // Try multiple paths for logo loading
            String[] logoPaths = {
                    "static/images/kcare-logo.png",
                    "/static/images/kcare-logo.png",
                    "images/kcare-logo.png",
                    "/images/kcare-logo.png",
                    "kcare-logo.png"
            };

            for (String logoPath : logoPaths) {
                try {
                    ClassPathResource logoResource = new ClassPathResource(logoPath);
                    log.info("Trying logo path: {}, exists: {}", logoPath, logoResource.exists());

                    if (logoResource.exists()) {
                        try (InputStream logoStream = logoResource.getInputStream()) {
                            // Load image preserving colors
                            BufferedImage logo = ImageIO.read(logoStream);
                            if (logo != null) {
                                log.info("Successfully loaded KCare logo from path: {}, dimensions: {}x{}, type: {}",
                                        logoPath, logo.getWidth(), logo.getHeight(), logo.getType());

                                // Log color information
                                if (logo.getColorModel().hasAlpha()) {
                                    log.info("Logo has transparency/alpha channel");
                                }
                                if (logo.getColorModel().getColorSpace()
                                        .getType() == java.awt.color.ColorSpace.TYPE_RGB) {
                                    log.info("Logo is in RGB color space - colors will be preserved");
                                }

                                return logo;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("Failed to load logo from path {}: {}", logoPath, e.getMessage());
                }
            }

            log.warn("Could not load KCare logo from any path, will use text fallback");

        } catch (Exception e) {
            log.error("Unexpected error loading KCare logo image: {}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * Scales an image to specified dimensions while preserving original colors
     */
    private BufferedImage scaleImage(BufferedImage original, int width, int height) {
        // Use ARGB to preserve colors and transparency
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();

        try {
            // Use high-quality scaling to preserve colors
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

            // Draw the image preserving all original colors
            g2d.drawImage(original, 0, 0, width, height, null);
        } finally {
            g2d.dispose();
        }

        return scaledImage;
    }

    /**
     * Draws "KCare" text as logo fallback with brand colors
     */
    private void drawLogoText(Graphics2D g2d, int x, int y, int size) {
        log.info("Drawing KCare text logo with brand colors at position: {},{} with size: {}", x, y, size);

        // Set font - professional size
        int fontSize = Math.max(size / 3, 16);
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g2d.setFont(font);

        // Enable text anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Get text metrics for centering
        FontMetrics fm = g2d.getFontMetrics();
        String text = "KCare";
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        // Center the text
        int textX = x + (size - textWidth) / 2;
        int textY = y + (size + textHeight) / 2 - fm.getDescent();

        log.info("Text positioning: text='{}', fontSize={}, textX={}, textY={}", text, fontSize, textX, textY);

        // Draw KCare text in a professional blue color (or your brand color)
        // You can change this color to match your brand
        g2d.setColor(new Color(0, 102, 204)); // Professional blue
        g2d.drawString(text, textX, textY);

        log.info("KCare text drawn successfully in brand colors");
    }

    /**
     * Generates QR code in PDF format with optional logo
     * 
     * @param text     The text to encode in QR code
     * @param width    The width of QR code
     * @param height   The height of QR code
     * @param withLogo Whether to include the company logo
     * @return byte array of PDF document
     * @throws WriterException          if QR code generation fails
     * @throws DocumentException        if PDF creation fails
     * @throws IOException              if image processing fails
     * @throws IllegalArgumentException if parameters are invalid
     */
    public byte[] generateQRCodePDFWithLogo(String text, int width, int height, boolean withLogo)
            throws WriterException, DocumentException, IOException, IllegalArgumentException {

        log.info("Generating QR code PDF with logo: {}, text length: {}, dimensions: {}x{}",
                withLogo, text != null ? text.length() : 0, width, height);

        // Generate high-quality PNG with or without logo
        byte[] pngImageData = withLogo ? generateQRCodeImageWithLogo(text, width, height, true)
                : generateQRCodeImage(text, width, height);

        // Create PDF document
        Document document = new Document();
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, pdfOutputStream);
            document.open();

            // Create image from PNG data
            Image qrCodeImage = Image.getInstance(pngImageData);

            // Scale image to fit page while maintaining aspect ratio
            qrCodeImage.scaleToFit(width, height);
            qrCodeImage.setAlignment(Image.ALIGN_CENTER);

            // Add some margin around the QR code
            document.add(qrCodeImage);

            log.info("Successfully generated QR code PDF with logo: {}", withLogo);

        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return pdfOutputStream.toByteArray();
    }

    /**
     * Debug method to check logo file accessibility
     */
    public Map<String, Object> debugLogoStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            // Check different logo paths
            String[] logoPaths = {
                    "static/images/kcare-logo.png",
                    "/static/images/kcare-logo.png",
                    "images/kcare-logo.png",
                    "/images/kcare-logo.png",
                    "kcare-logo.png"
            };

            for (String logoPath : logoPaths) {
                ClassPathResource logoResource = new ClassPathResource(logoPath);
                boolean exists = logoResource.exists();
                status.put("path_" + logoPath.replace("/", "_"), exists);

                if (exists) {
                    try (InputStream stream = logoResource.getInputStream()) {
                        BufferedImage img = ImageIO.read(stream);
                        if (img != null) {
                            status.put("found_logo_path", logoPath);
                            status.put("logo_width", img.getWidth());
                            status.put("logo_height", img.getHeight());
                            status.put("logo_loaded", true);
                            break;
                        }
                    } catch (Exception e) {
                        status.put("load_error_" + logoPath, e.getMessage());
                    }
                }
            }

            status.put("debug_timestamp", System.currentTimeMillis());

        } catch (Exception e) {
            status.put("debug_error", e.getMessage());
        }

        return status;
    }

    /**
     * Force-test method that shows a visible logo for debugging
     */
    public byte[] generateTestQRCodeWithVisibleLogo(String text, int width, int height)
            throws WriterException, IOException {

        log.info("GENERATING TEST QR CODE WITH VISIBLE LOGO FOR DEBUGGING");

        // Validate inputs
        validateQRCodeInput(text, width, height);

        width = Math.max(width, MIN_SIZE);
        height = Math.max(height, MIN_SIZE);
        width = Math.min(width, MAX_SIZE);
        height = Math.min(height, MAX_SIZE);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 2);

        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // Add highly visible logo for testing
        Graphics2D g2d = qrImage.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Make logo larger for test visibility
            int logoSize = Math.min(width, height) / 3;
            int logoX = (width - logoSize) / 2;
            int logoY = (height - logoSize) / 2;

            // Draw bright yellow background for high visibility
            g2d.setColor(new Color(255, 255, 0)); // Bright yellow
            g2d.fillOval(logoX - 10, logoY - 10, logoSize + 20, logoSize + 20);

            // Draw white background
            g2d.setColor(Color.WHITE);
            g2d.fillOval(logoX, logoY, logoSize, logoSize);

            // Draw thick border
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new java.awt.BasicStroke(3));
            g2d.drawOval(logoX, logoY, logoSize, logoSize);

            // Try to load actual logo first
            BufferedImage logoImage = loadLogoImage();
            if (logoImage != null) {
                log.info("TEST: Using actual KCare logo image with original colors");
                BufferedImage colorLogo = ensureColorImage(logoImage);
                BufferedImage scaledLogo = scaleImage(colorLogo, logoSize, logoSize);
                g2d.drawImage(scaledLogo, logoX, logoY, null);
            } else {
                // Draw large test text
                Font font = new Font("Arial", Font.BOLD, logoSize / 3);
                g2d.setFont(font);
                g2d.setColor(new Color(0, 102, 204)); // KCare blue

                FontMetrics fm = g2d.getFontMetrics();
                String testText = "KCare";
                int textWidth = fm.stringWidth(testText);
                int textHeight = fm.getHeight();

                int textX = logoX + (logoSize - textWidth) / 2;
                int textY = logoY + (logoSize + textHeight) / 2 - fm.getDescent();

                g2d.drawString(testText, textX, textY);
                log.info("TEST: Using KCare text fallback in brand colors");
            }

            log.info("TEST LOGO DRAWN - YELLOW BACKGROUND WITH LOGO/TEXT");

        } finally {
            g2d.dispose();
        }

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", pngOutputStream);

        return pngOutputStream.toByteArray();
    }

    /**
     * Ensures the image is in a color format that preserves original colors
     */
    private BufferedImage ensureColorImage(BufferedImage original) {
        // If already in a color format, return as-is
        if (original.getType() == BufferedImage.TYPE_INT_RGB ||
                original.getType() == BufferedImage.TYPE_INT_ARGB) {
            return original;
        }

        // Convert to color format
        BufferedImage colorImage = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = colorImage.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.drawImage(original, 0, 0, null);
        } finally {
            g2d.dispose();
        }

        return colorImage;
    }
}