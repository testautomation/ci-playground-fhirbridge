package org.ehrbase.fhirbridge.ehr.opt.befundderblutgasanalysecomposition.definition;

import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.generic.PartyProxy;
import org.ehrbase.client.annotations.Archetype;
import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.Path;
import org.ehrbase.fhirbridge.ehr.opt.shareddefinition.Language;

import java.time.temporal.TemporalAccessor;
import java.util.List;

@Entity
@Archetype("openEHR-EHR-OBSERVATION.laboratory_test_result.v1")
public class LaborergebnisObservation {
  @Path("/protocol[at0004]/items[at0110]")
  private List<Cluster> testDetails;

  @Path("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.laboratory_test_analyte.v1 and name/value='pH-Wert']")
  private PhWertCluster phWert;

  @Path("/language")
  private Language language;

  @Path("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.laboratory_test_analyte.v1 and name/value='Sauerstoffpartialdruck']")
  private SauerstoffpartialdruckCluster sauerstoffpartialdruck;

  @Path("/data[at0001]/events[at0002]/data[at0003]/items[at0118]")
  private List<Cluster> multimediaDarstellung;

  @Path("/data[at0001]/events[at0002]/data[at0003]/items[at0005]/value|defining_code")
  private LabortestBezeichnungDefiningcode labortestBezeichnungDefiningcode;

  @Path("/data[at0001]/events[at0002]/state[at0112]/items[at0114]")
  private List<Cluster> strukturierteErfassungDerStorfaktoren;

  @Path("/data[at0001]/events[at0002]/data[at0003]/items[at0122]")
  private List<Cluster> strukturierteTestdiagnostik;

  @Path("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.laboratory_test_analyte.v1 and name/value='Kohlendioxidpartialdruck']")
  private KohlendioxidpartialdruckCluster kohlendioxidpartialdruck;

  @Path("/data[at0001]/events[at0002]/data[at0003]/items[openEHR-EHR-CLUSTER.laboratory_test_analyte.v1 and name/value='Sauerstoffsättigung']")
  private SauerstoffsattigungCluster sauerstoffsattigung;

  @Path("/protocol[at0004]/items[at0117]")
  private List<Cluster> erweiterung;

  @Path("/data[at0001]/events[at0002]/time|value")
  private TemporalAccessor timeValue;

  @Path("/data[at0001]/events[at0002]/data[at0003]/items[at0065]")
  private List<Cluster> probendetail;

  @Path("/subject")
  private PartyProxy subject;

  @Path("/data[at0001]/origin|value")
  private TemporalAccessor originValue;

  @Path("/protocol[at0004]/items[at0017]")
  private Cluster laborWelchesDenUntersuchungsauftragAnnimmt;

  public void setTestDetails(List<Cluster> testDetails) {
     this.testDetails = testDetails;
  }

  public List<Cluster> getTestDetails() {
     return this.testDetails ;
  }

  public void setPhWert(PhWertCluster phWert) {
     this.phWert = phWert;
  }

  public PhWertCluster getPhWert() {
     return this.phWert ;
  }

  public void setLanguage(Language language) {
     this.language = language;
  }

  public Language getLanguage() {
     return this.language ;
  }

  public void setSauerstoffpartialdruck(SauerstoffpartialdruckCluster sauerstoffpartialdruck) {
     this.sauerstoffpartialdruck = sauerstoffpartialdruck;
  }

  public SauerstoffpartialdruckCluster getSauerstoffpartialdruck() {
     return this.sauerstoffpartialdruck ;
  }

  public void setMultimediaDarstellung(List<Cluster> multimediaDarstellung) {
     this.multimediaDarstellung = multimediaDarstellung;
  }

  public List<Cluster> getMultimediaDarstellung() {
     return this.multimediaDarstellung ;
  }

  public void setLabortestBezeichnungDefiningcode(
      LabortestBezeichnungDefiningcode labortestBezeichnungDefiningcode) {
     this.labortestBezeichnungDefiningcode = labortestBezeichnungDefiningcode;
  }

  public LabortestBezeichnungDefiningcode getLabortestBezeichnungDefiningcode() {
     return this.labortestBezeichnungDefiningcode ;
  }

  public void setStrukturierteErfassungDerStorfaktoren(
      List<Cluster> strukturierteErfassungDerStorfaktoren) {
     this.strukturierteErfassungDerStorfaktoren = strukturierteErfassungDerStorfaktoren;
  }

  public List<Cluster> getStrukturierteErfassungDerStorfaktoren() {
     return this.strukturierteErfassungDerStorfaktoren ;
  }

  public void setStrukturierteTestdiagnostik(List<Cluster> strukturierteTestdiagnostik) {
     this.strukturierteTestdiagnostik = strukturierteTestdiagnostik;
  }

  public List<Cluster> getStrukturierteTestdiagnostik() {
     return this.strukturierteTestdiagnostik ;
  }

  public void setKohlendioxidpartialdruck(
      KohlendioxidpartialdruckCluster kohlendioxidpartialdruck) {
     this.kohlendioxidpartialdruck = kohlendioxidpartialdruck;
  }

  public KohlendioxidpartialdruckCluster getKohlendioxidpartialdruck() {
     return this.kohlendioxidpartialdruck ;
  }

  public void setSauerstoffsattigung(SauerstoffsattigungCluster sauerstoffsattigung) {
     this.sauerstoffsattigung = sauerstoffsattigung;
  }

  public SauerstoffsattigungCluster getSauerstoffsattigung() {
     return this.sauerstoffsattigung ;
  }

  public void setErweiterung(List<Cluster> erweiterung) {
     this.erweiterung = erweiterung;
  }

  public List<Cluster> getErweiterung() {
     return this.erweiterung ;
  }

  public void setTimeValue(TemporalAccessor timeValue) {
     this.timeValue = timeValue;
  }

  public TemporalAccessor getTimeValue() {
     return this.timeValue ;
  }

  public void setProbendetail(List<Cluster> probendetail) {
     this.probendetail = probendetail;
  }

  public List<Cluster> getProbendetail() {
     return this.probendetail ;
  }

  public void setSubject(PartyProxy subject) {
     this.subject = subject;
  }

  public PartyProxy getSubject() {
     return this.subject ;
  }

  public void setOriginValue(TemporalAccessor originValue) {
     this.originValue = originValue;
  }

  public TemporalAccessor getOriginValue() {
     return this.originValue ;
  }

  public void setLaborWelchesDenUntersuchungsauftragAnnimmt(
      Cluster laborWelchesDenUntersuchungsauftragAnnimmt) {
     this.laborWelchesDenUntersuchungsauftragAnnimmt = laborWelchesDenUntersuchungsauftragAnnimmt;
  }

  public Cluster getLaborWelchesDenUntersuchungsauftragAnnimmt() {
     return this.laborWelchesDenUntersuchungsauftragAnnimmt ;
  }
}
