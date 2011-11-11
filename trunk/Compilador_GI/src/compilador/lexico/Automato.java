/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package compilador.lexico;

import compilador.TabelaDeSimbolos;
import compilador.token.Token;
import compilador.token.TokenCategory;
import compilador.token.TokenErro;
import compilador.token.TokenType;

/**
 *
 * @author Gabriel
 */
public class Automato {

    private static int TAMANHO_MAX_ID = 255;

    TabelaDeSimbolos simbolos;

    char[] codigoFonte;
    char caracter;                //caractere atual lido
    String lexemaAtual = "" ;

    int ponteiro = -1;           //indice do vetor de caracteres 
    int linhaAtual = 1;              //linha atual da leitura

    Estado estado = Estado.START;
    Token tokenAtual;


    public Automato(String codigoFonte, TabelaDeSimbolos simbolos)
    {
        codigoFonte = codigoFonte + " ";
        this.codigoFonte = codigoFonte.toCharArray();
        this.simbolos = simbolos;
    }

    //função principal - reconhece e retorna o próximo token do arquivo

    public Token getProxToken() {
        lexemaAtual = "";
        consumirProxCaracter();

        while (estado != Estado.FIM) {
     
            switch (estado) {
                case START: {
                    estadoInicial();
                    break;
                }
                case EM_ID: {
                    reconhecerIdentificador();
                    break;
                }
                case EM_NUM: {
                    reconhecerNumero();
                    break;
                }
                case EM_OPERADOR: {
                    reconhecerOperador();
                    break;
                }
                case EM_DELIMITADOR: {
                    reconhecerDelimitador();
                    break;
                }
                case EM_CADEIACONSTANTE: {
                    reconhecerCadeiaConstante();
                    break;
                }
                case EM_CARACTER: {
                    reconhecerCaracter();
                    break;
                }
                default: {
                    //criarTokenErro("Simbolo Inválido: ");
                    lexemaAtual = lexemaAtual.trim();
                    String mensagem = "#" + "Simbolo Inválido: " + "\"" + lexemaAtual + "\"" + "\t" + "linha: " + linhaAtual;
                    this.tokenAtual = new TokenErro(lexemaAtual, linhaAtual, mensagem, ponteiro);
                    estado = Estado.FIM;
                    break;
                }
                
            }
        }
        estado = Estado.START;
        return tokenAtual; 
    }

    //Estado Inicial do Autômato, delega o reconhecimento para algum dos subautomatos
    
    private void estadoInicial() {
        if (ehLetra(caracter))
        {
            estado = Estado.EM_ID;
        } 
        else if (ehDigito(caracter))
        {
            estado = Estado.EM_NUM;
        }
        else if (ehAspaDupla(caracter))
        {
            estado = Estado.EM_CADEIACONSTANTE;
        }
        else if (ehAspaSimples(caracter))
        {
            estado = Estado.EM_CARACTER;
        }
        else if (ehOperador(caracter))
        {
            estado = Estado.EM_OPERADOR;
        }
        else if (ehDelimitador(caracter))
        {
            estado = Estado.EM_DELIMITADOR;
        }
        else if (ehEspaco(caracter))
        {
            consumirEspacos();
        } 
        else if (ehFinalDeArquivo())
        {
            estado = Estado.FIM;
            criarTokenFinalDeArquivo();
        }        
        else {
            //criarTokenErro("Simbolo inválido: ");
            lexemaAtual = lexemaAtual.trim();
            String mensagem = "#" + "Simbolo Inválido: " + "\"" + lexemaAtual + "\"" + "\t" + "linha: " + linhaAtual;
            this.tokenAtual = new TokenErro(lexemaAtual, linhaAtual, mensagem, ponteiro);
            estado = Estado.FIM;
        }
    }

    //MÉTODOS DE RECONHECIMENTO - SUBAUTÔMATOS

    private void reconhecerIdentificador()
    {
        while (ehCaracterDeIdentificador(caracter)) {
            consumirProxCaracter();
        }

        if (ehSimboloInvalido(caracter) || caracter == '!') {
            criarTokenErro("Identificador Mal Formado: ");
        } else if (lexemaAtual.length() > TAMANHO_MAX_ID) {
            criarTokenErro("Identificador Muito Grande: ");
        } else {
            retrocederUmCaracter();
            if (simbolos.getSimbolo(lexemaAtual) != null) {
                tokenAtual = new Token(TokenType.PALAVRA_RESERVADA, TokenCategory.PALAVRA_RESERVADA, lexemaAtual, linhaAtual);
            } else {
                tokenAtual = new Token(TokenType.ID, TokenCategory.IDENTIFICADOR, lexemaAtual, linhaAtual);
            }
        }
        estado = Estado.FIM;
    }

