package org.ehrbase.fhirbridge.opt.diagnosecomposition.definition;

import java.lang.String;
import org.ehrbase.client.classgenerator.EnumValueSet;

public enum SchweregradDefiningcode implements EnumValueSet {
  SCHWER("Schwer", "Das Problem oder die Diagnose verhindert die normale Aktivität oder verursacht schwerwiegende gesundheitliche Schäden, falls es nicht behandelt wird.", "local", "at0049"),

  MAIG("Mäßig", "Das Problem oder die Diagnose beeinträchtigt die normale Aktivität oder verursacht bleibende gesundheitliche Schäden, falls es nicht behandelt wird.", "local", "at0048"),

  LEICHT("Leicht", "Das Problem oder die Diagnose beeinträchtigt die normale Aktivität nicht, bzw. verursacht nicht bleibende gesundheitliche Schäden, falls es nicht behandelt wird.", "local", "at0047");

  private String value;

  private String description;

  private String terminologyId;

  private String code;

  SchweregradDefiningcode(String value, String description, String terminologyId, String code) {
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
