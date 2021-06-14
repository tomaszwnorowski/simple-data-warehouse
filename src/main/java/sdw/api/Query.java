package sdw.api;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class Query {
  @Singular private List<Metric> metrics;

  @Singular private List<Filter> filters;

  @Singular private List<Group> groups;
}
