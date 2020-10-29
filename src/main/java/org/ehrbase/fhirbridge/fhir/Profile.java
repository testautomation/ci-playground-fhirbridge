package org.ehrbase.fhirbridge.fhir;

import org.hl7.fhir.r4.model.ResourceType;

import java.util.Objects;

/**
 * List of profiles supported by the FHIR Bridge
 */
public enum Profile {

    // DiagnosticReport Profiles

    DIAGNOSTIC_REPORT_LAB("https://www.medizininformatik-initiative.de/fhir/core/StructureDefinition/DiagnosticReportLab", ResourceType.DiagnosticReport),

    // Observation Profiles

    BODY_TEMP("http://hl7.org/fhir/StructureDefinition/bodytemp", ResourceType.Observation),

    FIO2("https://www.netzwerk-universitaetsmedizin.de/fhir/StructureDefinition/FiO2", ResourceType.Observation),

    BLOOD_PRESSURE("https://www.netzwerk-universitaetsmedizin.de/fhir/StructureDefinition/blood-pressure", ResourceType.Observation),

    HEART_RATE("http://hl7.org/fhir/StructureDefinition/heartrate", ResourceType.Observation),

    CORONARIRUS_NACHWEIS_TEST("https://charite.infectioncontrol.de/fhir/core/StructureDefinition/CoronavirusNachweisTest", ResourceType.Observation),

    OBSERVATION_LAB("https://www.medizininformatik-initiative.de/fhir/core/StructureDefinition/ObservationLab", ResourceType.Observation),

    SOFA_SCORE("https://www.netzwerk-universitaetsmedizin.de/fhir/StructureDefinition/sofa-score", ResourceType.Observation);


    private final String url;

    private final ResourceType type;

    Profile(String url, ResourceType type) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public ResourceType getType() {
        return type;
    }

    @Override
    public String toString() {
        return url;
    }

    public static Profile resolve(String url) {
        for (Profile profile : values()) {
            if (Objects.equals(profile.url, url)) {
                return profile;
            }
        }
        return null;
    }
}
