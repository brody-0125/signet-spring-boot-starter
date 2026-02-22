package work.brodykim.signet.baking;

/**
 * Composite {@link BadgeBaker} that detects image format automatically
 * and delegates to the appropriate format-specific implementation.
 *
 * <p>This is the primary entry point for applications using badge baking.
 */
public class CompositeBadgeBaker {

    private final PngBadgeBaker pngBaker;
    private final SvgBadgeBaker svgBaker;

    public CompositeBadgeBaker(PngBadgeBaker pngBaker, SvgBadgeBaker svgBaker) {
        this.pngBaker = pngBaker;
        this.svgBaker = svgBaker;
    }

    /**
     * Bake a credential into an image, auto-detecting the image format.
     *
     * @param imageData      the source image bytes (PNG or SVG)
     * @param credentialData the signed credential (JSON-LD or JWS string)
     * @return the baked image result
     */
    public BakedBadgeResult bake(byte[] imageData, String credentialData)
            throws BadgeBakingException {
        ImageFormat format = ImageFormatDetector.detect(imageData);
        BadgeBaker baker = selectBaker(format);
        byte[] baked = baker.bake(imageData, credentialData);
        return new BakedBadgeResult(baked, format, credentialData);
    }

    /**
     * Bake a credential into an image of a specified format.
     */
    public BakedBadgeResult bake(byte[] imageData, String credentialData,
                                 ImageFormat format) throws BadgeBakingException {
        BadgeBaker baker = selectBaker(format);
        byte[] baked = baker.bake(imageData, credentialData);
        return new BakedBadgeResult(baked, format, credentialData);
    }

    /**
     * Extract credential data from a baked image, auto-detecting the format.
     *
     * @param imageData the baked image bytes
     * @return the extracted credential string, or {@code null} if not baked
     */
    public String extract(byte[] imageData) throws BadgeBakingException {
        ImageFormat format = ImageFormatDetector.detect(imageData);
        BadgeBaker baker = selectBaker(format);
        return baker.extract(imageData);
    }

    /**
     * Check whether an image contains baked credential data.
     */
    public boolean isBaked(byte[] imageData) {
        try {
            ImageFormat format = ImageFormatDetector.detect(imageData);
            BadgeBaker baker = selectBaker(format);
            return baker.isBaked(imageData);
        } catch (BadgeBakingException e) {
            return false;
        }
    }

    /**
     * Detect the image format of the given data.
     */
    public ImageFormat detectFormat(byte[] imageData) throws BadgeBakingException {
        return ImageFormatDetector.detect(imageData);
    }

    private BadgeBaker selectBaker(ImageFormat format) throws BadgeBakingException {
        return switch (format) {
            case PNG -> pngBaker;
            case SVG -> svgBaker;
        };
    }
}
