<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Tela de confirmacao da finalizacao do Storytelling (espelha canvas-finalizado.jsp
     e finalize.jsp do Toolkit): exportar o Storytelling ENCERRA o modulo (status -> CF),
     entao em vez do alert() + corte seco mostra um cartao de sucesso e deixa a navegacao
     a cargo do usuario. Padroniza os 3 modulos que encerram (Storytelling/Canvas/Caixa). --%>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true"%>
<%@include file="header/headerCookies.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
	<head>
		<meta charset="UTF-8">
		<title>Storytelling finalizado</title>
	</head>
	<body class="center-align">
		<div style="min-height: 80vh">
			<div id="success">
				<div class="row">
					<div class="col s12 m8 offset-m2">
						<div class="card-panel teal" style="margin-top: 40px;">
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
	<script src="js/csrf.js"></script>
  </body>
	<%@include file="header/footer.jsp" %>
</html>
