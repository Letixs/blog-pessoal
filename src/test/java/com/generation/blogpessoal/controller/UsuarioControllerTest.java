package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@BeforeAll
	void start() {
		
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", "-"));
	}
	
	@Test 
	@DisplayName("👍Deve cadastrar um novo usuário")
	public void deveCriarUsuario() {
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Ana Maria", "ana@email.com", "12345678", "-"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test 
	@DisplayName("👍Não deve permitir a duplicação do usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Maria", "maria@email.com", "mariamaria", "-"));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, "Maria", "maria@email.com", "mariamaria", "-"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	@Test 
	@DisplayName("👍Deve atualizar os dados do usuário")
	public void deveAtualizarUsuario() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, "Celia", "celia@email.com", "celiacelia", "-"));
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(), "Celia Almeida", "celia12@email.com", "celiacl123", "-");
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
		
	}
	
	@Test 
	@DisplayName("👍Deve listar todos os usuários")
	public void deveListarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "João", "joao@email.com", "joaojoao", "-"));
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Julia", "julia@email.com", "juliajulia", "-"));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	@Test 
	@DisplayName("👍Deve procurar usuário por id")
	public void deveProcurarPorId() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, "Pedro", "pedro@email.com", "pedropedro", "-"));
		
		ResponseEntity<Usuario> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/" + usuarioCadastrado.get().getId(), HttpMethod.GET, null, Usuario.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}
	
	@Test 
	@DisplayName("👍Deve autenticar o usuário")
	public void deveAutenticarUsuario() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L, "Pedro", "pedro@email.com", "pedropedro", "-"));
		
		HttpEntity<UsuarioLogin> login = new HttpEntity<UsuarioLogin>(new UsuarioLogin(0L, "", "pedro@email.com", "pedropedro", "-", ""));
		
		ResponseEntity<UsuarioLogin> autenticacao = testRestTemplate
			.exchange("/usuarios/logar", HttpMethod.POST, login, UsuarioLogin.class);
		
		assertEquals(HttpStatus.OK, autenticacao.getStatusCode());
	}
	
}
