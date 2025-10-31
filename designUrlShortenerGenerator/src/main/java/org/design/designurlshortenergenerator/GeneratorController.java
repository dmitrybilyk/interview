package org.design.designurlshortenergenerator;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PostConstruct;
import java.net.URI;

@RestController
@RequestMapping("/api")
public class GeneratorController {
    private final UrlMappingRepository repo;
    private RangeAllocator allocator;

    @Value("${ZK_CONNECT:localhost:2181}")
    private String zk;

    public GeneratorController(UrlMappingRepository repo) {
        this.repo = repo;
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
