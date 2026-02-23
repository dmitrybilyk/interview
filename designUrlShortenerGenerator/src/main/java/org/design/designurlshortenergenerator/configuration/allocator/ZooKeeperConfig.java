package org.design.designurlshortenergenerator.configuration.allocator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
public class ZooKeeperConfig {

    // Додаємо localhost:2181 як значення за замовчуванням
    @Bean(destroyMethod = "close")
    public CuratorFramework curatorFramework(
            @Value("${zookeeper.connect:localhost:2181}") String connectString) throws InterruptedException {

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(30000)
                .build();

        client.start();

        // Рекомендую додати очікування з'єднання, щоб додаток не впав
        // при першому ж зверненні до біна, якщо ZK ще не "прокинувся"
        client.blockUntilConnected(10, TimeUnit.SECONDS);

        return client;
    }
}