<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Error | Biblioteca UDB</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
    <link href="${ctx}/css/estilo.css" rel="stylesheet">
</head>
<body>
<div class="app">
    <jsp:include page="navbar.jsp"><jsp:param name="activo" value="inicio"/></jsp:include>
    <main class="content">
        <div class="panel" style="max-width:720px">
            <h2><i class="bi bi-exclamation-triangle"></i> No pudimos completar la operación</h2>
            <p class="muted mt-2">Ocurrió un problema al acceder a la base de datos. Verifique que MySQL esté encendido
               (y que el puerto y la base <strong>bibliotecaudb</strong> coincidan con <code>Conexion.java</code>) y vuelva a intentarlo.</p>
            <div class="form-actions"><a class="btn btn-udb" href="${ctx}/index.jsp">Volver al inicio</a></div>
        </div>
    </main>
</div>
</body>
</html>
