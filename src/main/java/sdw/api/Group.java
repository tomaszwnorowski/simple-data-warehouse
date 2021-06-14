package sdw.api;

public interface Group extends Expression {

  static Group of(String by) {
    return new DefaultGroup(by);
  }
}
