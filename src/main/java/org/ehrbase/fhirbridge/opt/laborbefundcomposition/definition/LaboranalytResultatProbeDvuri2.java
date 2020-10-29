package org.ehrbase.fhirbridge.opt.laborbefundcomposition.definition;

import java.net.URI;
import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.OptionFor;
import org.ehrbase.client.annotations.Path;

@Entity
@OptionFor("DV_URI")
public class LaboranalytResultatProbeDvuri2 implements LaboranalytResultatProbeChoiceOrgEhrbaseEhrEncodeWrappersSnakecase1df98368 {
  @Path("|value")
  private URI probeValue;

  public void setProbeValue(URI probeValue) {
     this.probeValue = probeValue;
  }

  public URI getProbeValue() {
     return this.probeValue ;
  }
}
