# Signet Spring Boot Starter

Spring Boot auto-configuration starter for [Open Badges 3.0](https://www.imsglobal.org/spec/ob/v3p0). Provides ready-to-use credential signing, badge baking, and OB 3.0 API building blocks with zero boilerplate.

## Overview

This starter builds on [signet-core](../signet-core) and adds:

- **Auto-Configuration** — All core beans (`CredentialBuilder`, `CredentialSigner`, `JsonLdProcessor`, etc.) are auto-wired from `application.yml`
- **Badge Baking** — Embed credentials into PNG (iTXt chunk) and SVG (XML namespace) images per the OB 3.0 baking specification
- **OB 3.0 API Types** — Ready-made request/response types, pagination, error handling, and media types for the 1EdTech OB 3.0 REST API
- **Service Provider Interfaces (SPI)** — Implement `Ob3CredentialProvider` and `Ob3ProfileProvider` to plug in your own persistence layer

## Installation

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.brody-0125:signet-spring-boot-starter:v0.1.5")
}
```

### Gradle (Groovy DSL)

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.brody-0125:signet-spring-boot-starter:v0.1.5'
}
```

> `signet-core` is included transitively via `api` configuration — no need to declare it separately.

Version 0.1.5 includes Core 0.1.4, which adds the official Open Badges extension context to newly built credentials. Schema-validator, revocation-list and refresh-service types are therefore defined in their signed RDF. Existing signed credentials are not modified; adding a context requires signing a new credential.

