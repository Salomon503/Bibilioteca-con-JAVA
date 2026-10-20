<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Inicio | Biblioteca UDB</title>
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
<section class="hero">
    <div class="hero-text">
        <h1>Cada libro, en su lugar y a tiempo.</h1>
        <p>Registre libros y estudiantes, preste ejemplares y reciba devoluciones sin hojas de cálculo.</p>
        <a class="btn btn-gold" href="${ctx}/PrestamoController?accion=nuevo"><i class="bi bi-plus-lg"></i> Nuevo préstamo</a>
    </div>
    <div class="spines" aria-hidden="true"><i></i><i></i><i></i><i></i><i></i><i></i><i></i><i></i><i></i></div>
</section>
<div class="row g-4 mt-1">
<div class="col-md-4"><div class="tile"><span class="tile-ico"><i class="bi bi-book"></i></span><h2>Libros</h2><p>Catálogo por categoría con las existencias de cada título.</p><div class="acciones"><a class="btn btn-udb" href="${ctx}/LibroController?accion=nuevo">Registrar libro</a><a class="btn btn-outline-udb" href="${ctx}/LibroController">Ver lista</a></div></div></div><div class="col-md-4"><div class="tile"><span class="tile-ico"><i class="bi bi-people"></i></span><h2>Estudiantes</h2><p>Estudiantes habilitados para solicitar libros en préstamo.</p><div class="acciones"><a class="btn btn-udb" href="${ctx}/EstudianteController?accion=nuevo">Registrar estudiante</a><a class="btn btn-outline-udb" href="${ctx}/EstudianteController">Ver lista</a></div></div></div><div class="col-md-4"><div class="tile"><span class="tile-ico"><i class="bi bi-arrow-left-right"></i></span><h2>Préstamos</h2><p>Préstamos vigentes, vencidos y devueltos en un solo vistazo.</p><div class="acciones"><a class="btn btn-udb" href="${ctx}/PrestamoController?accion=nuevo">Nuevo préstamo</a><a class="btn btn-outline-udb" href="${ctx}/PrestamoController">Ver lista</a></div></div></div>
</div>
    </main>
</div>
<script src="${ctx}/js/app.js" defer></script>
</body>
</html>
