package com.example.postgres.lob.test;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Lazy
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("select d from Document d where d.id = ?1")
    Optional<Document> findByIdIs(Long id);

    List<Document> findByDocTextLike(String text);

    List<Document> findByDateCreatedIsBefore(LocalDateTime dateCreated);



}