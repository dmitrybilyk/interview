package com.conduct.interview.practise.spring.controller.multithreading.service.maps;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simplemap")
public class SimpleMapController {

//    private final SimpleMapService service;
//    private final SyncedMapService service;
    private final ConcurrentMapService service;

    public SimpleMapController(ConcurrentMapService service) {
        this.service = service;
    }

    @GetMapping("/add")
    public String add(@RequestParam(defaultValue = "1000") int items) {
        service.putItems(items);
        return "Triggered add of " + items + " items. Check map size later.";
    }

    @GetMapping("/size")
    public String size() {
        return "Current map size: " + service.getSize();
    }

    @GetMapping("/clear")
    public String clear() {
        service.clear();
        return "Map cleared";
    }
}
