package work.brodykim.signet.ob3api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds the OB 3.0 Service Description Document (OpenAPI 3.0 fragment).
 */
public final class ServiceDescriptionBuilder {

    private ServiceDescriptionBuilder() {
    }

    /**
     * Build the Service Description Document.
     *
     * @param serviceName        display name of the service
     * @param serviceDescription description of the service
     * @param termsOfServiceUrl  URL to terms of service
     * @param privacyPolicyUrl   URL to privacy policy
     * @param tokenUrl           OAuth 2.0 token endpoint URL
     * @param authorizationUrl   OAuth 2.0 authorization endpoint URL
     * @param registrationUrl    Dynamic Client Registration endpoint URL
     * @param refreshUrl         OAuth 2.0 refresh token URL (typically same as tokenUrl)
     * @param imageUrl           optional service image URL (may be null)
     * @return the SDD as a map suitable for JSON serialization
     */
    public static Map<String, Object> build(
            String serviceName,
            String serviceDescription,
            String termsOfServiceUrl,
            String privacyPolicyUrl,
            String tokenUrl,
            String authorizationUrl,
            String registrationUrl,
            String refreshUrl,
            String imageUrl
    ) {
        var sdd = new LinkedHashMap<String, Object>();
        sdd.put("openapi", "3.0.1");

        // info
        var info = new LinkedHashMap<String, Object>();
        info.put("title", serviceName);
        info.put("description", serviceDescription);
        info.put("termsOfService", termsOfServiceUrl);
        info.put("version", "1.0");
        sdd.put("info", info);

        // paths (empty per spec -- SDD only describes security)
        sdd.put("paths", Map.of());

        // components.securitySchemes
        var scopes = new LinkedHashMap<String, String>();
        scopes.put(Ob3Scopes.CREDENTIAL_READONLY,
                "Read OpenBadgeCredentials for the authenticated entity.");
        scopes.put(Ob3Scopes.CREDENTIAL_UPSERT,
                "Create or update OpenBadgeCredentials for the authenticated entity.");
        scopes.put(Ob3Scopes.PROFILE_READONLY,
                "Read the profile for the authenticated entity.");
        scopes.put(Ob3Scopes.PROFILE_UPDATE,
                "Update the profile for the authenticated entity.");

        var authCodeFlow = new LinkedHashMap<String, Object>();
        authCodeFlow.put("tokenUrl", tokenUrl);
        authCodeFlow.put("authorizationUrl", authorizationUrl);
        authCodeFlow.put("refreshUrl", refreshUrl);
        authCodeFlow.put("scopes", scopes);

        var oauth2Scheme = new LinkedHashMap<String, Object>();
        oauth2Scheme.put("type", "oauth2");
        oauth2Scheme.put("description", "OAuth 2.0 Authorization Code Grant");
        oauth2Scheme.put("x-imssf-name", serviceName);
        oauth2Scheme.put("x-imssf-privacyPolicyUrl", privacyPolicyUrl);
        oauth2Scheme.put("x-imssf-registrationUrl", registrationUrl);
        oauth2Scheme.put("x-imssf-termsOfServiceUrl", termsOfServiceUrl);
        if (imageUrl != null && !imageUrl.isBlank()) {
            oauth2Scheme.put("x-imssf-image", imageUrl);
        }
        oauth2Scheme.put("flows", Map.of("authorizationCode", authCodeFlow));

        sdd.put("components", Map.of(
                "securitySchemes", Map.of("OAuth2ACG", oauth2Scheme)
        ));

        return sdd;
    }
}
