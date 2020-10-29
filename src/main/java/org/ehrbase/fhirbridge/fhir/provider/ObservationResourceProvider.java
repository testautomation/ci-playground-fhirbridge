package org.ehrbase.fhirbridge.fhir.provider;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.api.dao.IFhirResourceDao;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.QuantityParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.param.UriParam;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.nedap.archie.rm.datavalues.quantity.DvQuantity;
import com.nedap.archie.rm.datavalues.quantity.datetime.DvDateTime;
import org.ehrbase.client.aql.query.Query;
import org.ehrbase.client.aql.record.Record1;
import org.ehrbase.client.aql.record.Record3;
import org.ehrbase.client.openehrclient.VersionUid;
import org.ehrbase.fhirbridge.fhir.Profile;
import org.ehrbase.fhirbridge.fhir.ProfileUtils;
import org.ehrbase.fhirbridge.fhir.audit.AuditService;
import org.ehrbase.fhirbridge.mapping.FHIRObservationFiO2OpenehrBeatmungswerte;
import org.ehrbase.fhirbridge.mapping.FHIRObservationHeartRateOpenehrHeartRate;
import org.ehrbase.fhirbridge.mapping.FhirDiagnosticReportOpenehrLabResults;
import org.ehrbase.fhirbridge.mapping.FhirObservationBloodPressureOpenehrBloodPressure;
import org.ehrbase.fhirbridge.mapping.FhirObservationSofaScoreOpenehrSofa;
import org.ehrbase.fhirbridge.mapping.FhirObservationTempOpenehrBodyTemperature;
import org.ehrbase.fhirbridge.mapping.FhirSarsTestResultOpenehrPathogenDetection;
import org.ehrbase.fhirbridge.opt.beatmungswertecomposition.BeatmungswerteComposition;
import org.ehrbase.fhirbridge.opt.blutdruckcomposition.BlutdruckComposition;
import org.ehrbase.fhirbridge.opt.herzfrequenzcomposition.HerzfrequenzComposition;
import org.ehrbase.fhirbridge.opt.intensivmedizinischesmonitoringkorpertemperaturcomposition.IntensivmedizinischesMonitoringKorpertemperaturComposition;
import org.ehrbase.fhirbridge.opt.kennzeichnungerregernachweissarscov2composition.KennzeichnungErregernachweisSARSCoV2Composition;
import org.ehrbase.fhirbridge.opt.laborbefundcomposition.LaborbefundComposition;
import org.ehrbase.fhirbridge.opt.sofacomposition.SOFAComposition;
import org.ehrbase.fhirbridge.rest.EhrbaseService;
import org.hl7.fhir.r4.model.AuditEvent;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.InstantType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Date.from;


/**
 * Resource provider for Observation
 */
@Component
public class ObservationResourceProvider extends AbstractResourceProvider {

    private final Logger logger = LoggerFactory.getLogger(ObservationResourceProvider.class);

    private final IFhirResourceDao<Observation> observationDao;

    public ObservationResourceProvider(FhirContext fhirContext, EhrbaseService ehrbaseService, AuditService auditService,
                                       IFhirResourceDao<Observation> observationDao) {
        super(fhirContext, ehrbaseService, auditService);
        this.observationDao = observationDao;
    }

    @Search
    public List<Observation> getAllObservations(
            @OptionalParam(name = "_profile") UriParam profile,
            @RequiredParam(name = Patient.SP_IDENTIFIER) TokenParam subjectId,
            @OptionalParam(name = Observation.SP_DATE) DateRangeParam dateRange,
            @OptionalParam(name = Observation.SP_VALUE_QUANTITY) QuantityParam qty,
            @OptionalParam(name = "bodyTempOption") StringParam bodyTempOption
    ) {
        logger.info("SEARCH OBSERVATION {} ", profile);
        List<Observation> result = new ArrayList<>();

        if (profile.getValue().equals(Profile.BODY_TEMP.getUrl())) {
            // testing alternative implementations
            if (bodyTempOption == null || bodyTempOption.isEmpty())
                result = processSearchBodyTemperature(subjectId, dateRange, qty);
            else
                result = processSearchBodyTemperature2(subjectId, dateRange, qty);
        } else if (profile.getValue().equals(Profile.CORONARIRUS_NACHWEIS_TEST.getUrl())) {
            result = processCovidLabReport(subjectId, dateRange, qty);
        } else if (profile.getValue().equals(Profile.OBSERVATION_LAB.getUrl())) {
            result = processLabResults(subjectId, dateRange, qty);
        } else {
            throw new InvalidRequestException(String.format("Template not supported %s", profile.getValue()));
        }

        return result;
    }

