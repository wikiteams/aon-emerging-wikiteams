package test;

import repast.simphony.parameter.StringConverter;

public class ModelConverter implements StringConverter<Model> {

  /**
   * Converts the specified object to a String representation and
   * returns that representation. The representation should be such
   * that <code>fromString</code> can recreate the Object.
   *
   * @param obj the Object to convert.
   * @return a String representation of the Object.
   */
  public String toString(Model obj) {
    return obj.getName();
  }

  /**
   * Creates an Object from a String representation.
   *
   * @param strRep the string representation
   * @return the created Object.
   */
  public Model fromString(String strRep) {
//    int index = strRep.indexOf(" ");
//    String first = strRep.substring(0, index);
//    String last = strRep.substring(index + 1, strRep.length());
    return new Model(strRep);
  }
}