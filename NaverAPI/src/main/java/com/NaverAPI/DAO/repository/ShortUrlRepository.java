package com.NaverAPI.DAO.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.NaverAPI.data.Entity.ShortUrl;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {

    ShortUrl findByUrl(String url);

    ShortUrl findByOrgUrl(String originalUrl);

}
