package fi.mediconsult.integration.pis.converter;

import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.HashMap;
import java.util.Map;

public class RecordValueConverter {
  private final JsonRecordValueConverter jsonRecordValueConverter = new JsonRecordValueConverter();

  private final Map<Class<?>, Converter> converters = Map.of(
      String.class, record -> (String) record.value(),
      HashMap.class, jsonRecordValueConverter,
      Struct.class, jsonRecordValueConverter
  );

  interface Converter {
    String convert(final SinkRecord record);
  }

  public String convert(final SinkRecord record) {
    if (!converters.containsKey(record.value().getClass())) {
      throw new DataException(
          "Record value must be String, Schema Struct or HashMap,"
              + " but " + record.value().getClass() + " is given");
    }
    return converters.get(record.value().getClass()).convert(record);
  }
}
