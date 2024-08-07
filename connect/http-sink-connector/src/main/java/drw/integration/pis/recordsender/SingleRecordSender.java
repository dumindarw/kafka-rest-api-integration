package drw.integration.pis.recordsender;

import drw.integration.pis.config.PISSinkConnectorConfig;
import drw.integration.pis.sender.HttpSender;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Properties;

public class SingleRecordSender extends RecordSender {

  private final static Logger logger = LoggerFactory.getLogger(SingleRecordSender.class);

  protected SingleRecordSender(final HttpSender httpSender) {
    super(httpSender);
  }

  @Override
  public void send(Collection<SinkRecord> records) {
    for (final SinkRecord sinkRecord : records) {
      final String body = recordValueConverter.convert(sinkRecord);

     httpSender.send(body);

    }
  }

  @Override
  public String send(SinkRecord sinkRecord, PISSinkConnectorConfig config) {
    final String body = recordValueConverter.convert(sinkRecord);

    Properties props = new Properties();
    props.put("bootstrap.servers","localhost:9092");
    props.put("acks", "all");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    KafkaProducer<String,String> producer = new KafkaProducer<>(props);

    logger.info("Sending : {} ", body);

    String response = httpSender.send(body);

    logger.info("Received : {} ", response);



    ProducerRecord<String, String> sendRecord = new ProducerRecord<>(config.producerTopic(), null, response);

    producer.send(sendRecord, (metadata, ex) -> {
      logger.info("Metadata : {}", metadata);
      if (ex != null) {
        logger.error(ex.getMessage(), ex);
      }
    });

    producer.close();

    return response;
  }
}
