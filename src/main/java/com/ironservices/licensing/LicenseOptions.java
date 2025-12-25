package com.ironservices.licensing;

import java.time.Duration;

/**
 * Configuration options for the LicenseClient.
 */
public class LicenseOptions {
    private static final String DEFAULT_API_BASE_URL = "https://api.ironlicensing.com";
    private static final Duration DEFAULT_HTTP_TIMEOUT = Duration.ofSeconds(30);
    private static final int DEFAULT_CACHE_VALIDATION_MINUTES = 60;
    private static final int DEFAULT_OFFLINE_GRACE_DAYS = 7;

    private String publicKey;
    private String productSlug;
    private String apiBaseUrl = DEFAULT_API_BASE_URL;
    private boolean debug = false;
    private boolean enableOfflineCache = true;
    private int cacheValidationMinutes = DEFAULT_CACHE_VALIDATION_MINUTES;
    private int offlineGraceDays = DEFAULT_OFFLINE_GRACE_DAYS;
    private Duration httpTimeout = DEFAULT_HTTP_TIMEOUT;

    public LicenseOptions() {}

    public LicenseOptions(String publicKey, String productSlug) {
        this.publicKey = publicKey;
        this.productSlug = productSlug;
    }

    public static Builder builder(String publicKey, String productSlug) {
        return new Builder(publicKey, productSlug);
    }

    public String getPublicKey() {
        return publicKey;
    }

    public LicenseOptions setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public LicenseOptions setProductSlug(String productSlug) {
        this.productSlug = productSlug;
        return this;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public LicenseOptions setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public LicenseOptions setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public boolean isEnableOfflineCache() {
        return enableOfflineCache;
    }

    public LicenseOptions setEnableOfflineCache(boolean enableOfflineCache) {
        this.enableOfflineCache = enableOfflineCache;
        return this;
    }

    public int getCacheValidationMinutes() {
        return cacheValidationMinutes;
    }

    public LicenseOptions setCacheValidationMinutes(int cacheValidationMinutes) {
        this.cacheValidationMinutes = cacheValidationMinutes;
        return this;
    }

    public int getOfflineGraceDays() {
        return offlineGraceDays;
    }

    public LicenseOptions setOfflineGraceDays(int offlineGraceDays) {
        this.offlineGraceDays = offlineGraceDays;
        return this;
    }

    public Duration getHttpTimeout() {
        return httpTimeout;
    }

    public LicenseOptions setHttpTimeout(Duration httpTimeout) {
        this.httpTimeout = httpTimeout;
        return this;
    }

    public static class Builder {
        private final LicenseOptions options;

        public Builder(String publicKey, String productSlug) {
            this.options = new LicenseOptions(publicKey, productSlug);
        }

        public Builder apiBaseUrl(String apiBaseUrl) {
            options.setApiBaseUrl(apiBaseUrl);
            return this;
        }

        public Builder debug(boolean debug) {
            options.setDebug(debug);
            return this;
        }

        public Builder enableOfflineCache(boolean enable) {
            options.setEnableOfflineCache(enable);
            return this;
        }

        public Builder cacheValidationMinutes(int minutes) {
            options.setCacheValidationMinutes(minutes);
            return this;
        }

        public Builder offlineGraceDays(int days) {
            options.setOfflineGraceDays(days);
            return this;
        }

        public Builder httpTimeout(Duration timeout) {
            options.setHttpTimeout(timeout);
            return this;
        }

        public LicenseOptions build() {
            if (options.getPublicKey() == null || options.getPublicKey().isEmpty()) {
                throw new IllegalArgumentException("Public key is required");
            }
            if (options.getProductSlug() == null || options.getProductSlug().isEmpty()) {
                throw new IllegalArgumentException("Product slug is required");
            }
            return options;
        }
    }
}
