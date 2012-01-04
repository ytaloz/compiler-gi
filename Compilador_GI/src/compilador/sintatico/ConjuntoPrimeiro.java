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
public class ConjuntoPrimeiro {

    private Map<String, Set> conjuntos = new HashMap<String, Set>();

    private Set<TokenType> bloco_constantes = new HashSet<TokenType>();
    private Set<TokenType> bloco_variaveis = new HashSet<TokenType>();
    private Set<TokenType> classes = new HashSet<TokenType>();

    private Set<TokenType> decl_constantes_mesmo_tipo = new HashSet<TokenType>();

    public ConjuntoPrimeiro()
    {
        init_bloco_constantes();
        init_bloco_variaveis();
        init_classes();
        init_decl_constantes_mesmo_tipo();

        armazenarConjuntos();
    }

    public Set<TokenType> getConjunto(String nome)
    {
        return conjuntos.get(nome);
    }

    private void init_bloco_constantes()
    {
        bloco_constantes.add(TokenType.CONSTANTES);
    }

    private void init_bloco_variaveis()
    {
        bloco_variaveis.add(TokenType.VARIAVEIS);
    }

    private void init_classes()
    {
        classes.add(TokenType.CLASSE);
    }

    private void init_decl_constantes_mesmo_tipo()
    {
        decl_constantes_mesmo_tipo.add(TokenType.INTEIRO);
        decl_constantes_mesmo_tipo.add(TokenType.REAL);
        decl_constantes_mesmo_tipo.add(TokenType.LOGICO);
        decl_constantes_mesmo_tipo.add(TokenType.CARACTERE);
        decl_constantes_mesmo_tipo.add(TokenType.CADEIA);
    }

    private void armazenarConjuntos()
    {
        conjuntos.put(AnalisadorSintatico.BLOCO_CONSTANTES, bloco_constantes);
        conjuntos.put(AnalisadorSintatico.BLOCO_VARIAVEIS, bloco_variaveis);
        conjuntos.put(AnalisadorSintatico.CLASSES, classes);
        conjuntos.put(AnalisadorSintatico.DECL_CONSTANTES_MESMO_TIPO, decl_constantes_mesmo_tipo);
    }

}
