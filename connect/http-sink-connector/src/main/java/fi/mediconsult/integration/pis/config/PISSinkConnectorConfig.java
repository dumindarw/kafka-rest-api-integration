package fi.mediconsult.integration.pis.config;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

public class PISSinkConnectorConfig extends AbstractConfig {

  public static final String NAME_CONFIG = "name";

  private static final String HTTP_BASIC_AUTH_VALUE = "http.headers.authorization.basic";

  private static final String PIS_HTTPS_URL = "http.pis.url";

  private static final String HTTP_CONTENT_TYPE = "http.headers.content.type";

  private static final String KEYSTORE_PASSWORD = "keystore.password";

  private static final String PIS_PRODUCER_TOPIC = "producer.topic";//"pis_basic_info_result";

  public static final ConfigDef CONFIG_DEF = configDef();

  public PISSinkConnectorConfig(Map<?, ?> originals) {
    super(CONFIG_DEF, originals);
  }

  private static ConfigDef configDef() {
    return new ConfigDef()
        .define(PIS_HTTPS_URL,
            ConfigDef.Type.STRING,
            ConfigDef.Importance.HIGH,
            "URL of PIS interface")
        .define(HTTP_BASIC_AUTH_VALUE,
            ConfigDef.Type.STRING,
            ConfigDef.Importance.HIGH,
            "Basic Auth value")
        .define(KEYSTORE_PASSWORD,
            ConfigDef.Type.STRING,
            ConfigDef.Importance.HIGH,
            "Keystore Password")
        .define(PIS_PRODUCER_TOPIC,
            ConfigDef.Type.STRING,
            ConfigDef.Importance.HIGH,
            "Producer Topic")
        .define(HTTP_CONTENT_TYPE,
            ConfigDef.Type.STRING,
            ConfigDef.Importance.HIGH,
            "Content type");
  }

  public String httpBasicAuthValue() {
    return getString(HTTP_BASIC_AUTH_VALUE);
  }

  public String httpContentTypeValue() {
    return getString(HTTP_CONTENT_TYPE);
  }

  public final String connectorName() {
    return originalsStrings().get(NAME_CONFIG);
  }

  public String pisUrlValue() {
    return getString(PIS_HTTPS_URL);
  }

  public String keystorePasswordValue() {
    return getString(KEYSTORE_PASSWORD);
  }

  public String producerTopic(){ return getString(PIS_PRODUCER_TOPIC);  }

}
