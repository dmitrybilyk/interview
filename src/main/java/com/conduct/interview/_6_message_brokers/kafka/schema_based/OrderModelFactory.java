package com.conduct.interview._6_message_brokers.kafka.schema_based;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

public class OrderModelFactory {

    // The Avro schema defines the exact fields, types, and constraints
    public static final String ORDER_SCHEMA_AVRO = "{\n" +
            "  \"type\": \"record\",\n" +
            "  \"name\": \"UserOrder\",\n" +
            "  \"namespace\": \"com.conduct.interview\",\n" +
            "  \"fields\": [\n" +
            "    {\"name\": \"orderId\", \"type\": \"string\"},\n" +
            "    {\"name\": \"userId\", \"type\": \"string\"},\n" +
            "    {\"name\": \"amount\", \"type\": \"double\"},\n" +
            "    {\"name\": \"status\", \"type\": \"string\"}\n" +
            "  ]\n" +
            "}";

    public static final Schema SCHEMA = new Schema.Parser().parse(ORDER_SCHEMA_AVRO);

    public static GenericRecord createOrderRecord(String orderId, String userId, double amount, String status) {
        GenericRecord record = new GenericData.Record(SCHEMA);
        record.put("orderId", orderId);
        record.put("userId", userId);
        record.put("amount", amount);
        record.put("status", status);
        return record;
    }
}