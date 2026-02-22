package work.brodykim.signet;

import com.fasterxml.jackson.databind.ObjectMapper;
import work.brodykim.signet.autoconfigure.OpenBadgesAutoConfiguration;
import work.brodykim.signet.autoconfigure.OpenBadgesProperties;
import work.brodykim.signet.credential.CredentialBuilder;
import work.brodykim.signet.credential.CredentialSigner;
import work.brodykim.signet.jsonld.CachedDocumentLoader;
import work.brodykim.signet.jsonld.JsonLdProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.jupiter.api.Assertions.*;

class OpenBadgesAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    JacksonAutoConfiguration.class,
                    OpenBadgesAutoConfiguration.class
            ))
            .withBean(ObjectMapper.class, ObjectMapper::new);

    @Test
    void shouldAutoConfigureWithDefaults() {
        contextRunner.run(context -> {
            assertNotNull(context.getBean(OpenBadgesProperties.class));
            assertNotNull(context.getBean(CredentialBuilder.class));
            assertNotNull(context.getBean(CredentialSigner.class));
            assertNotNull(context.getBean(CachedDocumentLoader.class));
            assertNotNull(context.getBean(JsonLdProcessor.class));
        });
    }

    @Test
    void shouldUseCustomProperties() {
        contextRunner
                .withPropertyValues(
                        "openbadges.base-url=https://badges.example.com",
                        "openbadges.issuer.name=Custom Issuer",
                        "openbadges.issuer.email=custom@example.com"
                )
                .run(context -> {
                    OpenBadgesProperties props = context.getBean(OpenBadgesProperties.class);
                    assertEquals("https://badges.example.com", props.getBaseUrl());
                    assertEquals("Custom Issuer", props.getIssuer().getName());
                    assertEquals("custom@example.com", props.getIssuer().getEmail());
                });
    }
}
