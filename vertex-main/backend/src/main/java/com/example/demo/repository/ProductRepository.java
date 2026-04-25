package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByIngredientsContaining(String ingredient);
}

/*
 * other JpaRepository
 * これ以外の選択肢 引用copilot
 * 
 * インターフェース パッケージ 説明
 * CrudRepository<T, ID> org.springframework.data.repository
 * 基本的なCRUD操作を提供。saveAll(Iterable<S> entities) が定義されている。
 * PagingAndSortingRepository<T, ID> org.springframework.data.repository
 * CrudRepository を拡張し、ページング・ソート機能を追加。saveAll() も利用可能。
 * JpaRepository<T, ID> org.springframework.data.jpa.repository
 * PagingAndSortingRepository を拡張し、JPA特有の機能を追加。もちろん saveAll() も利用可能。
 * ReactiveCrudRepository<T, ID> org.springframework.data.repository.reactive
 * リアクティブ対応のCRUD操作。戻り値は Flux / Mono。saveAll(Publisher<S> entities) が利用可能。
 * 
 */
