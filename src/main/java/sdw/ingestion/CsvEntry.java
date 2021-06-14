package sdw.ingestion;

import java.util.Date;
import lombok.Data;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@Data
@CsvRecord(separator = ",", skipFirstLine = true)
public class CsvEntry {
  @DataField(pos = 1)
  private String dataSource;

  @DataField(pos = 2)
  private String campaign;

  @DataField(pos = 3, pattern = "MM/dd/yy")
  private Date daily;

  @DataField(pos = 4)
  private long clicks;

  @DataField(pos = 5)
  private long impressions;
}
