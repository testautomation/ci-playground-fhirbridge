package org.ehrbase.fhirbridge.config;

public enum TerminologyMode {

    /**
     * Validates codes using the embedded mechanism provided by HAPI FHIR.
     */
    EMBEDDED,

    /**
     * Validates codes using a remote FHIR-based terminology server.
     */
    REMOTE,

    /**
     * Disable codes validation.
     */
    OFF
}
