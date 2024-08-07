/*
 * Copyright 2021 Aiven Oy and http-connector-for-apache-kafka project contributors
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

import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

interface HttpResponseHandler {

  Logger LOGGER = LoggerFactory.getLogger(HttpResponseHandler.class);

  void onResponse(final Response response, int remainingRetries) throws IOException;

  HttpResponseHandler ON_HTTP_ERROR_RESPONSE_HANDLER = (response, remainingRetries) -> {
    HttpResponse r = response.returnResponse();
    if (r.getCode() >= 400) {
      final var request = r.getReasonPhrase();
      final var reason = request != null ? request : "UNKNOWN";
      LOGGER.warn(
          "Got unexpected HTTP status code: {} . Reason: {}",
          r.getCode(),
          reason);
      throw new IOException("Server replied with status code " + r.getCode()
          + " and body " + reason);
    }
  };

}
