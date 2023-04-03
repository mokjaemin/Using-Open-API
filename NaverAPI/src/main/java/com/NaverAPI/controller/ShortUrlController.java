package com.NaverAPI.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.NaverAPI.data.DTO.ShortUrlResponseDto;
import com.NaverAPI.service.ShortUrlService;

@RestController
@RequestMapping("/api/v1/short-url")
public class ShortUrlController {

	private final Logger LOGGER = LoggerFactory.getLogger(ShortUrlController.class);

	// ID, PWD from applcation.properties
	@Value("${NaverShortURLOpenApiID}")
	private String CLIENT_ID;
	@Value("${NaverShortURLOpenApiPWD}")
	private String CLIENT_SECRET;

	ShortUrlService shortUrlService;

	@Autowired
	public ShortUrlController(ShortUrlService shortUrlService) {
		this.shortUrlService = shortUrlService;
	}

	// Generating Short API
	@PostMapping()
	public ShortUrlResponseDto generateShortUrl(String originalUrl) {
		LOGGER.info("[ShortURlController] Generating Short URL with CLIENT_ID : {}, CLIENT_SECRET : {}", CLIENT_ID,
				CLIENT_SECRET);
		return shortUrlService.generateShortUrl(CLIENT_ID, CLIENT_SECRET, originalUrl);
	}

	// Get Short API
	@GetMapping()
	public ShortUrlResponseDto getShortUrl(String originalUrl) {
		long startTime = System.currentTimeMillis();
		ShortUrlResponseDto shortUrlResponseDto = shortUrlService.getShortUrl(CLIENT_ID, CLIENT_SECRET, originalUrl);
		long endTime = System.currentTimeMillis();
		LOGGER.info("[ShortURlController] Getting Short URL, response Time : {}ms", (endTime - startTime));
		return shortUrlResponseDto;
	}

	// Update Short API
	@PutMapping("/")
	public ShortUrlResponseDto updateShortUrl(String originalUrl) {
		return null;
	}

	// Delete Short API form DB
	@DeleteMapping("/")
	public ResponseEntity<String> deleteShortUrl(String url) {
		try {
			shortUrlService.deleteShortUrl(url);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.OK).body("정상적으로 삭제되었습니다.");
	}

}
