package org.ehrbase.fhirbridge.fhir.questionnaireresponse;

import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.openehealth.ipf.commons.ihe.fhir.audit.GenericFhirAuditStrategy;
import org.openehealth.ipf.commons.ihe.fhir.support.OperationOutcomeOperations;

import java.util.Optional;

public class CreateQuestionnaireResponseAuditStrategy extends GenericFhirAuditStrategy<QuestionnaireResponse> {

    public CreateQuestionnaireResponseAuditStrategy(boolean serverSide) {
        super(serverSide, OperationOutcomeOperations.INSTANCE, questionnaireResponse -> {
            if (questionnaireResponse.hasSubject()) {
                return Optional.of(questionnaireResponse.getSubject());
            } else {
                return Optional.empty();
            }
        });
    }
}
