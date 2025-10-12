package com.conduct.interview.practise.spring.controller.multithreading.service.lists;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/simplelist")
public class SimpleListController {

//    private final SimpleListService service;
//    private final SyncedListService service;
    private final COWListService service;

    public SimpleListController(COWListService service) {
        this.service = service;
    }

    /** Each HTTP request adds items to the list */
    @GetMapping("/add")
    public String add(@RequestParam(defaultValue = "1000") int items) {
        service.addItems(items);
        log.info("Triggered add of " + items + " items. Check list size later.");
        return "Triggered add of " + items + " items. Check list size later.";
    }

    /** View current list size */
    @GetMapping("/size")
    public String size() {
        return "Current list size: " + service.getSize();
    }

    /** Clear the list */
    @GetMapping("/clear")
    public String clear() {
        service.clear();
        return "List cleared";
    }
}
