package br.Insper.ProvaFinal.Carros.service;

import br.Insper.ProvaFinal.Carros.model.Carro;
import br.Insper.ProvaFinal.Carros.repository.CarroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class CarroService {
    @Autowired
    private CarroRepository carroRepository;

    private static final String VALIDATION_URL = "http://184.72.80.215/usuario/validate";

    public Carro criarCarro(Carro carro) {
        return carroRepository.save(carro);
    }

    public Carro registrarCarro(String token, String carroId) {
        Map<String, Object> userInfo = validarToken(token);

        String cpf = userInfo.get("cpf") != null ? userInfo.get("cpf").toString() : null;

        if (cpf != null && !cpf.isEmpty()) {
            Carro carro = carroRepository.findById(carroId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado com ID: " + carroId));

            carro.setComprador_cpf(cpf);
            carro.setComprador_email(userInfo.get("email").toString());
            return carroRepository.save(carro);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF do usuário não disponível para registrar a compra.");
        }
    }

    public Carro buscarCarroPorId(String token, String id) {
        validarToken(token);

        return carroRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado com ID: " + id));
    }

    private Map<String, Object> validarToken(String token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    VALIDATION_URL,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> userInfo = response.getBody();

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
