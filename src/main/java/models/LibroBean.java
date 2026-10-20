package models;

/** Tabla libros; incluye su CategoriaBean (relacion FK). */
public class LibroBean implements java.io.Serializable {
    private int idLibro;
    private String titulo;
    private String autor;
    private String isbn;
    private CategoriaBean categoria;
    private int cantidadDisponible;

    /** Constructor sin argumentos (convencion JavaBean). */
    public LibroBean() { }

    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public CategoriaBean getCategoria() { return categoria; }
    public void setCategoria(CategoriaBean categoria) { this.categoria = categoria; }

    public int getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(int cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }
}
