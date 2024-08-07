## Create Keystore and import certificate

```shell

keytool -keystore pis-integration-keystore.jks -genkey -keyalg rsa -alias pis-integration-keystore -keypass pis123 -storepass pis123

keytool -import -v -trustcacerts -alias api-hiekkalaatikko-muutostietopalvelu-dvv-fi -file api-hiekkalaatikko-muutostietopalvelu-dvv-fi.crt
-keystore pis-integration-keystore.jks -keypass pis123 -storepass pis123
```

## Register Sink Connector

```shell
curl -d @pis-sink-connector.json -H "Content-Type: application/json" -X POST http://localhost:8083/connectors -v
```

pis-sink-connector.json

```json
{
  "name": "pis-sink-connector",
  "config": {
    "connector.class": "fi.mediconsult.integration.pis.PISSinkConnector",
    "topics": "pis_basic_info",
    "http.headers.authorization.basic": "Basic bXV0cFQxOnB3ZA==",
    "http.pis.url": "https://api.hiekkalaatikko.muutostietopalvelu.dvv.fi/api/v1/perustiedot-vko",
    "http.headers.content.type": "application/json",
    "keystore.password": "pis123",
    "tasks.max": "1",
    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter"
  }
}
```

## Get Available connectors

```shell
curl http://localhost:8083/connectors

["pis-sink-connector"]
```

## Producing Message to Topic

After setting up Kafka Bridge, you can produce to specified topics over HTTP. To produce to a topic, create a topic, and run the following
curl command.

```shell
curl -X POST http://localhost:8090/topics/pis_basic_info -H 'content-type: application/vnd.kafka.json.v2+json' -d '{"records": [{ "value": {"kuntakoodit": [ "091", "837" ]} }] }'
```

## Consuming Message from Topic

To consume messages over HTTP with Kafka Bridge:

1. Create a Kafka Bridge consumer

```shell
 curl -X POST http://localhost:8090/consumers/mc-integration-group  -H 'content-type: application/vnd.kafka.json.v2+json' -d '{ "name" : "pis-consumer",  "format" : "json",  "auto.offset.reset" : "earliest",  "enable.auto.commit" : false }' -v
```

2. Subscribe to a topic

```shell
curl -X POST http://localhost:8090/consumers/mc-integration-group/instances/pis-consumer/subscription -H 'content-type:application/vnd.kafka.json.v2+json' -d '{"topics" : [ "pis_basic_info_result" ]}' -v
```

3. Retrieve messages from the topic
   After creating and subscribing to a Kafka Bridge consumer, a first GET request will return an empty response because the poll operation
   starts a rebalancing process to assign partitions.

```shell
curl -v -X GET -H 'Accept: application/vnd.kafka.json.v2+json' http://localhost:8090/consumers/mc-integration-group/instances/pis-consumer/records -v
```

If the request is successful, the Kafka Bridge returns an HTTP status code 200 OK and the JSON body containing the messages from the topic.
Messages are retrieved from the latest offset by default.
In production, HTTP clients can call this endpoint repeatedly (in a loop)

4. Commiting offsets to the log

```shell
curl -X -v POST http://localhost:8090/consumers/mc-integration-group/instances/pis-consumer/offsets
```