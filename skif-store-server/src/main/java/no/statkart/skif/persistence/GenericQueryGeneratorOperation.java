package no.statkart.skif.persistence;

public enum GenericQueryGeneratorOperation {
   NULL("noop"), UNION("union"), UNION_ALL("union all"), INTERSECT("intersect"), MINUS("minus");

   private GenericQueryGeneratorOperation(String svalue) {
      this.operation = svalue;
   }

   private final String operation;

   public String getOperation() {
      return operation;
   }
}
