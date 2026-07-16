<%@ tag pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
	<head>
		<meta charset="UTF-8"/>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
		
		<title>${pageTitle}</title>
		
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet" />
		<link href="https://fonts.googleapis.com/css?family=Condiment" rel="stylesheet">
		<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@600' rel='stylesheet'>
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/materialize/css/materialize.min.css" />
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/font-awesome/css/font-awesome.min.css" />
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/materialize-tags/css/materialize-tags.min.css" />
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/enjoyhint.css" />
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css" />
		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-3.2.1.min.js"></script>
		<%-- DRY-LIC: licBasePath exposto pro custom.js (arquivo estatico, nao le EL). --%>
		<script>var contextPath = "${pageContext.request.contextPath}"; var licBasePath = "${initParam.licBasePath}";</script>
	</head>
	<%-- FUNDO-CINZA-TOOLKIT: fundo cinza, mesmo padrao do LIC. --%>
	<body class="blue-grey lighten-5" style="display:flex; flex-direction:column; min-height:100vh;">
		<div id="overlay">
			<div class="loader"></div>
		</div>
		<%-- TK-39/NAV-LOGO-01: id="nav-mobile" duplicado no logo (renomeado "nav-logo") + hamburguer apontava pra id inexistente. --%>
		<nav>
		    <div class="nav-wrapper red darken-3">
	      		<a href="${initParam.licBasePath}/index.jsp" class="brand-logo" style="left: 50px">
		          <ul id="nav-logo" class="left hide-on-med-and-down">
	            	<div class="row">
		              	<div class="col s1 red lighten-3 logo">
		                	<img src="${pageContext.request.contextPath}/resources/imgs/logo-ideiaware.png" alt="IdeiaWare">
		              	</div>
		            	<h1 class=" col s4 center-align title-app">IdeiaWare</h1>
		        	</div>
		      	</ul>
		      </a>

		      <a href="#" data-activates="nav-mobile-drawer" class="button-collapse">
		      	<i class="fa fa-bars" aria-hidden="true"></i>
		      </a>
		      <%-- TK-26: virou form POST (2 copias -- nav desktop + drawer mobile -- com id distinto). --%>
		      <form id="finalizeIdeiaFormDesktop" action="${pageContext.request.contextPath}/ideia/finalize" method="POST" style="display:none;">
		      	<input type="hidden" name="csrfToken" value="${csrfToken}"/>
		      </form>
		      <ul id="nav-mobile" class="right hide-on-med-and-down">
		        <li><a href="${pageContext.request.contextPath}/persona/lista">Persona</a></li>
		        <li><a href="${pageContext.request.contextPath}/point-of-view/lista">Point of View</a></li>
		        <li><a href="${pageContext.request.contextPath}/informacoes">Informações</a></li>
		        <li><a href="javascript:;" onclick="if (confirm('Finalizar a Caixa de Ferramentas conclui esta etapa da ideia e não pode ser desfeito. Deseja continuar?')) document.getElementById('finalizeIdeiaFormDesktop').submit();">Finalizar Caixa</a></li>
		        <li><a href="javascript:;" class="logout-link">Sair<i class="fa fa-sign-out" aria-hidden="true"></i></a></li>
		      </ul>
		    </div>
	  	</nav>
	  	<form id="finalizeIdeiaFormMobile" action="${pageContext.request.contextPath}/ideia/finalize" method="POST" style="display:none;">
	      	<input type="hidden" name="csrfToken" value="${csrfToken}"/>
	    </form>
	  	<ul class="side-nav" id="nav-mobile-drawer">
	        <li><a href="${pageContext.request.contextPath}/persona/lista">Persona</a></li>
	        <li><a href="${pageContext.request.contextPath}/point-of-view/lista">Point of View</a></li>
	        <li><a href="${pageContext.request.contextPath}/informacoes">Informações</a></li>
	        <li><a href="javascript:;" onclick="if (confirm('Finalizar a Caixa de Ferramentas conclui esta etapa da ideia e não pode ser desfeito. Deseja continuar?')) document.getElementById('finalizeIdeiaFormMobile').submit();">Finalizar Caixa</a></li>
	        <li><a href="javascript:;" class="logout-link">Sair<i class="fa fa-sign-out" aria-hidden="true"></i></a></li>
	    </ul>

	  	<main id="content" style="flex:1;">
			<div class="container">
				<div class="row">
					<div class="col s12">