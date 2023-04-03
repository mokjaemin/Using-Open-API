package com.NaverAPI.data.Entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass // 상속을 해주기 위한 애너테이션
@EntityListeners(AuditingEntityListener.class)
// 자신을 상속받는 Entity가 생성하는 시점에 자동으로 아래 프로퍼티 생성
// Application 파일에 @EnableJpaAuditing 붙여줘야 가능
public class BaseEntity {

	@CreatedDate
	@Column(updatable = false)
	// 처음 생성후 변경되지 않음
	private LocalDateTime createdAt;
	
	
	@LastModifiedDate
	private LocalDateTime updatedAt;

	/*
	 * @CreatedBy
	 * 
	 * @Column(updatable = false) private String createdBy;
	 */


	/*
	 * @LastModifiedBy private String updatedBy;
	 */

}
