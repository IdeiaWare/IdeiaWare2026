<%@ tag pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="pt-BR">
	<head>
		<meta charset="UTF-8"/>
		<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
		
		<title>${pageTitle}</title>
		
		<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet" />
		<link href="https://fonts.googleapis.com/css?family=Condiment" rel="stylesheet">
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/materialize/css/materialize.min.css" />
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/font-awesome/css/font-awesome.min.css" />
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/materialize-tags/css/materialize-tags.min.css" />
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/enjoyhint.css" />
		<link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css" />
		
		<script src="${pageContext.request.contextPath}/resources/js/jquery-3.2.1.min.js"></script>
		<script>var contextPath = "${pageContext.request.contextPath}"</script>
	</head>
	<body style="display:flex; flex-direction:column; min-height:100vh;">
		<div id="overlay">
			<div class="loader"></div>
		</div>
		<nav>
		    <div class="nav-wrapper red darken-1">
	      		<a href="/LIC/index.jsp" class="brand-logo" style="left: 50px">
		          <ul id="nav-mobile" class="left hide-on-med-and-down">
	            	<div class="row">
		              	<div class="col s1 red lighten-3 logo">
		                	<img src="${pageContext.request.contextPath}/resources/imgs/logo-ideiaware.png" alt="IdeiaWare">
		              	</div>
		            	<h1 class=" col s4 center-align title-app">IdeiaWare</h1>
		        	</div>
		      	</ul>
		      </a>		
		        
		      <a href="#" data-activates="mobile-demo" class="button-collapse">
		      	<i class="fa fa-bars" aria-hidden="true"></i>
		      </a>
		      <ul id="nav-mobile" class="right hide-on-med-and-down">
		        <li><a href="${pageContext.request.contextPath}/persona/lista">Persona</a></li>
		        <li><a href="${pageContext.request.contextPath}/point-of-view/lista">Point Of View</a></li>
		        <li><a href="${pageContext.request.contextPath}/informacoes">Informações</a></li>
		        <li><a href="${pageContext.request.contextPath}/ideia/finalize?csrfToken=${csrfToken}" onclick="return confirm('Finalizar a Caixa de Ferramentas conclui esta etapa da ideia e não pode ser desfeito. Deseja continuar?')">Finalizar Caixa</a></li>
		        <li><a href="javascript:;" id="logout">Sair<i class="fa fa-sign-out" aria-hidden="true"></i></a></li>
		      </ul>
		    </div>
	  	</nav>

	  	<div id="content" role="main" style="flex:1;">
			<div class="container">
				<div class="row">
					<div class="col s12">