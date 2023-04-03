package com.NaverAPI.DAO.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.NaverAPI.DAO.ShortUrlDAO;
import com.NaverAPI.DAO.repository.ShortUrlRepository;
import com.NaverAPI.data.Entity.ShortUrl;

@Component
public class ShortUrlDaoImpl implements ShortUrlDAO {

	private final ShortUrlRepository shortUrlRepository;

	@Autowired
	public ShortUrlDaoImpl(ShortUrlRepository shortUrlRepository) {
		this.shortUrlRepository = shortUrlRepository;
	}

	// Save
	@Override
	public ShortUrl saveShortUrl(ShortUrl shortUrl) {
		ShortUrl foundShortUrl = shortUrlRepository.save(shortUrl);
		return foundShortUrl;
	}

	// Get Short URL
	@Override
	public ShortUrl getShortUrl(String originalUrl) {
		ShortUrl foundShortUrl = shortUrlRepository.findByOrgUrl(originalUrl);
		return foundShortUrl;
	}

	// Get Original URL
	@Override
	public ShortUrl getOriginalUrl(String shortUrl) {
		ShortUrl foundShortUrl = shortUrlRepository.findByUrl(shortUrl);
		return foundShortUrl;
	}

	// Update new URL
	@Override
	public ShortUrl updateShortUrl(ShortUrl newShortUrl) {
		ShortUrl foundShortUrl = shortUrlRepository.findByOrgUrl(newShortUrl.getOrgUrl());
		foundShortUrl.setUrl(newShortUrl.getUrl());
		ShortUrl savedShortUrl = shortUrlRepository.save(foundShortUrl);
		return savedShortUrl;
	}

	// Delete URL by Short URL
	@Override
	public void deleteByShortUrl(String shortUrl) {
		ShortUrl foundShortUrl = shortUrlRepository.findByUrl(shortUrl);
		shortUrlRepository.delete(foundShortUrl);
	}

	// Delete URL by Original URL
	@Override
	public void deleteByOriginalUrl(String originalUrl) {
		ShortUrl foundShortUrl = shortUrlRepository.findByOrgUrl(originalUrl);
		shortUrlRepository.delete(foundShortUrl);
	}
}
