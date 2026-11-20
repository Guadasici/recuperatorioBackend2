package utnfc.backend.parcial;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EvolucionTest {

    @TempDir
    Path tempDir;

    private ResultadoParseo leerConCabecera(String contenido, String cabecera) throws IOException {
        Path csvFile = tempDir.resolve("prueba.csv");
        Files.writeString(csvFile, cabecera + "\n" + contenido);
        return new ParserArticulos().leer(csvFile);
    }

    private ResultadoParseo leer(String contenido) throws IOException {
        String cabeceraNueva = "id,descripcion,categoria,proveedor,precio,stock,estado,costoUnitario,descuentoPromocional";
        return leerConCabecera(contenido, cabeceraNueva);
    }

    @Test
    void formatoAnteriorSigueVigente() throws IOException {
        String cabeceraVieja = "id,descripcion,categoria,proveedor,precio,stock,estado,costoUnitario";
        String contenido = "A001,Leche entera,Lacteos,Proveedor Sur,100,10,ACTIVO,60\n";

        ResultadoParseo resultado = leerConCabecera(contenido, cabeceraVieja);

        assertEquals(1, resultado.getLeidas());
        assertEquals(1, resultado.getArticulos().size());
        assertEquals(0, resultado.getDescartadas());
        assertEquals(0, resultado.getInvalidas());

        Articulo art = resultado.getArticulos().get(0);
        // Ganancia comun: 10 * (100 - 60) = 400.0
        assertEquals(400.0, art.importe(), 0.01);
    }

    @Test
    void calculoImporteNormalYPromocional() {
        // Articulo comun (descuento 0): 10 * (200 - 100) = 1000.0
        Articulo normal = Articulo.desdeCampos(new String[] {
                "P1", "Galletas", "Panaderia", "Molino Sur", "200.0", "10", "ACTIVO", "100.0", "0"
        });
        assertEquals(1000.0, normal.importe(), 0.01);

        // Articulo en promocion (descuento 20%):
        // Precio con descuento: 200 * 0.80 = 160.0
        // Margen unitario: 160.0 - 100.0 = 60.0
        // Ganancia total: 10 * 60.0 = 600.0
        Articulo promo = Articulo.desdeCampos(new String[] {
                "P2", "Galletas Promo", "Panaderia", "Molino Sur", "200.0", "10", "ACTIVO", "100.0", "20"
        });
        assertEquals(600.0, promo.importe(), 0.01);
    }

    @Test
    void invariantesDeDescuentoYVentaSinPerdida() {
        // Descuentos permitidos: 0, 10, 20, 30
        assertDoesNotThrow(() -> Articulo.desdeCampos(new String[] {
                "P1", "Jugo", "Bebidas", "Distribuidora Norte", "200.0", "5", "ACTIVO", "100.0", "10"
        }));
        assertDoesNotThrow(() -> Articulo.desdeCampos(new String[] {
                "P2", "Jugo", "Bebidas", "Distribuidora Norte", "200.0", "5", "ACTIVO", "100.0", "30"
        }));

        // Descuento no admitido (ej: 15% o 50%) lanza IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> Articulo.desdeCampos(new String[] {
                "P3", "Jugo", "Bebidas", "Distribuidora Norte", "200.0", "5", "ACTIVO", "100.0", "15"
        }));
        assertThrows(IllegalArgumentException.class, () -> Articulo.desdeCampos(new String[] {
                "P4", "Jugo", "Bebidas", "Distribuidora Norte", "200.0", "5", "ACTIVO", "100.0", "50"
        }));

        // Invariante de negocio: el precio con descuento no puede ser menor al costoUnitario (no vender a perdida)
        // Precio: 100.0, Descuento: 30% -> Precio con desc: 70.0. CostoUnitario: 80.0 -> Invalido!
        assertThrows(IllegalArgumentException.class, () -> Articulo.desdeCampos(new String[] {
                "P5", "Harina", "Panaderia", "Molino Sur", "100.0", "10", "ACTIVO", "80.0", "30"
        }));
    }

    @Test
    void precedenciaDescarteYClasificacion() throws IOException {
        // Fila 1: CANCELADO -> se descarta directamente
        // Fila 2: Valido comun (descuento 0)
        // Fila 3: Valido promocional (descuento 20)
        // Fila 4: Invalido porque precio con desc (100 * 0.7 = 70) < costo 85
        // Fila 5: Invalido por descuento no permitido (50)
        String contenido =
                "P01,Manteca,Lacteos,Proveedor Sur,200.0,10,CANCELADO,100.0,20\n" +
                "P02,Fideos,Almacen,Norte,150.0,20,ACTIVO,100.0,0\n" +
                "P03,Queso Barra,Lacteos,Norte,300.0,10,ACTIVO,200.0,20\n" +
                "P04,Aceite,Almacen,Oeste,100.0,10,ACTIVO,85.0,30\n" +
                "P05,Gaseosa,Bebidas,Oeste,200.0,10,ACTIVO,120.0,50\n";

        ResultadoParseo resultado = leer(contenido);

        assertEquals(5, resultado.getLeidas());
        assertEquals(2, resultado.getArticulos().size()); // P02 y P03
        assertEquals(1, resultado.getDescartadas());       // P01
        assertEquals(2, resultado.getInvalidas());         // P04 y P05

        List<Articulo> articulos = resultado.getArticulos();
        assertEquals(1000.0, articulos.get(0).importe(), 0.01); // 20 * (150 - 100) = 1000.0
        assertEquals(400.0, articulos.get(1).importe(), 0.01);  // 10 * (300*0.80 - 200) = 10 * (240 - 200) = 400.0
    }

    @Test
    void anchoDebeCoincidirConCabecera() throws IOException {
        // Bajo cabecera nueva de 9 columnas, fila con 8 columnas es invalida
        String contenido = "P01,Galletitas,Panaderia,Norte,150.0,20,ACTIVO,100.0\n";
        ResultadoParseo resultado = leer(contenido);

        assertEquals(1, resultado.getLeidas());
        assertEquals(0, resultado.getArticulos().size());
        assertEquals(1, resultado.getInvalidas());
    }
}
