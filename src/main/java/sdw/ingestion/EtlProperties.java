package sdw.ingestion;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("etl")
public class EtlProperties {
  private String csvLocation;
}
