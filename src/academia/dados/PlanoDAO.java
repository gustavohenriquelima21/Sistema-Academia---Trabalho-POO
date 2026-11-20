package academia.dados;

import academia.modelo.Plano;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PlanoDAO{

    private final Path arquivo = Paths.get("dados", "planos.txt");

    public PlanoDAO(){
        prepararArquivo();
    }

    private void prepararArquivo(){
        try{
            Files.createDirectories(arquivo.getParent());

            if(!Files.exists(arquivo)){
                Files.createFile(arquivo);
            }

        } catch(IOException e){
            throw new IllegalStateException(
                "Erro ao preparar o arquivo de planos.", e);
        }
    }

    private String codificar(String texto){
        return Base64.getEncoder().encodeToString(
            texto.getBytes(StandardCharsets.UTF_8));
    }

    private String decodificar(String texto){
        return new String(
            Base64.getDecoder().decode(texto),
            StandardCharsets.UTF_8);
    }

    public List<Plano> listarTodos(){
        List<Plano> planos = new ArrayList<>();

        try{
            List<String> linhas = Files.readAllLines(
                arquivo, StandardCharsets.UTF_8);

            for(String linha : linhas){
                if(!linha.trim().isEmpty()){
                    planos.add(converterPlano(linha));
                }
            }

        } catch(IOException e){
            throw new IllegalStateException(
                "Erro ao ler os planos.", e);
        }

        return planos;
    }

    private Plano converterPlano(String linha){
        try{
            String[] dados = linha.split("\\|", -1);

            if(dados.length != 5){
                throw new IllegalArgumentException("Registro inválido.");
            }

            Plano plano = new Plano(
                Integer.parseInt(dados[0]),
                decodificar(dados[1]),
                Double.parseDouble(dados[2]),
                Integer.parseInt(dados[3])
            );

            plano.setAtivo(Boolean.parseBoolean(dados[4]));

            return plano;

        } catch(RuntimeException e){
            throw new IllegalStateException(
                "Erro ao interpretar um registro de plano: " + linha, e);
        }
    }

    public Plano buscarPorId(int id){
        for(Plano plano : listarTodos()){
            if(plano.getId() == id){
                return plano;
            }
        }

        return null;
    }

    public int proximoId(){
        int maiorId = 0;

        for(Plano plano : listarTodos()){
            if(plano.getId() > maiorId){
                maiorId = plano.getId();
            }
        }

        return maiorId + 1;
    }

    public void inserir(Plano plano){
        List<Plano> planos = listarTodos();
        planos.add(plano);
        salvarTodos(planos);
    }

    public void atualizar(Plano planoAtualizado){
        List<Plano> planos = listarTodos();
        boolean encontrado = false;

        for(int i = 0; i < planos.size(); i++){
            if(planos.get(i).getId() == planoAtualizado.getId()){
                planos.set(i, planoAtualizado);
                encontrado = true;
                break;
            }
        }

        if(encontrado){
            salvarTodos(planos);
        }
    }

    public void excluir(int id){
        List<Plano> planos = listarTodos();

        planos.removeIf(plano -> plano.getId() == id);

        salvarTodos(planos);
    }

    private void salvarTodos(List<Plano> planos){
        List<String> linhas = new ArrayList<>();

        for(Plano plano : planos){
            String linha =
                plano.getId() + "|" +
                codificar(plano.getNome()) + "|" +
                plano.getValorMensal() + "|" +
                plano.getDuracaoMeses() + "|" +
                plano.isAtivo();

            linhas.add(linha);
        }

        try{
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);

        } catch(IOException e){
            throw new IllegalStateException(
                "Erro ao salvar os planos.", e);
        }
    }
}