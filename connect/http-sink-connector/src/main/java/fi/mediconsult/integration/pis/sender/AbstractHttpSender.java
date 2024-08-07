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

package fi.mediconsult.integration.pis.sender;

import fi.mediconsult.integration.pis.config.PISSinkConnectorConfig;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

abstract class AbstractHttpSender {

  private static final Logger logger = LoggerFactory.getLogger(AbstractHttpSender.class);

  protected final CloseableHttpClient httpClient;

  protected final PISSinkConnectorConfig config;

  protected final HttpRequestBuilder httpRequestBuilder;

  protected AbstractHttpSender(
      final HttpRequestBuilder httpRequestBuilder, PISSinkConnectorConfig config
  ) {
    this.config = Objects.requireNonNull(config);
    this.httpRequestBuilder = Objects.requireNonNull(httpRequestBuilder);
    this.httpClient = Objects.requireNonNull(httpRequestBuilder.build(config).build());

  }

  public final String send(final String body) {

    final var requestBuilder = Request
        .post(config.pisUrlValue())
        .bodyString(body, ContentType.APPLICATION_JSON);

    return sendWithRetries(requestBuilder/*, config.maxRetries()*/);

  }

  protected String sendWithRetries(
      final Request requestBuilderWithPayload/*,
      final int retries*/
  ) {
    try {
      final Response response = requestBuilderWithPayload.execute(this.httpClient);

      return response.handleResponse(r -> {

        if (r.getCode() >= 400) {
          final var request = r.getReasonPhrase();
          final var reason = request != null ? request : "UNKNOWN";
          logger.warn(
              "Got unexpected HTTP status code: {} . Reason: {}",
              r.getCode(),
              reason);
          throw new IOException("Server replied with status code " + r.getCode()
              + " and reason " + reason);
        }

        String outcome = EntityUtils.toString(r.getEntity());

        logger.info("Response {}", outcome);

        return outcome;

      });

    }
    catch (IOException e) {
      logger.error("Exception while sendWithRetries...");
      throw new RuntimeException(e);
    }
  }

}
