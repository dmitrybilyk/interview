package org.design.designurlshortenergenerator.persistance.mongo.model.repository;

import org.design.designurlshortenergenerator.persistance.mongo.model.MongoUrlMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MongoDataLoader {

    @Autowired
    private MongoUrlMappingRepository repo;

    public void load(int count) {
        List<MongoUrlMapping> batch = new ArrayList<>(5000);

        for (int i = 1; i <= count; i++) {
            MongoUrlMapping m = new MongoUrlMapping();
            m.setId((long) i);
            m.setShortCode("code" + i);
            m.setTarget("https://example.com/" + i);

            Map<String, Object> extra = new HashMap<>();
            extra.put("createdAt", Instant.now().toString());
            extra.put("randomValue", Math.random());
            m.setAdditionalData(extra);

            batch.add(m);

            // batch insert every 5000 to keep it efficient
            if (batch.size() == 5000) {
                repo.saveAll(batch);
                batch.clear();
            }
        }
        if (!batch.isEmpty()) repo.saveAll(batch);

        System.out.println("🏁 Insert completed: " + count + " records");
    }
}
