package org.ehrbase.fhirbridge.ehr.opt.befundderblutgasanalysecomposition.definition;

import com.nedap.archie.rm.datastructures.Cluster;
import org.ehrbase.client.aql.containment.Containment;
import org.ehrbase.client.aql.field.AqlFieldImp;
import org.ehrbase.client.aql.field.ListAqlFieldImp;
import org.ehrbase.client.aql.field.ListSelectAqlField;
import org.ehrbase.client.aql.field.SelectAqlField;

public class KohlendioxidpartialdruckClusterContainment extends Containment {
  public SelectAqlField<KohlendioxidpartialdruckCluster> KOHLENDIOXIDPARTIALDRUCK_CLUSTER = new AqlFieldImp<KohlendioxidpartialdruckCluster>(KohlendioxidpartialdruckCluster.class, "", "KohlendioxidpartialdruckCluster", KohlendioxidpartialdruckCluster.class, this);

  public SelectAqlField<String> ERGEBNIS_STATUS_VALUE = new AqlFieldImp<String>(KohlendioxidpartialdruckCluster.class, "/items[at0005]/value|value", "ergebnisStatusValue", String.class, this);

  public SelectAqlField<UntersuchterAnalytDefiningcode3> UNTERSUCHTER_ANALYT_DEFININGCODE = new AqlFieldImp<UntersuchterAnalytDefiningcode3>(KohlendioxidpartialdruckCluster.class, "/items[at0024]/value|defining_code", "untersuchterAnalytDefiningcode", UntersuchterAnalytDefiningcode3.class, this);

  public SelectAqlField<Double> ANALYT_RESULTAT_MAGNITUDE = new AqlFieldImp<Double>(KohlendioxidpartialdruckCluster.class, "/items[at0001]/value|magnitude", "analytResultatMagnitude", Double.class, this);

  public SelectAqlField<String> ANALYT_RESULTAT_UNITS = new AqlFieldImp<String>(KohlendioxidpartialdruckCluster.class, "/items[at0001]/value|units", "analytResultatUnits", String.class, this);

  public ListSelectAqlField<Cluster> ANALYSEERGEBNIS_DETAILS = new ListAqlFieldImp<Cluster>(KohlendioxidpartialdruckCluster.class, "/items[at0014]", "analyseergebnisDetails", Cluster.class, this);

  private KohlendioxidpartialdruckClusterContainment() {
    super("openEHR-EHR-CLUSTER.laboratory_test_analyte.v1");
  }

  public static KohlendioxidpartialdruckClusterContainment getInstance() {
    return new KohlendioxidpartialdruckClusterContainment();
  }
}
