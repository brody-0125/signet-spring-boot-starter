package work.brodykim.signet.baking;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Test helper for generating minimal badge images.
 */
final class TestImageGenerator {

    private TestImageGenerator() {}

    /** Generate a minimal valid PNG of the given dimensions. */
    static byte[] createMinimalPng(int width, int height) {
        try {
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.BLUE);
            g.fillOval(0, 0, width, height);
            g.dispose();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create test PNG", e);
        }
    }

    /** Generate a minimal valid SVG of the given dimensions. */
    static byte[] createMinimalSvg(int width, int height) {
        String svg = """
            <?xml version="1.0" encoding="UTF-8"?>
            <svg xmlns="http://www.w3.org/2000/svg"
                 width="%d" height="%d" viewBox="0 0 %d %d">
              <circle cx="%d" cy="%d" r="%d" fill="#4A90D9"/>
            </svg>
            """.formatted(width, height, width, height,
                          width / 2, height / 2, Math.min(width, height) / 2 - 5);
        return svg.getBytes(StandardCharsets.UTF_8);
    }

    /** Sample Data Integrity credential JSON for testing. */
    static String sampleDataIntegrityCredential() {
        return """
            {"@context":["https://www.w3.org/ns/credentials/v2","https://purl.imsglobal.org/spec/ob/v3p0/context-3.0.3.json"],"id":"urn:uuid:test-credential","type":["VerifiableCredential","OpenBadgeCredential"],"issuer":{"id":"https://example.com/issuers/1","type":["Profile"],"name":"Test Issuer"},"credentialSubject":{"type":["AchievementSubject"],"achievement":{"id":"https://example.com/achievements/1","type":["Achievement"],"name":"Test Badge","criteria":{"narrative":"Completed testing"}}},"proof":{"type":"DataIntegrityProof","cryptosuite":"eddsa-rdfc-2022","created":"2024-01-01T00:00:00Z","verificationMethod":"https://example.com/issuers/1#key-1","proofPurpose":"assertionMethod","proofValue":"zTestProofValue"}}""";
    }

    /** Sample JWS compact serialization for testing. */
    static String sampleJwsCredential() {
        return "eyJhbGciOiJFZERTQSIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL2V4YW1wbGUuY29tIn0.signature";
    }
}
