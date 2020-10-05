package br.edu.uepb.example.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.edu.uepb.example.model.Pessoa;

public class PessoaRepository {
    
    private static HashMap<Integer, Pessoa> pessoas = new HashMap<>();

    public List<Pessoa> getAll() {
        return new ArrayList<Pessoa>(pessoas.values());
    }

    public Pessoa getById(int id) {
		return pessoas.get(id);
    }
    
    public Pessoa getByUsuario(String usuario) {
        Pessoa pessoa = null;
        for (Pessoa pessoaRegistrada : pessoas.values()) {
            if (pessoaRegistrada.getUsuario().equals(usuario)) {
                pessoa = pessoaRegistrada;
                break;
            }
        }
        return pessoa;
    }

	public void create(Pessoa pessoa) {
        if (pessoa.getId() == 0)
            pessoa.setId(generateId(pessoas.size() + 1));
        pessoas.put(pessoa.getId(), pessoa);
    }
    
    private int generateId(final int possible)  {
        if(pessoas.containsKey(possible))
            return generateId(possible + 1);
        return possible;
    }

	public void edit(Pessoa pessoa) {
        pessoas.remove(pessoa.getId());
        pessoas.put(pessoa.getId(), pessoa);
	}

	public void delete(int id) {
        pessoas.remove(id);
	}	
}