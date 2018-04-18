package com.example.demo.controllers;

import com.example.demo.entity.Pessoa;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Giovanni Silva
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = "/init-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = "/delete-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PessoaCtrlIntegTest {


    private static final String API_URL = "/api/pessoa";

    @LocalServerPort
    private String port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void getAll() {
        ResponseEntity<Pessoa[]> response = restTemplate.getForEntity(API_URL, Pessoa[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    public void post() {
        Pessoa pessoa = new Pessoa("nome", "email@email.com");
        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, pessoa, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        List<Pessoa> pessoas = listPessoa();
        assertThat(pessoas).hasSize(3);
        assertThat(pessoas).contains(pessoa);

    }

    private List<Pessoa> listPessoa() {
        return (List<Pessoa>) this.entityManager.createQuery("select p from Pessoa p").getResultList();
    }




}