    private void reconhecerNumero()
    {
        while (ehDigito(caracter)) {
            consumirProxCaracter();
        }

        if (ehSimboloInvalido(caracter) || ehLetra(caracter)) {
            criarTokenErro("Numero Mal Formado: ");
        } else {

            if (caracter == '.') {
                consumirProxCaracter();
                if (ehDigito(caracter)) {
                    while (ehDigito(caracter)) {
                        consumirProxCaracter();
                    }
                    if (ehEspaco(caracter) || ehDelimitador(caracter) || ehOperadorMenosOPonto(caracter)) {
                        retrocederUmCaracter();
                        tokenAtual = new Token(TokenType.NUM, TokenCategory.NUMERO, lexemaAtual, linhaAtual);
                        estado = Estado.FIM;
                    } else {
                        criarTokenErro("Numero Mal Formado: ");
                    }
                } else {
                    criarTokenErro("Numero Mal Formado: ");
                }
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.NUM, TokenCategory.NUMERO, lexemaAtual, linhaAtual);
                estado = Estado.FIM;
            }
            
        }
    }

    private void reconhecerOperador()
    {

        if(caracter=='+') {
            consumirProxCaracter();
            if(caracter=='+') {
                tokenAtual = new Token(TokenType.INCR, TokenCategory.OPERADOR, "++", linhaAtual);
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.ADICAO, TokenCategory.OPERADOR, "+", linhaAtual);
            }
        }
        if(caracter=='-') {
            consumirProxCaracter();
            if(caracter=='-') {
                tokenAtual = new Token(TokenType.DECR, TokenCategory.OPERADOR, "--", linhaAtual);
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.SUB, TokenCategory.OPERADOR, "-", linhaAtual);
            }
        }
        if(caracter=='*') {
            tokenAtual = new Token(TokenType.MULT, TokenCategory.OPERADOR, "*", linhaAtual);
        }
        if (caracter == '/') {
            consumirProxCaracter();
            if (caracter == '/') {
                consumirComentarioLinha();
                tokenAtual = new Token(TokenType.COMENTLINHA, TokenCategory.COMENTARIO, lexemaAtual, linhaAtual);
                linhaAtual++;
            } else if (caracter == '*') {
                consumirComentarioBloco();
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.DIV, TokenCategory.OPERADOR, "/", linhaAtual);
            }
        }
        if(caracter=='=') {
            consumirProxCaracter();
            if(caracter=='=') {
                tokenAtual = new Token(TokenType.IGUAL, TokenCategory.OPERADOR, "==", linhaAtual);
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.ATRIB, TokenCategory.OPERADOR, "=", linhaAtual);
            }
        }
        if(caracter=='!') {
            consumirProxCaracter();
            if(caracter=='=') {
                tokenAtual = new Token(TokenType.DIF, TokenCategory.OPERADOR, "!=", linhaAtual);
            } else {
                criarTokenErro("Simbolo Inválido: ");
            }
        }
        if(caracter=='>') {
            consumirProxCaracter();
            if(caracter=='=') {
                tokenAtual = new Token(TokenType.MAIORIGUAL, TokenCategory.OPERADOR, ">=", linhaAtual);
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.MAIOR, TokenCategory.OPERADOR, ">", linhaAtual);
            }
        }
        if(caracter=='<') {
            consumirProxCaracter();
            if(caracter=='=') {
                tokenAtual = new Token(TokenType.MENORIGUAL, TokenCategory.OPERADOR, "<=", linhaAtual);
            } else {
                retrocederUmCaracter();
                tokenAtual = new Token(TokenType.MENOR, TokenCategory.OPERADOR, "<", linhaAtual);
            }
        }
        if(caracter=='&') {
            consumirProxCaracter();
            if(caracter=='&') {
                tokenAtual = new Token(TokenType.E, TokenCategory.OPERADOR, "&&", linhaAtual);
            } else {
                criarTokenErro("Operador Mal Formado - esperava outro '&': ");
            }
        }
        if(caracter=='|') {
            consumirProxCaracter();
            if(caracter=='|') {
                tokenAtual = new Token(TokenType.OU, TokenCategory.OPERADOR, "||", linhaAtual);
            } else {
                criarTokenErro("Operador Mal Formado - esperava outro '|': ");
            }
        }
        if(caracter=='.') {
            tokenAtual = new Token(TokenType.PONTO, TokenCategory.OPERADOR, ".", linhaAtual);
        }

        estado = Estado.FIM;
    }

    private void reconhecerDelimitador() {
        if (caracter == ';') {
            tokenAtual = new Token(TokenType.PONTOVIRGULA, TokenCategory.DELIMITADOR, ";", linhaAtual);
        }
        if (caracter == ',') {
            tokenAtual = new Token(TokenType.VIRGULA, TokenCategory.DELIMITADOR, ",", linhaAtual);
        }
        if (caracter == '(') {
            tokenAtual = new Token(TokenType.ABREPAR, TokenCategory.DELIMITADOR, "(", linhaAtual);
        }
        if (caracter == ')') {
            tokenAtual = new Token(TokenType.FECHAPAR, TokenCategory.DELIMITADOR, ")", linhaAtual);
        }
        if (caracter == '{') {
            tokenAtual = new Token(TokenType.ABRECHAVE, TokenCategory.DELIMITADOR, "{", linhaAtual);
        }
        if (caracter == '}') {
            tokenAtual = new Token(TokenType.FECHACHAVE, TokenCategory.DELIMITADOR, "}", linhaAtual);
        }
        if (caracter == '[') {
            tokenAtual = new Token(TokenType.ABRECOLCH, TokenCategory.DELIMITADOR, "[", linhaAtual);
        }
        if (caracter == ']') {
            tokenAtual = new Token(TokenType.FECHACOLCH, TokenCategory.DELIMITADOR, "]", linhaAtual);
        }

        estado = Estado.FIM;
    }

    private void reconhecerCadeiaConstante() {
        consumirCadeiaConstante();
        if (ehAspaDupla(caracter)) {
            tokenAtual = new Token(TokenType.LITERAL, TokenCategory.CADEIA_CONSTANTE, lexemaAtual, linhaAtual);
        } else {
            criarTokenErro("String Não Fechada - esperava um '\"': ");
        }
        estado = Estado.FIM;
        
    }

    private void reconhecerCaracter() {
        consumirCadeiaConstanteCaracter();
        if (ehAspaSimples(caracter)) {
            tokenAtual = new Token(TokenType.CARACTER, TokenCategory.CADEIA_CONSTANTE, lexemaAtual, linhaAtual);
        } else {
            criarTokenErro("Caractere não fechado: ");
        }
        estado = Estado.FIM;

    }

    private void consumirCadeiaConstante() {
        consumirProxCaracter();
        while (caracter != '\n' && !ehFinalDeArquivo() && !ehAspaDupla(caracter)) {
            consumirProxCaracter();
        }
        if(caracter == '\n') linhaAtual++;
    }

    private void consumirCadeiaConstanteCaracter() {
        consumirProxCaracter();
        int i = 0;
        while (caracter != '\n' && !ehFinalDeArquivo() && !ehAspaSimples(caracter) && i<1) {
            consumirProxCaracter();
            i++;
        }
        if(caracter == '\n') linhaAtual++;
    }

    private void consumirComentarioLinha() {
        while (caracter != '\n' && !ehFinalDeArquivo()) {
            consumirProxCaracter();
        }
    }

    private void consumirComentarioBloco() {
//        while (caracter != '*' && !ehFinalDeArquivo()) {
//            if(caracter == '\n') linhaAtual++;
//            consumirProxCaracter();
//        }
//        if(caracter == '*') {
//            consumirProxCaracter();
//            if(caracter == '/') {
//                tokenAtual = new Token(TokenType.COMENTBLOCO, TokenCategory.COMENTARIO, lexemaAtual, linhaAtual);
//            } else if(caracter != '*') {
//                consumirComentarioBloco();
//            }
//        } else {
//            criarTokenErro("Fim Inesperado de Arquivo - necessário fechar o comentário de bloco: ");
//        }
        consumirComentarioBlocoPrimeiraParte();
    }

    private void consumirComentarioBlocoPrimeiraParte() {
        while (caracter != '*' && !ehFinalDeArquivo()) {
            if(caracter == '\n') linhaAtual++;
            consumirProxCaracter();
        }
        consumirComentarioBlocoSegundaParte();
    }

    private void consumirComentarioBlocoSegundaParte() {
        if(caracter == '*') {
            consumirProxCaracter();
            if(caracter == '/') {
                tokenAtual = new Token(TokenType.COMENTBLOCO, TokenCategory.COMENTARIO, lexemaAtual, linhaAtual);
            } else if(caracter != '*') {
                consumirComentarioBloco();
            } else consumirComentarioBlocoPrimeiraParte();
        } else {
            criarTokenErro("Fim Inesperado de Arquivo - necessário fechar o comentário de bloco: ");
        }
    }

     private void criarTokenFinalDeArquivo() {
        this.tokenAtual = new Token(TokenType.EOF, TokenCategory.EOF, "Fim de Arquivo", linhaAtual);
        estado = Estado.FIM;
    }

    private void criarTokenErro(String mensagem) {
        if (estado == estado.EM_NUM) {
            while (!ehEspaco(caracter) && !ehDelimitador(caracter) && !ehOperadorMenosOPonto(caracter)) {
                if (caracter == '\n') {
                    linhaAtual++;
                }
                consumirProxCaracter();
                //if(ehSimboloInvalido(caracter)) break;
            }
        } else {
            while (!ehEspaco(caracter) && !ehDelimitador(caracter) && !ehOperador(caracter)) {
                if (caracter == '\n') {
                    linhaAtual++;
                }
                consumirProxCaracter();
                //if(ehSimboloInvalido(caracter)) break;
            }
        }
        retrocederUmCaracter();
        this.lexemaAtual = this.lexemaAtual.trim();
        mensagem = "#" + mensagem + "\"" + lexemaAtual + "\"" + "\t" + "linha: " + linhaAtual;
        this.tokenAtual = new TokenErro(lexemaAtual, linhaAtual, mensagem, ponteiro);
        estado = Estado.FIM;
    }



    //MÉTODOS AUXILIARES

    private void consumirProxCaracter() {
        if (ehFinalDeArquivo()) {
            criarTokenFinalDeArquivo();
        } else {
            ponteiro++;
            this.caracter = this.codigoFonte[ponteiro];
            this.lexemaAtual = this.lexemaAtual + caracter;            
        }
    }

    private void retrocederUmCaracter() {
        if (!ehFinalDeArquivo()) {
            ponteiro--;
            this.caracter = this.codigoFonte[ponteiro];
            this.lexemaAtual = this.lexemaAtual.substring(0, (this.lexemaAtual.length() - 1));
            this.lexemaAtual = lexemaAtual.trim();
        }
    }

    private void consumirEspacos() {
        while (ehEspaco(caracter)) {
            ponteiro++;
            if (caracter == '\n') linhaAtual++;
            if (!ehFinalDeArquivo()) {
                this.caracter = this.codigoFonte[ponteiro];
            } else {
                criarTokenFinalDeArquivo();
                break;
            }
        }
        lexemaAtual = lexemaAtual + caracter;
    }
    
    private boolean ehDigito(char c) {
        return Character.isDigit(c);
    }

    private boolean ehLetra(char c) {
        return Character.isLetter(c);
    }

    private boolean ehEspaco(char c) {
        return (Character.isSpaceChar(c) || c == '\r' || c == '\n' || c=='\t');
    }

    private boolean ehDelimitador(char c) {
        return (c == ';' || c == ',' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}');
    }

    private boolean ehOperador(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || c == '!' || c == '>' || c == '<' || c == '&' || c == '|' || c == '.');
    }

    private boolean ehOperadorMenosOPonto(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/' || c == '=' || c == '!' || c == '>' || c == '<' || c == '&' || c == '|');
    }

    private boolean ehUnderline(char c) {
        return c == '_';
    }

    private boolean ehAspaDupla(char c) {
        return c=='\"';
    }

    private boolean ehAspaSimples(char c) {
        return c=='\'';
    }
    
    private boolean ehCaracterDeIdentificador(char c) {
        return (ehLetra(c) || ehDigito(c) || ehUnderline(c));
    }

    private boolean ehSimboloInvalido(char c) {
        return (!ehLetra(c) && !ehDigito(c) && !ehEspaco(c) && !ehDelimitador(c) && !ehOperador(c) && !ehUnderline(c) && !ehAspaDupla(c) && !ehAspaSimples(c));
    }

    private boolean ehFinalDeArquivo() {
        return ponteiro >= codigoFonte.length-1;
    }

}
