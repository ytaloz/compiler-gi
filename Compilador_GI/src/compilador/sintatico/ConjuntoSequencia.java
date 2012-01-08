/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.sintatico;

import compilador.token.TokenType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Gabriel
 */
public class ConjuntoSequencia {

    private Map<String, Set> conjuntos = new HashMap<String, Set>();
    

    private Set<TokenType> bloco_constantes = new HashSet<TokenType>();
    private Set<TokenType> decl_constantes_mesmo_tipo = new HashSet<TokenType>();

    private Set<TokenType> bloco_variaveis = new HashSet<TokenType>();
    private Set<TokenType> decl_variaveis_mesmo_tipo = new HashSet<TokenType>();

    private Set<TokenType> classe = new HashSet<TokenType>();

    public ConjuntoSequencia()
    {
        init_bloco_constantes();
        init_decl_constantes_mesmo_tipo();
        init_bloco_variaveis();
        init_decl_variaveis_mesmo_tipo();
        init_classe();


        armazenarConjuntos();
    }


    public Set<TokenType> getConjunto(String nome)
    {
        return conjuntos.get(nome);
    }

    private void init_bloco_constantes()
    {
        bloco_constantes.add(TokenType.VARIAVEIS);
        bloco_constantes.add(TokenType.CLASSE);
        bloco_constantes.add(TokenType.EOF);
    }

    private void init_bloco_variaveis()
    {
        bloco_variaveis.add(TokenType.CLASSE);
        bloco_variaveis.add(TokenType.EOF);
    }

    private void init_classe()
    {
        classe.add(TokenType.CLASSE);
        classe.add(TokenType.EOF);
    }

    private void init_decl_constantes_mesmo_tipo()
    {
        //conjunto primeiro
        decl_constantes_mesmo_tipo.add(TokenType.INTEIRO);
        decl_constantes_mesmo_tipo.add(TokenType.REAL);
        decl_constantes_mesmo_tipo.add(TokenType.LOGICO);
        decl_constantes_mesmo_tipo.add(TokenType.CARACTERE);
        decl_constantes_mesmo_tipo.add(TokenType.CADEIA);
        //sequencia
        decl_constantes_mesmo_tipo.add(TokenType.FECHACHAVE);
        decl_constantes_mesmo_tipo.add(TokenType.EOF);
    }

    private void init_decl_variaveis_mesmo_tipo()
    {
        //conjunto primeiro
        decl_variaveis_mesmo_tipo.add(TokenType.INTEIRO);
        decl_variaveis_mesmo_tipo.add(TokenType.REAL);
        decl_variaveis_mesmo_tipo.add(TokenType.LOGICO);
        decl_variaveis_mesmo_tipo.add(TokenType.CARACTERE);
        decl_variaveis_mesmo_tipo.add(TokenType.CADEIA);
        decl_variaveis_mesmo_tipo.add(TokenType.ID);
        //sequencia
        decl_variaveis_mesmo_tipo.add(TokenType.FECHACHAVE);
        decl_variaveis_mesmo_tipo.add(TokenType.EOF);
    }

    

     private void armazenarConjuntos()
    {
        conjuntos.put(AnalisadorSintatico.BLOCO_CONSTANTES, bloco_constantes);
        conjuntos.put(AnalisadorSintatico.DECL_CONSTANTES_MESMO_TIPO, decl_constantes_mesmo_tipo);
        conjuntos.put(AnalisadorSintatico.BLOCO_VARIAVEIS, bloco_variaveis);
        conjuntos.put(AnalisadorSintatico.DECL_VARIAVEIS_MESMO_TIPO, decl_variaveis_mesmo_tipo);
        conjuntos.put(AnalisadorSintatico.CLASSE, classe);
    }

}
