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
    private Set<TokenType> decl_variaveis_mesmo_tipo = new HashSet<TokenType>();
    private Set<TokenType> tipo_variavel = new HashSet<TokenType>();

    private Set<TokenType> parametro_real = new HashSet<TokenType>();
    private Set<TokenType> expressao_aritmetica = new HashSet<TokenType>();

    private Set<TokenType> comando_geral = new HashSet<TokenType>();
    private Set<TokenType> comando_linha = new HashSet<TokenType>();
    private Set<TokenType> comando_bloco = new HashSet<TokenType>();

    public ConjuntoPrimeiro()
    {
        init_bloco_constantes();
        init_bloco_variaveis();
        init_classes();
        init_decl_constantes_mesmo_tipo();
        init_decl_variaveis_mesmo_tipo();
        init_tipo_variavel();
        init_parametro_real();
        init_expressao_aritmetica();
        init_comando_geral();
        init_comando_linha();
        init_comando_bloco();

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

    private void init_decl_variaveis_mesmo_tipo()
    {
        decl_variaveis_mesmo_tipo.add(TokenType.INTEIRO);
        decl_variaveis_mesmo_tipo.add(TokenType.REAL);
        decl_variaveis_mesmo_tipo.add(TokenType.LOGICO);
        decl_variaveis_mesmo_tipo.add(TokenType.CARACTERE);
        decl_variaveis_mesmo_tipo.add(TokenType.CADEIA);
        decl_variaveis_mesmo_tipo.add(TokenType.ID);
    }

    private void init_tipo_variavel()
    {
        tipo_variavel.add(TokenType.INTEIRO);
        tipo_variavel.add(TokenType.REAL);
        tipo_variavel.add(TokenType.LOGICO);
        tipo_variavel.add(TokenType.CARACTERE);
        tipo_variavel.add(TokenType.CADEIA);
    }

    private void init_parametro_real()
    {
        parametro_real.add(TokenType.ID);
        parametro_real.add(TokenType.NUM);
        parametro_real.add(TokenType.LITERAL);
        parametro_real.add(TokenType.CARACTER);
        parametro_real.add(TokenType.VERDADEIRO);
        parametro_real.add(TokenType.FALSO);
    }

    private void init_expressao_aritmetica()
    {
        expressao_aritmetica.add(TokenType.ABREPAR);
        expressao_aritmetica.add(TokenType.ID);
        expressao_aritmetica.add(TokenType.NUM);
    }

    private void init_comando_geral()
    {
        comando_geral.add(TokenType.ESCREVA);
        comando_geral.add(TokenType.LEIA);
        comando_geral.add(TokenType.RETORNO);
        comando_geral.add(TokenType.INCR);
        comando_geral.add(TokenType.DECR);
        comando_geral.add(TokenType.ID);
        comando_geral.add(TokenType.SE);
        comando_geral.add(TokenType.PARA);
        comando_geral.add(TokenType.ENQUANTO);

    }

    private void init_comando_linha()
    {
        comando_linha.add(TokenType.ESCREVA);
        comando_linha.add(TokenType.LEIA);
        comando_linha.add(TokenType.RETORNO);
        comando_linha.add(TokenType.INCR);
        comando_linha.add(TokenType.DECR);
        comando_linha.add(TokenType.ID);
    }

    private void init_comando_bloco()
    {
        comando_bloco.add(TokenType.SE);
        comando_bloco.add(TokenType.PARA);
        comando_bloco.add(TokenType.ENQUANTO);
    }

    private void armazenarConjuntos()
    {
        conjuntos.put(AnalisadorSintatico.BLOCO_CONSTANTES, bloco_constantes);
        conjuntos.put(AnalisadorSintatico.BLOCO_VARIAVEIS, bloco_variaveis);
        conjuntos.put(AnalisadorSintatico.CLASSES, classes);
        conjuntos.put(AnalisadorSintatico.DECL_CONSTANTES_MESMO_TIPO, decl_constantes_mesmo_tipo);
        conjuntos.put(AnalisadorSintatico.DECL_VARIAVEIS_MESMO_TIPO, decl_variaveis_mesmo_tipo);
        conjuntos.put(AnalisadorSintatico.TIPO_VARIAVEL, tipo_variavel);
        conjuntos.put(AnalisadorSintatico.PARAMETRO_REAL, parametro_real);
        conjuntos.put(AnalisadorSintatico.EXPRESSAO_ARITMETICA, expressao_aritmetica);
        conjuntos.put(AnalisadorSintatico.COMANDO_GERAL, comando_geral);
        conjuntos.put(AnalisadorSintatico.COMANDO_LINHA, comando_linha);
        conjuntos.put(AnalisadorSintatico.COMANDO_BLOCO, comando_bloco);

    }

}
