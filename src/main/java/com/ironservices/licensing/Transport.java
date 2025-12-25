package com.ironservices.licensing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * HTTP transport layer for IronLicensing API.
 */
class Transport {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final String baseUrl;
    private final String publicKey;
    private final String productSlug;
    private final boolean debug;
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String machineId;

    Transport(LicenseOptions options) {
        this.baseUrl = options.getApiBaseUrl();
        this.publicKey = options.getPublicKey();
        this.productSlug = options.getProductSlug();
        this.debug = options.isDebug();
        this.httpClient = new OkHttpClient.Builder()
            .connectTimeout(options.getHttpTimeout().toMillis(), TimeUnit.MILLISECONDS)
            .readTimeout(options.getHttpTimeout().toMillis(), TimeUnit.MILLISECONDS)
            .writeTimeout(options.getHttpTimeout().toMillis(), TimeUnit.MILLISECONDS)
            .build();
        this.gson = new GsonBuilder().create();
        this.machineId = getOrCreateMachineId();
    }

    private void log(String message) {
        if (debug) {
            System.out.println("[IronLicensing] " + message);
        }
    }

    private String getOrCreateMachineId() {
        try {
            Path idPath = Paths.get(System.getProperty("user.home"), ".ironlicensing", "machine_id");
            if (Files.exists(idPath)) {
                return new String(Files.readAllBytes(idPath)).trim();
            }
            String id = UUID.randomUUID().toString();
            Files.createDirectories(idPath.getParent());
            Files.write(idPath, id.getBytes());
            return id;
        } catch (IOException e) {
            return UUID.randomUUID().toString();
        }
    }

    String getMachineId() {
        return machineId;
    }

    private String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }

    private String getPlatform() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) return "windows";
        if (os.contains("mac")) return "macos";
        if (os.contains("nix") || os.contains("nux")) return "linux";
        return os;
    }

    private Request.Builder createRequest(String path) {
        return new Request.Builder()
            .url(baseUrl + path)
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Public-Key", publicKey)
            .addHeader("X-Product-Slug", productSlug);
    }

    LicenseResult validate(String licenseKey) {
        log("Validating: " + licenseKey.substring(0, Math.min(10, licenseKey.length())) + "...");

        Map<String, String> body = new HashMap<>();
        body.put("licenseKey", licenseKey);
        body.put("machineId", machineId);

        Request request = createRequest("/api/v1/validate")
            .post(RequestBody.create(gson.toJson(body), JSON))
            .build();

        return executeRequest(request);
    }

    LicenseResult activate(String licenseKey, String machineName) {
        log("Activating: " + licenseKey.substring(0, Math.min(10, licenseKey.length())) + "...");

        if (machineName == null || machineName.isEmpty()) {
            machineName = getHostname();
        }

        Map<String, String> body = new HashMap<>();
        body.put("licenseKey", licenseKey);
        body.put("machineId", machineId);
        body.put("machineName", machineName);
        body.put("platform", getPlatform());

        Request request = createRequest("/api/v1/activate")
            .post(RequestBody.create(gson.toJson(body), JSON))
            .build();

        return executeRequest(request);
    }

    boolean deactivate(String licenseKey) {
        log("Deactivating license");

        Map<String, String> body = new HashMap<>();
        body.put("licenseKey", licenseKey);
        body.put("machineId", machineId);

        Request request = createRequest("/api/v1/deactivate")
            .post(RequestBody.create(gson.toJson(body), JSON))
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (IOException e) {
            log("Deactivation failed: " + e.getMessage());
            return false;
        }
    }

    LicenseResult startTrial(String email) {
        log("Starting trial for: " + email);

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("machineId", machineId);

        Request request = createRequest("/api/v1/trial")
            .post(RequestBody.create(gson.toJson(body), JSON))
            .build();

        return executeRequest(request);
    }

    List<ProductTier> getTiers() {
        log("Fetching product tiers");

        Request request = createRequest("/api/v1/tiers")
            .get()
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String json = response.body().string();
                Type type = new TypeToken<Map<String, List<ProductTier>>>(){}.getType();
                Map<String, List<ProductTier>> result = gson.fromJson(json, type);
                return result.getOrDefault("tiers", Collections.emptyList());
            }
        } catch (IOException e) {
            log("Failed to fetch tiers: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    CheckoutResult startCheckout(String tierId, String email) {
        log("Starting checkout for tier: " + tierId);

        Map<String, String> body = new HashMap<>();
        body.put("tierId", tierId);
        body.put("email", email);

        Request request = createRequest("/api/v1/checkout")
            .post(RequestBody.create(gson.toJson(body), JSON))
            .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String json = response.body() != null ? response.body().string() : "{}";
            if (response.isSuccessful()) {
                CheckoutResult result = gson.fromJson(json, CheckoutResult.class);
                result.setSuccess(true);
                return result;
            } else {
                Map<String, String> errorResponse = gson.fromJson(json,
                    new TypeToken<Map<String, String>>(){}.getType());
                return CheckoutResult.failure(errorResponse.getOrDefault("error", "Checkout failed"));
            }
        } catch (IOException e) {
            return CheckoutResult.failure(e.getMessage());
        }
    }

    private LicenseResult executeRequest(Request request) {
        try (Response response = httpClient.newCall(request).execute()) {
            String json = response.body() != null ? response.body().string() : "{}";
            if (response.isSuccessful()) {
                return gson.fromJson(json, LicenseResult.class);
            } else {
                Map<String, String> errorResponse = gson.fromJson(json,
                    new TypeToken<Map<String, String>>(){}.getType());
                return LicenseResult.failure(errorResponse.getOrDefault("error", "Request failed"));
            }
        } catch (IOException e) {
            return LicenseResult.failure(e.getMessage());
        }
    }
}
