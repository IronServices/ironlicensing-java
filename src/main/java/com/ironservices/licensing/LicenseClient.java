package com.ironservices.licensing;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * Main client for the IronLicensing SDK.
 * Thread-safe and can be used concurrently.
 */
public class LicenseClient {
    private final LicenseOptions options;
    private final Transport transport;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private License currentLicense;
    private String licenseKey;
    private Consumer<License> onLicenseChanged;

    /**
     * Creates a new LicenseClient with the given options.
     *
     * @param options Configuration options
     */
    public LicenseClient(LicenseOptions options) {
        this.options = options;
        this.transport = new Transport(options);
        if (options.isDebug()) {
            log("Client initialized");
        }
    }

    /**
     * Creates a new LicenseClient with public key and product slug.
     *
     * @param publicKey   The public key for your product
     * @param productSlug The product slug
     */
    public LicenseClient(String publicKey, String productSlug) {
        this(new LicenseOptions(publicKey, productSlug));
    }

    private void log(String message) {
        if (options.isDebug()) {
            System.out.println("[IronLicensing] " + message);
        }
    }

    /**
     * Sets a listener for license changes.
     *
     * @param listener The listener to call when license changes
     */
    public void setOnLicenseChanged(Consumer<License> listener) {
        this.onLicenseChanged = listener;
    }

    /**
     * Validates a license key.
     *
     * @param licenseKey The license key to validate
     * @return The validation result
     */
    public LicenseResult validate(String licenseKey) {
        LicenseResult result = transport.validate(licenseKey);
        if (result.isValid() && result.getLicense() != null) {
            updateLicense(licenseKey, result.getLicense());
        }
        return result;
    }

    /**
     * Validates a license key asynchronously.
     *
     * @param licenseKey The license key to validate
     * @return A CompletableFuture with the validation result
     */
    public CompletableFuture<LicenseResult> validateAsync(String licenseKey) {
        return CompletableFuture.supplyAsync(() -> validate(licenseKey));
    }

    /**
     * Activates a license key on this machine.
     *
     * @param licenseKey The license key to activate
     * @return The activation result
     */
    public LicenseResult activate(String licenseKey) {
        return activate(licenseKey, null);
    }

    /**
     * Activates a license key on this machine with a custom machine name.
     *
     * @param licenseKey  The license key to activate
     * @param machineName Optional machine name
     * @return The activation result
     */
    public LicenseResult activate(String licenseKey, String machineName) {
        LicenseResult result = transport.activate(licenseKey, machineName);
        if (result.isValid() && result.getLicense() != null) {
            updateLicense(licenseKey, result.getLicense());
        }
        return result;
    }

    /**
     * Activates a license key asynchronously.
     *
     * @param licenseKey  The license key to activate
     * @param machineName Optional machine name
     * @return A CompletableFuture with the activation result
     */
    public CompletableFuture<LicenseResult> activateAsync(String licenseKey, String machineName) {
        return CompletableFuture.supplyAsync(() -> activate(licenseKey, machineName));
    }

    /**
     * Deactivates the current license from this machine.
     *
     * @return true if deactivation was successful
     */
    public boolean deactivate() {
        lock.readLock().lock();
        String key;
        try {
            key = this.licenseKey;
        } finally {
            lock.readLock().unlock();
        }

        if (key == null || key.isEmpty()) {
            return false;
        }

        if (transport.deactivate(key)) {
            lock.writeLock().lock();
            try {
                this.currentLicense = null;
                this.licenseKey = null;
            } finally {
                lock.writeLock().unlock();
            }
            notifyLicenseChanged(null);
            return true;
        }
        return false;
    }

    /**
     * Deactivates the current license asynchronously.
     *
     * @return A CompletableFuture with the deactivation result
     */
    public CompletableFuture<Boolean> deactivateAsync() {
        return CompletableFuture.supplyAsync(this::deactivate);
    }

    /**
     * Starts a trial for the given email.
     *
     * @param email The email address for the trial
     * @return The trial result
     */
    public LicenseResult startTrial(String email) {
        LicenseResult result = transport.startTrial(email);
        if (result.isValid() && result.getLicense() != null) {
            updateLicense(result.getLicense().getKey(), result.getLicense());
        }
        return result;
    }

