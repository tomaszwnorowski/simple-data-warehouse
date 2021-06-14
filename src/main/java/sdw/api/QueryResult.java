package sdw.api;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class QueryResult {
  @Singular private List<String> aliases;

  @Singular private List<List<Object>> records;
}
