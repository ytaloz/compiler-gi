/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.tabeladesimbolos;

import compilador.tabeladesimbolos.simbolos.Classe;
import compilador.tabeladesimbolos.simbolos.Constante;
import compilador.tabeladesimbolos.simbolos.Metodo;
import compilador.tabeladesimbolos.simbolos.Simbolo;
import compilador.tabeladesimbolos.simbolos.Variavel;
import compilador.token.TokenType;
import java.util.HashMap;

/**
 *
 * @author Gabriel
 */
public class TabelaDeSimbolos {

    //palavras chave da linguagem
    private HashMap<String,String> palavrasChave = new HashMap<String,String>();

    //estrutura de dados onde os simbolos serão armazenados e recuperados, levando em consideração o escopo
    private ArvoreDeEscopo arvoreDeEscopo = new ArvoreDeEscopo();

    //flag referente à declaração do método principal
    private boolean metodoPrincipalFoiDeclarado = false;


    public TabelaDeSimbolos()
    {
        inicializarPalavrasChave();
    }

    public Simbolo getSimbolo(String id)
    {
        return arvoreDeEscopo.getSimbolo(id);
    }
    
    public Classe getClasse(String id)
    {
        return arvoreDeEscopo.getClasse(id);
    }

    public Classe getClasseAtual()
    {
        return arvoreDeEscopo.getClasseAtual();
    }

    public Escopo getEscopoAtual()
    {
        return arvoreDeEscopo.getEscopoAtual();
    }

//---------------- MÉTODOS RELACIONADOS A ESCOPO ------------------------------
   

    public void empilharNovoEscopo(Escopo escopo)
    {
        arvoreDeEscopo.empilharNovoEscopo(escopo);
    }

    public void desempilharEscopo()
    {
        arvoreDeEscopo.desempilharEscopo();
    }

    public boolean jaFoiDeclaradoNoEscopo(String id)
    {
        return arvoreDeEscopo.getSimbolo(id)!=null;
    }

    public boolean jaFoiDeclaradoNoBlocoAtual(String id)
    {
        return arvoreDeEscopo.getEscopoAtual().getSimbolo(id) != null;
    }

//------------------------ ADICIONAR SIMBOLOS DECLARADOS -----------------------


    public void addConstante(Constante con)
    {
        arvoreDeEscopo.addConstante(con);
    }

    public void addVariavel(Variavel var)
    {
        arvoreDeEscopo.addVariavel(var);
    }

    public void addClasse(Classe classe)
    {
        arvoreDeEscopo.addClasse(classe);
    }

    public void addMetodo(Metodo metodo)
    {
        arvoreDeEscopo.addMetodo(metodo);
    }

    public void addParametro(Variavel param)
    {
        arvoreDeEscopo.addParametro(param);
    }

    public void addConstrutor(Metodo construtor)
    {
        arvoreDeEscopo.addConstrutor(construtor);
    }
    
//--------------------------- MÉTODOS AUXILIARES -------------------------------

    public boolean metodoPrincipalFoiDeclarado()
    {
        return metodoPrincipalFoiDeclarado;
    }

    public void setMetodoPrincipal(boolean flag)
    {
        metodoPrincipalFoiDeclarado = flag;
    }

//--------------------------- PALAVRAS CHAVE -----------------------------------

    private void inserirPalavraChave(String lexema)
    {
        palavrasChave.put(lexema, lexema);
    }

    public String getPalavraChave(String chave)
    {
        return palavrasChave.get(chave);
    }

    public TokenType getTokenPalavraChave(String palavraChave)
    {
        if ( palavraChave.equals("variaveis") ) return TokenType.VARIAVEIS;
        if ( palavraChave.equals("metodos") ) return TokenType.METODOS;
        if ( palavraChave.equals("constantes") ) return TokenType.CONSTANTES;
        if ( palavraChave.equals("classe") ) return TokenType.CLASSE;
        if ( palavraChave.equals("retorno") ) return TokenType.RETORNO;
        if ( palavraChave.equals("vazio") ) return TokenType.VAZIO;
        if ( palavraChave.equals("principal") ) return TokenType.PRINCIPAL;
        if ( palavraChave.equals("se") ) return TokenType.SE;
        if ( palavraChave.equals("entao") ) return TokenType.ENTAO;
        if ( palavraChave.equals("senao") ) return TokenType.SENAO;
        if ( palavraChave.equals("enquanto") ) return TokenType.ENQUANTO;
        if ( palavraChave.equals("para") ) return TokenType.PARA;
        if ( palavraChave.equals("leia") ) return TokenType.LEIA;
        if ( palavraChave.equals("escreva") ) return TokenType.ESCREVA;
        if ( palavraChave.equals("inteiro") ) return TokenType.INTEIRO;
        if ( palavraChave.equals("real") ) return TokenType.REAL;
        if ( palavraChave.equals("logico") ) return TokenType.LOGICO;
        if ( palavraChave.equals("caractere") ) return TokenType.CARACTERE;
        if ( palavraChave.equals("cadeia") ) return TokenType.CADEIA;
        if ( palavraChave.equals("verdadeiro") ) return TokenType.VERDADEIRO;
        if ( palavraChave.equals("falso") ) return TokenType.FALSO;
        if ( palavraChave.equals("herda_de") ) return TokenType.HERDA_DE;

        return null;
    }

    private void inicializarPalavrasChave()
    {
        inserirPalavraChave("variaveis");
        inserirPalavraChave("metodos");
        inserirPalavraChave("constantes");
        inserirPalavraChave("classe");
        inserirPalavraChave("retorno");
        inserirPalavraChave("vazio");
        inserirPalavraChave("principal");
        inserirPalavraChave("se");
        inserirPalavraChave("entao");
        inserirPalavraChave("senao");
        inserirPalavraChave("enquanto");
        inserirPalavraChave("para");
        inserirPalavraChave("leia");
        inserirPalavraChave("escreva");
        inserirPalavraChave("inteiro");
        inserirPalavraChave("real");
        inserirPalavraChave("logico");
        inserirPalavraChave("caractere");
        inserirPalavraChave("cadeia");
        inserirPalavraChave("verdadeiro");
        inserirPalavraChave("falso");
        inserirPalavraChave("herda_de");
    }

    


}
