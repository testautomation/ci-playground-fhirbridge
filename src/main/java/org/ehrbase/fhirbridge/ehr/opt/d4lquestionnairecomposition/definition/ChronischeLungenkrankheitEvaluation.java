package org.ehrbase.fhirbridge.ehr.opt.d4lquestionnairecomposition.definition;

import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.generic.PartyProxy;
import java.lang.String;
import java.util.List;
import javax.annotation.processing.Generated;
import org.ehrbase.client.annotations.Archetype;
import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.Path;
import org.ehrbase.client.classgenerator.interfaces.EntryEntity;
import org.ehrbase.client.classgenerator.shareddefinition.Language;
import org.ehrbase.client.classgenerator.shareddefinition.NullFlavour;

@Entity
@Archetype("openEHR-EHR-EVALUATION.problem_diagnosis.v1")
@Generated(
    value = "org.ehrbase.client.classgenerator.ClassGenerator",
    date = "2021-01-25T13:06:41.115325+01:00",
    comments = "https://github.com/ehrbase/openEHR_SDK Version: 1.0.0"
)
public class ChronischeLungenkrankheitEvaluation implements EntryEntity {
  /**
   * Path: Selbstüberwachung/Vor-/Grunderkrankungen/Chronische Lungenkrankheit/Name des Problems/ der Diagnose
   * Description: Namentliche Identifikation des Problems oder der Diagnose.
   */
  @Path("/data[at0001]/items[at0002]/value|value")
  private String nameDesProblemsDerDiagnoseValue;

  /**
   * Path: Selbstüberwachung/Vor-/Grunderkrankungen/Chronische Lungenkrankheit/Structure/Name des Problems/ der Diagnose/null_flavour
   */
  @Path("/data[at0001]/items[at0002]/null_flavour|defining_code")
  private NullFlavour nameDesProblemsDerDiagnoseNullFlavourDefiningCode;

  /**
   * Path: Selbstüberwachung/Vor-/Grunderkrankungen/Chronische Lungenkrankheit/Vorhanden?
   * Description: Beschreibung des Problems oder der Diagnose.
   */
  @Path("/data[at0001]/items[at0009 and name/value='Vorhanden?']/value|defining_code")
  private VorhandenDefiningCode vorhandenDefiningCode;

  /**
   * Path: Selbstüberwachung/Vor-/Grunderkrankungen/Chronische Lungenkrankheit/Structure/Vorhanden?/null_flavour
   */
  @Path("/data[at0001]/items[at0009 and name/value='Vorhanden?']/null_flavour|defining_code")
  private NullFlavour vorhandenNullFlavourDefiningCode;

  /**
   * Path: Selbstüberwachung/Vor-/Grunderkrankungen/Chronische Lungenkrankheit/Anatomische Stelle (strukturiert)
   * Description: Eine strukturierte anatomische Lokalisation des Problems oder der Diagnose.
   * Comment: Verwenden Sie diesen SLOT, um die Archetypen CLUSTER.anatomical_location oder CLUSTER.relative_location einzufügen, wenn die Anforderungen für die Aufnahme der anatomischen Position zur Laufzeit der Anwendung bestimmt werden oder komplexere Modellierungen wie z.B. relative Positionen erforderlich sind. Ist die anatomische Lokalisation über präkoordinierte Codes im Namen des Problems/Diagnose enthalten, wird die Verwendung dieses SLOT überflüssig.
   */
  @Path("/data[at0001]/items[at0039]")
  private List<Cluster> anatomischeStelleStrukturiert;

  /**
   * Path: Selbstüberwachung/Vor-/Grunderkrankungen/Chronische Lungenkrankheit/Spezifische Angaben
   * Description: Zusätzlich benötigte Angaben, welche als eindeutige Merkmale des Problem/der Diagnose erfasst werden sollten.
   * Comment: Hier können strukturierte Angaben über die Einstufung oder das Stadium der Diagnose enthalten sein; diagnostische Kriterien, Klassifizierungskriterien oder formale Bewertungen des Schweregrades wie z.B. "Common Terminology Criteria for Adverse Events".
   */
  @Path("/data[at0001]/items[at0043]")
  private List<Cluster> spezifischeAngaben;

