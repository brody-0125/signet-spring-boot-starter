package work.brodykim.signet.baking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CredentialFormatTest {

    @Test
    void detect_jsonString_returnsDataIntegrity() throws BadgeBakingException {
        assertEquals(CredentialFormat.DATA_INTEGRITY,
            CredentialFormat.detect("{\"@context\":[\"https://www.w3.org/ns/credentials/v2\"]}"));
    }

    @Test
    void detect_jwsString_returnsJws() throws BadgeBakingException {
        assertEquals(CredentialFormat.JWS,
            CredentialFormat.detect(TestImageGenerator.sampleJwsCredential()));
    }

    @Test
    void detect_emptyString_throwsBadgeBakingException() {
        assertThrows(BadgeBakingException.class, () -> CredentialFormat.detect(""));
    }

    @Test
    void detect_nullString_throwsBadgeBakingException() {
        assertThrows(BadgeBakingException.class, () -> CredentialFormat.detect(null));
    }

    @Test
    void detect_unrecognizedFormat_throwsBadgeBakingException() {
        assertThrows(BadgeBakingException.class, () -> CredentialFormat.detect("just some text"));
    }
}
