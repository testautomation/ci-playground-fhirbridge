package org.ehrbase.fhirbridge.opt.beatmungswertecomposition.definition;

import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.generic.PartyProxy;
import java.time.temporal.TemporalAccessor;
import org.ehrbase.client.aql.containment.Containment;
import org.ehrbase.client.aql.field.AqlFieldImp;
import org.ehrbase.client.aql.field.SelectAqlField;
import org.ehrbase.fhirbridge.opt.shareddefinition.Language;

public class BeobachtungenAmBeatmungsgeratObservationContainment extends Containment {
  public SelectAqlField<BeobachtungenAmBeatmungsgeratObservation> BEOBACHTUNGEN_AM_BEATMUNGSGERAT_OBSERVATION = new AqlFieldImp<BeobachtungenAmBeatmungsgeratObservation>(BeobachtungenAmBeatmungsgeratObservation.class, "", "BeobachtungenAmBeatmungsgeratObservation", BeobachtungenAmBeatmungsgeratObservation.class, this);

  public SelectAqlField<EingeatmeterSauerstoffCluster> EINGEATMETER_SAUERSTOFF = new AqlFieldImp<EingeatmeterSauerstoffCluster>(BeobachtungenAmBeatmungsgeratObservation.class, "/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.inspired_oxygen.v1]", "eingeatmeterSauerstoff", EingeatmeterSauerstoffCluster.class, this);

  public SelectAqlField<Cluster> EINSTELLUNGEN_DES_BEATMUNGSGERATES = new AqlFieldImp<Cluster>(BeobachtungenAmBeatmungsgeratObservation.class, "/data[at0001]/events[at0002]/state[at0010]/items[at0011]", "einstellungenDesBeatmungsgerates", Cluster.class, this);

  public SelectAqlField<Language> LANGUAGE = new AqlFieldImp<Language>(BeobachtungenAmBeatmungsgeratObservation.class, "/language", "language", Language.class, this);

  public SelectAqlField<TemporalAccessor> TIME_VALUE = new AqlFieldImp<TemporalAccessor>(BeobachtungenAmBeatmungsgeratObservation.class, "/data[at0001]/events[at0002]/time|value", "timeValue", TemporalAccessor.class, this);

  public SelectAqlField<Cluster> GERAT = new AqlFieldImp<Cluster>(BeobachtungenAmBeatmungsgeratObservation.class, "/protocol[at0012]/items[at0014]", "gerat", Cluster.class, this);

  public SelectAqlField<PartyProxy> SUBJECT = new AqlFieldImp<PartyProxy>(BeobachtungenAmBeatmungsgeratObservation.class, "/subject", "subject", PartyProxy.class, this);

  public SelectAqlField<TemporalAccessor> ORIGIN_VALUE = new AqlFieldImp<TemporalAccessor>(BeobachtungenAmBeatmungsgeratObservation.class, "/data[at0001]/origin|value", "originValue", TemporalAccessor.class, this);

  private BeobachtungenAmBeatmungsgeratObservationContainment() {
    super("openEHR-EHR-OBSERVATION.ventilator_vital_signs.v0");
  }

  public static BeobachtungenAmBeatmungsgeratObservationContainment getInstance() {
    return new BeobachtungenAmBeatmungsgeratObservationContainment();
  }
}
