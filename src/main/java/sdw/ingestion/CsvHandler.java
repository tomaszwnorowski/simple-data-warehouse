package sdw.ingestion;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;
import sdw.warehouse.SimpleDataWarehouseMapper;

@Slf4j
@Component
public class CsvHandler {
  private static final int MAX_BATCH_SIZE = 512;

  private final SqlSessionFactory factory;

  public CsvHandler(SqlSessionFactory factory) {
    this.factory = factory;
  }

  public void batchInsert(Collection<CsvEntry> entries) {
    log.info("CSV: loading into warehouse started");
    try (var session = factory.openSession(ExecutorType.BATCH)) {
      var mapper = session.getMapper(SimpleDataWarehouseMapper.class);

      var currentBatchSize = 0;
      for (CsvEntry entry : entries) {
        mapper.insert(entry);

        currentBatchSize = currentBatchSize + 1;
        if (currentBatchSize >= MAX_BATCH_SIZE) {
          session.commit();
          currentBatchSize = 0;
        }
      }
      session.commit();
    }
    log.info("CSV: loading into warehouse completed");
  }
}
