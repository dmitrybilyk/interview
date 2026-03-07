package org.design.designurlshortenergenerator.generator.state.impl;

import lombok.extern.slf4j.Slf4j;
import org.design.designurlshortenergenerator.generator.state.api.UrlServiceState;
import org.design.designurlshortenergenerator.persistence.model.UrlMapping;
import org.design.designurlshortenergenerator.service.impl.UrlGeneratorServiceImpl;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActiveState implements UrlServiceState {

    @Override
    public String shorten(UrlGeneratorServiceImpl context, String originalUrl) {
        long id = context.getIdProvider().nextId();
        
        // Using the registry you implemented
        var strategy = context.getRegistry().getStrategy("base62CodeGeneratorStrategy");
        String code = strategy.encode(id);

        // Logic moved from the old service impl
        UrlMapping m = new UrlMapping();
        m.setId(id);
        m.setShortCode(code);
        m.setTarget(originalUrl);
        
        context.getJpaRepo().saveMapping(m);
        log.info("Url generated in SQL database (Active State)");

        context.getUrlEventPublisher().publishUrlCreated(code, originalUrl);
        context.saveToMongo(id, code, originalUrl);

        return code;
    }
}