package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.Autor;
import br.com.alura.literalura.model.DadosLivro;
import br.com.alura.literalura.model.Livro;
import br.com.alura.literalura.model.Results;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivrosRepository;
import br.com.alura.literalura.service.ConsumoAPI;
import br.com.alura.literalura.service.ConverteDados;

import java.util.*;

public class Principal {
    private final Scanner scan = new Scanner(System.in);
    private final ConsumoAPI consumo = new ConsumoAPI();
    private final ConverteDados conversor = new ConverteDados();
    private final LivrosRepository livrosRepositorio;
    private final AutorRepository autorRepositorio;
    private static final String API_URL = "https://gutendex.com/books/?search=";

    private List<Livro> livros;
    private List<Autor> autores;

    public Principal(LivrosRepository livrosRepositorio, AutorRepository autorRepositorio) {
        this.livrosRepositorio = livrosRepositorio;
        this.autorRepositorio = autorRepositorio;
    }

    public void exibeMenu() {
        int opcao = -1;
        while (opcao != 0) {
            String menu = """
                    |***************************************************|
                    |*****                BEM-VINDO               ******|
                    |***************************************************|
                    
                    1 - Buscar livro por nome
                    2 - Listar livros salvos
                    3 - Listar autores salvos
                    4 - Listar autores vivos em um determinado ano
                    5 - Listar livros por idioma
                    
                    0 - Sair
                    
                    |***************************************************|
                    |*****            ESCOLHA UMA OPÇÃO           ******|
                    |***************************************************|
                    """;

            try {
                System.out.println(menu);
                opcao = scan.nextInt();
                scan.nextLine();

                switch (opcao) {
                    case 1 -> buscarLivro();
                    case 2 -> listarLivrosSalvos();
                    case 3 -> listarAutoresSalvos();
                    case 4 -> listarAutoresVivosEmUmAno();
                    case 5 -> listarLivrosPorIdioma();
                    case 0 -> {
                        System.out.println("|********************************|");
                        System.out.println("|     ENCERRANDO A APLICAÇÃO     |");
                        System.out.println("|********************************|\n");
                    }
                    default -> {
                        System.out.println("|********************************|");
                        System.out.println("|         OPÇÃO INCORRETA        |");
                        System.out.println("|********************************|\n");
                        System.out.println("TENTE NOVAMENTE");
                    }
                }
            } catch (InputMismatchException e) {
                System.out.println("|***************************************************|");
                System.out.println("|*****         Insira um número válido        ******|");
                System.out.println("|***************************************************|");
                scan.nextLine();
            }
        }
    }

    private void listarLivrosPorIdioma() {
        System.out.println("Lista de livros por idioma\n------------");
        System.out.println("""
                \n\t---- Escolha o idioma ----
                \ten - Inglês
                \tes - Espanhol
                \tfr - Francês
                \tpt - Português
                """);
        
        String idioma = scan.nextLine();
        livros = livrosRepositorio.findByIdiomasContains(idioma);
        
        if (livros.isEmpty()) {
            System.out.println("Livros no idioma escolhido não encontrados");
            listarLivrosPorIdioma();
        } else {
            livros.stream()
                    .sorted(Comparator.comparing(Livro::getTitulo))
                    .forEach(System.out::println);
        }
    }

    private void listarAutoresVivosEmUmAno() {
        System.out.println("Liste os autores vivos em um determinado ano... Por favor, insira o ano");
        Integer ano = Integer.valueOf(scan.nextLine());
        
        autores = autorRepositorio
                .findByAnoNascimentoLessThanEqualAndAnoFalecimentoGreaterThanEqual(ano, ano);
        
        if (autores.isEmpty()) {
            System.out.println("Autores vivos não encontrados");
        } else {
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNome))
                    .forEach(System.out::println);
        }
    }

    private void listarAutoresSalvos() {
        System.out.println("Lista de autores no banco de dados\n------------");
        autores = autorRepositorio.findAll();
        
        autores.stream()
                .sorted(Comparator.comparing(Autor::getNome))
                .forEach(System.out::println);
    }

    private void listarLivrosSalvos() {
        System.out.println("Lista de livros no banco de dados\n------------");
        livros = livrosRepositorio.findAll();
        
        livros.stream()
                .sorted(Comparator.comparing(Livro::getTitulo))
                .forEach(System.out::println);
    }

    private void buscarLivro() {
        System.out.println("Qual livro deseja buscar?: ");
        String nomeLivro = scan.nextLine().toLowerCase();
        
        String json = consumo.obterDados(API_URL + nomeLivro.replace(" ", "%20").trim());
        Results dados = conversor.obterDados(json, Results.class);
        
        if (dados.results().isEmpty()) {
            System.out.println("Livro não encontrado");
        } else {
            DadosLivro dadosLivro = dados.results().get(0);
            Livro livro = new Livro(dadosLivro);
            Autor autor = new Autor().pegaAutor(dadosLivro);
            salvarDados(livro, autor);
        }
    }

    private void salvarDados(Livro livro, Autor autor) {
        Optional<Livro> livroEncontrado = livrosRepositorio.findByTituloContains(livro.getTitulo());
        
        if (livroEncontrado.isPresent()) {
            System.out.println("Esse livro já existe no banco de dados");
            System.out.println(livro);
        } else {
            try {
                livrosRepositorio.save(livro);
                System.out.println("Livro guardado");
                System.out.println(livro);
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }

        Optional<Autor> autorEncontrado = autorRepositorio.findByNomeContains(autor.getNome());
        
        if (autorEncontrado.isPresent()) {
            System.out.println("Esse autor já existe no banco de dados");
            System.out.println(autor);
        } else {
            try {
                autorRepositorio.save(autor);
                System.out.println("Autor guardado");
                System.out.println(autor);
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }
}
