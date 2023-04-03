package com.NaverAPI.service.Impl;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.NaverAPI.DAO.ShortUrlDAO;
import com.NaverAPI.DAO.repository.ShortUrlRedisRepository;
import com.NaverAPI.data.DTO.NaverUriDto;
import com.NaverAPI.data.DTO.ShortUrlResponseDto;
import com.NaverAPI.data.Entity.ShortUrl;
import com.NaverAPI.service.ShortUrlService;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

	private final Logger LOGGER = LoggerFactory.getLogger(ShortUrlServiceImpl.class);
	private final ShortUrlDAO shortUrlDAO;
	private final ShortUrlRedisRepository shortUrlRedisRepository;

	@Autowired
	public ShortUrlServiceImpl(ShortUrlDAO shortUrlDAO, ShortUrlRedisRepository shortUrlRedisRepository) {
		this.shortUrlDAO = shortUrlDAO;
		this.shortUrlRedisRepository = shortUrlRedisRepository;
	}

	
	// Generating Short Url
	@Override
	public ShortUrlResponseDto generateShortUrl(String clientId, String clientSecret, String originalUrl) {

		LOGGER.info("[ShortUrlServiceImpl : generateShortUrl] request data : {}", originalUrl);

		if (originalUrl.contains("me2.do")) {
			throw new RuntimeException();
		}

		// Sending orinalUri and Getting Short Uri
		ResponseEntity<NaverUriDto> responseEntity = requestShortUrl(clientId, clientSecret, originalUrl);
		String orgUrl = responseEntity.getBody().getResult().getOrgUrl();
		String shortUrl = responseEntity.getBody().getResult().getUrl();
		String hash = responseEntity.getBody().getResult().getHash();

		ShortUrl shortUrlEntity = new ShortUrl();
		shortUrlEntity.setOrgUrl(orgUrl);
		shortUrlEntity.setUrl(shortUrl);
		shortUrlEntity.setHash(hash);

		// DB 저장
		shortUrlDAO.saveShortUrl(shortUrlEntity);

		// Return DTO
		ShortUrlResponseDto shortUrlResponseDto = new ShortUrlResponseDto(orgUrl, shortUrl);

		// Cache Logic
		// shortUrlRedisRepository.save(shortUrlResponseDto);

		LOGGER.info("[ShortUrlServiceImpl : generateShortUrl] Response DTO : {}", shortUrlResponseDto);
		return shortUrlResponseDto;
	}

	
	// Get Short Url by Original Url
	@Override
	public ShortUrlResponseDto getShortUrl(String clientId, String clientSecret, String originalUrl) {
		LOGGER.info("[ShortUrlServiceImpl : getShortUrl] request data : {}", originalUrl);

		// Cache Logic
//		Optional<ShortUrlResponseDto> foundResponseDto = shortUrlRedisRepository.findById(originalUrl);
//		if (foundResponseDto.isPresent()) {
//			LOGGER.info("[ShortUrlServiceImpl : getShortUrl] Cache Data existed.");
//			return foundResponseDto.get();
//		} else {
//			LOGGER.info("[ShortUrlServiceImpl : getShortUrl] Cache Data does not existed.");
//		}

		
		// Searching Short URL
		ShortUrl getShortUrl = shortUrlDAO.getShortUrl(originalUrl);
		String orgUrl;
		String shortUrl;

		
		// If empty in DB, make a new one and save it
		if (getShortUrl == null) {
			LOGGER.info("[ShortUrlServiceImpl : getShortUrl] No Entity in Database.");
			ResponseEntity<NaverUriDto> responseEntity = requestShortUrl(clientId, clientSecret, originalUrl);
			orgUrl = responseEntity.getBody().getResult().getOrgUrl();
			shortUrl = responseEntity.getBody().getResult().getUrl();
			String hash = responseEntity.getBody().getResult().getHash();

			ShortUrl shortUrlEntity = new ShortUrl();
			shortUrlEntity.setOrgUrl(orgUrl);
			shortUrlEntity.setUrl(shortUrl);
			shortUrlEntity.setHash(hash);

			// 저장
			System.out.println(orgUrl);
			System.out.println(shortUrl);
			System.out.println(hash);
			shortUrlDAO.saveShortUrl(shortUrlEntity);
		}
		else {
			orgUrl = getShortUrl.getOrgUrl();
			shortUrl = getShortUrl.getUrl();
		}

		ShortUrlResponseDto shortUrlResponseDto = new ShortUrlResponseDto(orgUrl, shortUrl);

		// Saving in Cache
		// shortUrlRedisRepository.save(shortUrlResponseDto);

		LOGGER.info("[ShortUrlServiceImpl : getShortUrl] Response DTO : {}", shortUrlResponseDto);
		return shortUrlResponseDto;
	}

	
	// Updating 
	@Override
	public ShortUrlResponseDto updateShortUrl(String clientId, String clientSecret, String originalUrl) {
		return null;
	}

	// Deleting
	@Override
	public void deleteShortUrl(String url) {
		// Delete By Short URL
		if (url.contains("me2.do")) {
			LOGGER.info("[ShortUrlServiceImpl : deleteShortUrl] Request Url is 'ShortUrl'.");
			deleteByShortUrl(url);
		} 
		// Delete By Original URL
		else {
			LOGGER.info("[ShortUrlServiceImpl : deleteShortUrl] Request Url is 'OriginalUrl'.");
			deleteByOriginalUrl(url);
		}
	}

	private void deleteByShortUrl(String url) {
		LOGGER.info("[ShortUrlServiceImpl : deleteByShortUrl] delete record");
		shortUrlDAO.deleteByShortUrl(url);
	}

	private void deleteByOriginalUrl(String url) {
		LOGGER.info("[ShortUrlServiceImpl : deleteByOriginalUrl] delete record");
		shortUrlDAO.deleteByOriginalUrl(url);
	}

	
	// Connection with Naver
	private ResponseEntity<NaverUriDto> requestShortUrl(String clientId, String clientSecret, String originalUrl) {
		LOGGER.info("[ShortUrlServiceImpl : Server Connecting] client ID : ***, client Secret : ***, original URL : {}", originalUrl);

		// Generating Naver Short API
		URI uri = UriComponentsBuilder.fromUriString("https://openapi.naver.com").path("/v1/util/shorturl")
				.queryParam("url", originalUrl).encode().build().toUri();

		// Generating HTTP Header
		LOGGER.info("[ShortUrlServiceImpl] set HTTP Request Header");
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(new MediaType[] { MediaType.APPLICATION_JSON }));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Naver-Client-Id", clientId);
		headers.set("X-Naver-Client-Secret", clientSecret);
		HttpEntity<String> entity = new HttpEntity<>("", headers);

		// Sending and Getting Result
		LOGGER.info("[ShortUrlServiceImpl : Server Connecting] request by restTemplate");
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<NaverUriDto> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity,
				NaverUriDto.class);
		LOGGER.info("[ShortUrlServiceImpl : Server Connecting] request has been successfully complete.");

		return responseEntity;
	}

}
