package org.ehrbase.fhirbridge.fhir.provider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.ehrbase.client.aql.query.Query;
import org.ehrbase.client.aql.record.Record1;
import org.ehrbase.client.openehrclient.VersionUid;
import org.ehrbase.fhirbridge.fhir.audit.AuditService;
import org.ehrbase.fhirbridge.mapping.FhirConditionOpenehrDiagnose;
import org.ehrbase.fhirbridge.opt.diagnosecomposition.DiagnoseComposition;
import org.ehrbase.fhirbridge.opt.shareddefinition.DerDiagnoseDefiningcode;
import org.ehrbase.fhirbridge.rest.EhrbaseService;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Resource provider for Condition
 */
@Component
public class ConditionResourceProvider extends AbstractResourceProvider {

    private final Logger logger = LoggerFactory.getLogger(ConditionResourceProvider.class);

    private final IFhirResourceDao<Condition> conditionDao;

    public ConditionResourceProvider(FhirContext fhirContext, EhrbaseService ehrbaseService, AuditService auditService,
                                     IFhirResourceDao<Condition> conditionDao) {
        super(fhirContext, ehrbaseService, auditService);
        this.conditionDao = conditionDao;
    }

    @Read
    public Condition getConditionById(@IdParam IdType identifier) {
        Condition result = new Condition();

        // identifier.getValue() is the Resource/theId

        Query<Record1<DiagnoseComposition>> query = Query.buildNativeQuery(
                "SELECT c " +
                        "FROM EHR e CONTAINS COMPOSITION c " +
                        "WHERE c/archetype_details/template_id/value = 'Diagnose' AND " +
                        "c/uid/value = '" + identifier.getIdPart() + "'",
                DiagnoseComposition.class
        );

        List<Record1<DiagnoseComposition>> results;

        try {
            results = ehrbaseService.getClient().aqlEndpoint().execute(query);

            DiagnoseComposition compo;

            if (results.isEmpty()) {
                throw new ResourceNotFoundException("Resource not found"); // causes 404
            }

            compo = results.get(0).value1();

            // COMPOSITION => FHIR Condition
            result = FhirConditionOpenehrDiagnose.map(compo);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        // ...populate...
        retVal.addIdentifier().setSystem("urn:mrns").setValue("12345");
        retVal.addName().setFamily("Smith").addGiven("Tester").addGiven("Q");
        */

        return result;
    }

    @Search
    public List<Condition> getAllConditions(
            @OptionalParam(name = "_profile") UriParam profile,
            @RequiredParam(name = Patient.SP_IDENTIFIER) TokenParam subjectId,
            @OptionalParam(name = Condition.SP_RECORDED_DATE) DateRangeParam dateRange,
            @OptionalParam(name = Condition.SP_CODE) TokenParam code
    ) {
        logger.info("SEARCH CONDITION! subjectId: {}", subjectId);
        List<Condition> result = new ArrayList<>();

        // *************************************************************************************
        // We don't have a profile to ask for, we will try to map from a Diagnose composition
        // to a general condition.
        // *************************************************************************************

        String aql =
                "SELECT c " +
                        "FROM EHR e CONTAINS COMPOSITION c " +
                        "WHERE c/archetype_details/template_id/value = 'Diagnose' AND " +
                        "e/ehr_status/subject/external_ref/id/value = '" + subjectId.getValue() + "'";

        // filters
        if (dateRange != null) {
            // with date range we can also receive just one bound
            if (dateRange.getLowerBound() != null)
                aql += " AND '" + dateRange.getLowerBound().getValueAsString() + "' <= c/context/start_time/value";

            if (dateRange.getUpperBound() != null)
                aql += " AND c/context/start_time/value <= '" + dateRange.getUpperBound().getValueAsString() + "'";
        }

        if (code != null) {
            logger.info("code {}", code.getValue());
            String openEHRDiagnosis;
            switch (code.getValue()) {
                case "B97.2":
                    openEHRDiagnosis = DerDiagnoseDefiningcode.B972.getCode();
                    break;
                case "U07.1":
                    openEHRDiagnosis = DerDiagnoseDefiningcode.U071.getCode();
                    break;
                case "U07.2":
                    openEHRDiagnosis = DerDiagnoseDefiningcode.U072.getCode();
                    break;
                case "B34.2":
                    openEHRDiagnosis = DerDiagnoseDefiningcode.B342.getCode();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + code.getValue());
            }

            aql += " AND eval/data[at0001]/items[at0002]/value/defining_code/code_string = '" + openEHRDiagnosis + "'";
            //aql += " WHERE eval/data[at0001]/items[at0002]/value/defining_code/code_string = '"+ openEHRDiagnosis +"'";
        }


        // execute the query
        Query<Record1<DiagnoseComposition>> query = Query.buildNativeQuery(aql, DiagnoseComposition.class);

        List<Record1<DiagnoseComposition>> results;

        try {
            results = ehrbaseService.getClient().aqlEndpoint().execute(query);

            DiagnoseComposition compo;
            Condition condition;

            for (Record1<DiagnoseComposition> record : results) {
                compo = record.value1();

                logger.info("compo.uid is {}", compo.getVersionUid());

                // COMPOSITION => FHIR Condition
                condition = FhirConditionOpenehrDiagnose.map(compo);

                result.add(condition);
            }
        } catch (Exception e) {
            throw new InternalErrorException("There was a problem retrieving the results", e);
        }


        // ****************************************************
        // test if we can get partial structures from the query
        // this is getting NPE from the server https://github.com/ehrbase/ehrbase/issues/270
        /*
        Query<Record2<DiagnoseEvaluation, String>> test_query = Query.buildNativeQuery(
                "SELECT eval, c/uid/value "+
                        "FROM EHR e CONTAINS COMPOSITION c CONTAINS EVALUATION eval[openEHR-EHR-EVALUATION.problem_diagnosis.v1] "+
                        "WHERE c/archetype_details/template_id/value = 'Diagnose' AND "+
                        "e/ehr_status/subject/external_ref/id/value = '"+ subjectId.getValue() +"'",
                DiagnoseEvaluation.class, String.class
        );

        List<Record2<DiagnoseEvaluation, String>> test_results = service.getClient().aqlEndpoint().execute(test_query);

        String uid;
        DiagnoseEvaluation eval;

        for (Record2<DiagnoseEvaluation, String> test_record: test_results)
        {
            eval = test_record.value1();
            uid = test_record.value2();

            System.out.println(eval.toString());
            System.out.println(uid);
        }
        */
        // ****************************************************

        return result;
    }

