package com.example.rmesaage.utils;

import org.apache.kafka.common.utils.Time;

import java.util.Properties;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import scala.Option;

public class KafkaServ {
    private static KafkaServer server;
    private static boolean startup;
    public KafkaServ(String port){
        Properties props = new Properties();
        props.put("broker.id", "1");
        props.put("listeners", "PLAINTEXT://localhost:"+port);
        props.put("log.dirs", "/tmp/kafka-logs");

        KafkaConfig kafkaConfig = new KafkaConfig(props);
        server = new KafkaServer(kafkaConfig, Time.SYSTEM, Option.apply("kafka-server-thread"), true);
    }
    public static void start(){
        if (server!=null & !startup){
            server.startup();
            startup = true;
        }
    }
    public static void stop(){
        if (server!=null & startup){
            server.shutdown();
            startup = false;
        }

    }
}
