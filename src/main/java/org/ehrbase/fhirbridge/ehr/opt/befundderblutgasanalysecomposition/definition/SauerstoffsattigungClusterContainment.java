package org.ehrbase.fhirbridge.ehr.opt.befundderblutgasanalysecomposition.definition;

import com.nedap.archie.rm.datastructures.Cluster;
import org.ehrbase.client.aql.containment.Containment;
import org.ehrbase.client.aql.field.AqlFieldImp;
import org.ehrbase.client.aql.field.ListAqlFieldImp;
import org.ehrbase.client.aql.field.ListSelectAqlField;
import org.ehrbase.client.aql.field.SelectAqlField;

public class SauerstoffsattigungClusterContainment extends Containment {
  public SelectAqlField<SauerstoffsattigungCluster> SAUERSTOFFSATTIGUNG_CLUSTER = new AqlFieldImp<SauerstoffsattigungCluster>(SauerstoffsattigungCluster.class, "", "SauerstoffsattigungCluster", SauerstoffsattigungCluster.class, this);

  public SelectAqlField<String> ERGEBNIS_STATUS_VALUE = new AqlFieldImp<String>(SauerstoffsattigungCluster.class, "/items[at0005]/value|value", "ergebnisStatusValue", String.class, this);

  public SelectAqlField<UntersuchterAnalytDefiningcode4> UNTERSUCHTER_ANALYT_DEFININGCODE = new AqlFieldImp<UntersuchterAnalytDefiningcode4>(SauerstoffsattigungCluster.class, "/items[at0024]/value|defining_code", "untersuchterAnalytDefiningcode", UntersuchterAnalytDefiningcode4.class, this);

  public SelectAqlField<Double> ANALYT_RESULTAT_MAGNITUDE = new AqlFieldImp<Double>(SauerstoffsattigungCluster.class, "/items[at0001]/value|magnitude", "analytResultatMagnitude", Double.class, this);

  public SelectAqlField<String> ANALYT_RESULTAT_UNITS = new AqlFieldImp<String>(SauerstoffsattigungCluster.class, "/items[at0001]/value|units", "analytResultatUnits", String.class, this);

  public ListSelectAqlField<Cluster> ANALYSEERGEBNIS_DETAILS = new ListAqlFieldImp<Cluster>(SauerstoffsattigungCluster.class, "/items[at0014]", "analyseergebnisDetails", Cluster.class, this);

  private SauerstoffsattigungClusterContainment() {
    super("openEHR-EHR-CLUSTER.laboratory_test_analyte.v1");
  }

  public static SauerstoffsattigungClusterContainment getInstance() {
    return new SauerstoffsattigungClusterContainment();
  }
}
