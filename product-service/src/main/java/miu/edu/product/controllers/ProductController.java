package miu.edu.product.controllers;

import lombok.RequiredArgsConstructor;
import miu.edu.product.models.Product;
import miu.edu.product.services.ProductService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    private final JobLauncher launcher;

    private final Job importData;

    @GetMapping
    public List<Product> getAll() {
        return service.getAll();
    }

    @PutMapping("/batch")
    public ResponseEntity<Map<String, String>> batch(@PathVariable Long id) throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        launcher.run(importData, new JobParameters(new HashMap<>()));
        return ResponseEntity.ok(Map.of("response", "batch completed successfully"));
    }

    @GetMapping("{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return service.getById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public Product save(@RequestBody Product product) {
        return service.save(product);
    }

    @PutMapping("{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        return service.save(product);
    }

    @PutMapping("{id}/reduce-stocks/{count}")
    public void reduceStocks(@PathVariable Long id, @PathVariable Integer count) {
        service.reduceStocks(id, count);
    }

    @GetMapping("{id}/availability/{count}")
    public Map<String, Object> availability(@PathVariable Long id, @PathVariable Integer count) {
        return service.getAvailability(id, count);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
