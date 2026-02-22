package work.brodykim.signet.baking;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ImageFormatDetectorTest {

    @Test
    void detect_pngMagicBytes_returnsPng() throws BadgeBakingException {
        byte[] png = TestImageGenerator.createMinimalPng(100, 100);
        assertEquals(ImageFormat.PNG, ImageFormatDetector.detect(png));
    }

    @Test
    void detect_svgWithXmlDeclaration_returnsSvg() throws BadgeBakingException {
        byte[] svg = TestImageGenerator.createMinimalSvg(200, 200);
        assertEquals(ImageFormat.SVG, ImageFormatDetector.detect(svg));
    }

    @Test
    void detect_svgWithoutXmlDeclaration_returnsSvg() throws BadgeBakingException {
        String svgNoDecl = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"100\" height=\"100\"><circle/></svg>";
        assertEquals(ImageFormat.SVG, ImageFormatDetector.detect(svgNoDecl.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void detect_unknownFormat_throwsBadgeBakingException() {
        byte[] unknown = "This is just plain text".getBytes(StandardCharsets.UTF_8);
        assertThrows(BadgeBakingException.class, () -> ImageFormatDetector.detect(unknown));
    }

    @Test
    void detect_nullData_throwsBadgeBakingException() {
        assertThrows(BadgeBakingException.class, () -> ImageFormatDetector.detect(null));
    }

    @Test
    void detect_emptyData_throwsBadgeBakingException() {
        assertThrows(BadgeBakingException.class, () -> ImageFormatDetector.detect(new byte[0]));
    }
}
