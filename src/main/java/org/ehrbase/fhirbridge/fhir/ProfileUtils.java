package org.ehrbase.fhirbridge.fhir;

import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for profiles
 */
public class ProfileUtils {

    private ProfileUtils() {
    }

    public static List<Profile> getSupportedProfiles(ResourceType type) {
        return Arrays.stream(Profile.values())
                .filter(profile -> profile.getType() == type)
                .collect(Collectors.toList());
    }

    public static List<Profile> getProfiles(Resource resource) {
        return resource.getMeta().getProfile().stream()
                .map(profileUrl -> Profile.resolve(profileUrl.getValue()))
                .collect(Collectors.toList());
    }

    public static boolean hasProfile(Resource resource, Profile profile) {
        return getProfiles(resource).contains(profile);
    }
}
