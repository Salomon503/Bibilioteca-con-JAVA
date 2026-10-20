<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Nuevo préstamo | Biblioteca UDB</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Instrument+Sans:wght@400;500;600;700&family=Newsreader:wght@500;600&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${ctx}/css/estilo.css" rel="stylesheet">
</head>
<body>
<div class="app">
    <jsp:include page="navbar.jsp"><jsp:param name="activo" value="prestamos"/></jsp:include>
    <main class="content">
<div class="page-head"><div><h1>Registrar préstamo</h1><p class="muted">Elija al estudiante y el libro; el inventario se actualiza al guardar.</p></div><a class="btn btn-outline-udb" href="${ctx}/PrestamoController">Ver préstamos</a></div>
<c:if test="${not empty errores}">
    <div class="alert alert-udb"><strong><i class="bi bi-exclamation-circle"></i> Revise estos datos:</strong>
        <ul class="mb-0 mt-1"><c:forEach var="e" items="${errores}"><li><c:out value="${e}"/></li></c:forEach></ul>
    </div>
</c:if>
<form method="post" action="${ctx}/PrestamoController" class="panel" data-una-vez><div class="row g-3">
    <div class="col-md-6"><label class="form-label" for="idEstudiante">Estudiante</label>
        <select class="form-select" id="idEstudiante" name="idEstudiante" required>
            <option value="">Seleccione un estudiante</option>
            <c:forEach var="e" items="${estudiantes}">
                <option value="${e.idEstudiante}" ${param.idEstudiante == e.idEstudiante ? 'selected' : ''}><c:out value="${e.carnet} - ${e.nombreEstudiante}"/></option>
            </c:forEach>
        </select></div>
    <div class="col-md-6"><label class="form-label" for="idLibro">Libro</label>
        <select class="form-select" id="idLibro" name="idLibro" required>
            <option value="">Seleccione un libro</option>
            <c:forEach var="l" items="${libros}">
                <option value="${l.idLibro}" ${param.idLibro == l.idLibro ? 'selected' : ''}><c:out value="${l.titulo} - ${l.autor}"/> (${l.cantidadDisponible} disp.)</option>
            </c:forEach>
        </select><div class="form-text">Solo se muestran libros con existencias.</div></div>
<div class="col-md-6"><label class="form-label" for="fechaPrestamo">Fecha de préstamo</label><input class="form-control" id="fechaPrestamo" name="fechaPrestamo" type="date" min="${minPrestamo}" max="${hoy}" data-fecha-prestamo value="${empty param.fechaPrestamo ? hoy : fn:escapeXml(param.fechaPrestamo)}" required></div><div class="col-md-6"><label class="form-label" for="fechaDevolucion">Fecha de devolución</label><input class="form-control" id="fechaDevolucion" name="fechaDevolucion" type="date" min="${minDevolucion}" max="${maxDevolucion}" data-dias-min="${diasMin}" data-dias-max="${diasMax}" value="${fn:escapeXml(param.fechaDevolucion)}" required><div class="form-text">Plazo máximo: ${diasMax} días.</div></div>
</div>
<div class="form-actions"><button class="btn btn-udb" type="submit"><i class="bi bi-check2-circle"></i> Registrar préstamo</button><a class="btn btn-outline-udb" href="${ctx}/PrestamoController">Cancelar</a></div>
</form>
    </main>
</div>
<script src="${ctx}/js/app.js" defer></script>
</body>
</html>
