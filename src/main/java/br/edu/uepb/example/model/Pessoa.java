package br.edu.uepb.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pessoa {
    private int id;
    private String nome;
    private int idade;  
    private String usuario;
    private String senha; 
}