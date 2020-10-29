package org.ehrbase.fhirbridge.opt.diagnosecomposition.definition;

import java.lang.String;
import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.OptionFor;
import org.ehrbase.client.annotations.Path;

@Entity
@OptionFor("DV_TEXT")
public class AnatomischeLokalisationDiagnostischeSicherheitDvtext implements AnatomischeLokalisationDiagnostischeSicherheitChoice {
  @Path("|value")
  private String diagnostischeSicherheitValue;

  public void setDiagnostischeSicherheitValue(String diagnostischeSicherheitValue) {
     this.diagnostischeSicherheitValue = diagnostischeSicherheitValue;
  }

  public String getDiagnostischeSicherheitValue() {
     return this.diagnostischeSicherheitValue ;
  }
}
