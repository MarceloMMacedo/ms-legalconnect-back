package br.com.legalconnect.util;

public class Util {
    public static String sanitizeTenantId(String tenantId) {
        // Remove caracteres que não são letras, números ou underscore
        String cleaned = tenantId.replaceAll("[^a-zA-Z0-9]", "_");
        return cleaned;
    }
}
