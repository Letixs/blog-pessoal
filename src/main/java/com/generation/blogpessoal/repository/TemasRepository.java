package com.generation.blogpessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.generation.blogpessoal.model.Temas;

public interface TemasRepository extends JpaRepository<Temas, Long>{
	
	public List<Temas>findAllByTemaContainingIgnoreCase(String tema);
	
}
