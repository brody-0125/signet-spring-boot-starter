package work.brodykim.signet.baking;

import java.nio.charset.StandardCharsets;

/**
 * Detects image format from raw bytes using magic byte signatures.
 */
public final class ImageFormatDetector {

    private static final byte[] PNG_SIGNATURE = {
            (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    private ImageFormatDetector() {
    }

    /**
     * Detect the image format from raw bytes.
     *
     * @throws BadgeBakingException if the format cannot be determined
     */
    public static ImageFormat detect(byte[] data) throws BadgeBakingException {
        if (data == null || data.length < 8) {
            throw new BadgeBakingException("Image data is null or too small to detect format");
        }

        if (matchesPngSignature(data)) {
            return ImageFormat.PNG;
        }

        if (looksLikeSvg(data)) {
            return ImageFormat.SVG;
        }

        throw new BadgeBakingException(
                "Unsupported image format. Only PNG and SVG are supported for badge baking.");
    }

    private static boolean matchesPngSignature(byte[] data) {
        if (data.length < PNG_SIGNATURE.length) return false;
        for (int i = 0; i < PNG_SIGNATURE.length; i++) {
            if (data[i] != PNG_SIGNATURE[i]) return false;
        }
        return true;
    }

    private static boolean looksLikeSvg(byte[] data) {
        int limit = Math.min(data.length, 1024);
        String header = new String(data, 0, limit, StandardCharsets.UTF_8).trim();
        return header.startsWith("<?xml") || header.startsWith("<svg")
                || header.contains("<svg");
    }
}
