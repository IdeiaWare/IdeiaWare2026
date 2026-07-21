<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true"%>
<%@include file="header/headerCookies.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
	<head>
		<meta charset="UTF-8">
		<title>Colaboração finalizada</title>
	</head>
	<body class="center-align blue-grey lighten-5">
    <main>
		<div style="min-height: 80vh">
			<%-- UX-SUCESSO-CONSISTENCIA: mesmo padrao de canvas-finalizado.jsp/storytelling-finalizado.jsp, cor teal darken-2. --%>
			<div class="container">
				<div id="success">
					<div class="row">
						<div class="col s12 m8 offset-m2">
							<div class="card-panel teal darken-2" style="margin-top: 40px;">
								<div class="white-text center-align" style="font-size:55px;"><i class="fa fa-smile-o" aria-hidden="true"></i></div>
								<div class="white-text center-align" style="text-transform:uppercase; font-size:20px; padding-top:6px;">Colaboração finalizada!</div>
								<div class="white-text center-align" style="padding-top:8px;">Sua ideia avan&ccedil;ou para a etapa de Storytelling.</div>
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
</html>
<%@include file="header/footer.jsp" %>
