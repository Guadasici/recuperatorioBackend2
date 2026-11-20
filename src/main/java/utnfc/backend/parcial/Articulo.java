package utnfc.backend.parcial;

public class Articulo {
    private final String id;
    private final String descripcion;
    private final String categoria;
    private final String proveedor;
    private final double precio;
    private final int stock;
    private final String estado;
    private final double costoUnitario;

    public Articulo(String id, String descripcion, String categoria, String proveedor,
            double precio, int stock, String estado, double costoUnitario) {
        if (id == null || id.isBlank() || descripcion == null || descripcion.isBlank()
                || categoria == null || categoria.isBlank() || proveedor == null || proveedor.isBlank()
                || estado == null || estado.isBlank() || precio <= 0 || stock < 0
                || costoUnitario < 0 || costoUnitario > precio) {
            throw new IllegalArgumentException("datos invalidos");
        }
        this.id = id.strip();
        this.descripcion = descripcion.strip();
        this.categoria = categoria.strip();
        this.proveedor = proveedor.strip();
        this.precio = precio;
        this.stock = stock;
        this.estado = estado.strip();
        this.costoUnitario = costoUnitario;
    }

    public static Articulo desdeCampos(String[] campos) {
        if (campos.length != 8) {
            throw new IllegalArgumentException("columnas incorrectas");
        }
        return new Articulo(campos[0], campos[1], campos[2], campos[3],
                Double.parseDouble(campos[4]), Integer.parseInt(campos[5]), campos[6],
                Double.parseDouble(campos[7]));
    }

    public double importe() {
        return stock * (precio - costoUnitario);
    }

    public String getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getProveedor() {
        return proveedor;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public String getEstado() {
        return estado;
    }

    public double getCostoUnitario() {
        return costoUnitario;
    }
}
