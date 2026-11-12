public final class Empresa {
    private final String nombre;
    public Empresa(String nombre) { this.nombre = Validacion.texto(nombre, "Empresa"); }
    public String getNombre() { return nombre; }
    public String getClave() { return Validacion.clave(nombre); }
    @Override public boolean equals(Object otro) { return otro != null && otro.getClass() == Empresa.class && getClave().equals(((Empresa) otro).getClave()); }
    @Override public int hashCode() { return getClave().hashCode(); }
    @Override public String toString() { return nombre; }
}
