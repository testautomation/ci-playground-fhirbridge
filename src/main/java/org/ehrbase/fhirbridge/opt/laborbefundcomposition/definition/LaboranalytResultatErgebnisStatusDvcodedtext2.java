package org.ehrbase.fhirbridge.opt.laborbefundcomposition.definition;

import org.ehrbase.client.annotations.Entity;
import org.ehrbase.client.annotations.OptionFor;
import org.ehrbase.client.annotations.Path;

@Entity
@OptionFor("DV_CODED_TEXT")
public class LaboranalytResultatErgebnisStatusDvcodedtext2 implements LaboranalytResultatErgebnisStatusChoiceOrgEhrbaseEhrEncodeWrappersSnakecase4628b1d3 {
  @Path("|defining_code")
  private ErgebnisStatusDefiningcode ergebnisStatusDefiningcode;

  public void setErgebnisStatusDefiningcode(ErgebnisStatusDefiningcode ergebnisStatusDefiningcode) {
     this.ergebnisStatusDefiningcode = ergebnisStatusDefiningcode;
  }

  public ErgebnisStatusDefiningcode getErgebnisStatusDefiningcode() {
     return this.ergebnisStatusDefiningcode ;
  }
}