  /**
   * Path: Selbstüberwachung/Vor-/Grunderkrankungen/Chronische Lungenkrankheit/Status
   * Description: Strukturierte Angaben zu standort-, domänen-, episoden- oder workflow-spezifischen Aspekten des diagnostischen Prozesses.
   * Comment: Verwenden Sie Status- oder Kontext-Merkmale mit Vorsicht, da sie in der Praxis variabel eingesetzt werden und die Interoperabilität nicht gewährleistet werden kann, sofern die Verwendung nicht mit der Nutzungsgemeinschaft klar abgestimmt wird. Beispiel: aktiver Status - aktiv, inaktiv, genesen, in Remission; Entwicklungsstatus - initial, interim/working, final; zeitlicher Status - aktuell, vergangen; Episodenstatus - erstmalig, neu, laufend; Aufnahmestatus - Aufnahme, Entlassung; oder Prioritätsstatus - primär, sekundär.
   */
  @Path("/data[at0001]/items[at0046]")
  private List<Cluster> status;

  /**
   * Path: Selbstüberwachung/Vor-/Grunderkrankungen/Chronische Lungenkrankheit/Erweiterung
   * Description: Zusätzliche Informationen zur Erfassung lokaler Inhalte oder Anpassung an andere Referenzmodelle/Formalismen.
   * Comment: Zum Beispiel: Lokaler Informationsbedarf oder zusätzliche Metadaten zur Anpassung an FHIR-Ressourcen oder CIMI-Modelle.
   */
  @Path("/protocol[at0032]/items[at0071]")
  private List<Cluster> erweiterung;

  /**
   * Path: Selbstüberwachung/Vor-/Grunderkrankungen/Chronische Lungenkrankheit/subject
   */
  @Path("/subject")
  private PartyProxy subject;

  /**
   * Path: Selbstüberwachung/Vor-/Grunderkrankungen/Chronische Lungenkrankheit/language
   */
  @Path("/language")
  private Language language;

  /**
   * Path: Selbstüberwachung/Vor-/Grunderkrankungen/Chronische Lungenkrankheit/feeder_audit
   */
  @Path("/feeder_audit")
  private FeederAudit feederAudit;

  public void setNameDesProblemsDerDiagnoseValue(String nameDesProblemsDerDiagnoseValue) {
     this.nameDesProblemsDerDiagnoseValue = nameDesProblemsDerDiagnoseValue;
  }

  public String getNameDesProblemsDerDiagnoseValue() {
     return this.nameDesProblemsDerDiagnoseValue ;
  }

  public void setNameDesProblemsDerDiagnoseNullFlavourDefiningCode(
      NullFlavour nameDesProblemsDerDiagnoseNullFlavourDefiningCode) {
     this.nameDesProblemsDerDiagnoseNullFlavourDefiningCode = nameDesProblemsDerDiagnoseNullFlavourDefiningCode;
  }

  public NullFlavour getNameDesProblemsDerDiagnoseNullFlavourDefiningCode() {
     return this.nameDesProblemsDerDiagnoseNullFlavourDefiningCode ;
  }

  public void setVorhandenDefiningCode(VorhandenDefiningCode vorhandenDefiningCode) {
     this.vorhandenDefiningCode = vorhandenDefiningCode;
  }

  public VorhandenDefiningCode getVorhandenDefiningCode() {
     return this.vorhandenDefiningCode ;
  }

  public void setVorhandenNullFlavourDefiningCode(NullFlavour vorhandenNullFlavourDefiningCode) {
     this.vorhandenNullFlavourDefiningCode = vorhandenNullFlavourDefiningCode;
  }

  public NullFlavour getVorhandenNullFlavourDefiningCode() {
     return this.vorhandenNullFlavourDefiningCode ;
  }

  public void setAnatomischeStelleStrukturiert(List<Cluster> anatomischeStelleStrukturiert) {
     this.anatomischeStelleStrukturiert = anatomischeStelleStrukturiert;
  }

  public List<Cluster> getAnatomischeStelleStrukturiert() {
     return this.anatomischeStelleStrukturiert ;
  }

  public void setSpezifischeAngaben(List<Cluster> spezifischeAngaben) {
     this.spezifischeAngaben = spezifischeAngaben;
  }

  public List<Cluster> getSpezifischeAngaben() {
     return this.spezifischeAngaben ;
  }

  public void setStatus(List<Cluster> status) {
     this.status = status;
  }

  public List<Cluster> getStatus() {
     return this.status ;
  }

  public void setErweiterung(List<Cluster> erweiterung) {
     this.erweiterung = erweiterung;
  }

  public List<Cluster> getErweiterung() {
     return this.erweiterung ;
  }

  public void setSubject(PartyProxy subject) {
     this.subject = subject;
  }

  public PartyProxy getSubject() {
     return this.subject ;
  }

  public void setLanguage(Language language) {
     this.language = language;
  }

  public Language getLanguage() {
     return this.language ;
  }

  public void setFeederAudit(FeederAudit feederAudit) {
     this.feederAudit = feederAudit;
  }

  public FeederAudit getFeederAudit() {
     return this.feederAudit ;
  }
}
