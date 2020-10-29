package org.ehrbase.fhirbridge.mapping;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.FeederAuditDetails;
import com.nedap.archie.rm.datavalues.DvIdentifier;
import com.nedap.archie.rm.datavalues.encapsulated.DvParsable;
import org.ehrbase.fhirbridge.config.util.CommonData;
import org.ehrbase.fhirbridge.fhir.Profile;
import org.ehrbase.fhirbridge.opt.shareddefinition.CategoryDefiningcode;
import org.ehrbase.fhirbridge.opt.shareddefinition.Language;
import org.ehrbase.fhirbridge.opt.shareddefinition.SettingDefiningcode;
import org.ehrbase.fhirbridge.opt.shareddefinition.Territory;
import org.hl7.fhir.r4.model.*;
import org.ehrbase.fhirbridge.opt.laborbefundcomposition.*;
import org.ehrbase.fhirbridge.opt.laborbefundcomposition.definition.*;
import com.nedap.archie.rm.generic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import static java.util.Date.from;

/**
 * FHIR to openEHR - Laboratory report
 */
public class FhirDiagnosticReportOpenehrLabResults {

    private static final Logger logger = LoggerFactory.getLogger(FhirDiagnosticReportOpenehrLabResults.class);

    private FhirDiagnosticReportOpenehrLabResults() {}

    /**
     * this maps a single lab observation to a composition, the map(DiagnosticReport) method maps a
     * DiagnosticReport with a direct contained Observation to a composition.
     * @param fhirObservation the FHIR Observation resource received in the API.
     * @return the Composition defined by the laborbefund template.
     */
    public static LaborbefundComposition map(Observation fhirObservation) {

        LaborbefundComposition composition = new LaborbefundComposition();

        // set feeder audit
        FeederAudit fa = CommonData.constructFeederAudit(fhirObservation);
        composition.setFeederAudit(fa);


        LaboranalytResultatCluster resultCluster = mapObservation(fhirObservation);

        StandortJedesEreignisPointEvent resultEvent = new StandortJedesEreignisPointEvent();
        resultEvent.setTimeValue(OffsetDateTime.now()); // mandatory
        List<LaboranalytResultatCluster> items = new ArrayList<>();
        items.add(resultCluster);
        resultEvent.setLaboranalytResultat(items);

        LaborergebnisObservation resultObs = new LaborergebnisObservation();
        List<StandortJedesEreignisChoice> events = new ArrayList<>();
        events.add(resultEvent);
        resultObs.setJedesEreignis(events);
        resultObs.setOriginValue(OffsetDateTime.now()); // mandatory
        resultObs.setLanguage(Language.EN);
        resultObs.setSubject(new PartySelf());

        List<LaborergebnisObservation> observations = new ArrayList<>();
        observations.add(resultObs);
        composition.setLaborergebnis(observations);

        // ======================================================================================
        // Required fields by API
        composition.setLanguage(Language.EN);
        composition.setLocation("test");
        composition.setSettingDefiningcode(SettingDefiningcode.EMERGENCY_CARE);
        composition.setTerritory(Territory.DE);
        composition.setCategoryDefiningcode(CategoryDefiningcode.EVENT);
        composition.setStartTimeValue(OffsetDateTime.now());

        // FIXME: https://github.com/ehrbase/ehrbase_client_library/issues/31
        //        PartyProxy composer = new PartyIdentified();
        //        composition.setComposer(composer);

        composition.setComposer(new PartySelf());

        return composition;
    }


