package sdw.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sdw.api.SimpleDataWarehouse;
import sdw.api.SimpleDataWarehouseException;

@RequiredArgsConstructor
@RestController
public class SimpleDataWarehouseController {

  private final SimpleDataWarehouse warehouse;

  @PostMapping("/v1/queries")
  public QueryResponse query(@RequestBody QueryRequest request) {
    return QueryResponse.of(warehouse.query(request.toQuery()));
  }

  @ExceptionHandler(value = {SimpleDataWarehouseException.class})
  public ResponseEntity<Object> handleSimpleDataWarehouseException() {
    return ResponseEntity.badRequest()
        .body(
            QueryException.builder()
                .title("Unable to perform query")
                .detail("Query might be invalid from datastore engine point of view")
                .instance("/v1/queries")
                .build());
  }
}
