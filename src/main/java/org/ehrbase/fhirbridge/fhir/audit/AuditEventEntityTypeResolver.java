package org.ehrbase.fhirbridge.fhir.audit;

import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Procedure;
import org.hl7.fhir.r4.model.QuestionnaireResponse;

import java.util.HashMap;

public class AuditEventEntityTypeResolver {

    private static final HashMap<Class<?>, AuditEventEntityType> map = new HashMap<>();

    static {
        map.put(Condition.class, AuditEventEntityType.CONDITION);
        map.put(DiagnosticReport.class, AuditEventEntityType.DIAGNOSTIC_REPORT);
        map.put(Observation.class, AuditEventEntityType.OBSERVATION);
        map.put(Procedure.class, AuditEventEntityType.PROCEDURE);
        map.put(QuestionnaireResponse.class, AuditEventEntityType.QUESTIONNAIRE_RESPONSE);
    }

    public AuditEventEntityType resolve(Class<?> clazz) {
        AuditEventEntityType entityType = map.get(clazz);
        if (entityType == null) {
            throw new IllegalArgumentException("Unsupported type '" + clazz.getName() + "'");
        }
        return entityType;
    }
}
