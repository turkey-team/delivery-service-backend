package com.sparta.delivery.backend.sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

	@GetMapping("/hello")
	public String hello() {
		return "Hello World";
	}

	@PostMapping("/data")
	public String postData(@RequestBody String data) {
		return "Received: " + data;
	}

}
