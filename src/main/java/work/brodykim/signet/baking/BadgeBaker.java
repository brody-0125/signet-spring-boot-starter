package work.brodykim.signet.baking;

/**
 * Embeds and extracts Open Badges 3.0 credential data in badge images.
 *
 * <p>Implementations handle a specific image format (PNG or SVG).
 * Use {@link CompositeBadgeBaker} for automatic format detection and delegation.
 */
public interface BadgeBaker {

    /**
     * The image format this baker handles.
     */
    ImageFormat supportedFormat();

    /**
     * Bake a credential into an image.
     *
     * @param imageData      the source image bytes
     * @param credentialData the signed credential string (JSON-LD or JWS)
     * @return the baked image bytes
     * @throws BadgeBakingException if the image is invalid or baking fails
     */
    byte[] bake(byte[] imageData, String credentialData) throws BadgeBakingException;

    /**
     * Extract credential data from a baked image.
     *
     * @param imageData the baked image bytes
     * @return the credential string, or {@code null} if the image is not baked
     * @throws BadgeBakingException if the image is invalid or extraction fails
     */
    String extract(byte[] imageData) throws BadgeBakingException;

    /**
     * Check whether an image contains baked credential data.
     *
     * @param imageData the image bytes to check
     * @return {@code true} if the image contains an embedded credential
     */
    default boolean isBaked(byte[] imageData) {
        try {
            return extract(imageData) != null;
        } catch (BadgeBakingException e) {
            return false;
        }
    }
}
