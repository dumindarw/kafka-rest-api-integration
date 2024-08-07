/*
 * Copyright 2023 Aiven Oy and http-connector-for-apache-kafka project contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package drw.integration.pis.sender;

import drw.integration.pis.config.PISSinkConnectorConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

public class DefaultHttpSender extends AbstractHttpSender implements HttpSender {

  private static final Logger logger = LoggerFactory.getLogger(DefaultHttpSender.class);

  private static final String KEYSTORE_FILE = "pis-integration-keystore.jks";

  public DefaultHttpSender(final PISSinkConnectorConfig config) {
    super(new DefaultHttpRequestBuilder(), config);
  }

  static class DefaultHttpRequestBuilder implements HttpRequestBuilder {

    @Override
    public HttpClientBuilder build(final PISSinkConnectorConfig config) {

      final HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
          .setSSLSocketFactory(getSslSocketFactory(config))
          .build();

      var httpRequestBuilder = HttpClients.custom()
          .setConnectionManager(cm)
          .evictExpiredConnections();

      Header header = new BasicHeader(HEADER_CONTENT_TYPE, config.httpContentTypeValue());
      Header authHeader = new BasicHeader(HEADER_AUTHORIZATION, config.httpBasicAuthValue());
      httpRequestBuilder.setDefaultHeaders(List.of(header, authHeader));

      return httpRequestBuilder;

    }

    private SSLConnectionSocketFactory getSslSocketFactory(PISSinkConnectorConfig config) {

      try {

        final SSLContext sslcontext = SSLContexts.custom()
            .loadTrustMaterial(DefaultHttpSender.class.getClassLoader().getResource(KEYSTORE_FILE),
                config.keystorePasswordValue().toCharArray(),
                new TrustAllStrategy())
            .build();
        return SSLConnectionSocketFactoryBuilder.create()
            .setSslContext(sslcontext)
            .build();

      }
      catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException | CertificateException e) {
        logger.error(e.getMessage());
      }

      return null;
    }

  }

}
