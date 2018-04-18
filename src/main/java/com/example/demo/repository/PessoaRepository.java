package com.example.demo.repository;

import com.example.demo.entity.Pessoa;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Giovanni Silva
 */
public interface PessoaRepository extends CrudRepository<Pessoa, Long> {
}
