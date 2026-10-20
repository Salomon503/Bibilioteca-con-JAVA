<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Estudiantes | Biblioteca UDB</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Instrument+Sans:wght@400;500;600;700&family=Newsreader:wght@500;600&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${ctx}/css/estilo.css" rel="stylesheet">
</head>
<body>
<div class="app">
    <jsp:include page="navbar.jsp"><jsp:param name="activo" value="estudiantes"/></jsp:include>
    <main class="content">
<div class="page-head"><div><h1>Estudiantes</h1><p class="muted">Estudiantes habilitados para pedir libros.</p></div><a class="btn btn-udb" href="${ctx}/EstudianteController?accion=nuevo"><i class="bi bi-plus-lg"></i> Registrar estudiante</a></div>
<div class="panel panel-flush">
<c:choose>
<c:when test="${empty estudiantes}">
    <div class="vacio"><i class="bi bi-people"></i><h3 class="mt-2">Aún no hay registros</h3>
    <p class="muted">Empiece agregando el primero.</p>
    <a class="btn btn-gold" href="${ctx}/EstudianteController?accion=nuevo">Registrar estudiante</a></div>
</c:when>
<c:otherwise>
    <div class="toolbar">
        <input class="form-control" type="search" placeholder="Buscar por nombre, carné o carrera" data-filtro="#tabla" aria-label="Buscar">
        <span class="muted">${fn:length(estudiantes)} registros</span>
    </div>
    <div class="table-responsive"><table class="table tabla" id="tabla">
        <thead><tr><th>Estudiante</th><th>Carné</th><th>Carrera</th><th>Teléfono</th></tr></thead>
        <tbody>
        <c:forEach var="x" items="${estudiantes}">
            <tr><td><div class="cell-main"><c:out value="${x.nombreEstudiante}"/></div></td><td><c:out value="${x.carnet}"/></td><td><c:out value="${x.carrera}"/></td><td><c:out value="${empty x.telefono ? '-' : x.telefono}"/></td></tr>
        </c:forEach>
        </tbody>
    </table></div>
</c:otherwise>
</c:choose>
</div>
    </main>
</div>
<script src="${ctx}/js/app.js" defer></script>
</body>
</html>
