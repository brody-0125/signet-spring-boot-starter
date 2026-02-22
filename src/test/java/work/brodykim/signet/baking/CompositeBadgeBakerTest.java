package work.brodykim.signet.baking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompositeBadgeBakerTest {

    private CompositeBadgeBaker baker;

    @BeforeEach
    void setUp() {
        BadgeBakingProperties properties = new BadgeBakingProperties();
        PngBadgeBaker pngBaker = new PngBadgeBaker(properties);
        SvgBadgeBaker svgBaker = new SvgBadgeBaker(properties);
        baker = new CompositeBadgeBaker(pngBaker, svgBaker);
    }

    @Test
    void bake_autoDetectsPng_delegatesToPngBaker() throws BadgeBakingException {
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();

        BakedBadgeResult result = baker.bake(png, credential);

        assertEquals(ImageFormat.PNG, result.format());
        assertEquals("image/png", result.mediaType());
        assertEquals(".png", result.fileExtension());
        assertNotNull(result.imageData());
    }

    @Test
    void bake_autoDetectsSvg_delegatesToSvgBaker() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();

        BakedBadgeResult result = baker.bake(svg, credential);

        assertEquals(ImageFormat.SVG, result.format());
        assertEquals("image/svg+xml", result.mediaType());
        assertEquals(".svg", result.fileExtension());
    }

    @Test
    void extract_autoDetectsPng_delegatesToPngBaker() throws BadgeBakingException {
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();
        BakedBadgeResult baked = baker.bake(png, credential);

        String extracted = baker.extract(baked.imageData());

        assertEquals(credential, extracted);
    }

    @Test
    void extract_autoDetectsSvg_delegatesToSvgBaker() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        String credential = TestImageGenerator.sampleDataIntegrityCredential();
        BakedBadgeResult baked = baker.bake(svg, credential);

        String extracted = baker.extract(baked.imageData());

        assertEquals(credential.trim(), extracted);
    }

    @Test
    void isBaked_returnsTrueForBakedImage() throws BadgeBakingException {
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        BakedBadgeResult baked = baker.bake(png, TestImageGenerator.sampleDataIntegrityCredential());

        assertTrue(baker.isBaked(baked.imageData()));
    }

    @Test
    void isBaked_returnsFalseForNonBakedImage() {
        byte[] png = TestImageGenerator.createMinimalPng(200, 200);
        assertFalse(baker.isBaked(png));
    }

    @Test
    void isBaked_returnsFalseForInvalidData() {
        assertFalse(baker.isBaked("not an image".getBytes()));
    }

    @Test
    void detectFormat_png_returnsPng() throws BadgeBakingException {
        byte[] png = TestImageGenerator.createMinimalPng(100, 100);
        assertEquals(ImageFormat.PNG, baker.detectFormat(png));
    }

    @Test
    void detectFormat_svg_returnsSvg() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(100, 100);
        assertEquals(ImageFormat.SVG, baker.detectFormat(svg));
    }
}
