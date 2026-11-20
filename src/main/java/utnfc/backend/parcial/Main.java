package utnfc.backend.parcial;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        Path ruta = Path.of(args.length == 0 ? "datos/articulos.csv" : args[0]);
        ResultadoParseo resultado = new ParserArticulos().leer(ruta);
        Inventario inventario = new Inventario(resultado.getArticulos());

        System.out.println("Leidas=" + resultado.getLeidas()
                + " procesadas=" + resultado.getArticulos().size()
                + " descartadas=" + resultado.getDescartadas()
                + " invalidas=" + resultado.getInvalidas());
        System.out.println("ValorTotal=" + inventario.valorTotal());
        System.out.println("Por categoria=" + inventario.porCategoria());

        if (!resultado.getErrores().isEmpty()) {
            System.out.println("Errores=" + resultado.getErrores());
        }
    }
}
