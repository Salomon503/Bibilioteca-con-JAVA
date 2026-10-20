<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Registrar libro | Biblioteca UDB</title>
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
<div class="page-head"><div><h1>Registrar libro</h1><p class="muted">Agregue un título al catálogo con su categoría y existencias.</p></div><a class="btn btn-outline-udb" href="${ctx}/LibroController">Ver catálogo</a></div>
<c:if test="${not empty errores}">
    <div class="alert alert-udb"><strong><i class="bi bi-exclamation-circle"></i> Revise estos datos:</strong>
        <ul class="mb-0 mt-1"><c:forEach var="e" items="${errores}"><li><c:out value="${e}"/></li></c:forEach></ul>
    </div>
</c:if>
<form method="post" action="${ctx}/LibroController" class="panel" data-una-vez><div class="row g-3">
<div class="col-12"><label class="form-label" for="titulo">Título</label><input class="form-control" id="titulo" name="titulo" type="text" value="${fn:escapeXml(param.titulo)}" maxlength="150" required></div><div class="col-md-6"><label class="form-label" for="autor">Autor</label><input class="form-control" id="autor" name="autor" type="text" data-solo-nombre value="${fn:escapeXml(param.autor)}" maxlength="100" required></div><div class="col-md-6"><label class="form-label" for="isbn">ISBN</label><input class="form-control" id="isbn" name="isbn" type="text" data-isbn value="${fn:escapeXml(param.isbn)}" maxlength="20"><div class="form-text">Opcional. 10 o 13 dígitos (se aceptan guiones). No puede repetirse.</div></div>
    <div class="col-md-6"><label class="form-label" for="idCategoria">Categoría</label>
        <select class="form-select" id="idCategoria" name="idCategoria" data-categoria required>
            <option value="">Seleccione una categoría</option>
            <c:forEach var="c" items="${categorias}">
                <option value="${c.idCategoria}" ${param.idCategoria == c.idCategoria ? 'selected' : ''}><c:out value="${c.nombreCategoria}"/></option>
            </c:forEach>
            <option value="nueva" ${param.idCategoria == 'nueva' ? 'selected' : ''}>+ Otra categoría (crear nueva)</option>
        </select></div>
    <div class="col-md-6 d-none" id="bloqueNuevaCategoria"><label class="form-label" for="nuevaCategoria">Nombre de la nueva categoría</label>
        <input class="form-control" id="nuevaCategoria" name="nuevaCategoria" type="text" maxlength="80" data-solo-nombre value="${fn:escapeXml(param.nuevaCategoria)}"></div>
<div class="col-md-6"><label class="form-label" for="cantidad">Cantidad disponible</label><input class="form-control" id="cantidad" name="cantidad" type="number" value="${empty param.cantidad ? 1 : fn:escapeXml(param.cantidad)}" min="0" max="${maxEjemplares}" step="1" required><div class="form-text">Entre 0 y ${maxEjemplares}.</div></div>
</div>
<div class="form-actions"><button class="btn btn-udb" type="submit"><i class="bi bi-check2-circle"></i> Guardar libro</button><a class="btn btn-outline-udb" href="${ctx}/LibroController">Cancelar</a></div>
</form>
    </main>
</div>
<script src="${ctx}/js/app.js" defer></script>
</body>
</html>
