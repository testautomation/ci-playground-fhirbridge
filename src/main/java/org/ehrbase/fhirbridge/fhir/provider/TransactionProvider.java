package org.ehrbase.fhirbridge.fhir.provider;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;
import org.ehrbase.fhirbridge.fhir.audit.AuditService;
import org.ehrbase.fhirbridge.rest.EhrbaseService;
import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionProvider extends AbstractResourceProvider {

    private final Logger logger = LoggerFactory.getLogger(TransactionProvider.class);

    @Autowired
    public TransactionProvider(FhirContext fhirContext, EhrbaseService ehrbaseService, AuditService auditService) {
        super(fhirContext, ehrbaseService, auditService);
    }

    @Transaction
    public Bundle transaction(@TransactionParam Bundle tx) {
        logger.info("Bundle.id={}", tx.getId());
        //Bundle result = new Bundle();
        //return result;
        return tx;
    }

    @Override
    public Class<Bundle> getResourceType() {
        return Bundle.class;
    }
}
