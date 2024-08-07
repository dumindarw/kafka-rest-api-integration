package drw.integration.pis;

import drw.integration.pis.config.PISSinkConnectorConfig;
import drw.integration.pis.recordsender.RecordSender;
import drw.integration.pis.sender.DefaultHttpSender;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class PISSinkTask extends SinkTask {

  private static final Logger logger = LoggerFactory.getLogger(PISSinkTask.class);

  private KafkaProducer<String, String> producer;

  private RecordSender recordSender;

  private PISSinkConnectorConfig config;

  @Override
  public String version() {
    return null;
  }

  @Override
  public void start(Map<String, String> props) {
    Objects.requireNonNull(props);
    config = new PISSinkConnectorConfig(props);
    final var httpSender = new DefaultHttpSender(config);
    this.recordSender = RecordSender.createRecordSender(httpSender);

  }

  @Override
  public void put(Collection<SinkRecord> records) {
    logger.debug("Received {} records", records.size());

    sendEach(records);

    /*if (!records.isEmpty()) {
      // use the batch send if legacy send is enabled
      if (!useLegacySend) {
        sendEach(records);
      }
      else {
        sendBatch(records);
      }
    }*/
  }

  @Override
  public void stop() {

  }

  private void sendEach(final Collection<SinkRecord> records) {
    // send records to the sender one at a time
    for (final var record : records) {

      if (record.value() == null) {
        // TODO: consider optionally process them, e.g. use another verb or ignore
        throw new DataException("Record value must not be null");
      }

      try {
        recordSender.send(record, config);
        //sendRecord(record);
      }
      catch (final ConnectException e) {
        /*if (reporter != null) {
          reporter.report(record, e);
        }
        else {
          // otherwise, re-throw the exception
          throw new ConnectException(e.getMessage());
        }*/
      }
    }
  }

  private void sendBatch(final Collection<SinkRecord> records) {
    for (final var record : records) {
      if (record.value() == null) {
        // TODO: consider optionally process them, e.g. use another verb or ignore
        throw new DataException("Record value must not be null");
      }
    }

    recordSender.send(records);
  }

}
