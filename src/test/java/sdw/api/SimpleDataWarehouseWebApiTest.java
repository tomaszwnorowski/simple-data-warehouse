package sdw.api;

import static org.awaitility.Awaitility.await;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StreamUtils;
import sdw.ingestion.IngestionCompletedEvent;
import sdw.test.IntegrationTest;
import sdw.warehouse.SimpleDataWarehouseMapper;

@SpringBootTest
@IntegrationTest
@AutoConfigureMockMvc
class SimpleDataWarehouseWebApiTest {

  private static final int TEST_DATA_ROWS_NUM = 23198;

  @Autowired MockMvc mockMvc;

  @Autowired IngestionCompletedListener listener;

  @Autowired SimpleDataWarehouseMapper mapper;

  @Test
  void totalClicksRequest() throws Exception {
    // wait for data ingestion to be completed
    await()
        .atMost(Duration.ofSeconds(10))
        .pollDelay(Duration.ofSeconds(1))
        // we continue test when we receive event that ingestion has been completed
        // or if we already contain test data (to make this test idempotent)
        .until(() -> listener.getCompleted().get() || (mapper.count() == TEST_DATA_ROWS_NUM));

    // verify whether response is according to specification
    mockMvc
        .perform(
            post("/v1/queries")
                .contentType(APPLICATION_JSON_VALUE)
                .content(toByteArray(new ClassPathResource("requests/total-clicks-request.json"))))
        .andExpect(status().isOk())
        .andExpect(
            content().json(toJson(new ClassPathResource("responses/total-clicks-response.json"))));
  }

  @Test
  void invalidQuery() throws Exception {
    // verify whether response is according to specification
    mockMvc
        .perform(
            post("/v1/queries")
                .contentType(APPLICATION_JSON_VALUE)
                .content(toByteArray(new ClassPathResource("requests/invalid-request.json"))))
        .andExpect(status().isBadRequest())
        .andExpect(
            content().json(toJson(new ClassPathResource("responses/invalid-response.json"))));
  }

  private static String toJson(Resource resource) {
    return new String(toByteArray(resource), StandardCharsets.UTF_8);
  }

  private static byte[] toByteArray(Resource resource) {
    try (InputStream input = new BufferedInputStream(resource.getInputStream())) {
      return StreamUtils.copyToByteArray(input);
    } catch (IOException e) {
      throw new IllegalStateException("Unable to read '%s'".formatted(resource), e);
    }
  }

  @TestConfiguration
  static class SimpleDataWarehouseWebApiTestConfiguration {
    @Bean
    public IngestionCompletedListener ingestionCompleted() {
      return new IngestionCompletedListener();
    }
  }

  @Data
  static class IngestionCompletedListener {
    private final AtomicBoolean completed = new AtomicBoolean(false);

    @EventListener(classes = IngestionCompletedEvent.class)
    public void onIngestionCompleted() {
      completed.set(true);
    }
  }
}
