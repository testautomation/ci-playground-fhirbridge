package org.ehrbase.fhirbridge.fhir.audit;

import ca.uhn.fhir.model.api.annotation.DatatypeDef;
import org.hl7.fhir.r4.model.Coding;

@DatatypeDef(name="Coding")
public class AuditEventEntityType extends Coding {

    public static final AuditEventEntityType CONDITION = new AuditEventEntityType("Condition");

    public static final AuditEventEntityType DIAGNOSTIC_REPORT = new AuditEventEntityType("DiagnosticReport");

    public static final AuditEventEntityType OBSERVATION = new AuditEventEntityType("Observation");

    public static final AuditEventEntityType PROCEDURE = new AuditEventEntityType("Procedure");

    public static final AuditEventEntityType QUESTIONNAIRE_RESPONSE = new AuditEventEntityType("QuestionnaireResponse");

    public AuditEventEntityType(String entityType) {
        super("http://hl7.org/fhir/resource-types", entityType, entityType);
    }
}
