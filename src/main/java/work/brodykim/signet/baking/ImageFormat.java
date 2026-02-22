package work.brodykim.signet.baking;

/**
 * Supported image formats for badge baking.
 */
public enum ImageFormat {

    PNG("image/png", ".png"),
    SVG("image/svg+xml", ".svg");

    private final String mediaType;
    private final String fileExtension;

    ImageFormat(String mediaType, String fileExtension) {
        this.mediaType = mediaType;
        this.fileExtension = fileExtension;
    }

    public String mediaType() {
        return mediaType;
    }

    public String fileExtension() {
        return fileExtension;
    }
}
