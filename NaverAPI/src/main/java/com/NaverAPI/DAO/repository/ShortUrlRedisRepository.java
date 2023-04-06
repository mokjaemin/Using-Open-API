package com.NaverAPI.DAO.repository;

import org.springframework.data.repository.CrudRepository;

import com.NaverAPI.data.DTO.ShortUrlResponseDto;


public interface ShortUrlRedisRepository extends CrudRepository<ShortUrlResponseDto, String> {

}
