package fi.mediconsult.integration.pis.converter;

import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.sink.SinkRecord;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonRecordValueConverter implements RecordValueConverter.Converter {

  private final JsonConverter jsonConverter;

  public JsonRecordValueConverter() {
    this.jsonConverter = new JsonConverter();
    jsonConverter.configure(Map.of("schemas.enable", false, "converter.type", "value"));
  }

  @Override
  public String convert(SinkRecord record) {
    return new String(
        jsonConverter.fromConnectData(record.topic(), record.valueSchema(), record.value()),
        StandardCharsets.UTF_8);
  }
}
