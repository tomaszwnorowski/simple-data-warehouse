package sdw.warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import sdw.api.*;

@Component
public class MyBatisSimpleDataWarehouse implements SimpleDataWarehouse {
  private final SimpleDataWarehouseMapper mapper;

  public MyBatisSimpleDataWarehouse(SimpleDataWarehouseMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public QueryResult query(Query query) {
    try {
      return toQueryResult(query, mapper.query(query));
    } catch (Exception ex) {
      throw new SimpleDataWarehouseException("Unable to perform query", ex);
    }
  }

  private QueryResult toQueryResult(Query query, List<Map<String, Object>> result) {
    return QueryResult.builder()
        .aliases(query.getMetrics().stream().map(Metric::alias).collect(Collectors.toList()))
        .records(result.stream().map(e -> new ArrayList<>(e.values())).collect(Collectors.toList()))
        .build();
  }
}
