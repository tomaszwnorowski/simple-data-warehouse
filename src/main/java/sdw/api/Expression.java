package sdw.api;

// Generic interface that support both static and dynamic parts of SQL expression
public interface Expression {

  String evaluate();
}
