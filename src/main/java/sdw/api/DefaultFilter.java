package sdw.api;

record DefaultFilter(String key, String operator, Object value) implements Filter {

  @Override
  public String evaluate() {
    if (value instanceof String s) {
      return String.join(" ", key, operator, "'" + s + "'");
    } else {
      return String.join(" ", key, operator, value.toString());
    }
  }
}
