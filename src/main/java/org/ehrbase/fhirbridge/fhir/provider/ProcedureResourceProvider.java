package org.ehrbase.fhirbridge.fhir.provider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.datavalues.quantity.DvQuantity;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import org.ehrbase.client.aql.query.Query;
import org.ehrbase.client.aql.record.Record4;
import org.ehrbase.client.aql.record.Record5;
import org.ehrbase.client.openehrclient.VersionUid;
import org.ehrbase.fhirbridge.fhir.audit.AuditService;
import org.ehrbase.fhirbridge.mapping.FhirProcedureOpenehrProcedure;
import org.ehrbase.fhirbridge.opt.prozedurcomposition.ProzedurComposition;
import org.ehrbase.fhirbridge.rest.EhrbaseService;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Procedure;
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
public class ProcedureResourceProvider extends AbstractResourceProvider {

    private final Logger logger = LoggerFactory.getLogger(ProcedureResourceProvider.class);

    private final IFhirResourceDao<Procedure> procedureDao;

    public ProcedureResourceProvider(FhirContext fhirContext, EhrbaseService ehrbaseService, AuditService auditService,
                                     IFhirResourceDao<Procedure> procedureDao) {
        super(fhirContext, ehrbaseService, auditService);
        this.procedureDao = procedureDao;
    }

