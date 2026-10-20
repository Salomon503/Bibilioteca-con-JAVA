<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Libros | Biblioteca UDB</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Instrument+Sans:wght@400;500;600;700&family=Newsreader:wght@500;600&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${ctx}/css/estilo.css" rel="stylesheet">
</head>
<body>
<div class="app">
    <jsp:include page="navbar.jsp"><jsp:param name="activo" value="libros"/></jsp:include>
    <main class="content">
<div class="page-head"><div><h1>Libros</h1><p class="muted">Catálogo con las existencias de cada título.</p></div><a class="btn btn-udb" href="${ctx}/LibroController?accion=nuevo"><i class="bi bi-plus-lg"></i> Registrar libro</a></div>
<c:if test="${param.msg == 'cantidad'}"><div class="alert alert-ok"><i class="bi bi-check-circle"></i> Existencias actualizadas.</div></c:if>
<c:if test="${param.msg == 'errorCantidad'}"><div class="alert alert-udb"><i class="bi bi-exclamation-circle"></i> No se pudo actualizar: la cantidad debe ser un número entero entre 0 y ${maxEjemplares}.</div></c:if>
<div class="panel panel-flush">
<c:choose>
<c:when test="${empty libros}">
    <div class="vacio"><i class="bi bi-book"></i><h3 class="mt-2">Aún no hay registros</h3>
    <p class="muted">Empiece agregando el primero.</p>
    <a class="btn btn-gold" href="${ctx}/LibroController?accion=nuevo">Registrar libro</a></div>
</c:when>
<c:otherwise>
    <div class="toolbar">
        <input class="form-control" type="search" placeholder="Buscar por título, autor o categoría" data-filtro="#tabla" aria-label="Buscar">
        <span class="muted">${fn:length(libros)} registros</span>
    </div>
    <div class="table-responsive"><table class="table tabla" id="tabla">
        <thead><tr><th>Título</th><th>ISBN</th><th>Categoría</th><th>Existencias</th></tr></thead>
        <tbody>
        <c:forEach var="x" items="${libros}">
            <tr><td><div class="cell-main"><c:out value="${x.titulo}"/></div><div class="cell-sub"><c:out value="${x.autor}"/></div></td><td><c:out value="${empty x.isbn ? '-' : x.isbn}"/></td><td><span class="pill pill-cat"><c:out value="${x.categoria.nombreCategoria}"/></span></td><td><form method="post" action="${ctx}/LibroController" class="d-flex align-items-center gap-2 m-0">
                <input type="hidden" name="accion" value="actualizarCantidad">
                <input type="hidden" name="idLibro" value="${x.idLibro}">
                <input class="form-control form-control-sm" style="width:84px" type="number" min="0" max="${maxEjemplares}" step="1" name="cantidad" value="${x.cantidadDisponible}" aria-label="Existencias de ${fn:escapeXml(x.titulo)}">
                <button class="btn btn-sm btn-outline-udb" type="submit">Actualizar</button>
                <c:if test="${x.cantidadDisponible == 0}"><span class="pill pill-bad">Agotado</span></c:if>
            </form></td></tr>
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