    // Alternative method, using datavalue query
    List<Observation> processSearchBodyTemperature2(TokenParam subjectId, DateRangeParam dateRange, QuantityParam qty) {
        List<Observation> result = new ArrayList<>();

        String aql = "SELECT c/uid/value, o/data[at0002]/origin, o/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value " +
                "FROM EHR e CONTAINS COMPOSITION c CONTAINS OBSERVATION o[openEHR-EHR-OBSERVATION.body_temperature.v2] " +
                "WHERE e/ehr_status/subject/external_ref/id/value = '" + subjectId.getValue() + "'";

        if (dateRange != null) {
            // with date range we can also receive just one bound
            if (dateRange.getLowerBound() != null)
                aql += " AND '" + dateRange.getLowerBound().getValueAsString() + "' <= c/context/start_time/value";

            if (dateRange.getUpperBound() != null)
                aql += " AND c/context/start_time/value <= '" + dateRange.getUpperBound().getValueAsString() + "'";
        }

        if (qty != null) {
            ParamPrefixEnum prefix = qty.getPrefix();
            String operator = "";
            if (prefix == null) operator = "=";
            else {
                switch (prefix) {
                    case EQUAL:
                        operator = "=";
                        break;
                    case LESSTHAN:
                        operator = "<";
                        break;
                    case GREATERTHAN:
                        operator = ">";
                        break;
                    case LESSTHAN_OR_EQUALS:
                        operator = "<=";
                        break;
                    case GREATERTHAN_OR_EQUALS:
                        operator = ">=";
                        break;
                    default:
                        operator = "=";
                }
            }

            if (!operator.isBlank())
                aql += " AND o/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value/magnitude " + operator + " " + qty.getValue();
        }

        Query<Record3<String, DvDateTime, DvQuantity>> query =
                Query.buildNativeQuery(aql, String.class, DvDateTime.class, DvQuantity.class);

        List<Record3<String, DvDateTime, DvQuantity>> results = new ArrayList<>();

        try {
            results = this.ehrbaseService.getClient().aqlEndpoint().execute(query);

            String uid;
            DvDateTime datetime;
            DvQuantity quantity;
            Observation observation = new Observation();


            for (Record3<String, DvDateTime, DvQuantity> record : results) {
                uid = record.value1();
                datetime = record.value2();
                quantity = record.value3();

                // TODO: doing the mappging here to test, should be moved to a mapping class

                // FIXME: if there are two measurements of temperature in the same compo, this will return the same ID for two different FHIR observations.
                observation.setId(uid);

                observation.getValueQuantity().setValue(quantity.getMagnitude());
                observation.getValueQuantity().setUnit(quantity.getUnits());

                observation.getEffectiveDateTimeType().setValue(from(((OffsetDateTime) datetime.getValue()).toInstant()));

                // adds observation to the result
                result.add(observation);
            }

            logger.info("Results: {}", results.size());
        } catch (Exception e) {
            throw new InternalErrorException("There was a problem retrieving the results", e);
        }

        return result;
    }

