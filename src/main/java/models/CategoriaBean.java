package models;

/** Tabla categorias. */
public class CategoriaBean implements java.io.Serializable {
    private int idCategoria;
    private String nombreCategoria;

    /** Constructor sin argumentos (convencion JavaBean). */
    public CategoriaBean() { }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }
}
