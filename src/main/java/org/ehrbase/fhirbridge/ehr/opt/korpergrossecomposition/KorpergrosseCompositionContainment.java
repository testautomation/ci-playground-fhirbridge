package org.ehrbase.fhirbridge.ehr.opt.korpergrossecomposition;

import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.rm.generic.PartyIdentified;
import com.nedap.archie.rm.generic.PartyProxy;
import org.ehrbase.client.aql.containment.Containment;
import org.ehrbase.client.aql.field.AqlFieldImp;
import org.ehrbase.client.aql.field.ListAqlFieldImp;
import org.ehrbase.client.aql.field.ListSelectAqlField;
import org.ehrbase.client.aql.field.SelectAqlField;
import org.ehrbase.fhirbridge.ehr.opt.korpergrossecomposition.definition.GrosseLangeObservation;
import org.ehrbase.fhirbridge.ehr.opt.korpergrossecomposition.definition.StatusDefiningcode;
import org.ehrbase.fhirbridge.ehr.opt.shareddefinition.CategoryDefiningcode;
import org.ehrbase.fhirbridge.ehr.opt.shareddefinition.Language;
import org.ehrbase.fhirbridge.ehr.opt.shareddefinition.SettingDefiningcode;
import org.ehrbase.fhirbridge.ehr.opt.shareddefinition.Territory;

import java.time.temporal.TemporalAccessor;

public class KorpergrosseCompositionContainment extends Containment {
    public SelectAqlField<KorpergrosseComposition> KORPERGROSSE_COMPOSITION = new AqlFieldImp<KorpergrosseComposition>(KorpergrosseComposition.class, "", "KorpergrosseComposition", KorpergrosseComposition.class, this);

    public SelectAqlField<GrosseLangeObservation> GROSSE_LANGE = new AqlFieldImp<GrosseLangeObservation>(KorpergrosseComposition.class, "/content[openEHR-EHR-OBSERVATION.height.v2]", "grosseLange", GrosseLangeObservation.class, this);

    public SelectAqlField<TemporalAccessor> END_TIME_VALUE = new AqlFieldImp<TemporalAccessor>(KorpergrosseComposition.class, "/context/end_time|value", "endTimeValue", TemporalAccessor.class, this);

    public ListSelectAqlField<Participation> PARTICIPATIONS = new ListAqlFieldImp<Participation>(KorpergrosseComposition.class, "/context/participations", "participations", Participation.class, this);

    public SelectAqlField<Language> LANGUAGE = new AqlFieldImp<Language>(KorpergrosseComposition.class, "/language", "language", Language.class, this);

    public SelectAqlField<PartyIdentified> HEALTH_CARE_FACILITY = new AqlFieldImp<PartyIdentified>(KorpergrosseComposition.class, "/context/health_care_facility", "healthCareFacility", PartyIdentified.class, this);

    public SelectAqlField<StatusDefiningcode> STATUS_DEFININGCODE = new AqlFieldImp<StatusDefiningcode>(KorpergrosseComposition.class, "/context/other_context[at0001]/items[at0004]/value|defining_code", "statusDefiningcode", StatusDefiningcode.class, this);

    public SelectAqlField<String> KATEGORIE_VALUE = new AqlFieldImp<String>(KorpergrosseComposition.class, "/context/other_context[at0001]/items[at0005]/value|value", "kategorieValue", String.class, this);

    public SelectAqlField<Territory> TERRITORY = new AqlFieldImp<Territory>(KorpergrosseComposition.class, "/territory", "territory", Territory.class, this);

    public SelectAqlField<TemporalAccessor> START_TIME_VALUE = new AqlFieldImp<TemporalAccessor>(KorpergrosseComposition.class, "/context/start_time|value", "startTimeValue", TemporalAccessor.class, this);

    public SelectAqlField<PartyProxy> COMPOSER = new AqlFieldImp<PartyProxy>(KorpergrosseComposition.class, "/composer", "composer", PartyProxy.class, this);

    public SelectAqlField<SettingDefiningcode> SETTING_DEFININGCODE = new AqlFieldImp<SettingDefiningcode>(KorpergrosseComposition.class, "/context/setting|defining_code", "settingDefiningcode", SettingDefiningcode.class, this);

    public SelectAqlField<FeederAudit> FEEDER_AUDIT = new AqlFieldImp<FeederAudit>(KorpergrosseComposition.class, "/feeder_audit", "feederAudit", FeederAudit.class, this);

    public SelectAqlField<String> LOCATION = new AqlFieldImp<String>(KorpergrosseComposition.class, "/context/location", "location", String.class, this);

    public SelectAqlField<CategoryDefiningcode> CATEGORY_DEFININGCODE = new AqlFieldImp<CategoryDefiningcode>(KorpergrosseComposition.class, "/category|defining_code", "categoryDefiningcode", CategoryDefiningcode.class, this);

    public ListSelectAqlField<Cluster> ERWEITERUNG = new ListAqlFieldImp<Cluster>(KorpergrosseComposition.class, "/context/other_context[at0001]/items[at0002]", "erweiterung", Cluster.class, this);

    private KorpergrosseCompositionContainment() {
        super("openEHR-EHR-COMPOSITION.registereintrag.v1");
    }

    public static KorpergrosseCompositionContainment getInstance() {
        return new KorpergrosseCompositionContainment();
    }
}
