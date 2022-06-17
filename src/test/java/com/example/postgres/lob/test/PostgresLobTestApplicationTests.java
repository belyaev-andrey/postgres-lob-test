package com.example.postgres.lob.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
        doc.setText("This is the doc text");
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

        assertThrows(JpaSystemException.class, () -> documentRepository.findByTextLike("text"));

    }

    @Test
    @Sql(scripts = {"/create-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testSelectDataDerivedMethod(@Autowired DocumentRepository documentRepository){

        documentRepository.findByDateCreatedIsBefore(LocalDateTime.now()).forEach(d -> {
            assertEquals(2022, d.getDateCreated().getYear());
        });

    }


    @Test
    @Sql(scripts = {"/create-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testSelectData(@Autowired DocumentRepository documentRepository){

        documentRepository.findById(2L).ifPresent(d -> {
            assertEquals(2022, d.getDateCreated().getYear());
            assertEquals("This is the document text 679983d3-7fde-49c2-be77-719b810e7926", d.getText());
        });

    }


    @Test
    @Sql(scripts = {"/create-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"/delete-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testSelectData(@Autowired EntityManager em){

        assertThrows(PersistenceException.class,
                () -> em.createQuery("select d from Document d where d.id = 2L", Document.class).getResultList());

    }


    @Test
    @Sql(scripts = {"/create-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void testDeleteData(@Autowired DocumentRepository documentRepository){
        documentRepository.findById(2L).ifPresent(documentRepository::delete);

        assertTrue(documentRepository.findById(2L).isEmpty());
    }




}