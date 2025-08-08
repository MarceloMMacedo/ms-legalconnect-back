package br.com.legalconnect.common.util;

import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @class ValidatorUtil
 * @brief Classe utilitária para validações comuns, como CPF e CNPJ.
 *
 *        Contém métodos estáticos para validar documentos brasileiros,
 *        que são frequentemente necessários em aplicações de negócio.
 */
public final class ValidatorUtil {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Construtor privado para evitar instanciação.
     */
    private ValidatorUtil() {
        // Construtor privado para garantir que a classe não seja instanciada.
        // É uma classe de utilidade com apenas métodos estáticos.
    }

    /**
     * @brief Valida se um CPF é válido.
     *
     *        Implementa o algoritmo de validação de CPF brasileiro,
     *        incluindo a verificação dos dígitos verificadores.
     *
     * @param cpf O número do CPF (apenas dígitos).
     * @return True se o CPF for válido, false caso contrário.
     */
    public static boolean isValidCPF(String cpf) {
        if (cpf == null || cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false; // CPF nulo, com tamanho diferente de 11 ou com todos os dígitos iguais
        }

        char dig10, dig11;
        int sm, i, r, num, peso;

        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i = 0; i < 9; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else
                dig10 = (char) (r + 48);

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for (i = 0; i < 10; i++) {
                num = (int) (cpf.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig11 = '0';
            else
                dig11 = (char) (r + 48);

            // Verifica se os dígitos calculados conferem com os dígitos informados.
            return (dig10 == cpf.charAt(9)) && (dig11 == cpf.charAt(10));
        } catch (InputMismatchException erro) {
            return false;
        }
    }

    /**
     * @brief Valida se um CNPJ é válido.
     *
     *        Implementa o algoritmo de validação de CNPJ brasileiro,
     *        incluindo a verificação dos dígitos verificadores.
     *
     * @param cnpj O número do CNPJ (apenas dígitos).
     * @return True se o CNPJ for válido, false caso contrário.
     */
    public static boolean isValidCNPJ(String cnpj) {
        if (cnpj == null || cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) {
            return false; // CNPJ nulo, com tamanho diferente de 14 ou com todos os dígitos iguais
        }

        char dig13, dig14;
        int sm, i, r, num, peso;

        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i = 11; i >= 0; i--) {
                num = (int) (cnpj.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig13 = '0';
            else
                dig13 = (char) ((11 - r) + 48);

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 2;
            for (i = 12; i >= 0; i--) {
                num = (int) (cnpj.charAt(i) - 48);
                sm = sm + (num * peso);
                peso = peso + 1;
                if (peso == 10)
                    peso = 2;
            }

            r = sm % 11;
            if ((r == 0) || (r == 1))
                dig14 = '0';
            else
                dig14 = (char) ((11 - r) + 48);

            // Verifica se os dígitos calculados conferem com os dígitos informados.
            return (dig13 == cnpj.charAt(12)) && (dig14 == cnpj.charAt(13));
        } catch (InputMismatchException erro) {
            return false;
        }
    }

    /**
     * @brief Valida se um endereço de e-mail é válido.
     *
     *        Utiliza uma expressão regular para verificar o formato do e-mail.
     *
     * @param email O endereço de e-mail a ser validado.
     * @return True se o e-mail for válido, false caso contrário.
     */
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }
}