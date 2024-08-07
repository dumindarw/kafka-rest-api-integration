package fi.mediconsult.integration.pis.recordsender;

import fi.mediconsult.integration.pis.config.PISSinkConnectorConfig;
import fi.mediconsult.integration.pis.converter.RecordValueConverter;
import fi.mediconsult.integration.pis.sender.HttpSender;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Collection;
import java.util.Properties;

public abstract class RecordSender {

  protected final HttpSender httpSender;

  protected final RecordValueConverter recordValueConverter = new RecordValueConverter();

  public abstract void send(final Collection<SinkRecord> records);

  public abstract String send(final SinkRecord record, PISSinkConnectorConfig config);

  protected RecordSender(final HttpSender httpSender) {
    this.httpSender = httpSender;
  }

  public static RecordSender createRecordSender(final HttpSender httpSender) {
    return new SingleRecordSender(httpSender);
  }
}
