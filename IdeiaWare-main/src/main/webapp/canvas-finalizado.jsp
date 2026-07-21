<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true"%>
<%@include file="header/headerCookiesCV.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
	<head>
		<meta charset="UTF-8">
		<title>Canvas finalizado</title>
	</head>
	<body class="center-align blue-grey lighten-5">
    <main>
		<div style="min-height: 80vh">
			<%-- UX-SUCESSO-CONSISTENCIA: container adicionado, senao "col m8 offset-m2" fica quase full-width. --%>
			<div class="container">
				<div id="success">
					<div class="row">
						<div class="col s12 m8 offset-m2">
							<div class="card-panel blue darken-4" style="margin-top: 40px;">
								<div class="white-text center-align" style="font-size:55px;"><i class="fa fa-smile-o" aria-hidden="true"></i></div>
								<div class="white-text center-align" style="text-transform:uppercase; font-size:20px; padding-top:6px;">Canvas finalizado!</div>
								<div class="white-text center-align" style="padding-top:8px;">Seu modelo de neg&oacute;cios foi exportado e salvo com sucesso.</div>
								<%-- UX-SUCESSO-PROXIMA-ETAPA: Canvas e' o ultimo modulo, avisa que a jornada terminou. --%>
								<div class="white-text center-align" style="padding-top:4px;">Sua ideia concluiu todas as etapas do IdeiaWare.</div>
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
		</div>
	<script src="js/csrf.js"></script>
  </main>
    </body>
	<%@include file="header/canva-footer.jsp" %>
</html>
