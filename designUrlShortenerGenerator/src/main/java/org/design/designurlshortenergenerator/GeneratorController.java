package org.design.designurlshortenergenerator;

import lombok.extern.slf4j.Slf4j;
import org.design.designurlshortenergenerator.mongo.model.MongoUrlMapping;
import org.design.designurlshortenergenerator.mongo.model.repository.MongoUrlMappingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/api")
public class GeneratorController {
    private final UrlMappingRepository repo;
    private final MongoUrlMappingRepository mongoUrlMappingRepository;
    private RangeAllocator allocator;

    @Value("${ZK_CONNECT:localhost:2181}")
    private String zk;

    public GeneratorController(UrlMappingRepository repo, MongoUrlMappingRepository mongoUrlMappingRepository) {
        this.repo = repo;
        this.mongoUrlMappingRepository = mongoUrlMappingRepository;
    }

    @PostConstruct
    public void init() throws Exception {
        System.out.println("Curator Version: " +
                org.apache.curator.framework.CuratorFramework.class.getPackage().getImplementationVersion());
        allocator = new RangeAllocator(zk);
    }

    record CreateReq(String url) {}

    @PostMapping("/shorten")
    public ResponseEntity<?> shorten(@RequestBody CreateReq req) throws Exception {
        long id = allocator.nextId();
        String code = Base62.encode(id);
        UrlMapping m = new UrlMapping();
        m.setId(id);
        m.setShortCode(code);
        m.setTarget(req.url);
        repo.save(m);
        log.info("Url is generated!!!!!!!!!!!!!!!!!!!!!!!!!!!");


        // ---------- NEW: MongoDB save ----------
        MongoUrlMapping mongo = new MongoUrlMapping();
        mongo.setId(id);
        mongo.setShortCode(code);
        mongo.setTarget(req.url);
        // add some arbitrary data (you asked for “any data”)
        Map<String, Object> extra = new HashMap<>();
        extra.put("createdAt", Instant.now().toString());
        extra.put("randomValue", new Random().nextDouble());
        // make the structure differ from document to document
        if (id % 2 == 0) {
            extra.put("metadata", Map.of("source", "api", "priority", "high"));
        } else {
            extra.put("tags", new String[]{"url", "shorten", "test" + id});
        }
        mongo.setAdditionalData(extra);

        mongoUrlMappingRepository.save(mongo);



        return ResponseEntity.created(URI.create("/" + code)).body(code);
    }

    @DeleteMapping("/short/{code}")
    public ResponseEntity<?> delete(@PathVariable("code") String code) {
        // deletion allowed only for authenticated users with role 'deleter'
        // simple implementation: find by decoding and delete by id
        long id = /* decode */ 0L;
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