    @Read()
    @SuppressWarnings("unused")
    public Procedure getProcedureById(@IdParam IdType identifier)
    {
        Procedure result = new Procedure();

        // identifier.getValue() is the Resource/theId

        // Gets empty results if there is no data for the anatomical location
        //String aql = "SELECT c/uid/value, a/description[at0001]/items[at0002]/value, a/description[at0001]/items[at0049]/value, a/time, al/items[at0001]/value "+
        //    "FROM EHR e CONTAINS COMPOSITION c CONTAINS ACTION a[openEHR-EHR-ACTION.procedure.v1] CONTAINS CLUSTER al[openEHR-EHR-CLUSTER.anatomical_location.v1] "+
        //    "WHERE c/uid/value = '"+ identifier.getIdPart() +"'";

        // Using the full path to the anatomical location also returns empty results if there are no values
        //String aql = "SELECT c/uid/value, a/description[at0001]/items[at0002]/value, a/description[at0001]/items[at0049]/value, a/time, a/description[at0001]/items[openEHR-EHR-CLUSTER.anatomical_location.v1]/items[at0001]/value "+
        //        "FROM EHR e CONTAINS COMPOSITION c CONTAINS ACTION a[openEHR-EHR-ACTION.procedure.v1] "+
        //        "WHERE c/uid/value = '"+ identifier.getIdPart() +"'";

        // Workaround: ignores anatomical location while the issue is reviewed
        String aql = "SELECT c/uid/value, a/description[at0001]/items[at0002]/value, a/description[at0001]/items[at0049]/value, a/time "+
                "FROM EHR e CONTAINS COMPOSITION c CONTAINS ACTION a[openEHR-EHR-ACTION.procedure.v1] "+
                "WHERE c/uid/value = '"+ identifier.getIdPart() +"'";



        // uid. procedure name, procedure description, procedure time, anatomical location (removed because the empty results issue)
        Query<Record4<String, DvText, DvText, DvDateTime>> query = Query.buildNativeQuery(
            aql, String.class, DvText.class, DvText.class, DvDateTime.class
        );

        List<Record4<String, DvText, DvText, DvDateTime>> results = new ArrayList<>();

        try
        {
            results = ehrbaseService.getClient().aqlEndpoint().execute(query);

            if (results.isEmpty())
            {
                throw new ResourceNotFoundException("Resource not found"); // causes 404
            }

            Record4<String, DvText, DvText, DvDateTime> record = results.get(0);

            String uid = record.value1();

            DvText procedureName = record.value2();
            DvText procedureDescription = record.value3(); // optional

            DvDateTime time = record.value4();

            // Workaround for issue getting results with anatomical location
            DvText bodyLocation = null; //record.value5();

            // COMPOSITION => FHIR Procedure
            result = FhirProcedureOpenehrProcedure.map(uid, procedureName, procedureDescription, time, bodyLocation);
        }
        catch (ResourceNotFoundException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return result;
    }

    @Search
    @SuppressWarnings("unused")
    public List<Procedure> getAllProcedures(
            @OptionalParam(name="_profile") UriParam profile,
            @RequiredParam(name=Patient.SP_IDENTIFIER) TokenParam subjectId
    )
    {
        List<Procedure> result = new ArrayList<>();

        // Issue getting results with anatomical location
        // Workaround: ignores anatomical location while the issue is reviewed
        String aql = "SELECT c/uid/value, a/description[at0001]/items[at0002]/value, a/description[at0001]/items[at0049]/value, a/time "+
                "FROM EHR e CONTAINS COMPOSITION c CONTAINS ACTION a[openEHR-EHR-ACTION.procedure.v1] "+
                "WHERE e/ehr_status/subject/external_ref/id/value = '"+ subjectId.getValue() +"'";

        // uid. procedure name, procedure description, procedure time, anatomical location (removed because the empty results issue)
        Query<Record4<String, DvText, DvText, DvDateTime>> query = Query.buildNativeQuery(
                aql, String.class, DvText.class, DvText.class, DvDateTime.class
        );

        List<Record4<String, DvText, DvText, DvDateTime>> results = new ArrayList<>();

        try
        {
            results = ehrbaseService.getClient().aqlEndpoint().execute(query);

            Procedure procedure;

            for (Record4<String, DvText, DvText, DvDateTime> record: results)
            {
                String uid = record.value1();
                DvText procedureName = record.value2();
                DvText procedureDescription = record.value3(); // optional
                DvDateTime time = record.value4();

                // Workaround for issue getting results with anatomical location
                DvText bodyLocation = null; //record.value5();

                // COMPOSITION => FHIR Procedure
                procedure = FhirProcedureOpenehrProcedure.map(uid, procedureName, procedureDescription, time, bodyLocation);
                result.add(procedure);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new InternalErrorException("There was a problem retrieving the results", e);
        }


        return result;
    }

    /*
    @Search
    @SuppressWarnings("unused")
    public List<Procedure> getAllProcedures(
            @OptionalParam(name="_profile") UriParam profile,
            @RequiredParam(name=Patient.SP_IDENTIFIER) TokenParam subjectId,
            @OptionalParam(name=Condition.SP_RECORDED_DATE) DateRangeParam dateRange,
            @OptionalParam(name=Condition.SP_CODE) TokenParam code
    )
    {
        logger.info("SEARCH CONDITION! subjectId: {}", subjectId);
        List<Condition> result = new ArrayList<>();

        // *************************************************************************************
        // We don't have a profile to ask for, we will try to map from a Diagnose composition
        // to a general condition.
        // *************************************************************************************

        String aql =
            "SELECT c "+
            "FROM EHR e CONTAINS COMPOSITION c "+
            "WHERE c/archetype_details/template_id/value = 'Diagnose' AND "+
            "e/ehr_status/subject/external_ref/id/value = '"+ subjectId.getValue() +"'";

        // filters
        if (dateRange != null)
        {
            // with date range we can also receive just one bound
            if (dateRange.getLowerBound() != null)
                aql += " AND '"+ dateRange.getLowerBound().getValueAsString() + "' <= c/context/start_time/value";

            if (dateRange.getUpperBound() != null)
                aql += " AND c/context/start_time/value <= '"+ dateRange.getUpperBound().getValueAsString() +"'";
        }

        if (code != null)
        {
            logger.info("code {}", code.getValue());
            String openEHRDiagnosis;
            switch (code.getValue())
            {
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

            aql += " AND eval/data[at0001]/items[at0002]/value/defining_code/code_string = '"+ openEHRDiagnosis +"'";
            //aql += " WHERE eval/data[at0001]/items[at0002]/value/defining_code/code_string = '"+ openEHRDiagnosis +"'";
        }


        // execute the query
        Query<Record1<DiagnoseComposition>> query = Query.buildNativeQuery(aql, DiagnoseComposition.class);

        List<Record1<DiagnoseComposition>> results;

        try
        {
            results = service.getClient().aqlEndpoint().execute(query);

            DiagnoseComposition compo;
            Condition condition;

            for (Record1<DiagnoseComposition> record: results)
            {
                compo = record.value1();

                logger.info("compo.uid is {}", compo.getVersionUid());

                // COMPOSITION => FHIR Condition
                condition = FhirConditionOpenehrDiagnose.map(compo);

                result.add(condition);
            }
        }
        catch (Exception e)
        {
            throw new InternalErrorException("There was a problem retrieving the results", e);
        }

        return result;
    }
*/
    @Create
    public MethodOutcome createProcedure(@ResourceParam Procedure procedure) {
        procedureDao.create(procedure);
        auditService.registerCreateResourceSuccessEvent(procedure);

        // will throw exceptions and block the request if the patient doesn't have an EHR
        UUID ehrUid = getEhrUidForSubjectId(procedure.getSubject().getReference().split(":")[2]);

        // *************************************************************************************
        // TODO: we don't have a profile for the diagnostic report to filter
        // *************************************************************************************

        VersionUid versionUid = null;

        try {
            // FHIR Condition => COMPOSITION
            ProzedurComposition composition = FhirProcedureOpenehrProcedure.map(procedure);
            versionUid = ehrbaseService.saveProcedure(ehrUid, composition);

            logger.info("Composition created with UID {}", versionUid);
            auditService.registerMapResourceEvent(AuditEvent.AuditEventOutcome._0, "Success", procedure);

        } catch (Exception e) {
            auditService.registerMapResourceEvent(AuditEvent.AuditEventOutcome._8, e.getMessage(), procedure);
            throw new UnprocessableEntityException("There was a problem saving the composition" + e.getMessage(), e);
        }

        procedure.setId(new IdType(versionUid.toString()));
        procedure.getMeta().setVersionId("1");
        procedure.getMeta().setLastUpdatedElement(InstantType.withCurrentTime());

        return new MethodOutcome()
                .setCreated(true)
                .setResource(procedure);
    }

    @Override
    public Class<Procedure> getResourceType() {
        return Procedure.class;
    }
}