    /**
     * Starts a trial asynchronously.
     *
     * @param email The email address for the trial
     * @return A CompletableFuture with the trial result
     */
    public CompletableFuture<LicenseResult> startTrialAsync(String email) {
        return CompletableFuture.supplyAsync(() -> startTrial(email));
    }

    /**
     * Checks if a feature is available in the current license.
     *
     * @param featureKey The feature key to check
     * @return true if the feature is enabled
     */
    public boolean hasFeature(String featureKey) {
        lock.readLock().lock();
        try {
            return currentLicense != null && currentLicense.hasFeature(featureKey);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Requires a feature to be available, throws if not.
     *
     * @param featureKey The feature key to require
     * @throws LicenseRequiredException if the feature is not available
     */
    public void requireFeature(String featureKey) throws LicenseRequiredException {
        if (!hasFeature(featureKey)) {
            throw new LicenseRequiredException(featureKey);
        }
    }

    /**
     * Gets a feature from the current license.
     *
     * @param featureKey The feature key
     * @return The feature, or null if not found
     */
    public Feature getFeature(String featureKey) {
        lock.readLock().lock();
        try {
            if (currentLicense != null) {
                return currentLicense.getFeature(featureKey);
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets the current license.
     *
     * @return The current license, or null if not licensed
     */
    public License getLicense() {
        lock.readLock().lock();
        try {
            return currentLicense;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets the current license status.
     *
     * @return The license status
     */
    public LicenseStatus getStatus() {
        lock.readLock().lock();
        try {
            if (currentLicense != null) {
                return currentLicense.getStatus();
            }
            return LicenseStatus.NOT_ACTIVATED;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Checks if the application is licensed.
     *
     * @return true if licensed (valid or trial)
     */
    public boolean isLicensed() {
        lock.readLock().lock();
        try {
            if (currentLicense == null) return false;
            LicenseStatus status = currentLicense.getStatus();
            return status == LicenseStatus.VALID || status == LicenseStatus.TRIAL;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Checks if running in trial mode.
     *
     * @return true if in trial mode
     */
    public boolean isTrial() {
        lock.readLock().lock();
        try {
            if (currentLicense == null) return false;
            return currentLicense.getStatus() == LicenseStatus.TRIAL ||
                   currentLicense.getType() == LicenseType.TRIAL;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Gets available product tiers for purchase.
     *
     * @return List of product tiers
     */
    public List<ProductTier> getTiers() {
        return transport.getTiers();
    }

    /**
     * Gets available product tiers asynchronously.
     *
     * @return A CompletableFuture with the list of tiers
     */
    public CompletableFuture<List<ProductTier>> getTiersAsync() {
        return CompletableFuture.supplyAsync(this::getTiers);
    }

    /**
     * Starts a checkout session for the specified tier.
     *
     * @param tierId The tier ID to purchase
     * @param email  The customer's email
     * @return The checkout result with URL
     */
    public CheckoutResult startPurchase(String tierId, String email) {
        return transport.startCheckout(tierId, email);
    }

    /**
     * Starts a checkout session asynchronously.
     *
     * @param tierId The tier ID to purchase
     * @param email  The customer's email
     * @return A CompletableFuture with the checkout result
     */
    public CompletableFuture<CheckoutResult> startPurchaseAsync(String tierId, String email) {
        return CompletableFuture.supplyAsync(() -> startPurchase(tierId, email));
    }

    /**
     * Gets the machine ID used for activations.
     *
     * @return The machine ID
     */
    public String getMachineId() {
        return transport.getMachineId();
    }

    private void updateLicense(String key, License license) {
        lock.writeLock().lock();
        try {
            this.licenseKey = key;
            this.currentLicense = license;
        } finally {
            lock.writeLock().unlock();
        }
        notifyLicenseChanged(license);
    }

    private void notifyLicenseChanged(License license) {
        if (onLicenseChanged != null) {
            try {
                onLicenseChanged.accept(license);
            } catch (Exception e) {
                log("License change listener error: " + e.getMessage());
            }
        }
    }
}
