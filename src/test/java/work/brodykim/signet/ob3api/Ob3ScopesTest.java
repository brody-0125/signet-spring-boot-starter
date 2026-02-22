package work.brodykim.signet.ob3api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Ob3ScopesTest {

    @Test
    void scopeBaseEndsWithSlash() {
        assertTrue(Ob3Scopes.SCOPE_BASE.endsWith("/"));
    }

    @Test
    void allScopesStartWithBase() {
        for (String scope : Ob3Scopes.ALL) {
            assertTrue(scope.startsWith(Ob3Scopes.SCOPE_BASE),
                    "Scope should start with base URI: " + scope);
        }
    }

    @Test
    void allSetContainsExactlyFourScopes() {
        assertEquals(4, Ob3Scopes.ALL.size());
        assertTrue(Ob3Scopes.ALL.contains(Ob3Scopes.CREDENTIAL_READONLY));
        assertTrue(Ob3Scopes.ALL.contains(Ob3Scopes.CREDENTIAL_UPSERT));
        assertTrue(Ob3Scopes.ALL.contains(Ob3Scopes.PROFILE_READONLY));
        assertTrue(Ob3Scopes.ALL.contains(Ob3Scopes.PROFILE_UPDATE));
    }

    @Test
    void scopeUrisMatchSpec() {
        assertEquals("https://purl.imsglobal.org/spec/ob/v3p0/scope/credential.readonly",
                Ob3Scopes.CREDENTIAL_READONLY);
        assertEquals("https://purl.imsglobal.org/spec/ob/v3p0/scope/credential.upsert",
                Ob3Scopes.CREDENTIAL_UPSERT);
        assertEquals("https://purl.imsglobal.org/spec/ob/v3p0/scope/profile.readonly",
                Ob3Scopes.PROFILE_READONLY);
        assertEquals("https://purl.imsglobal.org/spec/ob/v3p0/scope/profile.update",
                Ob3Scopes.PROFILE_UPDATE);
    }
}
