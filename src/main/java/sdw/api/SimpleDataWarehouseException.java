package sdw.api;

public class SimpleDataWarehouseException extends RuntimeException {
  public SimpleDataWarehouseException(String message, Exception ex) {
    super(message, ex);
  }
}
