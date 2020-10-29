package org.ehrbase.fhirbridge.opt.laborbefundcomposition.definition;

import com.nedap.archie.rm.datavalues.DvIdentifier;
import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.OptionFor;
import org.ehrbase.client.annotations.Path;

@Entity
@OptionFor("DV_IDENTIFIER")
public class LaboranalytResultatProbeDvidentifier2 implements LaboranalytResultatProbeChoiceOrgEhrbaseEhrEncodeWrappersSnakecase1df98368 {
  @Path("")
  private DvIdentifier probe;

  public void setProbe(DvIdentifier probe) {
     this.probe = probe;
  }

  public DvIdentifier getProbe() {
     return this.probe ;
  }
}
