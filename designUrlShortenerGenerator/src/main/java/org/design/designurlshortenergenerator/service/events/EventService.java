package org.design.designurlshortenergenerator.service.events;

import org.design.designurlshortenergenerator.exceptions.SomeBusinessException;
import org.design.designurlshortenergenerator.persistence.model.sql.Event;
import org.design.designurlshortenergenerator.persistence.repository.sql.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    @Transactional(propagation = Propagation.NESTED)
    public void saveEvent(String message) {
        eventRepository.save(new Event(message));
        throw new SomeBusinessException("business");
    }
}