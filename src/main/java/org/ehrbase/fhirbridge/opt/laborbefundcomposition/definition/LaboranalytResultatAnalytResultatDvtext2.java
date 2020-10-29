package org.ehrbase.fhirbridge.opt.laborbefundcomposition.definition;

import java.lang.String;
import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.OptionFor;
import org.ehrbase.client.annotations.Path;

@Entity
@OptionFor("DV_TEXT")
public class LaboranalytResultatAnalytResultatDvtext2 implements LaboranalytResultatAnalytResultatChoiceOrgEhrbaseEhrEncodeWrappersSnakecase77cf3f8b {
  @Path("|value")
  private String analytResultatValue;

  public void setAnalytResultatValue(String analytResultatValue) {
     this.analytResultatValue = analytResultatValue;
  }

  public String getAnalytResultatValue() {
     return this.analytResultatValue ;
  }
}
