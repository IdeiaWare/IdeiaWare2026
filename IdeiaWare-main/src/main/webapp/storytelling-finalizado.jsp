<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Tela de confirmacao da finalizacao do Storytelling (espelha canvas-finalizado.jsp); header proprio (nao carrega o JS pesado do Konva). --%>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true"%>
<%@page import="edu.unisc.lic.dao.UsuarioDAO"%>
<%@page import="edu.unisc.lic.domain.Usuario"%>
<%
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    Usuario usuario = null;
    if (session.getAttribute("codigoUsuario") == null) {
        response.sendRedirect("login.jsp");
        return;
    } else {
        usuario = usuarioDAO.buscar((Long) session.getAttribute("codigoUsuario"));
    }
    if (usuario != null) {
        request.setAttribute("nome", usuario.getNome());
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
	<head>
		<meta charset="UTF-8">
		<title>Storytelling finalizado</title>
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
		<link type="text/css" rel="stylesheet" href="css/materialize.min.css" media="screen,projection"/>
		<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
		<%-- FONT-TITLE-APP-01: fonte Condiment + .title-app (faltava, header proprio caia no fallback Arial). --%>
		<link href='https://fonts.googleapis.com/css?family=Condiment' rel='stylesheet'>
		<style>.title-app { color:#fff; font-family: 'Condiment'; font-size: 42px; line-height: 25px; }</style>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
	</head>
	<body class="center-align">
		<nav>
			<div class="nav-wrapper indigo lighten-1 z-depth-2">
				<a href="index.jsp" class="brand-logo" style="left: 50px">
					<ul id="nav-logo" style="width:300px" class="left hide-on-med-and-down">
						<div class="row" style="padding-left: 10px">
							<div class="col s1 indigo lighten-3" style="width:50px; height:50px; margin-top:5px; padding:6px; border-radius:100%; box-sizing:border-box;">
								<img style="display:block; width:62%; margin-left:7px; margin-top:-1px" src="imagens/idea.png" alt="IdeiaWare"/>
							</div>
							<h1 class="col s4 center-align title-app">IdeiaWare</h1>
						</div>
					</ul>
				</a>
				<ul id="nav-links" class="right hide-on-med-and-down">
					<li><a href="LogOutServlet">Sair<i style="padding-left: 20px" class="fa fa-sign-out" aria-hidden="true"></i></a></li>
				</ul>
			</div>
		</nav>
		<div style="min-height: 80vh">
			<div id="success">
				<div class="row">
					<div class="col s12 m8 offset-m2">
						<%-- UX-COR: indigo lighten-1 (cor exata do header/footer desta tela) --%>
						<div class="card-panel indigo lighten-1" style="margin-top: 40px;">
							<div class="white-text center-align" style="font-size:55px;"><i class="fa fa-smile-o" aria-hidden="true"></i></div>
							<div class="white-text center-align" style="text-transform:uppercase; font-size:20px; padding-top:6px;">Storytelling finalizado!</div>
							<div class="white-text center-align" style="padding-top:8px;">Seu Storytelling foi exportado e salvo com sucesso.</div>
							<div class="right-align" style="padding-top:16px;">
								<a class="white-text" href="minha-ideia.jsp" style="text-transform:uppercase;">
									<i class="fa fa-angle-double-left" aria-hidden="true"></i> Ver minhas ideias
								</a>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	<script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
	<script type="text/javascript" src="js/materialize.min.js"></script>
	<script src="js/csrf.js"></script>
	</body>
	<footer class="center indigo lighten-1 page-footer">
		<div class="container">
			<div class="row">
				<i class="small material-icons">account_circle</i><h6 class="white-text"> <c:out value="${nome}"/></h6>
			</div>
		</div>
		<div class="footer-copyright">
			<div class="container">
				© 2026 IdeiaWare UNISC
			</div>
		</div>
	</footer>
</html>
