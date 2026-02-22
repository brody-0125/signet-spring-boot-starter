package work.brodykim.signet.baking;

/**
 * Result of a badge baking operation.
 *
 * @param imageData  the baked image bytes
 * @param format     the image format (PNG or SVG)
 * @param credential the credential string that was baked in
 */
public record BakedBadgeResult(
        byte[] imageData,
        ImageFormat format,
        String credential
) {
    /**
     * HTTP-friendly media type string (e.g., "image/png" or "image/svg+xml").
     */
    public String mediaType() {
        return format.mediaType();
    }

    /**
     * Suggested file extension (e.g., ".png" or ".svg").
     */
    public String fileExtension() {
        return format.fileExtension();
    }
}
