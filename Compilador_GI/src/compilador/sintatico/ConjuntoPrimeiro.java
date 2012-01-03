/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.sintatico;

import compilador.token.TokenType;
import compilador.token.TokenType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Set;

/**
 *
 * @author Gabriel
 */
public class ConjuntoPrimeiro {

    private Map<String, Set> conjuntos = new HashMap<String, Set>();

    private Set<TokenType> bloco_constantes;
    private Set<TokenType> bloco_variaveis;
    private Set<TokenType> classes;

    public ConjuntoPrimeiro()
    {
        init_bloco_constantes();
        init_bloco_variaveis();
        init_classes();

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

    private void armazenarConjuntos()
    {
        conjuntos.put(AnalisadorSintatico.BLOCO_CONSTANTES, bloco_constantes);
        conjuntos.put(AnalisadorSintatico.BLOCO_VARIAVEIS, bloco_variaveis);
        conjuntos.put(AnalisadorSintatico.CLASSES, classes);
    }

}
