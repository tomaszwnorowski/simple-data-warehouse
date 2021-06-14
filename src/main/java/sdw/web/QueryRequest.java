package sdw.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import sdw.api.Filter;
import sdw.api.Group;
import sdw.api.Metric;
import sdw.api.Query;

@Data
public class QueryRequest {
  @JsonProperty("metrics")
  private List<QueryMetric> metrics = new ArrayList<>();

  @JsonProperty("filters")
  private List<QueryFilter> filters = new ArrayList<>();

  @JsonProperty("groups")
  private List<QueryGroup> groups = new ArrayList<>();

  @Data
  public static class QueryMetric {
    @JsonProperty("metric")
    private String value;

    @JsonProperty("alias")
    private String alias;
  }

  @Data
  public static class QueryFilter {
    @JsonProperty("key")
    private String key;

    @JsonProperty("operator")
    private String operator;

    @JsonProperty("value")
    private Object value;
  }

  @Data
  public static class QueryGroup {
    @JsonProperty("group")
    private String group;
  }

  public Query toQuery() {
    var builder = Query.builder();

    metrics.stream().map(QueryRequest::toMetric).forEach(builder::metric);
    filters.stream().map(QueryRequest::toFilter).forEach(builder::filter);
    groups.stream().map(QueryRequest::toGroup).forEach(builder::group);

    return builder.build();
  }

  private static Metric toMetric(QueryMetric metric) {
    return Metric.of(
        metric.getValue(), Optional.ofNullable(metric.getAlias()).orElse(metric.getValue()));
  }

  private static Filter toFilter(QueryFilter filter) {
    return Filter.of(filter.getKey(), filter.getOperator(), filter.getValue());
  }

  private static Group toGroup(QueryGroup group) {
    return Group.of(group.getGroup());
  }
}
