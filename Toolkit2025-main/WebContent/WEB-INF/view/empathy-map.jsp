<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<%-- UX-VOLTAR-V2: icone circular flutuante, volta pra lista de personas. --%>
	<a href="${pageContext.request.contextPath}/persona/lista" class="btn-floating btn-large red darken-3 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar"><i class="material-icons">arrow_back</i></a>
	<nav class="crumb">
	    <div class="nav-wrapper">
	        <a href="${pageContext.request.contextPath}/persona/lista" class="breadcrumb">Personas</a>
	        <span class="breadcrumb active">Mapa de Empatia</span>
	    </div>
  	</nav>
  	<div class="row">
  		<div class="col m5">
  			<h2 class="title-page">Mapa de Empatia 
  				<a href="${pageContext.request.contextPath}/resources/imgs/mapa-de-empatia.png" target="_blank"
  					class="tooltipped tour" 
					data-position="right" 
					data-delay="50" 
					data-tooltip="Exemplo">
  					<i class="fa fa-info-circle" aria-hidden="true"></i>
  				</a>
  			</h2>
  		</div>
  		<%-- UX: link "Visão Detalhada" removido do menu (duplicava a Visão geral); rota/controller continuam ativos. --%>
  		<div class="col m7 right-align">
  			<a class="btn waves-effect waves-light red darken-3 empathy-overview" href="${pageContext.request.contextPath}/persona/empatia/visao-geral?personaId=${persona.id}">
				<i class="fa fa-binoculars left" aria-hidden="true"></i> Visão geral
			</a>
  		</div>
  	</div>
	
	<div class="persona-pre-data">
		<div class="row">
			<div class="col s12 m6">
				<label>Nome: </label>
				<span><c:out value="${persona.name}"/></span>
			</div>
			<div class="col s12 m6">
				<label>Idade: </label>
				<span>${persona.age}</span>
			</div>
		</div>
	</div>
	<div class="persona-select-item-view">
		<div class="col s12">
			<div class="row" style="position:relative;">
				<a href="o-que-pensa-e-sente?personaId=${persona.id}" >
					<div class="col s12 m6 grey lighten-5 first">
						<span>O que ela</span>
						<h3>SENTE E PENSA?</h3>
					</div>
				</a>
				<a href="o-que-ve?personaId=${persona.id}">
					<div class="col s12 m6 grey lighten-5 second">
						<span>O que ela</span> 
						<h3>VÊ?</h3>
					</div>
				</a>
				<i class="fa fa-user-circle-o centered-icon" aria-hidden="true"></i>
				<a href="o-que-diz-e-faz?personaId=${persona.id}" >
					<div class="col s12 m6 grey lighten-5 third">
						<span>O que ela</span>
						<h3>DIZ E FAZ?</h3>
					</div>
				</a>
				<a href="o-que-escuta?personaId=${persona.id}">
					<div class="col s12 m6 grey lighten-5 fourth">
						<span>O que ela</span>
						<h3>ESCUTA?</h3>
					</div>
				</a>
			</div>
			<div class="row">
				<a href="quais-sao-as-dores?personaId=${persona.id}">
					<div class="col s12 m6 red">
						<h3>DORES</h3>
					</div>
				</a>
				<a href="quais-sao-os-ganhos?personaId=${persona.id}">
					<div class="col s12 m6 green"> 
						<h3>GANHOS</h3>
					</div>
				</a>
			</div>
		</div>
	</div>
<t:footer></t:footer>