    /**
     * this maps a DiagnosticReport with a direct contained Observation to an
     * openEHR composition generated for the Laborbefund template.
     * @param fhirDiagnosticReport the DiagnosticReport FHIR resource received in the API
     * @return the Composition defined by the laborbefund template
     */
    public static LaborbefundComposition map(DiagnosticReport fhirDiagnosticReport) {

        LaborbefundComposition composition = new LaborbefundComposition();

        logger.debug("Contained size: {}", fhirDiagnosticReport.getContained().size());

        // one contained Observation is expected
        if (fhirDiagnosticReport.getContained().size() != 1)
        {
            throw new UnprocessableEntityException("One contained Observation was expected "+ fhirDiagnosticReport.getContained().size() +" were received in DiagnosticReport "+ fhirDiagnosticReport.getId());
        }
        if (fhirDiagnosticReport.getContained().get(0).getResourceType() != ResourceType.Observation)
        {
            throw new UnprocessableEntityException("One contained Observation was expected, contained is there but is not Observation, it is "+ fhirDiagnosticReport.getContained().get(0).getResourceType().toString());
        }

        Observation fhirObservation = (Observation)fhirDiagnosticReport.getContained().get(0);

        LaboranalytResultatCluster resultCluster = mapObservation(fhirObservation);

        StandortJedesEreignisPointEvent resultEvent = new StandortJedesEreignisPointEvent();
        resultEvent.setTimeValue(fhirObservation.getEffectiveDateTimeType().getValueAsCalendar().toZonedDateTime());
        resultEvent.setLabortestBezeichnungValue(fhirDiagnosticReport.getCode().getText());
        resultEvent.setLabortestBezeichnungValueTree("test name");
        resultEvent.setSchlussfolgerungValueTree(fhirDiagnosticReport.getConclusion());
        resultEvent.setSchlussfolgerungValue("conclusion");

        GesamtteststatusDefiningcode openEHRStatus = null;
        // FHIR value set: https://simplifier.net/packages/simplifier.core.r4.valuesets/4.0.0/files/18799
        // The openEHR template only accepts the 3 codes below
        switch (fhirDiagnosticReport.getStatus())
        {
            case FINAL:
                openEHRStatus = GesamtteststatusDefiningcode.FINAL;
            break;
            case REGISTERED:
                openEHRStatus = GesamtteststatusDefiningcode.REGISTRIERT;
            break;
            case CANCELLED:
                openEHRStatus = GesamtteststatusDefiningcode.ABGEBROCHEN;
            break;
            default:
                openEHRStatus = GesamtteststatusDefiningcode.REGISTRIERT;
        }
        resultEvent.setGesamtteststatusDefiningcode(openEHRStatus);
        resultEvent.setGesamtteststatusValue("test status");

        List<LaboranalytResultatCluster> items = new ArrayList<>();
        items.add(resultCluster);
        resultEvent.setLaboranalytResultat(items);


        StandortDetailsDerTestanforderungCluster testRequestDetails = new StandortDetailsDerTestanforderungCluster();
        DvIdentifier receiverOrderIdentifier = new DvIdentifier();
        receiverOrderIdentifier.setId(fhirDiagnosticReport.getIdentifier().get(0).getValue());
        receiverOrderIdentifier.setType(fhirDiagnosticReport.getIdentifier().get(0).getSystem());
        testRequestDetails.setAuftragsIdEmpfanger(receiverOrderIdentifier);


        LaborergebnisObservation resultObs = new LaborergebnisObservation();

        List<StandortJedesEreignisChoice> events = new ArrayList<>();
        events.add(resultEvent);
        resultObs.setJedesEreignis(events);

        List<StandortDetailsDerTestanforderungCluster> testRequestDetailsList = new ArrayList<>();
        testRequestDetailsList.add(testRequestDetails);
        resultObs.setDetailsDerTestanforderung(testRequestDetailsList);

        resultObs.setOriginValue(fhirObservation.getEffectiveDateTimeType().getValueAsCalendar().toZonedDateTime());
        resultObs.setLanguage(Language.EN); // FIXME: the lang should be retrieved from the template
        resultObs.setSubject(new PartySelf());


        List<LaborergebnisObservation> observations = new ArrayList<>();
        observations.add(resultObs);
        composition.setLaborergebnis(observations);


        // ======================================================================================
        // Required fields by API
        composition.setLanguage(Language.EN); // FIXME: the lang should be retrieved from the template
        composition.setLocation("test");
        composition.setSettingDefiningcode(SettingDefiningcode.EMERGENCY_CARE);
        composition.setTerritory(Territory.DE);
        composition.setCategoryDefiningcode(CategoryDefiningcode.EVENT);
        composition.setStartTimeValue(fhirDiagnosticReport.getEffectiveDateTimeType().getValueAsCalendar().toZonedDateTime());


        // FIXME: https://github.com/ehrbase/ehrbase_client_library/issues/31
        //        PartyProxy composer = new PartyIdentified();
        //        composition.setComposer(composer);

        composition.setComposer(new PartySelf());

        return composition;
    }


