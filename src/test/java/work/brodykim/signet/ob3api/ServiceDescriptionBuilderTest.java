package work.brodykim.signet.ob3api;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
class ServiceDescriptionBuilderTest {

    private Map<String, Object> buildDefault() {
        return ServiceDescriptionBuilder.build(
                "Test Service",
                "A test OB 3.0 service",
                "https://example.com/terms",
                "https://example.com/privacy",
                "https://example.com/auth/token",
                "https://example.com/auth/authorize",
                "https://example.com/auth/register",
                "https://example.com/auth/token",
                null
        );
    }

    @Test
    void hasRequiredTopLevelFields() {
        Map<String, Object> sdd = buildDefault();
        assertEquals("3.0.1", sdd.get("openapi"));
        assertNotNull(sdd.get("info"));
        assertNotNull(sdd.get("paths"));
        assertNotNull(sdd.get("components"));
    }

    @Test
    void infoContainsRequiredFields() {
        var sdd = buildDefault();
        var info = (Map<String, Object>) sdd.get("info");

        assertEquals("Test Service", info.get("title"));
        assertEquals("A test OB 3.0 service", info.get("description"));
        assertEquals("https://example.com/terms", info.get("termsOfService"));
        assertEquals("1.0", info.get("version"));
    }

    @Test
    void oauthSchemePresentWithCorrectType() {
        var sdd = buildDefault();
        var components = (Map<String, Object>) sdd.get("components");
        var schemes = (Map<String, Object>) components.get("securitySchemes");
        var oauth = (Map<String, Object>) schemes.get("OAuth2ACG");

        assertEquals("oauth2", oauth.get("type"));
        assertEquals("OAuth 2.0 Authorization Code Grant", oauth.get("description"));
    }

    @Test
    void imssfExtensionFieldsPresent() {
        var sdd = buildDefault();
        var components = (Map<String, Object>) sdd.get("components");
        var schemes = (Map<String, Object>) components.get("securitySchemes");
        var oauth = (Map<String, Object>) schemes.get("OAuth2ACG");

        assertEquals("Test Service", oauth.get("x-imssf-name"));
        assertEquals("https://example.com/privacy", oauth.get("x-imssf-privacyPolicyUrl"));
        assertEquals("https://example.com/auth/register", oauth.get("x-imssf-registrationUrl"));
        assertEquals("https://example.com/terms", oauth.get("x-imssf-termsOfServiceUrl"));
    }

    @Test
    void scopeUrisMatchSpec() {
        var sdd = buildDefault();
        var components = (Map<String, Object>) sdd.get("components");
        var schemes = (Map<String, Object>) components.get("securitySchemes");
        var oauth = (Map<String, Object>) schemes.get("OAuth2ACG");
        var flows = (Map<String, Object>) oauth.get("flows");
        var authCode = (Map<String, Object>) flows.get("authorizationCode");
        var scopes = (Map<String, String>) authCode.get("scopes");

        assertEquals(4, scopes.size());
        assertTrue(scopes.containsKey(Ob3Scopes.CREDENTIAL_READONLY));
        assertTrue(scopes.containsKey(Ob3Scopes.CREDENTIAL_UPSERT));
        assertTrue(scopes.containsKey(Ob3Scopes.PROFILE_READONLY));
        assertTrue(scopes.containsKey(Ob3Scopes.PROFILE_UPDATE));
    }

    @Test
    void oauthUrlsCorrect() {
        var sdd = buildDefault();
        var components = (Map<String, Object>) sdd.get("components");
        var schemes = (Map<String, Object>) components.get("securitySchemes");
        var oauth = (Map<String, Object>) schemes.get("OAuth2ACG");
        var flows = (Map<String, Object>) oauth.get("flows");
        var authCode = (Map<String, Object>) flows.get("authorizationCode");

        assertEquals("https://example.com/auth/token", authCode.get("tokenUrl"));
        assertEquals("https://example.com/auth/authorize", authCode.get("authorizationUrl"));
        assertEquals("https://example.com/auth/token", authCode.get("refreshUrl"));
    }

    @Test
    void imageUrlOmittedWhenNull() {
        var sdd = buildDefault();
        var components = (Map<String, Object>) sdd.get("components");
        var schemes = (Map<String, Object>) components.get("securitySchemes");
        var oauth = (Map<String, Object>) schemes.get("OAuth2ACG");

        assertFalse(oauth.containsKey("x-imssf-image"));
    }

    @Test
    void imageUrlIncludedWhenProvided() {
        var sdd = ServiceDescriptionBuilder.build(
                "Test", "Desc", "https://t.co/terms", "https://t.co/privacy",
                "https://t.co/token", "https://t.co/auth", "https://t.co/register",
                "https://t.co/token", "https://t.co/logo.png"
        );
        var components = (Map<String, Object>) sdd.get("components");
        var schemes = (Map<String, Object>) components.get("securitySchemes");
        var oauth = (Map<String, Object>) schemes.get("OAuth2ACG");

        assertEquals("https://t.co/logo.png", oauth.get("x-imssf-image"));
    }
}
