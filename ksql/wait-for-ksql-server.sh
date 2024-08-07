#!/bin/sh
# wait-for-ksql-server.sh

set -e

 
while [ $(curl -s -o response.txt -w "%{http_code}" http://127.0.0.1:8088/info) -ne  200 ];
do
  >&2 echo "KSQL Server is unavailable - retrying"
  sleep 1
done

>&2 echo "KSQL Server is up"


# "value.converter.schema.registry.url": "http://schema-registry:8081",
# "value.converter.schemas.enable": false,
#"change.data.capture.handler": "com.mongodb.kafka.connect.sink.cdc.mongodb.ChangeStreamHandler", 
curl -X POST -H "Content-Type: application/json" -d '{"name": "ema-drug-sink", "config": {  "connector.class": "com.mongodb.kafka.connect.MongoSinkConnector", "topics": "FORMATTED_EMA_DRUGS_JSON_STREAM",  "connection.uri": "mongodb://root:tr33r00t@mongo:27017/?authSource=admin", "key.converter": "org.apache.kafka.connect.storage.StringConverter", "value.converter": "io.confluent.connect.avro.AvroConverter", "database": "drugdb", "collection": "ema_drugs", "schema.registry.url": "http://schema-registry:8081", "value.converter.schema.registry.url": "http://schema-registry:8081", "document.id.strategy": "com.mongodb.kafka.connect.sink.processor.id.strategy.PartialValueStrategy", "document.id.strategy.partial.value.projection.type": "AllowList", "document.id.strategy.partial.value.projection.list": "PRODUCT_NAME,ROUTES", "writemodel.strategy" :"com.mongodb.kafka.connect.sink.writemodel.strategy.ReplaceOneBusinessKeyStrategy" }}' http://connect:8083/connectors


curl -X POST -H "Content-Type: application/json" -d '{"name": "fda-drug-sink", "config": {  "connector.class": "com.mongodb.kafka.connect.MongoSinkConnector", "topics": "FORMATTED_FDA_DRUGS_JSON_STREAM",  "connection.uri": "mongodb://root:tr33r00t@mongo:27017/?authSource=admin", "key.converter": "org.apache.kafka.connect.storage.StringConverter", "value.converter": "io.confluent.connect.avro.AvroConverter", "database": "drugdb", "collection": "fda_drugs", "schema.registry.url": "http://schema-registry:8081", "value.converter.schema.registry.url": "http://schema-registry:8081", "document.id.strategy": "com.mongodb.kafka.connect.sink.processor.id.strategy.PartialValueStrategy", "document.id.strategy.partial.value.projection.type": "AllowList", "document.id.strategy.partial.value.projection.list": "PRODUCT_NAME,ROUTES", "writemodel.strategy" :"com.mongodb.kafka.connect.sink.writemodel.strategy.ReplaceOneBusinessKeyStrategy" }}' http://connect:8083/connectors


>&2 echo "Extractor Agent is completed - executing command"

ksql --file /ksql/queries.sql -- http://ksql-server:8088
# Print and execute all other arguments starting with `$1`
# So `exec "$1" "$2" "$3" ...`
keepgoing=1
trap '{ echo "sigint"; keepgoing=0; }' SIGINT

while (( keepgoing )); do
    #echo "sleeping"
    sleep 10
done