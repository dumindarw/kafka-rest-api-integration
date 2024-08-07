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

package fi.mediconsult.integration.pis.sender;

import fi.mediconsult.integration.pis.config.PISSinkConnectorConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

interface HttpRequestBuilder {

  String HEADER_AUTHORIZATION = "Authorization";

  String HEADER_CONTENT_TYPE = "Content-Type";

  HttpClientBuilder build(PISSinkConnectorConfig config);

}
