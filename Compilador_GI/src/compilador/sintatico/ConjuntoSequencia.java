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

    public ConjuntoSequencia()
    {
        init_bloco_constantes();
        init_decl_constantes_mesmo_tipo();


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

     private void armazenarConjuntos()
    {
        conjuntos.put(AnalisadorSintatico.BLOCO_CONSTANTES, bloco_constantes);
        conjuntos.put(AnalisadorSintatico.DECL_CONSTANTES_MESMO_TIPO, decl_constantes_mesmo_tipo);


    }

}
