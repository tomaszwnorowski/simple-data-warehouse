package sdw.ingestion;

import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EtlRoute extends RouteBuilder {
  private final ApplicationEventPublisher publisher;

  private final EtlProperties properties;

  @Override
  public void configure() {
    // extract
    from(properties.getCsvLocation())
        // transform
        .unmarshal(new BindyCsvDataFormat(CsvEntry.class))
        // load
        .to("bean:csvHandler?method=batchInsert")
        // inform rest of the application that we are ready to process queries
        .process(exchange -> publisher.publishEvent(new IngestionCompletedEvent()));
  }
}
