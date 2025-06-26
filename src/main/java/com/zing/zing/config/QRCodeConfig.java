package com.zing.zing.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for QR Code generation
 */
@Configuration
@ConfigurationProperties(prefix = "qrcode")
@Data
public class QRCodeConfig {

    private Dimensions defaults = new Dimensions();

    private Limits limits = new Limits();

    @Data
    public static class Dimensions {
        private int width = 300;
        private int height = 300;
    }

    @Data
    public static class Limits {
        private int minSize = 100;
        private int maxSize = 2000;
        private int maxTextLength = 4000;
    }
}
