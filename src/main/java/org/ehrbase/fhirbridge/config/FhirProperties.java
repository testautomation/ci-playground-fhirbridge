package org.ehrbase.fhirbridge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "fhir-bridge.fhir")
public class FhirProperties {

    private boolean narrativeGeneration;

    private String urlMapping;

    private final Validation validation = new Validation();

    public boolean isNarrativeGeneration() {
        return narrativeGeneration;
    }

    public void setNarrativeGeneration(boolean narrativeGeneration) {
        this.narrativeGeneration = narrativeGeneration;
    }

    public String getUrlMapping() {
        return urlMapping;
    }

    public void setUrlMapping(String urlMapping) {
        this.urlMapping = urlMapping;
    }

    public Validation getValidation() {
        return validation;
    }

    public static class Validation {

        private final Terminology terminology = new Terminology();

        public Terminology getTerminology() {
            return terminology;
        }
    }

    public static class Terminology {

        private TerminologyMode mode;

        private final Remote remote = new Remote();

        public TerminologyMode getMode() {
            return mode;
        }

        public void setMode(TerminologyMode mode) {
            this.mode = mode;
        }

        public Remote getRemote() {
            return remote;
        }
    }

    public static class Remote {

        private String serverUrl;

        public String getServerUrl() {
            return serverUrl;
        }

        public void setServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
        }
    }
}
