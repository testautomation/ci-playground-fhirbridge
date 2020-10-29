package org.ehrbase.fhirbridge.fhir.audit;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import org.hl7.fhir.r4.model.Coding;

@DatatypeDef(name="Coding")
public class AuditEventType extends Coding {

    /**
     * Execution of a RESTful operation as defined by FHIR.
     */
    public static final AuditEventType REST = new AuditEventType("http://terminology.hl7.org/CodeSystem/audit-event-type", "rest", "RESTful Operation");

    /**
     * Occurs when an agent causes the system to change the form, language or code system used to represent record entry content.
     */
    public static final AuditEventType TRANSFORM = new AuditEventType("http://terminology.hl7.org/CodeSystem/iso-21089-lifecycle", "transform", "Transform/Translate Record Lifecycle Event");

    public AuditEventType(String theSystem, String theCode, String theDisplay) {
        super(theSystem, theCode, theDisplay);
    }
}
