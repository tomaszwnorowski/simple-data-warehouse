package sdw.api;

public interface Filter extends Expression {

  static Filter of(String key, String operator, Object value) {
    return new DefaultFilter(key, operator, value);
  }
}
