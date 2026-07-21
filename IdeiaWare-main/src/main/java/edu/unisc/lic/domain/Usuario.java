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

@SuppressWarnings("serial")
@Entity
public class Usuario extends GenericDomain implements Serializable{

    @Column(length = 64, nullable = false)
    private String nome;

    // RACE-01: unique=true trava no banco, nao so em Java
    @Column(length = 32, nullable = false, unique = true)
    private String usuario;

    @Column(length = 128, nullable = false)
    private String senha;

    @Column(length = 3, nullable = false)
    private String permissao;

    // RACE-01/MODELAGEM-01: unique=true, length=50 alinhado ao banco real
    @Column(length = 50, nullable = true, unique = true)
    private String email;
    
    @Column(length = 1, nullable = true)
    private String anonimizado;
    
    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date DataAnonimizado;

    // RESET-TOKEN: hash SHA-256 do token, nunca o token em claro
    @Column(length = 64, nullable = true)
    private String resetTokenHash;

    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date resetTokenExpira;

    public Usuario() {
    }

    public Usuario(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = CriptografaSenha(senha);   // SEC-22: bcrypt
    }

    public Usuario(String nome, String usuario, String senha, String permissao, String email) {
        this.nome = nome;
        this.usuario = usuario;
        this.senha = senha;
        this.permissao = permissao;
        this.email = email;
        this.anonimizado = "N";
    }

    @Override
    public String toString() {
        return "Usuario{" + "nome(" + nome + "), usuario(" + usuario + "), senha(" + senha + "), "
                          + "email(" + email + "), permissao(" + permissao + ")}";
    }
    
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
    
    // SEC-22: bcrypt com salt aleatorio
    private static String CriptografaSenha(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    public boolean checaSenha(String senhaEmClaro) {
        if (senhaEmClaro == null || this.senha == null || this.senha.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(senhaEmClaro, this.senha);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
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
    
    public void setSenha(String senha, Boolean crip) {
        if (crip){
            this.senha = CriptografaSenha(senha);   // SEC-22: bcrypt
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
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResetTokenHash() {
        return resetTokenHash;
    }

    public void setResetTokenHash(String resetTokenHash) {
        this.resetTokenHash = resetTokenHash;
    }

    public Date getResetTokenExpira() {
        return resetTokenExpira;
    }

    public void setResetTokenExpira(Date resetTokenExpira) {
        this.resetTokenExpira = resetTokenExpira;
    }
}
