package fi.mediconsult.integration.pis;

import fi.mediconsult.integration.pis.config.PISSinkConnectorConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static fi.mediconsult.integration.pis.config.PISSinkConnectorConfig.CONFIG_DEF;

public class PISSinkConnector extends SinkConnector {

  private static final Logger logger = LoggerFactory.getLogger(PISSinkConnector.class);

  private PISSinkConnectorConfig connectorConfig;

  private Map<String, String> configProps;

  @Override
  public void start(Map<String, String> props) {

    Objects.requireNonNull(props);

    this.connectorConfig = new PISSinkConnectorConfig(props);
    this.configProps = Collections.unmodifiableMap(props);

    logger.info("Starting connector {}", connectorConfig.connectorName());

  }

  @Override
  public Class<? extends Task> taskClass() {
    return PISSinkTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    return Collections.nCopies(maxTasks, Map.copyOf(configProps));
  }

  @Override
  public void stop() {
    logger.info("Stopping connector {}", connectorConfig.connectorName());

  }

  @Override
  public ConfigDef config() {
    return CONFIG_DEF;
  }

  @Override
  public String version() {
    return Version.VERSION;
  }
}
