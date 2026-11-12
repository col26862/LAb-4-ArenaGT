import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
public final class EntradaConsola {
    private final Scanner scanner;
    public EntradaConsola(Scanner scanner) { this.scanner = scanner; }
    public String texto(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) throw new FinEntrada();
        return scanner.nextLine().trim();
    }
    public int entero(String mensaje) {
        while (true) try { return Integer.parseInt(texto(mensaje)); }
        catch (NumberFormatException ex) { System.out.println("Ingrese un entero dentro del rango de int."); }
    }
    public BigDecimal decimalDesde(String valor) {
        try { return new BigDecimal(valor.replace(',', '.')); }
        catch (NumberFormatException ex) { throw new IllegalArgumentException("Número decimal inválido."); }
    }
    public LocalDate fecha(String mensaje) {
        while (true) try { return LocalDate.parse(texto(mensaje)); }
        catch (DateTimeParseException ex) { System.out.println("Use una fecha válida con formato AAAA-MM-DD."); }
    }
    public static final class FinEntrada extends RuntimeException { private static final long serialVersionUID = 1L; }
}