Use a published semantic version from [Releases](https://github.com/brody-0125/signet-spring-boot-starter/releases). JitPack coordinates include the `v` prefix from the release tag.

To verify publication locally with Java 17+, run `./gradlew build publishToMavenLocal`.

### Releasing

Set `version` in `gradle.properties` to `MAJOR.MINOR.PATCH` and merge the reviewed change. Tag that commit as `vMAJOR.MINOR.PATCH` and push the tag. The release workflow verifies the tag against the project version, runs tests, builds the Maven publication, requests the tagged JitPack POM/JAR, and publishes a GitHub release only after those checks succeed.

Stable releases use semantic versioning: major for breaking changes, minor for compatible features, and patch for compatible fixes. Published tags must not be moved or reused. Commit-hash coordinates are reserved for unreleased development testing.

## Quick Start

### 1. Configure Properties

```yaml
# application.yml
openbadges:
  base-url: https://badges.example.com
  recipient-salt: my-secret-salt
  issuer:
    name: "My Organization"
    email: "badges@example.com"
    description: "We issue Open Badges"
  signing:
    algorithm: ED25519
    auto-generate-key: true
  baking:
    enabled: true
```

### 2. Inject and Use

```java
@Service
public class MyBadgeService {

    private final CredentialBuilder credentialBuilder;
    private final CredentialSigner credentialSigner;
    private final CompositeBadgeBaker badgeBaker;

    public MyBadgeService(
            CredentialBuilder credentialBuilder,
            CredentialSigner credentialSigner,
            CompositeBadgeBaker badgeBaker) {
        this.credentialBuilder = credentialBuilder;
        this.credentialSigner = credentialSigner;
        this.badgeBaker = badgeBaker;
    }

    public byte[] issueAndBake(CredentialRequest request, byte[] badgeImage) {
        // Build → Sign → Bake into image
        var credential = credentialBuilder.buildCredential(request);
        var signed = credentialSigner.signWithDataIntegrity(
            credential, privateKey, verificationMethodId);
        return badgeBaker.bake(badgeImage, toJson(signed)).imageData();
    }
}
```

### 3. Implement the OB 3.0 SPI

```java
@Component
public class MyCredentialProvider implements Ob3CredentialProvider {

    @Override
    public CredentialPage getCredentials(String userId, PaginationParams params) {
        // Return credentials from your data store
    }

    @Override
    public UpsertResult upsertCredential(String userId, String rawPayload, boolean isJws) {
        // Persist the credential and return the result
    }
}
```

## Configuration Reference

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `openbadges.base-url` | String | `http://localhost:8080` | Base URL for badge endpoints and JSON-LD identifiers |
| `openbadges.recipient-salt` | String | `openbadges` | Salt for recipient identity hashing |
| `openbadges.issuer.name` | String | `My Organization` | Default issuer display name |
| `openbadges.issuer.url` | String | *(falls back to base-url)* | Issuer homepage URL |
| `openbadges.issuer.email` | String | | Issuer contact email |
| `openbadges.issuer.description` | String | | Issuer description |
| `openbadges.signing.algorithm` | Enum | `ED25519` | Signing algorithm (`ED25519`) |
| `openbadges.signing.auto-generate-key` | boolean | `true` | Auto-generate key pair on startup |
| `openbadges.baking.enabled` | boolean | `true` | Enable badge baking support |
| `openbadges.baking.max-image-size-bytes` | long | `1048576` | Maximum image file size (bytes) |
| `openbadges.baking.min-dimension` | int | `90` | Minimum PNG image dimension (px) |
| `openbadges.baking.max-dimension` | int | `4096` | Maximum PNG image dimension (px) |
| `openbadges.baking.overwrite-existing` | boolean | `true` | Overwrite existing credential data in baked images |
| `openbadges.api.enabled` | boolean | `true` | Enable REST API endpoints (hint) |
| `openbadges.api.base-path` | String | `/api/v1` | REST API base path (hint) |
| `openbadges.admin-ui.enabled` | boolean | `true` | Enable admin web UI (hint) |
| `openbadges.admin-ui.base-path` | String | `/admin/badges` | Admin UI base path (hint) |

## Auto-Configured Beans

All beans are registered with `@ConditionalOnMissingBean` — declare your own to override.

| Bean | Type | Source | Condition |
|------|------|--------|-----------|
| `openBadgesValidator` | `OpenBadgesValidator` | signet-core | Always |
| `credentialBuilder` | `CredentialBuilder` | signet-core | Always |
| `cachedDocumentLoader` | `CachedDocumentLoader` | signet-core | Always |
| `jsonLdProcessor` | `JsonLdProcessor` | signet-core | Always |
| `credentialSigner` | `CredentialSigner` | signet-core | Always |
| `pngBadgeBaker` | `PngBadgeBaker` | starter | `openbadges.baking.enabled=true` |
| `svgBadgeBaker` | `SvgBadgeBaker` | starter | `openbadges.baking.enabled=true` |
| `compositeBadgeBaker` | `CompositeBadgeBaker` | starter | `openbadges.baking.enabled=true` |

## Badge Baking

Embed signed credentials directly into badge images per the OB 3.0 specification.

### Supported Formats

| Format | Embedding Method | Keyword / Namespace |
|--------|-----------------|---------------------|
| PNG | iTXt chunk | `openbadgecredential` |
| SVG | XML element | `<openbadges:credential>` in OB 3.0 namespace |

### Credential Format Detection

```java
// Auto-detect from credential string
CredentialFormat format = CredentialFormat.detect(credentialString);
// → DATA_INTEGRITY (JSON-LD with proof block)
// → JWS (compact serialization: header.payload.signature)
```

## OB 3.0 API Types

### Pagination

```java
PaginationParams params = PaginationParams.of(
    requestedLimit, requestedOffset,
    defaultPageSize, maxPageSize
);

HttpHeaders linkHeaders = PaginationLinkBuilder.build(
    baseUrl, "/credentials", params.limit(), params.offset(), totalCount
);
```

### Error Handling

```java
// Typed exceptions map to OB 3.0 IMS status codes
throw new Ob3NotFoundException("Credential not found");
// → 404 with ImsxStatusInfo { codeMajor: FAILURE, codeMinor: UNKNOWN_OBJECT }

throw new Ob3ForbiddenException("Insufficient scope");
// → 403 with ImsxStatusInfo { codeMajor: FAILURE, codeMinor: FORBIDDEN }
```

### Media Types

```java
Ob3MediaTypes.VC_LD_JSON   // application/vc+ld+json
Ob3MediaTypes.LD_JSON      // application/ld+json
```

### OAuth 2.0 Scopes

```java
Ob3Scopes.CREDENTIAL_READONLY  // .../scope/credential.readonly
Ob3Scopes.CREDENTIAL_UPSERT    // .../scope/credential.upsert
Ob3Scopes.PROFILE_READONLY     // .../scope/profile.readonly
Ob3Scopes.PROFILE_UPDATE       // .../scope/profile.update
```

## Package Structure

| Package | Description |
|---------|-------------|
| `work.brodykim.signet.autoconfigure` | Spring Boot auto-configuration and properties |
| `work.brodykim.signet.baking` | Composite badge baker, PNG/SVG bakers, format detection |
| `work.brodykim.signet.ob3api` | OB 3.0 API types, exceptions, pagination, media types |
| `work.brodykim.signet.ob3api.spi` | Service Provider Interfaces for persistence integration |

## Requirements

- Java 17+
- Spring Boot 3.x

## Specification Compliance

This starter implements:

- [Open Badges Specification v3.0](https://www.imsglobal.org/spec/ob/v3p0) by 1EdTech Consortium
- [Open Badges v3.0 Image Baking](https://www.imsglobal.org/spec/ob/v3p0#baked-open-badges) (PNG and SVG)
- [Open Badges v3.0 REST API](https://www.imsglobal.org/spec/ob/v3p0#api) types and error model

> **Note:** This implementation is not certified by 1EdTech. See the [NOTICE](NOTICE) file for full compliance details.

## License

Licensed under the [Apache License 2.0](LICENSE).
