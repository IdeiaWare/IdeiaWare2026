package edu.unisc.lic.domain;

import edu.unisc.lic.classes.Constantes;
import edu.unisc.lic.classes.Data;
import java.io.Serializable;
import java.util.Date;
import org.mindrot.jbcrypt.BCrypt;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author m78208
 */
@SuppressWarnings("serial")
@Entity
public class Usuario extends GenericDomain implements Serializable{

    //Atributos
    @Column(length = 64, nullable = false)
    private String nome;

    // RACE-01: unique=true trava no BANCO (nao so em Java) que dois usuarios tenham o
    // mesmo login. Antes, CadastroUsuarioServlet fazia "verifica se existe -> insere" em
    // 2 passos sem nenhuma trava real: 2 cadastros simultaneos com o mesmo login passavam
    // os 2 pela verificacao e os 2 inseriam -> 2 contas com o mesmo login -> LogInServlet
    // exige lista.size()==1 pra deixar logar, entao as 2 contas ficavam trancadas pra
    // sempre (sem erro visivel, so "login e/ou senha invalido"), so recuperavel com
    // intervencao manual no banco.
    @Column(length = 32, nullable = false, unique = true)
    private String usuario;

    @Column(length = 128, nullable = false)
    private String senha;

    @Column(length = 3, nullable = false)
    private String permissao;

    // RACE-01: mesma razao do campo usuario acima -- CadastroUsuarioServlet tambem
    // checava email duplicado em Java sem trava no banco.
    // MODELAGEM-01 (2026-07-03): estava length=100 aqui, mas o banco real (estrutura-
    // lic_bd.sql) sempre foi varchar(50) -- alinhado pro numero que ja esta em producao
    // (mudar a anotacao Java, NAO o banco, evita ALTER TABLE numa coluna que pode ja ter
    // dados reais).
    @Column(length = 50, nullable = true, unique = true)
    private String email;
    
    @Column(length = 1, nullable = true)
    private String anonimizado;
    
    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date DataAnonimizado;
  
    //Métodos Construtores
    public Usuario() {
    }
    
    // LucasFreitag 2024
    public Usuario(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = CriptografaSenha(senha);   // SEC-22: bcrypt (era SHA-256)
    }

    public Usuario(String nome, String usuario, String senha, String permissao, String email) {
        this.nome = nome;
        this.usuario = usuario;
        this.senha = senha;
        this.permissao = permissao;
        this.email = email; // LucasFreitag 2024
        this.anonimizado = "N";
    }

//	@Column(precision = 7, scale = 2, nullable = false) // precision são quantos números ao total, scale é quantos números após a vírgula
//	private BigDecimal salario;                         // xxxxx,xx
    
    //Métodos
    @Override
    public String toString() {
        return "Usuario{" + "nome(" + nome + "), usuario(" + usuario + "), senha(" + senha + "), "
                          + "email(" + email + "), permissao(" + permissao + ")}";
    }
    
    // LucasFreitag 2024
    //Retorna informações pessoais
    public String getDadosPessoais(String delimitador) {
        return "Usuário: " + usuario + delimitador +
               "Nome: " + nome + delimitador +
               "E-mail: " + email + delimitador;
    }
    public void AnonimizaDadosPessoais(){
        this.nome = "Nome anonimizado "+this.getCodigo();
        this.usuario = "UsuarioAnonimizado"+this.getCodigo();
        this.email = "EmailAnonimizado"+this.getCodigo()+"@anonimizado.com";
        this.anonimizado = "S";
        this.DataAnonimizado = Data.horaAtual();
        this.ResetaSenha();
    }

    public String getAnonimizado() {
        return anonimizado;
    }

    public void setAnonimizado(String anonimizado) {
        this.anonimizado = anonimizado;
    }

    public Date getDataAnonimizado() {
        return DataAnonimizado;
    }

    public void setDataAnonimizado(Date DataAnonimizado) {
        this.DataAnonimizado = DataAnonimizado;
    }
    public String getDadosPessoais(){
        return getDadosPessoais("\n");
    }
    
    //Gera senha aleatória de 10 dígitos
    public String ResetaSenha() {
        int tamSenha = 10;
        StringBuilder senha = new StringBuilder(tamSenha);
        
        senha.append(Constantes.LETRAS_MIN.charAt(Constantes.random.nextInt(Constantes.LETRAS_MIN.length())));
        senha.append(Constantes.LETRAS_MAI.charAt(Constantes.random.nextInt(Constantes.LETRAS_MAI.length())));
        senha.append(Constantes.NUMEROS.charAt(Constantes.random.nextInt(Constantes.NUMEROS.length())));
        senha.append(Constantes.CARAC_ESP.charAt(Constantes.random.nextInt(Constantes.CARAC_ESP.length())));
        
        for (int i = 4; i < tamSenha; i++) {
            senha.append(Constantes.CARACTERES.charAt(Constantes.random.nextInt(Constantes.CARACTERES.length())));
        }
        String senhaNova = EmbaralharString(senha.toString());
        this.setSenha(senhaNova,true);
        return senhaNova;
    }
        
    //Embaralha senha aleatória
    private static String EmbaralharString(String str){
        char[] caracteres = str.toCharArray();
        for (int i = caracteres.length - 1; i > 0; i--) {
            int j = Constantes.random.nextInt(i + 1);
            char temp = caracteres[i];
            caracteres[i] = caracteres[j];
            caracteres[j] = temp;
        }
        return new String(caracteres);
    }
    
    // SEC-22: hashing de senha com bcrypt (era SHA-256 salgado com o usuario). O bcrypt
    // ja gera salt aleatorio por hash (nao precisa concatenar o usuario) e tem custo
    // ajustavel -> resistente a brute-force/GPU. A verificacao e via checaSenha().
    private static String CriptografaSenha(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    /** Verifica a senha em claro contra o hash bcrypt armazenado (this.senha). */
    public boolean checaSenha(String senhaEmClaro) {
        if (senhaEmClaro == null || this.senha == null || this.senha.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(senhaEmClaro, this.senha);
        } catch (IllegalArgumentException e) {
            // hash em formato invalido (dado legado/corrompido) -> nao autentica
            return false;
        }
    }
    
    //Getters and Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    // LucasFreitag 2024
    public void setSenha(String senha, Boolean crip) {
        if (crip){
            this.senha = CriptografaSenha(senha);   // SEC-22: bcrypt (era SHA-256+usuario)
        } else {
            this.senha = senha;
        }
    }

    public String getPermissao() {
        return permissao;
    }

    public void setPermissao(String permissao) {
        this.permissao = permissao;
    }
    // LucasFreitag 2024 
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
