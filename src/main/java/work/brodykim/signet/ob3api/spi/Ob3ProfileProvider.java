package work.brodykim.signet.ob3api.spi;

import java.util.Map;

/**
 * SPI for profile storage used by OB 3.0 API endpoints.
 *
 * <p>Implement this interface in the consuming application to provide
 * persistence for user profiles accessed via the OB 3.0 API.</p>
 */
public interface Ob3ProfileProvider {

    /**
     * Get the profile for the given user.
     *
     * @param userId the authenticated user's identifier
     * @return the profile as a JSON-LD map, or null if not found
     */
    Map<String, Object> getProfile(String userId);

    /**
     * Update the profile for the given user.
     *
     * @param userId      the authenticated user's identifier
     * @param profileData the updated profile data
     * @return the updated profile as a JSON-LD map
     */
    Map<String, Object> updateProfile(String userId, Map<String, Object> profileData);
}
