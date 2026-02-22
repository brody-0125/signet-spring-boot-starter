package work.brodykim.signet.autoconfigure;

import com.apicatalog.jsonld.loader.DocumentLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import work.brodykim.signet.baking.BadgeBakingProperties;
import work.brodykim.signet.baking.CompositeBadgeBaker;
import work.brodykim.signet.baking.PngBadgeBaker;
import work.brodykim.signet.baking.SvgBadgeBaker;
import work.brodykim.signet.core.OpenBadgesValidator;
import work.brodykim.signet.credential.CredentialBuilder;
import work.brodykim.signet.credential.CredentialSigner;
import work.brodykim.signet.jsonld.CachedDocumentLoader;
import work.brodykim.signet.jsonld.JsonLdProcessor;

@AutoConfiguration(after = JacksonAutoConfiguration.class)
@EnableConfigurationProperties({OpenBadgesProperties.class, BadgeBakingProperties.class})
public class OpenBadgesAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OpenBadgesValidator openBadgesValidator() {
        return new OpenBadgesValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public CredentialBuilder credentialBuilder(OpenBadgesProperties properties) {
        return new CredentialBuilder(
                properties.getBaseUrl(),
                properties.getRecipientSalt()
        );
    }

    @Bean
    @ConditionalOnMissingBean(DocumentLoader.class)
    public CachedDocumentLoader cachedDocumentLoader() {
        return new CachedDocumentLoader();
    }

    @Bean
    @ConditionalOnMissingBean
    public JsonLdProcessor jsonLdProcessor(DocumentLoader documentLoader) {
        return new JsonLdProcessor(documentLoader);
    }

    @Bean
    @ConditionalOnMissingBean
    public CredentialSigner credentialSigner(ObjectMapper objectMapper, JsonLdProcessor jsonLdProcessor) {
        return new CredentialSigner(objectMapper, jsonLdProcessor);
    }

    // --- Badge Baking beans ---

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "openbadges.baking", name = "enabled", matchIfMissing = true)
    public PngBadgeBaker pngBadgeBaker(BadgeBakingProperties bakingProperties) {
        return new PngBadgeBaker(bakingProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "openbadges.baking", name = "enabled", matchIfMissing = true)
    public SvgBadgeBaker svgBadgeBaker(BadgeBakingProperties bakingProperties) {
        return new SvgBadgeBaker(bakingProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "openbadges.baking", name = "enabled", matchIfMissing = true)
    public CompositeBadgeBaker compositeBadgeBaker(PngBadgeBaker pngBaker, SvgBadgeBaker svgBaker) {
        return new CompositeBadgeBaker(pngBaker, svgBaker);
    }
}
