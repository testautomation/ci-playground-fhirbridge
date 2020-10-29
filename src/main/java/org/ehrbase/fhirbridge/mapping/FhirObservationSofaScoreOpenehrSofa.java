package org.ehrbase.fhirbridge.mapping;


import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvCodedText;
import com.nedap.archie.rm.datavalues.quantity.DvOrdinal;
import com.nedap.archie.rm.generic.PartySelf;
import com.nedap.archie.rm.support.identification.TerminologyId;
import org.ehrbase.fhirbridge.config.util.CommonData;
import org.ehrbase.fhirbridge.opt.shareddefinition.CategoryDefiningcode;
import org.ehrbase.fhirbridge.opt.shareddefinition.Language;
import org.ehrbase.fhirbridge.opt.shareddefinition.SettingDefiningcode;
import org.ehrbase.fhirbridge.opt.shareddefinition.Territory;
import org.ehrbase.fhirbridge.opt.sofacomposition.SOFAComposition;
import org.ehrbase.fhirbridge.opt.sofacomposition.definition.SOFAScoreObservation;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FhirObservationSofaScoreOpenehrSofa {
    private static final Logger logger = LoggerFactory.getLogger(FhirObservationSofaScoreOpenehrSofa.class);

    private static DvOrdinal ATEMFREQUENZ_SCORE_1 = new DvOrdinal(1L,
            new DvCodedText("1", new CodePhrase(new TerminologyId("local"), "at0016")));

    private static DvOrdinal ATEMFREQUENZ_SCORE_2 = new DvOrdinal(2L,
            new DvCodedText("2", new CodePhrase(new TerminologyId("local"), "at0017")));

    private static DvOrdinal ATEMFREQUENZ_SCORE_3 = new DvOrdinal(3L,
            new DvCodedText("3", new CodePhrase(new TerminologyId("local"), "at0018")));

    private static DvOrdinal ATEMFREQUENZ_SCORE_4 = new DvOrdinal(4L,
            new DvCodedText("4", new CodePhrase(new TerminologyId("local"), "at0019")));


    private static DvOrdinal NERVENSYSTEM_SCORE_1 = new DvOrdinal(1L,
            new DvCodedText("1", new CodePhrase(new TerminologyId("local"), "at0020")));

    private static DvOrdinal NERVENSYSTEM_SCORE_2 = new DvOrdinal(2L,
            new DvCodedText("2", new CodePhrase(new TerminologyId("local"), "at0021")));

    private static DvOrdinal NERVENSYSTEM_SCORE_3 = new DvOrdinal(3L,
            new DvCodedText("3", new CodePhrase(new TerminologyId("local"), "at0022")));

    private static DvOrdinal NERVENSYSTEM_SCORE_4 = new DvOrdinal(4L,
            new DvCodedText("4", new CodePhrase(new TerminologyId("local"), "at0023")));


    private static DvOrdinal HERZKREISLAUFSYSTEM_SCORE_1 = new DvOrdinal(1L,
            new DvCodedText("1", new CodePhrase(new TerminologyId("local"), "at0024")));

    private static DvOrdinal HERZKREISLAUFSYSTEM_SCORE_2 = new DvOrdinal(2L,
            new DvCodedText("2", new CodePhrase(new TerminologyId("local"), "at0025")));

    private static DvOrdinal HERZKREISLAUFSYSTEM_SCORE_3 = new DvOrdinal(3L,
            new DvCodedText("3", new CodePhrase(new TerminologyId("local"), "at0026")));

    private static DvOrdinal HERZKREISLAUFSYSTEM_SCORE_4 = new DvOrdinal(4L,
            new DvCodedText("4", new CodePhrase(new TerminologyId("local"), "at0027")));


    private static DvOrdinal LEBERFUNKTIONS_SCORE_1 = new DvOrdinal(1L,
            new DvCodedText("1", new CodePhrase(new TerminologyId("local"), "at0028")));

    private static DvOrdinal LEBERFUNKTIONS_SCORE_2 = new DvOrdinal(2L,
            new DvCodedText("2", new CodePhrase(new TerminologyId("local"), "at0029")));

    private static DvOrdinal LEBERFUNKTIONS_SCORE_3 = new DvOrdinal(3L,
            new DvCodedText("3", new CodePhrase(new TerminologyId("local"), "at0030")));

    private static DvOrdinal LEBERFUNKTIONS_SCORE_4 = new DvOrdinal(4L,
            new DvCodedText("4", new CodePhrase(new TerminologyId("local"), "at0031")));


    private static DvOrdinal BLUTGERINNUNGS_SCORE_1 = new DvOrdinal(1L,
            new DvCodedText("1", new CodePhrase(new TerminologyId("local"), "at0032")));

    private static DvOrdinal BLUTGERINNUNGS_SCORE_2 = new DvOrdinal(2L,
            new DvCodedText("2", new CodePhrase(new TerminologyId("local"), "at0033")));

    private static DvOrdinal BLUTGERINNUNGS_SCORE_3 = new DvOrdinal(3L,
            new DvCodedText("3", new CodePhrase(new TerminologyId("local"), "at0034")));

    private static DvOrdinal BLUTGERINNUNGS_SCORE_4 = new DvOrdinal(4L,
            new DvCodedText("4", new CodePhrase(new TerminologyId("local"), "at0035")));


    private static DvOrdinal NIERENFUNKTIONS_SCORE_1 = new DvOrdinal(1L,
            new DvCodedText("1", new CodePhrase(new TerminologyId("local"), "at0036")));

    private static DvOrdinal NIERENFUNKTIONS_SCORE_2 = new DvOrdinal(2L,
            new DvCodedText("2", new CodePhrase(new TerminologyId("local"), "at0037")));

    private static DvOrdinal NIERENFUNKTIONS_SCORE_3 = new DvOrdinal(3L,
            new DvCodedText("3", new CodePhrase(new TerminologyId("local"), "at0038")));

    private static DvOrdinal NIERENFUNKTIONS_SCORE_4 = new DvOrdinal(4L,
            new DvCodedText("4", new CodePhrase(new TerminologyId("local"), "at0039")));


    private FhirObservationSofaScoreOpenehrSofa() {}

    public static SOFAComposition map(Observation fhirObservation) {

        SOFAComposition sofaScoreComposition = new SOFAComposition();

        // set feeder audit
        FeederAudit fa = CommonData.constructFeederAudit(fhirObservation);
        sofaScoreComposition.setFeederAudit(fa);


        SOFAScoreObservation sofaScore = new SOFAScoreObservation();

        DateTimeType fhirEffectiveDateTime = fhirObservation.getEffectiveDateTimeType();

        try {

            String atemtaetigkeitCode = fhirObservation.getComponent().get(0).getValueCodeableConcept().
                    getCoding().get(0).getCode();


            if (atemtaetigkeitCode.equals("resp1")) {
                sofaScore.setAtemtatigkeit(ATEMFREQUENZ_SCORE_1);
            } else if (atemtaetigkeitCode.equals("resp2")) {
                sofaScore.setAtemtatigkeit(ATEMFREQUENZ_SCORE_2);
            } else if (atemtaetigkeitCode.equals("resp3")) {
                sofaScore.setAtemtatigkeit(ATEMFREQUENZ_SCORE_3);
            } else if (atemtaetigkeitCode.equals("resp4")) {
                sofaScore.setAtemtatigkeit(ATEMFREQUENZ_SCORE_4);
            }


            String nervensystemCode = fhirObservation.getComponent().get(1)
                    .getValueCodeableConcept().getCoding().get(0).getCode();

            if (nervensystemCode.equals("ns1")) {
                sofaScore.setZentralesNervensystem(NERVENSYSTEM_SCORE_1);
            } else if (nervensystemCode.equals("ns2")) {
                sofaScore.setZentralesNervensystem(NERVENSYSTEM_SCORE_2);
            } else if (nervensystemCode.equals("ns3")) {
                sofaScore.setZentralesNervensystem(NERVENSYSTEM_SCORE_3);
            } else if (nervensystemCode.equals("ns4")) {
                sofaScore.setZentralesNervensystem(NERVENSYSTEM_SCORE_4);
            }


            String herzKreislaufSystemCode = fhirObservation.getComponent().get(2).getValueCodeableConcept().
                    getCoding().get(0).getCode();

            if (herzKreislaufSystemCode.equals("cvs1")) {
                sofaScore.setHerzKreislaufSystem(HERZKREISLAUFSYSTEM_SCORE_1);
            } else if (nervensystemCode.equals("cvs2")) {
                sofaScore.setHerzKreislaufSystem(HERZKREISLAUFSYSTEM_SCORE_2);
            } else if (nervensystemCode.equals("cvs3")) {
                sofaScore.setHerzKreislaufSystem(HERZKREISLAUFSYSTEM_SCORE_3);
            } else if (nervensystemCode.equals("cvs4")) {
                sofaScore.setHerzKreislaufSystem(HERZKREISLAUFSYSTEM_SCORE_4);
            }


            String leberfunktionsCode = fhirObservation.getComponent().get(3).getValueCodeableConcept().
                    getCoding().get(0).getCode();

            if (leberfunktionsCode.equals("liv1")) {
                sofaScore.setLeberfunktion(LEBERFUNKTIONS_SCORE_1);
            } else if (leberfunktionsCode.equals("liv2")) {
                sofaScore.setLeberfunktion(LEBERFUNKTIONS_SCORE_2);
            } else if (leberfunktionsCode.equals("liv3")) {
                sofaScore.setLeberfunktion(LEBERFUNKTIONS_SCORE_3);
            } else if (leberfunktionsCode.equals("liv4")) {
                sofaScore.setLeberfunktion(LEBERFUNKTIONS_SCORE_4);
            }

            String blutgerinnungsCode = fhirObservation.getComponent().get(4).getValueCodeableConcept().
                    getCoding().get(0).getCode();

            if (blutgerinnungsCode.equals("coa1")) {
                sofaScore.setBlutgerinnung(BLUTGERINNUNGS_SCORE_1);
            } else if (blutgerinnungsCode.equals("coa2")) {
                sofaScore.setBlutgerinnung(BLUTGERINNUNGS_SCORE_2);
            } else if (blutgerinnungsCode.equals("coa3")) {
                sofaScore.setBlutgerinnung(BLUTGERINNUNGS_SCORE_3);
            } else if (blutgerinnungsCode.equals("coa4")) {
                sofaScore.setBlutgerinnung(BLUTGERINNUNGS_SCORE_4);
            }


            String nierenfunktionsCode = fhirObservation.getComponent().get(5).getValueCodeableConcept().
                    getCoding().get(0).getCode();

            if (nierenfunktionsCode.equals("kid1")) {
                sofaScore.setNierenfunktion(NIERENFUNKTIONS_SCORE_1);
            } else if (nierenfunktionsCode.equals("kid2")) {
                sofaScore.setNierenfunktion(NIERENFUNKTIONS_SCORE_2);
            } else if (nierenfunktionsCode.equals("kid3")) {
                sofaScore.setNierenfunktion(NIERENFUNKTIONS_SCORE_3);
            } else if (nierenfunktionsCode.equals("kid4")) {
                sofaScore.setNierenfunktion(NIERENFUNKTIONS_SCORE_4);
            }


            String sofaScoreCode = fhirObservation.getCode().getCoding().get(0).getCode();
            Long sofaScoreCodeLong = Long.parseLong(sofaScoreCode);

            sofaScore.setSofaScoreMagnitude(sofaScoreCodeLong);

        } catch (Exception e) {
            throw new UnprocessableEntityException(e.getMessage());
        }


        sofaScore.setSubject(new PartySelf());
        sofaScore.setLanguage(Language.DE); // FIXME: we need to grab the language from the template

        sofaScore.setTimeValue(fhirEffectiveDateTime.getValueAsCalendar().toZonedDateTime());
        sofaScore.setOriginValue(fhirEffectiveDateTime.getValueAsCalendar().toZonedDateTime());


        sofaScoreComposition.setSofaScore(sofaScore);

        // ======================================================================================
        // Required fields by API
        sofaScoreComposition.setLanguage(Language.DE); // FIXME: we need to grab the language from the template
        sofaScoreComposition.setLocation("test"); // FIXME: Location abfangen?
        sofaScoreComposition.setSettingDefiningcode(SettingDefiningcode.SECONDARY_MEDICAL_CARE);
        sofaScoreComposition.setTerritory(Territory.DE);
        sofaScoreComposition.setCategoryDefiningcode(CategoryDefiningcode.EVENT);

        sofaScoreComposition.setStartTimeValue(fhirEffectiveDateTime.getValueAsCalendar().toZonedDateTime());

        sofaScoreComposition.setComposer(new PartySelf());

        return sofaScoreComposition;
    }

}
