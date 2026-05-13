import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisPlayground {
    public static void main(String[] args) throws InterruptedException {
        // 1. Підключення (URI формат: redis://host:port)
        RedisClient redisClient = RedisClient.create("redis://localhost:6379");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();

        System.out.println("--- Case 1: Strings & TTL ---");

        // 2. Простий SET і GET
        syncCommands.set("user:123:name", "Dmytro");
        String name = syncCommands.get("user:123:name");
        System.out.println("Збережене ім'я: " + name);

        // 3. SETEX (Set with Expiration) - Ключ на 5 секунд
        syncCommands.setex("temp_token", 5, "SECRET_ABC");
        
        System.out.println("Токен існує: " + syncCommands.get("temp_token"));

        // 4. Перевіряємо TTL (Time To Live)
        System.out.println("Залишилось жити (сек): " + syncCommands.ttl("temp_token"));

        // 5. Чекаємо 6 секунд, щоб ключ зник
        System.out.println("Чекаємо, поки токен видалиться...");
        Thread.sleep(6000);

        String expiredToken = syncCommands.get("temp_token");
        System.out.println("Токен після очікування: " + (expiredToken == null ? "ВИДАЛЕНО" : expiredToken));

        // Закриваємо з'єднання
        connection.close();
        redisClient.shutdown();
    }
}