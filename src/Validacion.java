public final class Validacion {
    private Validacion() { }
    public static void exigir(boolean condicion, String mensaje) {
        if (!condicion) throw new IllegalArgumentException(mensaje);
    }
    public static String texto(String valor, String campo) {
        exigir(valor != null && !valor.trim().isEmpty(), campo + " no puede estar vacío.");
        return valor.trim();
    }
    public static String codigo(String valor) { return texto(valor, "Código").toUpperCase(java.util.Locale.ROOT); }
    public static String clave(String valor) { return texto(valor, "Nombre").toLowerCase(java.util.Locale.ROOT); }
}
