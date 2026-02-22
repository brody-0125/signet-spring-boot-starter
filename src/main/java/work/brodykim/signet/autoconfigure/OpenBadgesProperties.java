package work.brodykim.signet.autoconfigure;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "openbadges")
public class OpenBadgesProperties {

    /**
     * Base URL for badge endpoints (default: http://localhost:8080)
     */
    @NotBlank
    private String baseUrl = "http://localhost:8080";

    /**
     * Issuer configuration
     */
    private IssuerProperties issuer = new IssuerProperties();

    /**
     * Salt used for recipient identity hashing (default: "openbadges")
     */
    @NotBlank
    private String recipientSalt = "openbadges";

    /**
     * Signing configuration
     */
    private SigningProperties signing = new SigningProperties();

    /**
     * API endpoint configuration (hint for consuming application)
     */
    private ApiProperties api = new ApiProperties();

    /**
     * Admin UI configuration (hint for consuming application)
     */
    private AdminUiProperties adminUi = new AdminUiProperties();

    public String resolvedIssuerUrl() {
        return issuer.getUrl() != null ? issuer.getUrl() : baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public IssuerProperties getIssuer() {
        return issuer;
    }

    public void setIssuer(IssuerProperties issuer) {
        this.issuer = issuer;
    }

    public String getRecipientSalt() {
        return recipientSalt;
    }

    public void setRecipientSalt(String recipientSalt) {
        this.recipientSalt = recipientSalt;
    }

    public SigningProperties getSigning() {
        return signing;
    }

    public void setSigning(SigningProperties signing) {
        this.signing = signing;
    }

    public ApiProperties getApi() {
        return api;
    }

    public void setApi(ApiProperties api) {
        this.api = api;
    }

    public AdminUiProperties getAdminUi() {
        return adminUi;
    }

    public void setAdminUi(AdminUiProperties adminUi) {
        this.adminUi = adminUi;
    }

    public enum SigningAlgorithm {
        ED25519
    }

    public static class IssuerProperties {
        /**
         * Issuer display name (default: "My Organization")
         */
        @NotBlank
        private String name = "My Organization";
        /**
         * Issuer URL (defaults to baseUrl if not set)
         */
        private String url = null;
        /**
         * Issuer email
         */
        private String email = null;
        /**
         * Issuer description
         */
        private String description = null;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class SigningProperties {
        /**
         * Signing algorithm (default: ED25519)
         */
        private SigningAlgorithm algorithm = SigningAlgorithm.ED25519;
        /**
         * Auto-generate signing key pair on startup if none exists (default: true)
         */
        private boolean autoGenerateKey = true;

        public SigningAlgorithm getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(SigningAlgorithm algorithm) {
            this.algorithm = algorithm;
        }

        public boolean isAutoGenerateKey() {
            return autoGenerateKey;
        }

        public void setAutoGenerateKey(boolean autoGenerateKey) {
            this.autoGenerateKey = autoGenerateKey;
        }
    }

    public static class ApiProperties {
        /**
         * Enable REST API endpoints (default: true)
         */
        private boolean enabled = true;
        /**
         * API base path (default: /api/v1)
         */
        private String basePath = "/api/v1";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
    }

    public static class AdminUiProperties {
        /**
         * Enable admin web UI (default: true)
         */
        private boolean enabled = true;
        /**
         * Admin UI path prefix (default: /admin/badges)
         */
        private String basePath = "/admin/badges";

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getBasePath() {
            return basePath;
        }

        public void setBasePath(String basePath) {
            this.basePath = basePath;
        }
    }
}
