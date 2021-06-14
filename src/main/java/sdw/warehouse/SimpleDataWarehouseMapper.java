package sdw.warehouse;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import sdw.api.Query;
import sdw.ingestion.CsvEntry;

@Mapper
public interface SimpleDataWarehouseMapper {

  void insert(CsvEntry entry);

  int count();

  List<Map<String, Object>> query(Query query);
}
