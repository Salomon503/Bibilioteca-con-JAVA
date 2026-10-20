<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Préstamos | Biblioteca UDB</title>
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
<div class="page-head"><div><h1>Préstamos</h1><p class="muted">Vigentes, vencidos y devueltos.</p></div><a class="btn btn-udb" href="${ctx}/PrestamoController?accion=nuevo"><i class="bi bi-plus-lg"></i> Nuevo préstamo</a></div>
<c:if test="${param.msg == 'devuelto'}"><div class="alert alert-ok"><i class="bi bi-check-circle"></i> Devolución registrada. El ejemplar volvió al inventario.</div></c:if>
<c:if test="${param.msg == 'error'}"><div class="alert alert-udb"><i class="bi bi-exclamation-circle"></i> No se pudo registrar la devolución: el préstamo no existe o ya estaba devuelto.</div></c:if>
<div class="panel panel-flush">
<c:choose>
<c:when test="${empty prestamos}">
    <div class="vacio"><i class="bi bi-arrow-left-right"></i><h3 class="mt-2">Aún no hay registros</h3>
    <p class="muted">Empiece agregando el primero.</p>
    <a class="btn btn-gold" href="${ctx}/PrestamoController?accion=nuevo">Nuevo préstamo</a></div>
</c:when>
<c:otherwise>
    <div class="toolbar">
        <input class="form-control" type="search" placeholder="Buscar por estudiante, libro o estado" data-filtro="#tabla" aria-label="Buscar">
        <span class="muted">${fn:length(prestamos)} registros</span>
    </div>
    <div class="table-responsive"><table class="table tabla" id="tabla">
        <thead><tr><th>N.º</th><th>Estudiante</th><th>Libro</th><th>Prestado</th><th>Devolver antes de</th><th>Estado</th><th></th></tr></thead>
        <tbody>
        <c:forEach var="x" items="${prestamos}">
            <tr><td>${x.idPrestamo}</td><td><div class="cell-main"><c:out value="${x.estudiante.nombreEstudiante}"/></div><div class="cell-sub"><c:out value="${x.estudiante.carnet}"/></div></td><td><div class="cell-main"><c:out value="${x.libro.titulo}"/></div><div class="cell-sub"><c:out value="${x.libro.autor}"/></div></td><td>${x.fechaPrestamo}</td><td>${x.fechaDevolucion}</td><td><c:choose><c:when test="${x.estado == 'Activo'}"><span class="pill pill-ok"><i class="bi bi-clock"></i> Activo</span></c:when><c:when test="${x.estado == 'Vencido'}"><span class="pill pill-bad"><i class="bi bi-exclamation-triangle"></i> Vencido</span></c:when><c:otherwise><span class="pill pill-off"><i class="bi bi-check2"></i> <c:out value="${x.estado}"/></span></c:otherwise></c:choose></td><td><c:if test="${x.estado != 'Devuelto'}"><form method="post" action="${ctx}/PrestamoController" class="m-0"><input type="hidden" name="accion" value="devolver"><input type="hidden" name="idPrestamo" value="${x.idPrestamo}"><button class="btn btn-sm btn-outline-udb" type="submit">Registrar devolución</button></form></c:if></td></tr>
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
