package br.Insper.ProvaFinal.Artigos.controller;

import br.Insper.ProvaFinal.Artigos.model.Artigo;
import br.Insper.ProvaFinal.Artigos.service.ArtigoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/artigos")
public class ArtigoController {

    @Autowired
    private ArtigoService artigoService;

    @PostMapping
    public Artigo criarArtigo(@RequestHeader("Authorization") String token, @RequestBody Artigo artigo) {
        return artigoService.criarArtigo(token, artigo);
    }

    @DeleteMapping("/{artigoId}")
    public void deletarArtigo(@RequestHeader("Authorization") String token, @PathVariable String artigoId) {
        artigoService.deletarArtigo(token, artigoId);
    }

    @GetMapping
    public List<Artigo> listarTodosOsArtigos(@RequestHeader("Authorization") String token) {
        return artigoService.listarTodosOsArtigos(token);
    }

    @GetMapping("/{artigoId}")
    public Artigo buscarArtigoPorId(@RequestHeader("Authorization") String token, @PathVariable String artigoId) {
        return artigoService.buscarArtigoPorId(token, artigoId);
    }
}