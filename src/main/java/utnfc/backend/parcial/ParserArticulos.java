package utnfc.backend.parcial;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParserArticulos {
    public ResultadoParseo leer(Path ruta) throws IOException {
        List<Articulo> articulos = new ArrayList<>();
        List<String> errores = new ArrayList<>();
        int leidas = 0;
        int descartadas = 0;
        try (BufferedReader lector = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            if (!"id,descripcion,categoria,proveedor,precio,stock,estado,costoUnitario"
                    .equals(lector.readLine())) {
                throw new IllegalArgumentException("encabezado incorrecto");
            }
            String linea;
            while ((linea = lector.readLine()) != null) {
                leidas++;
                try {
                    String[] campos = Arrays.stream(linea.split(",", -1))
                            .map(String::strip)
                            .collect(Collectors.toList())
                            .toArray(new String[0]);
                    if (campos.length != 8) {
                        throw new IllegalArgumentException("columnas incorrectas");
                    }
                    if (campos[6].equals("CANCELADO")) {
                        descartadas++;
                        continue;
                    }
                    if (!campos[6].equals("ACTIVO") && !campos[6].equals("SUSPENDIDO")) {
                        throw new IllegalArgumentException("estado desconocido");
                    }
                    articulos.add(Articulo.desdeCampos(campos));
                } catch (IllegalArgumentException error) {
                    errores.add("Linea " + (leidas + 1) + ": " + error.getMessage());
                }
            }
        }
        return new ResultadoParseo(articulos, leidas, descartadas, errores);
    }
}
