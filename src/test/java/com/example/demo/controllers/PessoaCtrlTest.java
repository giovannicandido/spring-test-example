package com.example.demo.controllers;

import com.example.demo.entity.Pessoa;
import com.example.demo.repository.PessoaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Giovanni Silva
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = PessoaCtrl.class)
public class PessoaCtrlTest {
    private static final String API_URL = "/api/pessoa";


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private PessoaRepository repository;

    private List<Pessoa> pessoas = Arrays.asList(
            new Pessoa("Alan", "teste@teste.com"),
            new Pessoa("Bob", "teste2@teste.com")
    );

    @Test
    public void getAll() throws Exception {

        doReturn(pessoas).when(repository).findAll();

        this.mockMvc.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

    }

    @Test
    public void findOne() throws Exception {
        doReturn(Optional.of(pessoas.get(0))).when(repository).findById(1L);
        this.mockMvc.perform(get(API_URL + "/1")).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Alan")));


    }

    @Test
    public void findOneNotFound() throws Exception {
        doReturn(Optional.of(pessoas.get(0))).when(repository).findById(1L);
        this.mockMvc.perform(get(API_URL + "/21")).
                andExpect(status().isNotFound())
                .andExpect(content().string(is("Pessoa with id 21 is not found")));


    }

    @Test
    public void deletePessoa() throws Exception {
        doReturn(Optional.of(pessoas.get(0))).when(repository).findById(1L);

        this.mockMvc.perform(delete(API_URL + "/1"))
                .andExpect(status().is2xxSuccessful());

        verify(repository, times(1)).delete(pessoas.get(0));

    }

    @Test
    public void createPessoa() throws Exception {
        createPost(pessoas.get(1))
                .andExpect(status().isCreated());

        verify(repository).save(pessoas.get(1));
    }

    @Test
    public void deleteNotFound() throws Exception {
        doReturn(Optional.empty()).when(repository).findById(anyLong());

        this.mockMvc.perform(delete(API_URL + "/1"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void emailBlank() throws Exception {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Bla");
        createPost(pessoa)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void nomeBlank() throws Exception {
        Pessoa pessoa = new Pessoa();
        pessoa.setEmail("email@email.com");
        createPost(pessoa)
                .andExpect(status().isBadRequest());
    }

    private ResultActions createPost(Object object) throws Exception {
        return this.mockMvc.perform(post(API_URL).content(mapper.writeValueAsBytes(object))
                .contentType(MediaType.APPLICATION_JSON));
    }
}