    @Create
    public MethodOutcome createCondition(@ResourceParam Condition condition) {
        conditionDao.create(condition);
        auditService.registerCreateResourceSuccessEvent(condition);

        // will throw exceptions and block the request if the patient doesn't have an EHR
        UUID ehrUid = getEhrUidForSubjectId(condition.getSubject().getReference().split(":")[2]);

        // *************************************************************************************
        // TODO: we don't have a profile for the diagnostic report to filter
        // *************************************************************************************

        try {
            // FHIR Condition => COMPOSITION
            DiagnoseComposition composition = FhirConditionOpenehrDiagnose.map(condition);

            //UUID ehr_id = service.createEhr(); // <<< reflections error!
            VersionUid versionUid = ehrbaseService.saveDiagnosis(ehrUid, composition);
            logger.info("Composition created with UID {}", versionUid);
            auditService.registerMapResourceEvent(AuditEvent.AuditEventOutcome._0, "Success", condition);
        } catch (Exception e) {
            auditService.registerMapResourceEvent(AuditEvent.AuditEventOutcome._8, e.getMessage(), condition);
            throw new UnprocessableEntityException("There was a problem saving the composition" + e.getMessage(), e);
        }

        return new MethodOutcome()
                .setCreated(true)
                .setResource(condition);
    }

    @Override
    public Class<Condition> getResourceType() {
        return Condition.class;
    }
}
