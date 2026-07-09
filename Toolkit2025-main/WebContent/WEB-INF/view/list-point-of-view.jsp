<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<%-- UX-VOLTAR-V2: mesmo padrao do resto do app -- icone circular flutuante no
	     canto superior esquerdo. Tela funda (breadcrumb ja mostra Personas > Point
	     Of View), so tinha a logo do cabecalho (que pula direto pro /LIC/index.jsp). --%>
	<a href="${pageContext.request.contextPath}/persona/lista" class="btn-floating btn-large red darken-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar"><i class="material-icons">arrow_back</i></a>
	<nav class="crumb">
	    <div class="nav-wrapper">
	        <a href="${pageContext.request.contextPath}/persona/lista" class="breadcrumb">Personas</a>
	        <span class="breadcrumb active">Point Of View</span>
	    </div>
  	</nav>
  	
  	<div class="row">
  		<div class="col s12 m5">
			<h2 class="title-page">Point Of Views - Lista</h2>
		</div>
	</div>
	<div id="editor"></div>
	<div class="row">
		<div class="col s12 pov-list">
			<table class="striped">
		        <thead>
		          <tr>
	          		<th>Persona(s)</th>
	          		<th>Usuário(s)</th>
		          	<th>Necessidade(s)</th>
		           	<th>Introspecção(ões)</th>
		           	<th>Ações</th>
		          </tr>
		        </thead>
		        <tbody>
					<c:if test="${empty povs}"><tr><td colspan="5" class="center-align grey-text" style="padding: 30px;">Nenhum Point of View criado ainda. Crie uma persona e gere o primeiro POV.</td></tr></c:if>
					<c:forEach var="tempPOV" items="${povs}">						
						<%-- REVISAO 2026-07-08 (varredura Toolkit, achado MEDIA -- csrfToken em GET):
						     virou form POST -- mesmo motivo do list-personas.jsp, ver o comentario
						     la pro detalhe completo. --%>
						<form id="deletePOVForm${tempPOV.value.povID}" action="${pageContext.request.contextPath}/point-of-view/deletar" method="POST" style="display:none;">
							<input type="hidden" name="csrfToken" value="${csrfToken}"/>
							<input type="hidden" name="povId" value="${tempPOV.value.povID}"/>
						</form>

						<tr>
							<td>
								<a class="modal-trigger tooltipped pov-info" href="#!"
									data-position="top" 
									data-delay="50"
									data-tooltip="Clique para visualizar item"><c:out value="${tempPOV.value.names}"/></a>
							</td>
							<td>
								<div class="ellipsed-data">
									<a class="modal-trigger tooltipped pov-info" href="#!"
										data-position="top" 
										data-delay="50"
										data-tooltip="Clique para visualizar item"><c:out value="${tempPOV.value.user}"/></a>
								</div>
							</td>
							<td>
								<div class="ellipsed-data">
									<a class="modal-trigger tooltipped pov-info" href="#!"
										data-position="top" 
										data-delay="50"
										data-tooltip="Clique para visualizar item"><c:out value="${tempPOV.value.need}"/></a>
								</div>
							</td>
							<td>								
								<div class="ellipsed-data">
									<a class="modal-trigger tooltipped pov-info" href="#!"
										data-position="top" 
										data-delay="50"
										data-tooltip="Clique para visualizar item"><c:out value="${tempPOV.value.insight}"/></a>
								</div>
							</td>
							<td>
								<%-- UX: acoes viraram btn-floating (mesmo padrao do Canvas do LIC e
								     do list-personas.jsp) -- antes eram icones soltos, dificeis de
								     bater o olho. Separadores "ellipsis-v" removidos: botoes reais
								     ja tem espacamento proprio, nao precisam de divisor entre eles. --%>
								<a class="btn-floating btn-small red darken-1 tooltipped pov-overview" href="${pageContext.request.contextPath}/point-of-view/visao-geral?povId=${tempPOV.value.povID}"
									data-position="top"
									data-delay="50"
									data-tooltip="Visão Geral">
									<i class="fa fa-binoculars" aria-hidden="true"></i>
								</a>
								<a href="javascript:;" class="btn-floating btn-small red darken-1 tooltipped edit-pov"
									data-position="top"
									data-delay="50"
									data-tooltip="Editar"
									data-povid="${tempPOV.value.povID}" data-names="<c:out value='${tempPOV.value.names}'/>" data-user="<c:out value='${tempPOV.value.user}'/>" data-need="<c:out value='${tempPOV.value.need}'/>" data-insight="<c:out value='${tempPOV.value.insight}'/>" data-personasid="<c:out value='${tempPOV.value.personasID}'/>" onclick="Toolkit.PointOfView.buildPOVInfoEditOnModal(this.dataset.povid, this.dataset.names, this.dataset.user, this.dataset.need, this.dataset.insight, this.dataset.personasid)">
									<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
								</a>
								<a href="javascript:;" class="btn-floating btn-small red darken-1 tooltipped"
									data-position="top"
									data-delay="50"
									data-tooltip="Excluir"
									onclick="if (confirm('Você tem certeza que deseja deletar este Point of View?')) document.getElementById('deletePOVForm${tempPOV.value.povID}').submit();">
									<i class="fa fa-trash" aria-hidden="true"></i>
								</a>
							</td>
						</tr>					
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	
	<span class="allPersonas hide">
		<span class="names"><c:forEach var="persona" items="${personas}" varStatus="loop">
			${persona.name}<c:if test="${!loop.last}">,</c:if>
		</c:forEach>
		</span>
		<span class="ids"><c:forEach var="persona" items="${personas}" varStatus="loop">
			${persona.id}<c:if test="${!loop.last}">,</c:if>
		</c:forEach>
		</span>
	</span>	
	
  	<!-- Modal Information View -->
  	<div class="modal modal-pov-info">
	   	<div class="modal-content">
     		<h4>Informações Gerais</h4>
     		<label>Nome(s)</label>
     		<div class="names"></div>
     		<label>Usuário(s)</label>
     		<div class="user"></div>
     		<label>Necessidade(s)</label>
     		<div class="need"></div>
     		<label>Introspecção(ões)</label>
     		<div class="insight"></div>     		
	    </div>
	   	<div class="modal-footer">
	      	<a href="javascript:;" class="modal-action modal-close waves-effect waves-light btn grey lighten-3 black-text">Fechar</a>
	   	</div>
  	</div>
  	
  	<!-- Modal Information Edit-->
  	<div class="modal modal-pov-edit pov-modal">
  		<form:form action="${pageContext.request.contextPath}/point-of-view/atualizar" modelAttribute="pov" method="POST" class="col s12">
	   	<div class="modal-content">
	   		<div class="row">
	   			<form:hidden id="pov-id" path="id" />
	   			<form:hidden path="ideiaCodigo"/>
	   			<div class="input-field col s12 space">
	   				<label for="name-tags">Nome</label>
		   			<input id="name-tags" type="text"/>
		   			<form:hidden id="personas-id" path="personasId"/>
		   			<div class="names-error"></div>
	   			</div>
	   			<div class="input-field col s12">
		          <form:textarea id="user-text" class="materialize-textarea" path="userText" />
		          <label for="user-text">Usuário</label>
		          <div class="user-error"></div>
		        </div>
	     		<div class="input-field col s12">
		          <form:textarea id="need-text" class="materialize-textarea" path="needText" />
		          <label for="need-text">Necessidade</label>
		          <div class="need-error"></div>
		        </div>
	     		<div class="input-field col s12">
		          <form:textarea id="insight-text" class="materialize-textarea" path="insightText" />
		          <label for="insight-text">Introspecção</label>
		          <div class="insight-error"></div>
		        </div> 
     		</div>    		
	    </div>
	   	<div class="modal-footer">
	   		<input type="submit" value="Atualizar" class="waves-effect waves-light btn"/>
	      	<a href="javascript:;" class="modal-action modal-close waves-effect waves-light btn grey lighten-3 black-text btn-cancelar">Fechar</a>
	   	</div>
	   	<input type="hidden" name="csrfToken" value="${csrfToken}"/></form:form>
  	</div>

	<%-- UX: feedback (toast) na propria pagina apos salvar/exportar, sem tela intermediaria. --%>
	<c:if test="${not empty toastOk}">
		<script>$(document).ready(function(){ Materialize.toast('<c:out value="${toastOk}"/>', 4000, 'green'); });</script>
	</c:if>
	<c:if test="${not empty toastErro}">
		<script>$(document).ready(function(){ Materialize.toast('<c:out value="${toastErro}"/>', 5000, 'red'); });</script>
	</c:if>
<t:footer></t:footer>