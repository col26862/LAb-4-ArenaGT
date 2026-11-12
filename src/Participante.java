public final class Participante {
    private final String codigo;
    private final String nombre;
    private final Empresa empresa;
    public Participante(String codigo, String nombre, Empresa empresa) {
        this.codigo = Validacion.codigo(codigo);
        this.nombre = Validacion.texto(nombre, "Participante");
        Validacion.exigir(empresa != null, "Empresa obligatoria.");
        this.empresa = empresa;
    }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public Empresa getEmpresa() { return empresa; }
    @Override public String toString() { return codigo + " - " + nombre + " (" + empresa + ")"; }
}