    List<Observation> processSearchBodyTemperature(TokenParam subjectId, DateRangeParam dateRange, QuantityParam qty) {
        List<Observation> result = new ArrayList<>();

        // get all compositions for the body temperature template
        // TODO: filter by patient if the patient id parameter is used
        /*
        Query<Record1<IntensivmedizinischesMonitoringKorpertemperaturComposition>> query =
            Query.buildNativeQuery("SELECT c FROM EHR e CONTAINS COMPOSITION c where "
                 + "c/archetype_details/template_id/value = 'Intensivmedizinisches Monitoring Korpertemperatur' AND "
                 + "e/ehr_status/subject/external_ref/id/value = '"+ subjectId.getValue() +"'",
                 IntensivmedizinischesMonitoringKorpertemperaturComposition.class);

        List<Record1<IntensivmedizinischesMonitoringKorpertemperaturComposition>> results = new ArrayList<Record1<IntensivmedizinischesMonitoringKorpertemperaturComposition>>();
        */
        // Workaround for not getting the composition uid in the result (https://github.com/ehrbase/openEHR_SDK/issues/44)
        String aql =
                "SELECT c " +
                        "FROM EHR e CONTAINS COMPOSITION c CONTAINS OBSERVATION o[openEHR-EHR-OBSERVATION.body_temperature.v2] " +
                        "WHERE c/archetype_details/template_id/value = 'Intensivmedizinisches Monitoring Korpertemperatur' AND " +
                        "e/ehr_status/subject/external_ref/id/value = '" + subjectId.getValue() + "'";

        if (dateRange != null) {
            // with date range we can also receive just one bound
            if (dateRange.getLowerBound() != null)
                aql += " AND '" + dateRange.getLowerBound().getValueAsString() + "' <= c/context/start_time/value";

            if (dateRange.getUpperBound() != null)
                aql += " AND c/context/start_time/value <= '" + dateRange.getUpperBound().getValueAsString() + "'";
        }

        if (qty != null) {
            ParamPrefixEnum prefix = qty.getPrefix();
            String operator = "";
            if (prefix == null) operator = "=";
            else {
                switch (prefix) {
                    case EQUAL:
                        operator = "=";
                        break;
                    case LESSTHAN:
                        operator = "<";
                        break;
                    case GREATERTHAN:
                        operator = ">";
                        break;
                    case LESSTHAN_OR_EQUALS:
                        operator = "<=";
                        break;
                    case GREATERTHAN_OR_EQUALS:
                        operator = ">=";
                        break;
                    default:
                        operator = "=";
                }
            }

            if (!operator.isBlank())
                aql += " AND o/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value/magnitude " + operator + " " + qty.getValue();
        }

        Query<Record1<IntensivmedizinischesMonitoringKorpertemperaturComposition>> query =
                Query.buildNativeQuery(aql, IntensivmedizinischesMonitoringKorpertemperaturComposition.class);

        List<Record1<IntensivmedizinischesMonitoringKorpertemperaturComposition>> results = new ArrayList<>();

        try {
            results = this.ehrbaseService.getClient().aqlEndpoint().execute(query);

            IntensivmedizinischesMonitoringKorpertemperaturComposition compo;
            Observation observation;

            //for (Record1<IntensivmedizinischesMonitoringKorpertemperaturComposition> record: results)
            for (Record1<IntensivmedizinischesMonitoringKorpertemperaturComposition> record : results) {
                compo = record.value1();

                observation = FhirObservationTempOpenehrBodyTemperature.map(compo);

                // adds observation to the result
                result.add(observation);
            }

            logger.info("Results: {}", results.size());
        } catch (Exception e) {
            throw new InternalErrorException("There was a problem retrieving the results", e);
        }

        return result;
    }

    List<Observation> processLabResults(TokenParam subjectId, DateRangeParam dateRange, QuantityParam qty) {
        List<Observation> result = new ArrayList<>();

        String aql =
                "SELECT c " +
                        "FROM EHR e CONTAINS COMPOSITION c " +
                        "WHERE c/archetype_details/template_id/value = 'Laborbefund' AND " +
                        "e/ehr_status/subject/external_ref/id/value = '" + subjectId.getValue() + "'";

            /* getting 400 from this query, tried to get the cluster to compare with the date range param since that is the real effectiveTime of the resource, not the compo time.
            String aql =
                    "SELECT c, c/uid/value "+
                            "FROM EHR e CONTAINS COMPOSITION c CONTAINS CLUSTER cluster[openEHR-EHR-CLUSTER.laboratory_test_analyte.v1] "+
                            "WHERE c/archetype_details/template_id/value = 'Laborbefund' AND "+
                            "e/ehr_status/subject/external_ref/id/value = '"+ subjectId.getValue() +"'";
            */

        if (dateRange != null) {
            // with date range we can also receive just one bound
            if (dateRange.getLowerBound() != null) {
                // this is for filtering against the effective time in the cluster but the query above doesn't work
                //aql += " AND '"+ dateRange.getLowerBound().getValueAsString() + "' <= cluster/items[at0006]/value/value";
                aql += " AND '" + dateRange.getLowerBound().getValueAsString() + "' <= c/context/start_time/value";
            }

            if (dateRange.getUpperBound() != null) {
                //aql += " AND cluster/items[at0006]/value/value <= '"+ dateRange.getUpperBound().getValueAsString() +"'";
                aql += " AND c/context/start_time/value <= '" + dateRange.getUpperBound().getValueAsString() + "'";
            }
        }

        Query<Record1<LaborbefundComposition>> query =
                Query.buildNativeQuery(aql, LaborbefundComposition.class);

        List<Record1<LaborbefundComposition>> results = new ArrayList<>();

        try {
            results = ehrbaseService.getClient().aqlEndpoint().execute(query);

            LaborbefundComposition compo;
            Observation observation;

            for (Record1<LaborbefundComposition> record : results) {
                compo = record.value1();

                // Lab Results COMPOSITION => FHIR Observation
                observation = FhirDiagnosticReportOpenehrLabResults.map(compo);

                // adds observation to the result
                result.add(observation);
            }
        } catch (Exception e) {
            throw new InternalErrorException("There was a problem retrieving the results", e);
        }

        return result;
    }

