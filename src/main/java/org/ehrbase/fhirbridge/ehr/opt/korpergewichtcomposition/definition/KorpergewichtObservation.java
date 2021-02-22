package org.ehrbase.fhirbridge.ehr.opt.korpergewichtcomposition.definition;

import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.generic.PartyProxy;
import org.ehrbase.client.annotations.Archetype;
import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.Path;
import org.ehrbase.fhirbridge.ehr.opt.shareddefinition.Language;

import java.time.temporal.TemporalAccessor;
import java.util.List;

@Entity
@Archetype("openEHR-EHR-OBSERVATION.body_weight.v2")
public class KorpergewichtObservation {
    @Path("/protocol[at0015]/items[at0027]")
    private List<Cluster> erweiterung;

    @Path("/language")
    private Language language;

    @Path("/data[at0002]/origin|value")
    private TemporalAccessor originValue;

    @Path("/protocol[at0015]/items[at0020]")
    private Cluster gerat;

    @Path("/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value|magnitude")
    private Double gewichtMagnitude;

    @Path("/data[at0002]/events[at0003]/data[at0001]/items[at0004]/value|units")
    private String gewichtUnits;

    @Path("/subject")
    private PartyProxy subject;

    @Path("/data[at0002]/events[at0003]/time|value")
    private TemporalAccessor timeValue;

    public void setErweiterung(List<Cluster> erweiterung) {
        this.erweiterung = erweiterung;
    }

    public List<Cluster> getErweiterung() {
        return this.erweiterung;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return this.language;
    }

    public void setOriginValue(TemporalAccessor originValue) {
        this.originValue = originValue;
    }

    public TemporalAccessor getOriginValue() {
        return this.originValue;
    }

    public void setGerat(Cluster gerat) {
        this.gerat = gerat;
    }

    public Cluster getGerat() {
        return this.gerat;
    }

    public void setGewichtMagnitude(Double gewichtMagnitude) {
        this.gewichtMagnitude = gewichtMagnitude;
    }

    public Double getGewichtMagnitude() {
        return this.gewichtMagnitude;
    }

    public void setGewichtUnits(String gewichtUnits) {
        this.gewichtUnits = gewichtUnits;
    }

    public String getGewichtUnits() {
        return this.gewichtUnits;
    }

    public void setSubject(PartyProxy subject) {
        this.subject = subject;
    }

    public PartyProxy getSubject() {
        return this.subject;
    }

    public void setTimeValue(TemporalAccessor timeValue) {
        this.timeValue = timeValue;
    }

    public TemporalAccessor getTimeValue() {
        return this.timeValue;
    }
}
