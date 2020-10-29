package org.ehrbase.fhirbridge.rest;

import com.nedap.archie.rm.generic.PartySelf;
import org.ehrbase.client.openehrclient.VersionUid;
import org.ehrbase.fhirbridge.opt.diagnosecomposition.DiagnoseComposition;
import org.ehrbase.fhirbridge.opt.intensivmedizinischesmonitoringkorpertemperaturcomposition.IntensivmedizinischesMonitoringKorpertemperaturComposition;
import org.ehrbase.fhirbridge.opt.kennzeichnungerregernachweissarscov2composition.KennzeichnungErregernachweisSARSCoV2Composition;
import org.ehrbase.fhirbridge.opt.laborbefundcomposition.LaborbefundComposition;
import org.ehrbase.fhirbridge.opt.laborbefundcomposition.definition.LaboranalytResultatAnalytResultatDvquantity;
import org.ehrbase.fhirbridge.opt.laborbefundcomposition.definition.LaboranalytResultatCluster;
import org.ehrbase.fhirbridge.opt.laborbefundcomposition.definition.LaborergebnisObservation;
import org.ehrbase.fhirbridge.opt.laborbefundcomposition.definition.StandortJedesEreignisPointEvent;
import org.ehrbase.fhirbridge.opt.shareddefinition.CategoryDefiningcode;
import org.ehrbase.fhirbridge.opt.shareddefinition.Language;
import org.ehrbase.fhirbridge.opt.shareddefinition.SettingDefiningcode;
import org.ehrbase.fhirbridge.opt.shareddefinition.Territory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/rest")
public class OpenEhrController {

    private final EhrbaseService service;

    @Autowired
    public OpenEhrController(EhrbaseService service) {
        this.service = service;
    }

    // probably not to include in end-product, but tester and helper endpoint for now
    @PostMapping(path = "/ehr")
    public ResponseEntity<UUID> postEhr() {
        UUID ehrId = service.createEhr();
        return ResponseEntity.ok(ehrId);
    }

    @PostMapping(path = "/{ehr_id}/labor")
    public ResponseEntity<VersionUid> postLabor(
            @PathVariable(value = "ehr_id") UUID ehrId,
            @RequestBody LaborbefundComposition body) {

        // TESTING      TODO: remove
        body = new LaborbefundComposition();
        body.setComposer(new PartySelf());
        body.setLanguage(Language.DE);
        body.setCategoryDefiningcode(CategoryDefiningcode.EVENT);
        body.setStartTimeValue(OffsetDateTime.now());
        body.setSettingDefiningcode(SettingDefiningcode.NURSING_HOME_CARE);
        body.setTerritory(Territory.DE);

        LaboranalytResultatAnalytResultatDvquantity result_value = new LaboranalytResultatAnalytResultatDvquantity();
        result_value.setAnalytResultatMagnitude(11.00);
        result_value.setAnalytResultatUnits("mg");

        LaboranalytResultatCluster result_cluster = new LaboranalytResultatCluster();
        result_cluster.setAnalytResultat(result_value);

        StandortJedesEreignisPointEvent result_event = new StandortJedesEreignisPointEvent();
        List items = new ArrayList();
        items.add(result_cluster);
        result_event.setLaboranalytResultat(items);

        LaborergebnisObservation result_obs = new LaborergebnisObservation();
        List events = new ArrayList();
        events.add(result_event);
        result_obs.setJedesEreignis(events);

        List observations = new ArrayList();
        observations.add(result_obs);
        body.setLaborergebnis(observations);
        // END

        VersionUid versionUid = service.saveLab(ehrId, body);
        return ResponseEntity.ok(versionUid);
    }

    @PostMapping(path = "/{ehr_id}/diagnosis")
    public ResponseEntity<VersionUid> postDiagnosis(
            @PathVariable(value = "ehr_id") UUID ehrId,
            @RequestBody DiagnoseComposition body) {
        VersionUid versionUid = service.saveDiagnosis(ehrId, body);
        return ResponseEntity.ok(versionUid);
    }

    @PostMapping(path = "/{ehr_id}/temp")
    public ResponseEntity<VersionUid> postTemp(
            @PathVariable(value = "ehr_id") UUID ehrId,
            @RequestBody IntensivmedizinischesMonitoringKorpertemperaturComposition body) {
        VersionUid versionUid = service.saveTemp(ehrId, body);
        return ResponseEntity.ok(versionUid);
    }

    @PostMapping(path = "/{ehr_id}/test")
    public ResponseEntity<VersionUid> postTest(
            @PathVariable(value = "ehr_id") UUID ehrId,
            @RequestBody KennzeichnungErregernachweisSARSCoV2Composition body) {
        VersionUid versionUid = service.saveTest(ehrId, body);
        return ResponseEntity.ok(versionUid);
    }

}