    List<Observation> processCovidLabReport(TokenParam subjectId, DateRangeParam dateRange, QuantityParam qty) {
        List<Observation> result = new ArrayList<>();

        // another approach, asking for the data points in AQL directly without retrieving the whole compo
        // this doesnt work https://github.com/ehrbase/openEHR_SDK/issues/45
            /*
            Query<Record> query = Query.buildNativeQuery(
                "SELECT c/uid/value as uid, eval/data[at0001]/items[at0015]/value/value as effective_time "+
                "FROM EHR e CONTAINS COMPOSITION c CONTAINS EVALUATION eval[openEHR-EHR-EVALUATION.flag_pathogen.v0] "+
                "WHERE c/archetype_details/template_id/value = 'Kennzeichnung Erregernachweis SARS-CoV-2' AND "+
                "e/ehr_status/subject/external_ref/id/value = '"+ subjectId.getValue() +"'"
            );

            List<Record> results = new ArrayList<Record>();
            */

        String aql =
                "SELECT c " +
                        "FROM EHR e CONTAINS COMPOSITION c CONTAINS EVALUATION eval[openEHR-EHR-EVALUATION.flag_pathogen.v0] " +
                        "WHERE c/archetype_details/template_id/value = 'Kennzeichnung Erregernachweis SARS-CoV-2' AND " +
                        "e/ehr_status/subject/external_ref/id/value = '" + subjectId.getValue() + "'";

        if (dateRange != null) {
            // with date range we can also receive just one bound
            if (dateRange.getLowerBound() != null)
                aql += " AND '" + dateRange.getLowerBound().getValueAsString() + "' <= c/context/start_time/value";

            if (dateRange.getUpperBound() != null)
                aql += " AND c/context/start_time/value <= '" + dateRange.getUpperBound().getValueAsString() + "'";
        }

        Query<Record1<KennzeichnungErregernachweisSARSCoV2Composition>> query =
                Query.buildNativeQuery(aql, KennzeichnungErregernachweisSARSCoV2Composition.class);

        //List<Record> results = new ArrayList<Record>();
        List<Record1<KennzeichnungErregernachweisSARSCoV2Composition>> results = new ArrayList<>();

        try {
            results = ehrbaseService.getClient().aqlEndpoint().execute(query);

            String uid;
            KennzeichnungErregernachweisSARSCoV2Composition compo;

            Observation observation;

            for (Record1<KennzeichnungErregernachweisSARSCoV2Composition> record : results)
            //for (Record record: results)
            {
                compo = record.value1();

                    /* not working because results are not populated when using Record
                    uid = (String)record.value(0);
                    effective_time = (TemporalAccessor)record.value(1);
                    */

                logger.info("Record: {}", record); // org.ehrbase.client.aql.record.RecordImp
                logger.info("Record values {}", record.values().length); // using Record instead of Record2 gives 0
                logger.info("Record fields {}", record.fields().length); // using Record instead of Record2 gives 0


                // COMPOSITION => Coronavirus Lab Result Observation
                observation = FhirSarsTestResultOpenehrPathogenDetection.map(compo);


                // adds observation to the result
                result.add(observation);
            }

            logger.info("Results {}", results.size());
        } catch (Exception e) {
            throw new InternalErrorException("There was a problem retrieving the results", e);
        }

        return result;
    }

