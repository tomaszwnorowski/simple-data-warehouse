package sdw.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sdw.test.UnitTest;

@UnitTest
class FilterTest {

  @Test
  void givenStringFilter_whenEvaluate_thenSurroundWithApostrophe() {
    // given
    var filter = Filter.of("key", "=", "string");

    // when
    var expression = filter.evaluate();

    // then
    assertThat(expression).contains("'string'");
  }

  @Test
  void givenNonStringFilter_whenEvaluate_thenDontSurroundWithApostrophe() {
    // given
    var filter = Filter.of("key", "=", 2);

    // when
    var expression = filter.evaluate();

    // then
    assertThat(expression).contains("2");
  }
}
