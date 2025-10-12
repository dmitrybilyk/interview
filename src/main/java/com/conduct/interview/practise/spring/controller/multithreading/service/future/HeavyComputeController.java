package com.conduct.interview.practise.spring.controller.multithreading.service.future;

import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/compute")
public class HeavyComputeController {

    private final HeavyComputationService service;
    private final FutureComputationService futureComputationService;

    public HeavyComputeController(HeavyComputationService service, FutureComputationService futureComputationService) {
        this.service = service;
        this.futureComputationService = futureComputationService;
    }

    @GetMapping("/sequential")
    public String runSequential(
            @RequestParam(defaultValue = "5") int tasks,
            @RequestParam(defaultValue = "50000") int limit
    ) {
        return service.runSequential(tasks, limit);
    }

    @GetMapping("/parallel")
    public String runParallel(
            @RequestParam(defaultValue = "5") int tasks,
            @RequestParam(defaultValue = "50000") int limit
    ) throws ExecutionException, InterruptedException {
        return futureComputationService.runParallel(tasks, limit);
    }
}
