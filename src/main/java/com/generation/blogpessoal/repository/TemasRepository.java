package com.generation.blogpessoal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.generation.blogpessoal.model.Temas;

public interface TemasRepository extends JpaRepository<Temas, Long>{

}
