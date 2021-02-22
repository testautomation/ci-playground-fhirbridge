package org.ehrbase.fhirbridge.ehr.opt.geccoradiologischerbefundcomposition.definition;

import java.lang.String;
import org.ehrbase.client.classgenerator.EnumValueSet;

public enum StatusDefiningCode implements EnumValueSet {
  VORLAEUFIG("vorläufig", "*", "local", "at0011"),

  FINAL("final", "*", "local", "at0012"),

  REGISTRIERT("registriert", "*", "local", "at0010"),

  GEAENDERT("geändert", "*", "local", "at0013");

  private String value;

  private String description;

  private String terminologyId;

  private String code;

  StatusDefiningCode(String value, String description, String terminologyId, String code) {
    this.value = value;
    this.description = description;
    this.terminologyId = terminologyId;
    this.code = code;
  }

  public String getValue() {
     return this.value ;
  }

  public String getDescription() {
     return this.description ;
  }

  public String getTerminologyId() {
     return this.terminologyId ;
  }

  public String getCode() {
     return this.code ;
  }
}
