package edu.unisc.lic.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author viniciussdsilva
 */
@SuppressWarnings("serial")
@Entity
public class ElementosStorytelling extends GenericDomain {

    // Chaves estrangeiras
    @ManyToOne
    @JoinColumn(nullable = false)
    private Storytelling storytelling;

    // Atributos
    @Column(length = 15, nullable = false)
    private String tipo;

    @Column(nullable = false, columnDefinition = "longtext")
    private String caminho;

    // Caso for do tipo texto
    @Column(length = 20)
    private String fonte;
    @Column(length = 400)
    private String informacaoTexto;
    @Column
    private int tamanhoFonte;
    @Column(length = 10)
    private String tipoFonte;
    @Column(length = 30)
    private String corFonte;

    // Localização no canvas
    @Column
    private int camada;
    // REVISAO 2026-07-07: precision/scale so tem efeito em DECIMAL/NUMERIC -- em campo
    // double o Hibernate ignora e mapeia como DOUBLE mesmo (confirmado no schema real:
    // "double DEFAULT NULL"). Removido pra nao sugerir uma precisao que nunca foi imposta.
    @Column
    private double x;
    @Column
    private double y;
    @Column
    private double altura;
    @Column
    private double largura;

    // Contrutores
    public ElementosStorytelling() {
        storytelling = new Storytelling();
    }

    public ElementosStorytelling(Storytelling storytelling, String tipo, String caminho, double x, double y, double altura, double largura) {
        this.storytelling = storytelling;
        this.tipo = tipo;
        this.caminho = caminho;
        this.x = x;
        this.y = y;
        this.altura = altura;
        this.largura = largura;
    }
    
    // Métodos

    @Override
    public String toString() {
        return "ElementosStorytelling{" + "storytelling=" + storytelling + ", tipo=" + tipo + ", caminho=" + caminho + ", fonte=" + fonte + ", informacaoTexto=" + informacaoTexto + ", tamanhoFonte=" + tamanhoFonte + ", tipoFonte=" + tipoFonte + ", corFonte=" + corFonte + ", camada=" + camada + ", x=" + x + ", y=" + y + ", altura=" + altura + ", largura=" + largura + '}';
    }

    // Getters e Setters
    public Storytelling getStorytelling() {
        return storytelling;
    }

    public void setStorytelling(Storytelling storytelling) {
        this.storytelling = storytelling;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getInformacaoTexto() {
        return informacaoTexto;
    }

    public void setInformacaoTexto(String informacaoTexto) {
        this.informacaoTexto = informacaoTexto;
    }

    public int getTamanhoFonte() {
        return tamanhoFonte;
    }

    public void setTamanhoFonte(int tamanhoFonte) {
        this.tamanhoFonte = tamanhoFonte;
    }

    public String getTipoFonte() {
        return tipoFonte;
    }

    public void setTipoFonte(String tipoFonte) {
        this.tipoFonte = tipoFonte;
    }

    public String getCorFonte() {
        return corFonte;
    }

    public void setCorFonte(String corFonte) {
        this.corFonte = corFonte;
    }

    public int getCamada() {
        return camada;
    }

    public void setCamada(int camada) {
        this.camada = camada;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    public double getLargura() {
        return largura;
    }

    public void setLargura(double largura) {
        this.largura = largura;
    }

}
