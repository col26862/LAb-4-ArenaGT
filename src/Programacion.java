import java.time.LocalDate;
public record Programacion(LocalDate fecha, int hora, String sede) {
    public Programacion {
        Validacion.exigir(fecha != null, "Fecha obligatoria.");
        Validacion.exigir(hora >= 0 && hora <= 23, "La hora debe estar entre 0 y 23.");
        sede = Validacion.texto(sede, "Sede");
    }
    @Override public String toString() { return String.format("%s %02d:00 | %s", fecha, hora, sede); }
}
