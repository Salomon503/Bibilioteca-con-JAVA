<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Registrar estudiante | Biblioteca UDB</title>
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
<div class="page-head"><div><h1>Registrar estudiante</h1><p class="muted">Dé de alta a un estudiante para que pueda pedir libros.</p></div><a class="btn btn-outline-udb" href="${ctx}/EstudianteController">Ver estudiantes</a></div>
<c:if test="${not empty errores}">
    <div class="alert alert-udb"><strong><i class="bi bi-exclamation-circle"></i> Revise estos datos:</strong>
        <ul class="mb-0 mt-1"><c:forEach var="e" items="${errores}"><li><c:out value="${e}"/></li></c:forEach></ul>
    </div>
</c:if>
<form method="post" action="${ctx}/EstudianteController" class="panel" data-una-vez><div class="row g-3">
<div class="col-md-6"><label class="form-label" for="carnet">Carné</label><input class="form-control" id="carnet" name="carnet" type="text" data-carnet pattern="[A-Za-z]{2}[0-9]{4,8}" title="2 letras seguidas de 4 a 8 números (ej. AB210001)" value="${fn:escapeXml(param.carnet)}" maxlength="10" required><div class="form-text">2 letras seguidas de 4 a 8 números (ej. AB210001).</div></div><div class="col-md-6"><label class="form-label" for="nombre">Nombre completo</label><input class="form-control" id="nombre" name="nombre" type="text" data-solo-nombre minlength="3" pattern="[A-Za-zÁÉÍÓÚÜÑáéíóúüñ][A-Za-zÁÉÍÓÚÜÑáéíóúüñ .'’\-]{2,99}" title="Solo letras, espacios, punto, apóstrofe o guion (mínimo 3)" value="${fn:escapeXml(param.nombre)}" maxlength="100" required></div><div class="col-md-6"><label class="form-label" for="carrera">Carrera</label><input class="form-control" id="carrera" name="carrera" type="text" value="${fn:escapeXml(param.carrera)}" maxlength="80" required data-solo-letras minlength="3" pattern="[A-Za-zÁÉÍÓÚÜÑáéíóúüñ][A-Za-zÁÉÍÓÚÜÑáéíóúüñ. ]{2,79}" title="Solo letras y espacios (mínimo 3)"></div><div class="col-md-6"><label class="form-label" for="telefono">Teléfono</label><input class="form-control" id="telefono" name="telefono" type="text" value="${fn:escapeXml(param.telefono)}" maxlength="9" data-telefono inputmode="numeric" pattern="[267][0-9]{3}-[0-9]{4}" title="Formato 7777-7777; debe empezar con 2, 6 o 7" placeholder="7777-7777"><div class="form-text">Opcional. Formato 7777-7777 (empieza con 2, 6 o 7).</div></div>
</div>
<div class="form-actions"><button class="btn btn-udb" type="submit"><i class="bi bi-check2-circle"></i> Guardar estudiante</button><a class="btn btn-outline-udb" href="${ctx}/EstudianteController">Cancelar</a></div>
</form>
    </main>
</div>
<script src="${ctx}/js/app.js" defer></script>
</body>
</html>
