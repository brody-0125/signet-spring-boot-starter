package work.brodykim.signet.baking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PngBadgeBakerTest {

    private BadgeBakingProperties properties;
    private PngBadgeBaker baker;

    @BeforeEach
    void setUp() {
        properties = new BadgeBakingProperties();
        baker = new PngBadgeBaker(properties);
    }

    // --- Baking tests ---

    @Test
    void bake_validPngAndDataIntegrityCredential_producesValidPng() throws BadgeBakingException {
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();

        byte[] baked = baker.bake(png, credential);

        assertNotNull(baked);
        assertTrue(baked.length > png.length);
        // Verify PNG magic bytes
        assertEquals((byte) 0x89, baked[0]);
        assertEquals((byte) 0x50, baked[1]); // 'P'
        assertEquals((byte) 0x4E, baked[2]); // 'N'
        assertEquals((byte) 0x47, baked[3]); // 'G'
    }

    @Test
    void bake_jwsCredential_embedsCompactStringInITxt() throws BadgeBakingException {
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        String jws = TestImageGenerator.sampleJwsCredential();

        byte[] baked = baker.bake(png, jws);
        String extracted = baker.extract(baked);

        assertEquals(jws, extracted);
    }

    @Test
    void bake_alreadyBakedImage_overwritesWhenConfigured() throws BadgeBakingException {
        properties.setOverwriteExisting(true);
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        String cred1 = TestImageGenerator.sampleDataIntegrityCredential();
        String cred2 = TestImageGenerator.sampleJwsCredential();

        byte[] baked1 = baker.bake(png, cred1);
        byte[] baked2 = baker.bake(baked1, cred2);
        String extracted = baker.extract(baked2);

        assertEquals(cred2, extracted);
    }

    @Test
    void bake_alreadyBakedImage_throwsWhenOverwriteDisabled() throws BadgeBakingException {
        properties.setOverwriteExisting(false);
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();

        byte[] baked = baker.bake(png, credential);

        assertThrows(BadgeBakingException.class, () -> baker.bake(baked, credential));
    }

    @Test
    void bake_invalidPngMagicBytes_throwsBadgeBakingException() {
        byte[] notPng = "not a png file".getBytes();
        assertThrows(BadgeBakingException.class,
            () -> baker.bake(notPng, TestImageGenerator.sampleDataIntegrityCredential()));
    }

    @Test
    void bake_exceedsMaxFileSize_throwsBadgeBakingException() {
        properties.setMaxImageSizeBytes(100); // very small limit
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        assertThrows(BadgeBakingException.class,
            () -> baker.bake(png, TestImageGenerator.sampleDataIntegrityCredential()));
    }

    @Test
    void bake_belowMinDimension_throwsBadgeBakingException() {
        properties.setMinDimension(90);
        byte[] tinyPng = TestImageGenerator.createMinimalPng(50, 50);
        assertThrows(BadgeBakingException.class,
            () -> baker.bake(tinyPng, TestImageGenerator.sampleDataIntegrityCredential()));
    }

    @Test
    void bake_aboveMaxDimension_throwsBadgeBakingException() {
        properties.setMaxDimension(100);
        byte[] largePng = TestImageGenerator.createMinimalPng(200, 200);
        assertThrows(BadgeBakingException.class,
            () -> baker.bake(largePng, TestImageGenerator.sampleDataIntegrityCredential()));
    }

    // --- Extraction tests ---

    @Test
    void extract_bakedPng_returnsCredentialString() throws BadgeBakingException {
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();
        byte[] baked = baker.bake(png, credential);

        String extracted = baker.extract(baked);

        assertEquals(credential, extracted);
    }

    @Test
    void extract_nonBakedPng_returnsNull() throws BadgeBakingException {
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        assertNull(baker.extract(png));
    }

    @Test
    void extract_invalidData_throwsBadgeBakingException() {
        byte[] notPng = "not a png file".getBytes();
        assertThrows(BadgeBakingException.class, () -> baker.extract(notPng));
    }

    // --- Round-trip tests ---

    @Test
    void roundTrip_bakeAndExtract_dataIntegrity_credentialPreserved() throws BadgeBakingException {
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();

        byte[] baked = baker.bake(png, credential);
        String extracted = baker.extract(baked);

        assertEquals(credential, extracted);
    }

    @Test
    void roundTrip_bakeAndExtract_jws_credentialPreserved() throws BadgeBakingException {
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        String jws = TestImageGenerator.sampleJwsCredential();

        byte[] baked = baker.bake(png, jws);
        String extracted = baker.extract(baked);

        assertEquals(jws, extracted);
    }

    // --- isBaked tests ---

    @Test
    void isBaked_bakedPng_returnsTrue() throws BadgeBakingException {
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        byte[] baked = baker.bake(png, TestImageGenerator.sampleDataIntegrityCredential());
        assertTrue(baker.isBaked(baked));
    }

    @Test
    void isBaked_nonBakedPng_returnsFalse() {
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        assertFalse(baker.isBaked(png));
    }
}
