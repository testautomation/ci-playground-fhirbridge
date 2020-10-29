package org.ehrbase.fhirbridge.fhir.provider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.ehrbase.client.openehrclient.VersionUid;
import org.ehrbase.fhirbridge.fhir.Profile;
import org.ehrbase.fhirbridge.fhir.ProfileUtils;
import org.ehrbase.fhirbridge.fhir.audit.AuditService;
import org.ehrbase.fhirbridge.mapping.FhirDiagnosticReportOpenehrLabResults;
import org.ehrbase.fhirbridge.opt.laborbefundcomposition.LaborbefundComposition;
import org.ehrbase.fhirbridge.rest.EhrbaseService;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Resource provider for DiagnosticReport
 */
@Component
public class DiagnosticReportResourceProvider extends AbstractResourceProvider {

    private final Logger logger = LoggerFactory.getLogger(DiagnosticReportResourceProvider.class);

    private final IFhirResourceDao<DiagnosticReport> diagnosticReportDao;

    public DiagnosticReportResourceProvider(FhirContext fhirContext, EhrbaseService ehrbaseService, AuditService auditService,
                                            IFhirResourceDao<DiagnosticReport> diagnosticReportDao) {
        super(fhirContext, ehrbaseService, auditService);
        this.diagnosticReportDao = diagnosticReportDao;
    }

    @Create
    public MethodOutcome createDiagnosticReport(@ResourceParam DiagnosticReport diagnosticReport) {
        checkProfiles(diagnosticReport);

        diagnosticReportDao.create(diagnosticReport);
        auditService.registerCreateResourceSuccessEvent(diagnosticReport);

        logger.info(">>>>>>>>>>>>>>>>>> DIAGNOSTIC REPORT {}", diagnosticReport.getIdentifier().get(0).getValue());
        logger.info(">>>>>>>>>>>>>>>>>> CONTAINED {}", diagnosticReport.getContained().size());
        logger.info(">>>>>>>>>>>>>>>>>> PATIENT {}", diagnosticReport.getSubject().getReference()); // Patient/XXXX

        // will throw exceptions and block the request if the patient doesn't have an EHR
        UUID ehrUid = getEhrUidForSubjectId(diagnosticReport.getSubject().getReference().split(":")[2]);

        if (ProfileUtils.hasProfile(diagnosticReport, Profile.DIAGNOSTIC_REPORT_LAB)) {
            try {
                LaborbefundComposition composition = FhirDiagnosticReportOpenehrLabResults.map(diagnosticReport);
                //UUID ehr_id = service.createEhr(); // <<< reflections error!
                VersionUid versionUid = ehrbaseService.saveLab(ehrUid, composition);
                logger.info("Composition created with UID {} for FHIR profile {}", versionUid, Profile.DIAGNOSTIC_REPORT_LAB);
                auditService.registerMapResourceEvent(AuditEvent.AuditEventOutcome._0, "Success", diagnosticReport);
            } catch (Exception e) {
                auditService.registerMapResourceEvent(AuditEvent.AuditEventOutcome._8, e.getMessage(), diagnosticReport);
                throw new UnprocessableEntityException("There was a problem saving the composition" + e.getMessage(), e);
            }
        }

        return new MethodOutcome()
                .setCreated(true)
                .setResource(diagnosticReport);
    }

    @Override
    public Class<DiagnosticReport> getResourceType() {
        return DiagnosticReport.class;
    }

    @Override
    public boolean isDefaultProfileSupported() {
        return false;
    }
}
