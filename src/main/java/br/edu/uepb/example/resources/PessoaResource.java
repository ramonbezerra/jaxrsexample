package br.edu.uepb.example.resources;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import br.edu.uepb.example.filters.Authorize;
import br.edu.uepb.example.model.AuthError;
import br.edu.uepb.example.model.JwtToken;
import br.edu.uepb.example.model.Pessoa;
import br.edu.uepb.example.repository.PessoaRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("pessoas")
public class PessoaResource {

    private PessoaRepository pessoaRepository = new PessoaRepository();

    private final SecretKey secretKey = 
        Keys.hmacShaKeyFor("7f-j&CKk=coNzZc0y7_4obMP?#TfcYq%fcD0mDpenW2nc!lfGoZ|d?f&RNbDHUX6"
            .getBytes(StandardCharsets.UTF_8));
    
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(Pessoa usuario) {
        try{
            if(pessoaRepository.getByUsuario(usuario.getUsuario()) != null) {
                String jwtToken = Jwts.builder()
                    .setSubject(usuario.getUsuario())
                    .setIssuer("localhost:8080")
                    .setIssuedAt(new Date())
                    .setExpiration(
                        Date.from(
                            LocalDateTime.now().plusMinutes(15L)
                                .atZone(ZoneId.systemDefault())
                            .toInstant()))
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();

                return Response.ok(new JwtToken(jwtToken)).build();
            } else
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new AuthError("Usuário e/ou senha inválidos")).build();
        }
        catch(Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(e.getMessage()).build();
        } 
    }

    @Authorize
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPessoas() {
        return Response.ok(pessoaRepository.getAll()).build();
    }
    
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPessoaById(@PathParam("id") int id) {
        return Response.ok(pessoaRepository.getById(id)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createPessoa(Pessoa pessoa) {
        pessoaRepository.create(pessoa);
        return Response.status(Response.Status.CREATED).entity(pessoa).build();
    }

    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editPessoa(@PathParam("id") int id, Pessoa pessoa)
    {
        Pessoa p = pessoaRepository.getById(id);
        if(p == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        try{
            pessoa.setId(id);
            pessoaRepository.edit(pessoa);
            return Response.ok(pessoa).build();
        }
        catch(Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        } 
    }

    @DELETE
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePessoa(@PathParam("id") int id)
    {
        Pessoa p = pessoaRepository.getById(id);
        if(p == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        try {
            pessoaRepository.delete(id);
            return Response.noContent().build();
        }
        catch(Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(ex.getMessage()).build();
        } 
    }
}