    /**
     * Maps a FHIR Observation to an openEHR LaboranalytResultatCluster generated from the Laborbefund template.
     * @param fhirObservation the FHIR Observation resource received in the API.
     * @return the cluster defined in the OPT that maps to the FHIR observation
     */
    private static LaboranalytResultatCluster mapObservation(Observation fhirObservation) {

        // ========================================================================================
        // value quantity is expected
        Quantity fhirValue = null;
        BigDecimal fhirValueNumeric = null;
        String fhirCodeName = null;
        DateTimeType fhirEffectiveDateTime = null;

        try {
            fhirValue = fhirObservation.getValueQuantity();
            fhirValueNumeric = fhirValue.getValue();
            fhirCodeName = fhirObservation.getCode().getCoding().get(0).getDisplay();
            fhirEffectiveDateTime = fhirObservation.getEffectiveDateTimeType();
        } catch (Exception e) {
            throw new UnprocessableEntityException(e.getMessage());
        }

        if (fhirValueNumeric == null)
        {
            throw new UnprocessableEntityException("Value is required in FHIR Observation and should be Quantity");
        }
        if (fhirEffectiveDateTime == null)
        {
            throw new UnprocessableEntityException("effectiveDateTime is required in FHIR Observation");
        }
        if (fhirCodeName == null)
        {
            throw new UnprocessableEntityException("code is required in FHIR Observation");
        }

        // mapping to openEHR
        LaboranalytResultatAnalytResultatDvquantity resultValue = new LaboranalytResultatAnalytResultatDvquantity();
        resultValue.setAnalytResultatMagnitude(fhirValueNumeric.doubleValue());
        resultValue.setAnalytResultatUnits(fhirValue.getUnit());

        // =======================================================================================
        // rest of the structure to build the composition with the value taken from FHIR
        LaboranalytResultatCluster resultCluster = new LaboranalytResultatCluster();
        resultCluster.setAnalytResultat(resultValue);
        resultCluster.setAnalytResultatValue("result"); // this is the ELEMENT.name
        resultCluster.setUntersuchterAnalytValue(fhirCodeName);
        resultCluster.setUntersuchterAnalytValueName("Analyte name");
        resultCluster.setZeitpunktErgebnisStatusValue(fhirEffectiveDateTime.getValueAsCalendar().toZonedDateTime());
        resultCluster.setZeitpunktErgebnisStatusValueName("Result status time");

        return resultCluster;
    }

    public static Observation map(LaborbefundComposition compo)
    {
        Observation observation = new Observation();

        TemporalAccessor temporal;
        Coding coding;

        LaboranalytResultatCluster cluster = ((StandortJedesEreignisPointEvent)compo.getLaborergebnis().get(0).getJedesEreignis().get(0)).getLaboranalytResultat().get(0);

        // cluster . time -> observation . effective_date
        temporal = cluster.getZeitpunktErgebnisStatusValue();
        observation.getEffectiveDateTimeType().setValue(from(((OffsetDateTime)temporal).toInstant()));

        // cluster . value -> observation . value
        LaboranalytResultatAnalytResultatDvquantity value = ((LaboranalytResultatAnalytResultatDvquantity)cluster.getAnalytResultat());
        observation.getValueQuantity().setValue(value.getAnalytResultatMagnitude());
        observation.getValueQuantity().setUnit(value.getAnalytResultatUnits());
        observation.getValueQuantity().setSystem("http://unitsofmeasure.org");
        observation.getValueQuantity().setCode(value.getAnalytResultatUnits());

        // set codes that come hardcoded in the inbound resources

        // observation . category
        observation.getCategory().add(new CodeableConcept());
        coding = observation.getCategory().get(0).addCoding();
        coding.setSystem("http://terminology.hl7.org/CodeSystem/observation-category");
        coding.setCode("laboratory");
        coding = observation.getCategory().get(0).addCoding();
        coding.setSystem("http://loing.org");
        coding.setCode("26436-6");

        // observation . code
        coding = observation.getCode().addCoding();
        coding.setSystem("http://loing.org");
        coding.setCode("59826-8");
        coding.setDisplay("Creatinine [Moles/volume] in Blood");
        observation.getCode().setText("Kreatinin");

        // set patient
        //observation.getSubject().setReference("Patient/"+ subjectId.getValue());

        observation.setStatus(Observation.ObservationStatus.FINAL);

        observation.getMeta().addProfile(Profile.OBSERVATION_LAB.getUrl());

        observation.setId(compo.getVersionUid().toString());

        return observation;
    }
}