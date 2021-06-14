package sdw.api;

record DefaultMetric(String expression, String alias) implements Metric {

  @Override
  public String evaluate() {
    return expression;
  }
}
