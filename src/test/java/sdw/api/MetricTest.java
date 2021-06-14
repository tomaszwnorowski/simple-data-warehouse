package sdw.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sdw.test.UnitTest;

@UnitTest
class MetricTest {

  @Test
  void givenMetricWithoutAlias_whenRetrieveAlias_thenUseExpressionAsAlias() {
    // given
    var metric = Metric.of("column_name");

    // when
    var alias = metric.alias();

    // then
    assertThat(alias).isEqualTo("column_name");
  }

  @Test
  void givenMetricWithAlias_whenRetrieveAlias_thenUseAlias() {
    // given
    var metric = Metric.of("column_name", "column_alias");

    // when
    var alias = metric.alias();

    // then
    assertThat(alias).isEqualTo("column_alias");
  }
}
