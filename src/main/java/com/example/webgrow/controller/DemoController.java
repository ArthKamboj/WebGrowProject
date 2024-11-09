package com.example.webgrow.controller;

import com.example.webgrow.models.DTOClass;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@NoArgsConstructor
public class DemoController {
    @GetMapping
    public ResponseEntity<DTOClass> sayHello() {
        DTOClass dtoclass = new DTOClass("Hello From Secured Endpoint !");
        return ResponseEntity.ok(dtoclass);
    }
}
