# IronLicensing Java SDK

Official Java SDK for [IronLicensing](https://ironlicensing.com) - Software licensing and activation for your applications.

## Installation

### Maven

```xml
<dependency>
    <groupId>com.ironservices</groupId>
    <artifactId>licensing</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.ironservices:licensing:1.0.0'
```

## Quick Start

### Using Static API

```java
import com.ironservices.licensing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize the SDK
        IronLicensing.init("pk_live_your_public_key", "your-product-slug");

        // Validate a license
        LicenseResult result = IronLicensing.validate("IRON-XXXX-XXXX-XXXX-XXXX");
        if (result.isValid()) {
            System.out.println("License is valid!");
            System.out.println("Status: " + result.getLicense().getStatus());
        } else {
            System.out.println("Validation failed: " + result.getError());
        }

        // Check for features
        if (IronLicensing.hasFeature("premium")) {
            System.out.println("Premium features enabled!");
        }
    }
}
```

### Using Client Instance

```java
import com.ironservices.licensing.*;

public class Main {
    public static void main(String[] args) {
        LicenseOptions options = LicenseOptions.builder("pk_live_your_public_key", "your-product-slug")
            .debug(true)
            .build();

        LicenseClient client = new LicenseClient(options);

        // Activate license
        LicenseResult result = client.activate("IRON-XXXX-XXXX-XXXX-XXXX", "My Machine");

        if (result.isValid()) {
            System.out.println("Activated! License type: " + result.getLicense().getType());
        }
    }
}
```

## Configuration

### Builder Pattern

```java
LicenseOptions options = LicenseOptions.builder("pk_live_xxx", "your-product")
    .apiBaseUrl("https://api.ironlicensing.com")  // Custom API URL
    .debug(true)                                   // Enable debug logging
    .enableOfflineCache(true)                      // Cache for offline use
    .cacheValidationMinutes(60)                    // Cache duration
    .offlineGraceDays(7)                           // Offline grace period
    .httpTimeout(Duration.ofSeconds(30))           // Request timeout
    .build();
```

### Fluent Setters

```java
LicenseOptions options = new LicenseOptions("pk_live_xxx", "your-product")
    .setDebug(true)
    .setEnableOfflineCache(true);
```

## License Validation

```java
// Synchronous validation
LicenseResult result = client.validate("IRON-XXXX-XXXX-XXXX-XXXX");

// Asynchronous validation
client.validateAsync("IRON-XXXX-XXXX-XXXX-XXXX")
    .thenAccept(r -> {
        if (r.isValid()) {
            License license = r.getLicense();
            System.out.println("License: " + license.getKey());
            System.out.println("Status: " + license.getStatus());
            System.out.println("Type: " + license.getType());
            System.out.printf("Activations: %d/%d%n",
                license.getCurrentActivations(),
                license.getMaxActivations());
        }
    });
```

## License Activation

```java
// Simple activation (uses hostname as machine name)
LicenseResult result = client.activate("IRON-XXXX-XXXX-XXXX-XXXX");

// With custom machine name
LicenseResult result = client.activate("IRON-XXXX-XXXX-XXXX-XXXX", "Production Server");

if (result.isValid()) {
    System.out.println("License activated successfully!");

    // View activations
    for (Activation activation : result.getActivations()) {
        System.out.printf("- %s (%s)%n", activation.getMachineName(), activation.getPlatform());
    }
}

// Async activation
client.activateAsync(licenseKey, "My Machine")
    .thenAccept(r -> System.out.println("Activated: " + r.isValid()));
```

## License Deactivation

```java
// Synchronous
if (client.deactivate()) {
    System.out.println("License deactivated from this machine");
}

// Asynchronous
client.deactivateAsync()
    .thenAccept(success -> {
        if (success) {
            System.out.println("Deactivated successfully");
        }
    });
```

## Feature Checking

```java
// Check if feature is available
if (client.hasFeature("advanced-analytics")) {
    // Enable advanced analytics
}

// Require feature (throws LicenseRequiredException if not available)
try {
    client.requireFeature("export-pdf");
    // Feature is available, continue with export
} catch (LicenseRequiredException e) {
    System.out.println("Feature not available: " + e.getFeature());
}

// Get feature details
Feature feature = client.getFeature("max-users");
if (feature != null) {
    System.out.printf("Feature: %s - %s%n", feature.getName(), feature.getDescription());
}
```

## Trial Management

```java
LicenseResult result = client.startTrial("user@example.com");

if (result.isValid()) {
    System.out.println("Trial started!");
    System.out.println("Trial key: " + result.getLicense().getKey());

    String expiresAt = result.getLicense().getExpiresAt();
    if (expiresAt != null) {
        System.out.println("Expires: " + expiresAt);
    }
}
```

## In-App Purchase

```java
// Get available tiers
List<ProductTier> tiers = client.getTiers();
for (ProductTier tier : tiers) {
    System.out.printf("%s - $%.2f %s%n", tier.getName(), tier.getPrice(), tier.getCurrency());
}

// Start checkout
CheckoutResult checkout = client.startPurchase("tier-id", "user@example.com");
if (checkout.isSuccess()) {
    System.out.println("Checkout URL: " + checkout.getCheckoutUrl());
    // Open URL in browser for user to complete purchase
}

// Async purchase
client.startPurchaseAsync("tier-id", "user@example.com")
    .thenAccept(c -> {
        if (c.isSuccess()) {
            // Handle success
        }
    });
```

## License Status

```java
// Get current license
License license = client.getLicense();
if (license != null) {
    System.out.println("Licensed to: " + license.getEmail());
}

// Check status
LicenseStatus status = client.getStatus();
switch (status) {
    case VALID:
        System.out.println("License is valid");
        break;
    case EXPIRED:
        System.out.println("License has expired");
        break;
    case TRIAL:
        System.out.println("Running in trial mode");
        break;
    case NOT_ACTIVATED:
        System.out.println("No license activated");
        break;
    default:
        System.out.println("Status: " + status);
}

// Quick checks
if (client.isLicensed()) {
    System.out.println("Application is licensed");
}

if (client.isTrial()) {
    System.out.println("Running in trial mode");
}
```

## License Change Listener

```java
client.setOnLicenseChanged(license -> {
    if (license != null) {
        System.out.println("License updated: " + license.getStatus());
    } else {
        System.out.println("License removed");
    }
});
```

## License Types

| Type | Description |
|------|-------------|
| `PERPETUAL` | One-time purchase, never expires |
| `SUBSCRIPTION` | Recurring payment, expires if not renewed |
| `TRIAL` | Time-limited trial license |

## License Statuses

| Status | Description |
|--------|-------------|
| `VALID` | License is valid and active |
| `EXPIRED` | License has expired |
| `SUSPENDED` | License temporarily suspended |
| `REVOKED` | License permanently revoked |
| `TRIAL` | Active trial license |
| `TRIAL_EXPIRED` | Trial period ended |
| `NOT_ACTIVATED` | No license activated |

## Thread Safety

The client is thread-safe and can be used concurrently:

```java
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 10; i++) {
    executor.submit(() -> {
        if (client.hasFeature("concurrent-feature")) {
            // Safe to call from multiple threads
        }
    });
}
executor.shutdown();
```

## Error Handling

```java
// Validation errors
LicenseResult result = client.validate(licenseKey);
if (!result.isValid()) {
    String error = result.getError();
    switch (error) {
        case "license_not_found":
            System.out.println("Invalid license key");
            break;
        case "license_expired":
            System.out.println("Your license has expired");
            break;
        case "max_activations_reached":
            System.out.println("No more activations available");
            break;
        default:
            System.out.println("Error: " + error);
    }
}

// Feature requirement errors
try {
    client.requireFeature("premium");
} catch (LicenseRequiredException e) {
    System.out.printf("Feature '%s' requires a valid license%n", e.getFeature());
}
```

## Machine ID

The SDK automatically generates and persists a unique machine ID at `~/.ironlicensing/machine_id`. This ID is used for:
- Tracking activations per machine
- Preventing license sharing
- Offline validation

```java
String machineId = client.getMachineId();
```

## Requirements

- Java 11 or later
- OkHttp 4.x
- Gson 2.x

## License

MIT License - see LICENSE file for details.
