<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Registro guardado | Biblioteca UDB</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Instrument+Sans:wght@400;500;600;700&family=Newsreader:wght@500;600&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${ctx}/css/estilo.css" rel="stylesheet">
</head>
<body>
<div class="app">
    <jsp:include page="navbar.jsp"><jsp:param name="activo" value="inicio"/></jsp:include>
    <main class="content">
<div class="panel" style="max-width:720px">
<div class="ok-ico"><i class="bi bi-check-lg"></i></div>
<c:choose>
<c:when test="${tipo == 'libro'}"><h2>Libro registrado</h2><dl class="datos mt-3"><dt>Título</dt><dd><c:out value="${libro.titulo}"/></dd><dt>Autor</dt><dd><c:out value="${libro.autor}"/></dd><dt>ISBN</dt><dd><c:out value="${empty libro.isbn ? '-' : libro.isbn}"/></dd><dt>Categoría</dt><dd><c:out value="${libro.categoria.nombreCategoria}"/></dd><dt>Existencias</dt><dd>${libro.cantidadDisponible}</dd></dl><div class="form-actions"><a class="btn btn-udb" href="${ctx}/LibroController?accion=nuevo">Registrar otro libro</a><a class="btn btn-outline-udb" href="${ctx}/LibroController">Ver lista</a></div></c:when><c:when test="${tipo == 'estudiante'}"><h2>Estudiante registrado</h2><dl class="datos mt-3"><dt>Carné</dt><dd><c:out value="${estudiante.carnet}"/></dd><dt>Nombre</dt><dd><c:out value="${estudiante.nombreEstudiante}"/></dd><dt>Carrera</dt><dd><c:out value="${estudiante.carrera}"/></dd><dt>Teléfono</dt><dd><c:out value="${empty estudiante.telefono ? '-' : estudiante.telefono}"/></dd></dl><div class="form-actions"><a class="btn btn-udb" href="${ctx}/EstudianteController?accion=nuevo">Registrar otro estudiante</a><a class="btn btn-outline-udb" href="${ctx}/EstudianteController">Ver lista</a></div></c:when><c:when test="${tipo == 'prestamo'}"><h2>Préstamo registrado</h2><dl class="datos mt-3"><dt>N.º de préstamo</dt><dd>${prestamo.idPrestamo}</dd><dt>Estudiante</dt><dd><c:out value="${prestamo.estudiante.carnet} - ${prestamo.estudiante.nombreEstudiante}"/></dd><dt>Libro</dt><dd><c:out value="${prestamo.libro.titulo}"/></dd><dt>Prestado</dt><dd>${prestamo.fechaPrestamo}</dd><dt>Devolver antes de</dt><dd>${prestamo.fechaDevolucion}</dd><dt>Estado</dt><dd><span class="pill pill-ok">${prestamo.estado}</span></dd></dl><div class="form-actions"><a class="btn btn-udb" href="${ctx}/PrestamoController?accion=nuevo">Registrar otro préstamo</a><a class="btn btn-outline-udb" href="${ctx}/PrestamoController">Ver lista</a></div></c:when>
</c:choose>
</div>
    </main>
</div>
<script src="${ctx}/js/app.js" defer></script>
</body>
</html>
