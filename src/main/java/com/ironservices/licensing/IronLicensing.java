package com.ironservices.licensing;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Static facade for the IronLicensing SDK.
 * Provides a simple global API for license operations.
 */
public final class IronLicensing {
    private static volatile LicenseClient client;
    private static final Object lock = new Object();

    private IronLicensing() {}

    /**
     * Initializes the global IronLicensing client.
     *
     * @param publicKey   The public key for your product
     * @param productSlug The product slug
     */
    public static void init(String publicKey, String productSlug) {
        init(new LicenseOptions(publicKey, productSlug));
    }

    /**
     * Initializes the global IronLicensing client with options.
     *
     * @param options Configuration options
     */
    public static void init(LicenseOptions options) {
        synchronized (lock) {
            client = new LicenseClient(options);
        }
    }

    /**
     * Gets the global client instance.
     *
     * @return The client, or null if not initialized
     */
    public static LicenseClient getClient() {
        return client;
    }

    private static LicenseClient requireClient() {
        LicenseClient c = client;
        if (c == null) {
            throw new IllegalStateException("IronLicensing not initialized. Call IronLicensing.init() first.");
        }
        return c;
    }

    /**
     * Validates a license key.
     *
     * @param licenseKey The license key to validate
     * @return The validation result
     */
    public static LicenseResult validate(String licenseKey) {
        return requireClient().validate(licenseKey);
    }

    /**
     * Validates a license key asynchronously.
     *
     * @param licenseKey The license key to validate
     * @return A CompletableFuture with the validation result
     */
    public static CompletableFuture<LicenseResult> validateAsync(String licenseKey) {
        return requireClient().validateAsync(licenseKey);
    }

    /**
     * Activates a license key on this machine.
     *
     * @param licenseKey The license key to activate
     * @return The activation result
     */
    public static LicenseResult activate(String licenseKey) {
        return requireClient().activate(licenseKey);
    }

    /**
     * Activates a license key with a custom machine name.
     *
     * @param licenseKey  The license key to activate
     * @param machineName The machine name
     * @return The activation result
     */
    public static LicenseResult activate(String licenseKey, String machineName) {
        return requireClient().activate(licenseKey, machineName);
    }

    /**
     * Deactivates the current license from this machine.
     *
     * @return true if deactivation was successful
     */
    public static boolean deactivate() {
        return requireClient().deactivate();
    }

    /**
     * Starts a trial for the given email.
     *
     * @param email The email address for the trial
     * @return The trial result
     */
    public static LicenseResult startTrial(String email) {
        return requireClient().startTrial(email);
    }

    /**
     * Checks if a feature is available.
     *
     * @param featureKey The feature key to check
     * @return true if the feature is enabled
     */
    public static boolean hasFeature(String featureKey) {
        return requireClient().hasFeature(featureKey);
    }

    /**
     * Requires a feature to be available.
     *
     * @param featureKey The feature key to require
     * @throws LicenseRequiredException if the feature is not available
     */
    public static void requireFeature(String featureKey) throws LicenseRequiredException {
        requireClient().requireFeature(featureKey);
    }

    /**
     * Gets a feature from the current license.
     *
     * @param featureKey The feature key
     * @return The feature, or null if not found
     */
    public static Feature getFeature(String featureKey) {
        return requireClient().getFeature(featureKey);
    }

    /**
     * Gets the current license.
     *
     * @return The current license, or null if not licensed
     */
    public static License getLicense() {
        return requireClient().getLicense();
    }

    /**
     * Gets the current license status.
     *
     * @return The license status
     */
    public static LicenseStatus getStatus() {
        return requireClient().getStatus();
    }

    /**
     * Checks if the application is licensed.
     *
     * @return true if licensed
     */
    public static boolean isLicensed() {
        return requireClient().isLicensed();
    }

    /**
     * Checks if running in trial mode.
     *
     * @return true if in trial mode
     */
    public static boolean isTrial() {
        return requireClient().isTrial();
    }

    /**
     * Gets available product tiers.
     *
     * @return List of product tiers
     */
    public static List<ProductTier> getTiers() {
        return requireClient().getTiers();
    }

    /**
     * Starts a checkout session.
     *
     * @param tierId The tier ID to purchase
     * @param email  The customer's email
     * @return The checkout result
     */
    public static CheckoutResult startPurchase(String tierId, String email) {
        return requireClient().startPurchase(tierId, email);
    }

    /**
     * Sets a listener for license changes.
     *
     * @param listener The listener to call when license changes
     */
    public static void setOnLicenseChanged(Consumer<License> listener) {
        requireClient().setOnLicenseChanged(listener);
    }
}
