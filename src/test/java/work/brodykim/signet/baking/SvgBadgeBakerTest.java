package work.brodykim.signet.baking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class SvgBadgeBakerTest {

    private BadgeBakingProperties properties;
    private SvgBadgeBaker baker;

    @BeforeEach
    void setUp() {
        properties = new BadgeBakingProperties();
        baker = new SvgBadgeBaker(properties);
    }

    // --- Baking tests ---

    @Test
    void bake_validSvgAndDataIntegrityCredential_addsCdataElement() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();

        byte[] baked = baker.bake(svg, credential);
        String bakedStr = new String(baked, StandardCharsets.UTF_8);

        assertTrue(bakedStr.contains("openbadges:credential"));
        assertTrue(bakedStr.contains("CDATA"));
        assertTrue(bakedStr.contains("openbadgecredential") || bakedStr.contains(credential.substring(0, 30)));
    }

    @Test
    void bake_validSvgAndJwsCredential_addsVerifyAttribute() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        String jws = TestImageGenerator.sampleJwsCredential();

        byte[] baked = baker.bake(svg, jws);
        String bakedStr = new String(baked, StandardCharsets.UTF_8);

        assertTrue(bakedStr.contains("verify=\""));
        assertTrue(bakedStr.contains(jws));
    }

    @Test
    void bake_addsNamespaceDeclaration() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();

        byte[] baked = baker.bake(svg, credential);
        String bakedStr = new String(baked, StandardCharsets.UTF_8);

        assertTrue(bakedStr.contains("xmlns:openbadges=\"https://purl.imsglobal.org/ob/v3p0\""));
    }

    @Test
    void bake_alreadyBakedSvg_overwritesWhenConfigured() throws BadgeBakingException {
        properties.setOverwriteExisting(true);
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        String cred1 = TestImageGenerator.sampleDataIntegrityCredential();
        String cred2 = TestImageGenerator.sampleJwsCredential();

        byte[] baked1 = baker.bake(svg, cred1);
        byte[] baked2 = baker.bake(baked1, cred2);
        String extracted = baker.extract(baked2);

        assertEquals(cred2, extracted);
    }

    @Test
    void bake_alreadyBakedSvg_throwsWhenOverwriteDisabled() throws BadgeBakingException {
        properties.setOverwriteExisting(false);
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();

        byte[] baked = baker.bake(svg, credential);

        assertThrows(BadgeBakingException.class, () -> baker.bake(baked, credential));
    }

    @Test
    void bake_nonSvgXml_throwsBadgeBakingException() {
        String nonSvg = "<?xml version=\"1.0\"?><html><body/></html>";
        assertThrows(BadgeBakingException.class,
            () -> baker.bake(nonSvg.getBytes(StandardCharsets.UTF_8),
                TestImageGenerator.sampleDataIntegrityCredential()));
    }

    @Test
    void bake_exceedsMaxFileSize_throwsBadgeBakingException() {
        properties.setMaxImageSizeBytes(10); // very small limit
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        assertThrows(BadgeBakingException.class,
            () -> baker.bake(svg, TestImageGenerator.sampleDataIntegrityCredential()));
    }

    // --- Extraction tests ---

    @Test
    void extract_bakedSvgWithCdata_returnsJsonCredential() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();

        byte[] baked = baker.bake(svg, credential);
        String extracted = baker.extract(baked);

        assertEquals(credential.trim(), extracted);
    }

    @Test
    void extract_bakedSvgWithVerifyAttribute_returnsJwsString() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        String jws = TestImageGenerator.sampleJwsCredential();

        byte[] baked = baker.bake(svg, jws);
        String extracted = baker.extract(baked);

        assertEquals(jws, extracted);
    }

    @Test
    void extract_nonBakedSvg_returnsNull() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        assertNull(baker.extract(svg));
    }

    // --- Round-trip tests ---

    @Test
    void roundTrip_bakeAndExtract_dataIntegrity_credentialPreserved() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();

        byte[] baked = baker.bake(svg, credential);
        String extracted = baker.extract(baked);

        assertEquals(credential.trim(), extracted);
    }

    @Test
    void roundTrip_bakeAndExtract_jws_credentialPreserved() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        String jws = TestImageGenerator.sampleJwsCredential();

        byte[] baked = baker.bake(svg, jws);
        String extracted = baker.extract(baked);

        assertEquals(jws, extracted);
    }

    @Test
    void roundTrip_originalSvgContentPreserved() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();

        byte[] baked = baker.bake(svg, credential);
        String bakedStr = new String(baked, StandardCharsets.UTF_8);

        // Original SVG content should still be present
        assertTrue(bakedStr.contains("<circle"));
        assertTrue(bakedStr.contains("fill=\"#4A90D9\""));
    }
}
