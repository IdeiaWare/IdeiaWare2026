<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<%-- UX-VOLTAR-V2: icone circular flutuante, aponta pro LIC (minha-ideia.jsp, cross-webapp). --%>
	<a href="${initParam.licBasePath}/minha-ideia.jsp" class="btn-floating btn-large red darken-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar"><i class="material-icons">arrow_back</i></a>
	<nav class="crumb">
	    <div class="nav-wrapper">
	        <span class="breadcrumb active">Personas</span>
	    </div>
  	</nav>
	
	<div class="row">
  		<div class="col s12 m5">
			<h2 class="title-page persona-home">Personas - Lista</h2>
		</div>
		<div class="col s12 m7 right-align">
			<a href="javascript:;" onclick="Toolkit.callTour()"
				class="tooltipped tour" 
				data-position="bottom" 
				data-delay="50" 
				data-tooltip="Iniciar Tour">
				<i class="fa fa-map-marker" aria-hidden="true"></i> Tour
			</a>
		</div>
	</div>
	
	<div class="fixed-action-btn">
    	<a class="btn-floating btn-large teal lighten-1">
	      	<i class="fa fa-plus" aria-hidden="true"></i>
	    </a>
	    <ul>
	    	<li><a class="btn-floating tooltipped light-blue lighten-1 criar-pov disabled"
	      		data-position="left" data-delay="50" data-tooltip="Criar Point of View">
	      		<i class="fa fa-eye" aria-hidden="true"></i></a>
	      	</li>
	    	<li><a class="btn-floating tooltipped light-blue lighten-1 criar-persona"
	    		data-position="left" data-delay="50" data-tooltip="Criar Persona"
	    		onclick="Toolkit.Persona.buildPersonaModal()">
	    		<i class="fa fa-user" aria-hidden="true"></i></a>
	    	</li>
    	</ul>
  	</div>
	
	<div class="row">
		<div class="col s12 personas-list">
			<table class="striped">
		        <thead>
		          <tr>
		          	<th>#</th>
	          		<th>Nome</th>
		          	<th>Idade</th>
		           	<th>Ações</th>
		          </tr>
		        </thead>
		        <tbody>
					<c:if test="${empty personas}"><tr><td colspan="4" class="center-align grey-text" style="padding: 30px;">Nenhuma persona criada ainda. Clique no botão + para criar a primeira.</td></tr></c:if>
					<c:forEach var="tempPersona" items="${personas}">
						
						<c:url var="viewLink" value="/persona/empatia/mapa">
							<c:param name="personaId" value="${tempPersona.id}" />
						</c:url>
						
						<%-- TK-26: excluir persona virou form POST (era link GET com token na query). --%>
						<form id="deletePersonaForm${tempPersona.id}" action="${pageContext.request.contextPath}/persona/deletar" method="POST" style="display:none;">
							<input type="hidden" name="csrfToken" value="${csrfToken}"/>
							<input type="hidden" name="personaId" value="${tempPersona.id}"/>
						</form>

						<tr>
							<td>
								<%-- TK-41: aria-label no input (label fica vazio de proposito, Materialize desenha o checkbox via CSS). --%>
								<%-- TK-45b: ID antes do nome no value (era Nome+Id) -- nome pode ter "+", ID nunca tem. --%>
								<input type="checkbox" value="${tempPersona.id}+<c:out value='${tempPersona.name}'/>" class="filled-in chkPersona" id="filled-in-box${tempPersona.id}" aria-label="Selecionar <c:out value='${tempPersona.name}'/>"/>
								<label for="filled-in-box${tempPersona.id}"></label>
							</td>
							<td>
								<a href="${viewLink}" class="tooltipped define-persona" 
									data-position="top" 
									data-delay="50" 
									data-tooltip="Clique para definir persona">
									<c:out value="${tempPersona.name}"/>
								</a>
							</td>
							<td>${tempPersona.age}</td>
							<td>
								<%-- UX: acoes viraram btn-floating (mesmo padrao do Canvas do LIC); icone de Mapa de Empatia adicionado. --%>
								<a href="${viewLink}" class="btn-floating btn-small red darken-1 tooltipped"
									data-position="top"
									data-delay="50"
									data-tooltip="Abrir Mapa de Empatia">
									<i class="fa fa-eye" aria-hidden="true"></i>
								</a>
								<a href="javascript:;" class="btn-floating btn-small red darken-1 tooltipped"
									data-position="top"
									data-delay="50"
									data-tooltip="Editar"
									data-id="${tempPersona.id}" data-name="<c:out value='${tempPersona.name}'/>" data-age="${tempPersona.age}" onclick="Toolkit.Persona.buildPersonaEditModal(this.dataset.id, this.dataset.name, this.dataset.age)">
									<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
								</a>
								<a href="javascript:;" class="btn-floating btn-small red darken-1 tooltipped"
									data-position="top"
									data-delay="50"
									data-tooltip="Excluir"
									onclick="if (confirm('Você tem certeza que deseja deletar esta persona?')) document.getElementById('deletePersonaForm${tempPersona.id}').submit();">
									<i class="fa fa-trash" aria-hidden="true"></i>
								</a>
							</td>
						</tr>					
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	
	<!-- Modal - Form Persona -->
  	<div class="modal persona-modal">
  		<form:form action="salvar-persona" modelAttribute="persona" method="POST" class="col s12">
	   	<div class="modal-content">
	   		<div class="row">
				<form:hidden id="persona-id" path="id"/>
					
				<div class="input-field col s12 name-input">
		          <form:input id="name" type="text" path="name" />
		          <label for="name">Nome *</label>
		          <div class="name-error"></div>
		        </div>
				<div class="input-field col s12 age-input">
		          <form:input id="age" type="number" path="age" />
		          <label for="age">Idade *</label>
		          <div class="age-error"></div>
		        </div>
	        </div>    		
	    </div>
	   	<div class="modal-footer">
	   		<input type="submit" value="Adicionar" class="waves-effect waves-light btn adicionar"/>
	      	<a href="#!" class="modal-action modal-close waves-effect waves-light btn grey lighten-3 black-text btn-cancelar">Fechar</a>
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