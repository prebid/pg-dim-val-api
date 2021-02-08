package org.prebid.pg.dimval.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${services.admin-base-url}")
public class AdminController {

    @GetMapping("/v1/test")
    public ResponseEntity<String> adminTest() {
        return new ResponseEntity<>("Response from test admin endpoint", HttpStatus.OK);
    }
}
