package br.Insper.ProvaFinal.Artigos.service;

import br.Insper.ProvaFinal.Artigos.dto.UsuarioDTO;
import br.Insper.ProvaFinal.Artigos.model.Artigo;
import br.Insper.ProvaFinal.Artigos.repository.ArtigoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtigoService {
    @Autowired
    private ArtigoRepository artigoRepository;

    private static final String VALIDATION_URL = "http://184.72.80.215/usuario/validate";

    public Artigo criarArtigo(String token, Artigo artigo) {
        UsuarioDTO userInfo = validarToken(token);

        if ("ADMIN".equalsIgnoreCase(userInfo.getPapel())) {
            artigo.setDataCriacao(LocalDateTime.now());
            return artigoRepository.save(artigo);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas usuários com papel ADMIN podem criar artigos.");
        }
    }

    public Artigo deletarArtigo(String token, String artigoId) {
        UsuarioDTO userInfo = validarToken(token);

        if ("ADMIN".equalsIgnoreCase(userInfo.getPapel())) {
            Artigo artigo = artigoRepository.findById(artigoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artigo não encontrado com ID: " + artigoId));

            artigoRepository.delete(artigo);
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas usuários com papel ADMIN podem deletar artigos.");
        }

        return null;
    }

    public List<Artigo> listarTodosOsArtigos(String token) {
        UsuarioDTO userInfo = validarToken(token);

        if (isAuthorized(userInfo)) {
            return artigoRepository.findAll();
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas usuários com papel ADMIN ou DEVELOPER podem listar artigos.");
        }
    }

    public Artigo buscarArtigoPorId(String token, String artigoId) {
        UsuarioDTO userInfo = validarToken(token);

        if (isAuthorized(userInfo)) {
            return artigoRepository.findById(artigoId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artigo não encontrado com ID: " + artigoId));
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas usuários com papel ADMIN ou DEVELOPER podem buscar artigos.");
        }
    }

    private boolean isAuthorized(UsuarioDTO userInfo) {
        return "ADMIN".equalsIgnoreCase(userInfo.getPapel()) || "DEVELOPER".equalsIgnoreCase(userInfo.getPapel());
    }

    private UsuarioDTO validarToken(String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<UsuarioDTO> response = restTemplate.exchange(
                    VALIDATION_URL,
                    HttpMethod.GET,
                    entity,
                    UsuarioDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                UsuarioDTO userInfo = response.getBody();

                if (userInfo != null) {
                    return userInfo;
                } else {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Resposta da validação de token está vazia.");
                }
            } else {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Erro ao validar token: " + e.getMessage());
        }
    }

}