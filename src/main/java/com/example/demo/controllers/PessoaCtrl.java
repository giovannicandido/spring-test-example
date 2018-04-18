package com.example.demo.controllers;

import com.example.demo.entity.Pessoa;
import com.example.demo.repository.PessoaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * @author Giovanni Silva
 */
@RestController
@RequestMapping("/api/pessoa")
public class PessoaCtrl {
    private final PessoaRepository repository;

    public PessoaCtrl(PessoaRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Iterable<Pessoa> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity findOne(@PathVariable Long id) {
        Optional<Pessoa> byId = repository.findById(id);
        return orElseReturn(byId, id);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        Optional<Pessoa> byId = repository.findById(id);
        if (byId.isPresent()) {
            repository.delete(byId.get());
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @Valid Pessoa pessoa) {
        repository.save(pessoa);
    }


    private <T> ResponseEntity orElseReturn(Optional<T> optional, Long id) {
        if(optional.isPresent()) {
            return optional.map(ResponseEntity::ok).get();
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pessoa with id " + id + " is not found");
        }
    }


}
