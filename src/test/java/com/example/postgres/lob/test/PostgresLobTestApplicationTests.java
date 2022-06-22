package com.example.postgres.lob.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = {"classpath:test-application.properties"})
class PostgresLobTestApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    @Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testInsertData(@Autowired DocumentRepository documentRepository){
        Document doc = new Document();
        doc.setId(1L);
        doc.setDateCreated(LocalDateTime.of(2020, 1, 1, 10, 10));
        doc.setDocText("This is the doc text");
        Document saved = documentRepository.save(doc);

        assertEquals(1L, saved.getId());
    }


    @Test
    @Sql(scripts = {"/create-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testSelectDataJpql(@Autowired DocumentRepository documentRepository){

        assertThrows(JpaSystemException.class, () -> documentRepository.findByIdIs(2L).orElseThrow(RuntimeException::new));

    }


    @Test
    @Sql(scripts = {"/create-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testSelectDataLike(@Autowired DocumentRepository documentRepository){

        assertThrows(JpaSystemException.class, () -> documentRepository.findByDocTextLike("text"));

    }

    @Test
    @Sql(scripts = {"/create-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testSelectDataDerivedMethod(@Autowired DocumentRepository documentRepository){

        assertThrows(JpaSystemException.class, () -> documentRepository.findByDateCreatedIsBefore(LocalDateTime.now()));

    }


    @Test
    @Sql(scripts = {"/create-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testSelectEntityById(@Autowired DocumentRepository documentRepository){

        documentRepository.findById(2L).ifPresent(d -> {
            assertEquals(2021, d.getDateCreated().getYear());
            assertEquals("This is the document text 679983d3-7fde-49c2-be77-719b810e7926", d.getDocText());
        });

    }

    @Test
    @Sql(scripts = {"/create-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testSelectAllEntities(@Autowired DocumentRepository documentRepository){

        List<Document> documents = documentRepository.findAll();
        assertEquals(2, documents.size());
        documents.forEach(d -> assertEquals(2021, d.getDateCreated().getYear()));

    }

    @Test
    @Sql(scripts = {"/create-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testSelectEntityById(@Autowired EntityManager em){

        assertThrows(PersistenceException.class,
                () -> em.createQuery("select d from Document d where d.id = 2L", Document.class).getResultList());

    }


    @Test
    @Sql(scripts = {"/create-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testDeleteData(@Autowired DocumentRepository documentRepository){
        Document document = documentRepository.findById(2L).orElseThrow(IllegalStateException::new);
        documentRepository.delete(document);
        documentRepository.flush();
        assertTrue(documentRepository.findById(2L).isEmpty());
    }




}