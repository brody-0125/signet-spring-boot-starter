package work.brodykim.signet.baking;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for badge baking.
 *
 * <p>Prefix: {@code openbadges.baking}
 */
@ConfigurationProperties(prefix = "openbadges.baking")
public class BadgeBakingProperties {

    /**
     * Enable badge baking support (default: true).
     */
    private boolean enabled = true;

    /**
     * Maximum image file size in bytes before baking (default: 1 MB = 1048576).
     */
    private long maxImageSizeBytes = 1_048_576L;

    /**
     * Minimum image dimension in pixels for PNG (default: 90).
     */
    private int minDimension = 90;

    /**
     * Maximum image dimension in pixels for PNG (default: 4096).
     */
    private int maxDimension = 4096;

    /**
     * Whether to overwrite existing credential data when baking (default: true).
     */
    private boolean overwriteExisting = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getMaxImageSizeBytes() {
        return maxImageSizeBytes;
    }

    public void setMaxImageSizeBytes(long maxImageSizeBytes) {
        this.maxImageSizeBytes = maxImageSizeBytes;
    }

    public int getMinDimension() {
        return minDimension;
    }

    public void setMinDimension(int minDimension) {
        this.minDimension = minDimension;
    }

    public int getMaxDimension() {
        return maxDimension;
    }

    public void setMaxDimension(int maxDimension) {
        this.maxDimension = maxDimension;
    }

    public boolean isOverwriteExisting() {
        return overwriteExisting;
    }

    public void setOverwriteExisting(boolean overwriteExisting) {
        this.overwriteExisting = overwriteExisting;
    }
}
