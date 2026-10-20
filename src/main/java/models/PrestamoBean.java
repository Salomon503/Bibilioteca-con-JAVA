package models;

/** Tabla prestamos; incluye EstudianteBean y LibroBean. */
public class PrestamoBean implements java.io.Serializable {
    private int idPrestamo;
    private EstudianteBean estudiante;
    private LibroBean libro;
    private java.sql.Date fechaPrestamo;
    private java.sql.Date fechaDevolucion;
    private String estado;

    /** Constructor sin argumentos (convencion JavaBean). */
    public PrestamoBean() { }

    public int getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(int idPrestamo) { this.idPrestamo = idPrestamo; }

    public EstudianteBean getEstudiante() { return estudiante; }
    public void setEstudiante(EstudianteBean estudiante) { this.estudiante = estudiante; }

    public LibroBean getLibro() { return libro; }
    public void setLibro(LibroBean libro) { this.libro = libro; }

    public java.sql.Date getFechaPrestamo() { return fechaPrestamo; }
    public void setFechaPrestamo(java.sql.Date fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }

    public java.sql.Date getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(java.sql.Date fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
