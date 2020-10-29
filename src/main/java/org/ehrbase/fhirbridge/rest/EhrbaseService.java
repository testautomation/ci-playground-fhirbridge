package org.ehrbase.fhirbridge.rest;

import com.nedap.archie.rm.ehr.EhrStatus;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;

import org.ehrbase.client.aql.query.Query;
import org.ehrbase.client.aql.record.Record1;
import org.ehrbase.client.aql.record.Record2;
import org.ehrbase.client.openehrclient.VersionUid;
import org.ehrbase.client.openehrclient.defaultrestclient.DefaultRestClient;
import org.ehrbase.fhirbridge.opt.beatmungswertecomposition.BeatmungswerteComposition;
import org.ehrbase.fhirbridge.opt.blutdruckcomposition.BlutdruckComposition;
import org.ehrbase.fhirbridge.opt.diagnosecomposition.DiagnoseComposition;
import org.ehrbase.fhirbridge.opt.herzfrequenzcomposition.HerzfrequenzComposition;
import org.ehrbase.fhirbridge.opt.intensivmedizinischesmonitoringkorpertemperaturcomposition.IntensivmedizinischesMonitoringKorpertemperaturComposition;
import org.ehrbase.fhirbridge.opt.kennzeichnungerregernachweissarscov2composition.KennzeichnungErregernachweisSARSCoV2Composition;
import org.ehrbase.fhirbridge.opt.laborbefundcomposition.LaborbefundComposition;
import org.ehrbase.fhirbridge.opt.sofacomposition.SOFAComposition;
import org.ehrbase.fhirbridge.opt.prozedurcomposition.ProzedurComposition;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EhrbaseService {

    private final DefaultRestClient client;

    public EhrbaseService(DefaultRestClient client) {
        this.client = client;
    }

    public UUID createEhr() {
        return client.ehrEndpoint().createEhr();
    }

    public UUID createEhr(EhrStatus ehrStatus)
    {
        return client.ehrEndpoint().createEhr(ehrStatus);
    }

    public boolean ehrExistsBySubjectId(String subjectId)
    {
        Query<Record1<String>> query = Query.buildNativeQuery("SELECT e/ehr_id/value FROM EHR e WHERE e/ehr_status/subject/external_ref/id/value = '"+ subjectId +"'", String.class);
        List<Record1<String>> results = new ArrayList<>();
        try {
            results = client.aqlEndpoint().execute(query);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        /*
        Query<Record2<String, OffsetDateTime>> query = Query.buildNativeQuery("SELECT e/ehr_id/value, e/time_created/value FROM EHR e WHERE e/ehr_status/subject/external_ref/id/value = '"+ subject_id +"'", String.class, OffsetDateTime.class);
        List<Record2<String, OffsetDateTime>> results = new ArrayList<Record2<String, OffsetDateTime>>();

        try {
            System.out.println(client.aqlEndpoint().getClass().toString());
            results = client.aqlEndpoint().execute(query);
            System.out.println("what");
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println(e.getCause().getMessage());
            e.printStackTrace();
        }
        */

        return !results.isEmpty();
    }

    public DefaultRestClient getClient() {
        return client;
    }

    public String ehrIdBySubjectId(String subjectId)
    {
        Query<Record1<String>> query = Query.buildNativeQuery("SELECT e/ehr_id/value FROM EHR e WHERE e/ehr_status/subject/external_ref/id/value = '"+ subjectId +"'", String.class);
        List<Record1<String>> results = new ArrayList<>();
        try {
            results = client.aqlEndpoint().execute(query);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        if (!results.isEmpty())
            return results.get(0).value1();
        else
            return null;
    }

    public VersionUid saveLab(UUID ehrId, LaborbefundComposition composition) {
        // TODO invoke post processing

        client.compositionEndpoint(ehrId).mergeCompositionEntity(composition);

        return composition.getVersionUid();
    }

    public VersionUid saveDiagnosis(UUID ehrId, DiagnoseComposition composition) {

        client.compositionEndpoint(ehrId).mergeCompositionEntity(composition);
        return composition.getVersionUid();
    }

    public VersionUid saveTemp(UUID ehrId, IntensivmedizinischesMonitoringKorpertemperaturComposition composition) {

        client.compositionEndpoint(ehrId).mergeCompositionEntity(composition);
        return composition.getVersionUid();
    }
    
    public VersionUid saveFIO2(UUID ehrId, BeatmungswerteComposition composition) {

        client.compositionEndpoint(ehrId).mergeCompositionEntity(composition);
        return composition.getVersionUid();
    }

    public VersionUid saveHeartRate(UUID ehrId, HerzfrequenzComposition composition) {

        try
        {
            client.compositionEndpoint(ehrId).mergeCompositionEntity(composition);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new UnprocessableEntityException("There was a Error in saveHeartRate", e);
        }

        return composition.getVersionUid();
    }

    public VersionUid saveTest(UUID ehrId, KennzeichnungErregernachweisSARSCoV2Composition composition) {

        client.compositionEndpoint(ehrId).mergeCompositionEntity(composition);
        return composition.getVersionUid();
    }

    public VersionUid saveBloodPressure(UUID ehrId, BlutdruckComposition composition) {

        client.compositionEndpoint(ehrId).mergeCompositionEntity(composition);
        return composition.getVersionUid();
    }

    public VersionUid saveSOFAScore(UUID ehrId, SOFAComposition composition) {
        // TODO invoke post processing

        client.compositionEndpoint(ehrId).mergeCompositionEntity(composition);

        return composition.getVersionUid();
    }

    public VersionUid saveProcedure(UUID ehrId, ProzedurComposition composition) {

        client.compositionEndpoint(ehrId).mergeCompositionEntity(composition);

        return composition.getVersionUid();
    }

}

