package sdw.api;

record DefaultGroup(String column) implements Group {

  @Override
  public String evaluate() {
    return column;
  }
}
