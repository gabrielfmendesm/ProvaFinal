package br.Insper.ProvaFinal.Artigos.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "artigos")
@Getter
@Setter
public class Artigo {
    @MongoId
    private String id;
    private String titulo;
    private String conteudo;
    private String autor;
    private String dataCriacao;
}
