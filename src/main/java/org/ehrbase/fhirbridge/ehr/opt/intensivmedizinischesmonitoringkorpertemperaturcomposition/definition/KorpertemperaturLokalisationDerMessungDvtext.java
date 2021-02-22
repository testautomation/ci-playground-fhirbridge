package org.ehrbase.fhirbridge.ehr.opt.intensivmedizinischesmonitoringkorpertemperaturcomposition.definition;

import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.OptionFor;
import org.ehrbase.client.annotations.Path;

@Entity
@OptionFor("DV_TEXT")
public class KorpertemperaturLokalisationDerMessungDvtext implements KorpertemperaturLokalisationDerMessungChoice {
    @Path("|value")
    private String lokalisationDerMessungValue;

    public void setLokalisationDerMessungValue(String lokalisationDerMessungValue) {
        this.lokalisationDerMessungValue = lokalisationDerMessungValue;
    }

    public String getLokalisationDerMessungValue() {
        return this.lokalisationDerMessungValue;
    }
}
