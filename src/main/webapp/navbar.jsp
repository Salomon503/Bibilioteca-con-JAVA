<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<aside class="side">
  <a class="brand" href="${pageContext.request.contextPath}/index.jsp">
    <span class="brand-mark">UDB</span>
    <span class="brand-name">Biblioteca<br>Central</span>
  </a>
  <nav class="d-flex flex-column gap-1">
    <a class="nav-item ${param.activo == 'inicio' ? 'active' : ''}" href="${pageContext.request.contextPath}/index.jsp"><i class="bi bi-house-door"></i>Inicio</a>
    <a class="nav-item ${param.activo == 'libros' ? 'active' : ''}" href="${pageContext.request.contextPath}/LibroController"><i class="bi bi-book"></i>Libros</a>
    <a class="nav-item ${param.activo == 'estudiantes' ? 'active' : ''}" href="${pageContext.request.contextPath}/EstudianteController"><i class="bi bi-people"></i>Estudiantes</a>
    <a class="nav-item ${param.activo == 'prestamos' ? 'active' : ''}" href="${pageContext.request.contextPath}/PrestamoController"><i class="bi bi-arrow-left-right"></i>Préstamos</a>
  </nav>
  <p class="side-foot">Desafío práctico 03</p>
</aside>
