package edu.unisc.lic.classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ManipulaTxt {

    public final String NOME_ARQUIVO = "cfgbanco.txt";

    //Faz a leitura de cada linha do TXT e armazena em uma lista
    public ArrayList<String> lerTxt() throws FileNotFoundException, IOException {

        ArrayList<String> lista = new ArrayList<>();

        try (BufferedReader leitor = new BufferedReader(new FileReader(retornaDiretorioArquivo()))) {

            String linha;
            while ((linha = leitor.readLine()) != null) {
                lista.add(linha);
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }

        return lista;
    }
    
    //Verifica se o arquivo existe
    public boolean verificarSeArquivoExiste() {

        File arquivo = new File(retornaDiretorioArquivo());
        return arquivo.exists();
    }
    
    private String retornaDiretorioArquivo() {
        return System.getProperty("catalina.base") + "/conf/"+NOME_ARQUIVO;
    }

}
