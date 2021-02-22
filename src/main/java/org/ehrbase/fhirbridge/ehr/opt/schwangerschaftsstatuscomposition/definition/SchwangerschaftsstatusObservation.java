package org.ehrbase.fhirbridge.ehr.opt.schwangerschaftsstatuscomposition.definition;

import com.nedap.archie.rm.datastructures.Cluster;
import com.nedap.archie.rm.generic.PartyProxy;
import org.ehrbase.client.annotations.Archetype;
import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.Path;
import org.ehrbase.fhirbridge.ehr.opt.shareddefinition.Language;

import java.time.temporal.TemporalAccessor;
import java.util.List;

@Entity
@Archetype("openEHR-EHR-OBSERVATION.pregnancy_status.v0")
public class SchwangerschaftsstatusObservation {
    @Path("/data[at0001]/events[at0002]/time|value")
    private TemporalAccessor timeValue;

    @Path("/subject")
    private PartyProxy subject;

    @Path("/data[at0001]/origin|value")
    private TemporalAccessor originValue;

    @Path("/language")
    private Language language;

    @Path("/protocol[at0021]/items[at0022]")
    private List<Cluster> erweiterungen;

    @Path("/data[at0001]/events[at0002]/data[at0003]/items[at0011]/value|defining_code")
    private StatusDefiningcode statusDefiningcode;

    public void setTimeValue(TemporalAccessor timeValue) {
        this.timeValue = timeValue;
    }

    public TemporalAccessor getTimeValue() {
        return this.timeValue;
    }

    public void setSubject(PartyProxy subject) {
        this.subject = subject;
    }

    public PartyProxy getSubject() {
        return this.subject;
    }

    public void setOriginValue(TemporalAccessor originValue) {
        this.originValue = originValue;
    }

    public TemporalAccessor getOriginValue() {
        return this.originValue;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return this.language;
    }

    public void setErweiterungen(List<Cluster> erweiterungen) {
        this.erweiterungen = erweiterungen;
    }

    public List<Cluster> getErweiterungen() {
        return this.erweiterungen;
    }

    public void setStatusDefiningcode(StatusDefiningcode statusDefiningcode) {
        this.statusDefiningcode = statusDefiningcode;
    }

    public StatusDefiningcode getStatusDefiningcode() {
        return this.statusDefiningcode;
    }
}