    @Create
    @SuppressWarnings("unused")
    public MethodOutcome createObservation(@ResourceParam Observation observation) {
        checkProfiles(observation);

        observationDao.create(observation);
        auditService.registerCreateResourceSuccessEvent(observation);

        // will throw exceptions and block the request if the patient doesn't have an EHR
        UUID ehrUid = getEhrUidForSubjectId(observation.getSubject().getReference().split(":")[2]);

        try {
            if (ProfileUtils.hasProfile(observation, Profile.OBSERVATION_LAB)) {
                logger.info(">>>>>>>>>>>>>>>>>>> OBSERVATION LAB {}", observation.getIdentifier().get(0).getValue());

                // test map FHIR to openEHR
                LaborbefundComposition composition = FhirDiagnosticReportOpenehrLabResults.map(observation);

                //UUID ehrId = service.createEhr(); // <<< reflections error!
                VersionUid versionUid = ehrbaseService.saveLab(ehrUid, composition);
                logger.info("Composition created with UID {} for FHIR profile {}", versionUid, Profile.OBSERVATION_LAB);
            } else if (ProfileUtils.hasProfile(observation, Profile.CORONARIRUS_NACHWEIS_TEST)) {
                logger.info(">>>>>>>>>>>>>>>>>>>> OBSERVATION COVID");

                // Map CoronavirusNachweisTest to openEHR


                // test map FHIR to openEHR
                KennzeichnungErregernachweisSARSCoV2Composition composition = FhirSarsTestResultOpenehrPathogenDetection.map(observation);

                //UUID ehrId = service.createEhr(); // <<< reflections error!
                VersionUid versionUid = ehrbaseService.saveTest(ehrUid, composition);
                logger.info("Composition created with UID {} for FHIR profile {}", versionUid, Profile.CORONARIRUS_NACHWEIS_TEST);
            } else if (ProfileUtils.hasProfile(observation, Profile.SOFA_SCORE)) {
                logger.info(">>>>>>>>>>>>>>>>>>>> OBSERVATION SOFA SCORE");

                // Map SOFA Score to openEHR


                // test map FHIR to openEHR
                SOFAComposition composition = FhirObservationSofaScoreOpenehrSofa.map(observation);

                //UUID ehrId = service.createEhr(); // <<< reflections error!
                VersionUid versionUid = ehrbaseService.saveSOFAScore(ehrUid, composition);
                logger.info("Composition created with UID {} for FHIR profile {}", versionUid, Profile.SOFA_SCORE);

            } else if (ProfileUtils.hasProfile(observation, Profile.BODY_TEMP)) {

                logger.info(">>>>>>>>>>>>>>>>>> OBSERVATION TEMP");

                // FHIR Observation Temp => openEHR COMPOSITION
                IntensivmedizinischesMonitoringKorpertemperaturComposition composition = FhirObservationTempOpenehrBodyTemperature.map(observation);

                //UUID ehrId = service.createEhr(); // <<< reflections error!
                VersionUid versionUid = ehrbaseService.saveTemp(ehrUid, composition);
                logger.info("Composition created with UID {} for FHIR profile {}", versionUid, Profile.BODY_TEMP);
            } else if (ProfileUtils.hasProfile(observation, Profile.FIO2)) {

                logger.info(">>>>>>>>>>>>>>>>>> OBSERVATION FIO2");

                // FHIR Observation Temp => openEHR COMPOSITION
                BeatmungswerteComposition composition = FHIRObservationFiO2OpenehrBeatmungswerte.map(observation);

                //UUID ehrId = service.createEhr(); // <<< reflections error!
                VersionUid versionUid = ehrbaseService.saveFIO2(ehrUid, composition);
                logger.info("Composition created with UID {} for FHIR profile {}", versionUid, Profile.FIO2);

            } else if (ProfileUtils.hasProfile(observation, Profile.BLOOD_PRESSURE)) {

                logger.info(">>>>>>>>>>>>>>>>>> OBSERVATION BLOOD_PRESSURE");

                BlutdruckComposition composition = FhirObservationBloodPressureOpenehrBloodPressure.map(observation);

                VersionUid versionUid = ehrbaseService.saveBloodPressure(ehrUid, composition);
                logger.info("Composition created with UID {} for FHIR profile {}", versionUid, Profile.BLOOD_PRESSURE);
            } else if (ProfileUtils.hasProfile(observation, Profile.HEART_RATE)) {

                logger.info(">>>>>>>>>>>>>>>>>> OBSERVATION HR");

                // FHIR Observation Temp => openEHR COMPOSITION
                HerzfrequenzComposition composition = FHIRObservationHeartRateOpenehrHeartRate.map(observation);

                //UUID ehrId = service.createEhr(); // <<< reflections error!
                VersionUid versionUid = ehrbaseService.saveHeartRate(ehrUid, composition);
                logger.info("Composition created with UID {} for FHIR profile {}", versionUid, Profile.HEART_RATE);
            }

            auditService.registerMapResourceEvent(AuditEvent.AuditEventOutcome._0, "Success", observation);

        } catch (Exception e) {
            auditService.registerMapResourceEvent(AuditEvent.AuditEventOutcome._8, e.getMessage(), observation);
            throw new UnprocessableEntityException("There was a problem saving the composition" + e.getMessage(), e);
        }

        return new MethodOutcome()
            .setCreated(true)
            .setResource(observation);
    }

    @Override
    public Class<Observation> getResourceType() {
        return Observation.class;
    }

    @Override
    public boolean isDefaultProfileSupported() {
        return false;
    }
}
