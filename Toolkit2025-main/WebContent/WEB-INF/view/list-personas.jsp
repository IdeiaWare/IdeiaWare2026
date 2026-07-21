<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<%-- UX-VOLTAR-V2: icone circular flutuante aponta pro LIC --%>
	<a href="${initParam.licBasePath}/minha-ideia.jsp" class="btn-floating btn-large red darken-3 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar"><i class="material-icons">arrow_back</i></a>
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
			<%-- TOOLKIT-PERSONA-LISTA-MODELAGEM/3: pill secundario, nao compete com botoes solidos --%>
			<a href="javascript:;" onclick="Toolkit.callTour()"
				class="tooltipped tour tour-page-link"
				data-position="bottom"
				data-delay="50"
				data-tooltip="Iniciar Tour">
				<i class="fa fa-map-marker" aria-hidden="true"></i> Tour
			</a>
		</div>
	</div>

	<%-- UX-FAB-VISIVEL: botoes de acao sempre visiveis, no lugar do FAB --%>
	<div class="row" style="margin-bottom: 8px;">
		<div class="col s12" style="display: flex; gap: 12px; flex-wrap: wrap;">
			<a href="javascript:;" class="btn waves-effect waves-light teal lighten-1 criar-persona" onclick="Toolkit.Persona.buildPersonaModal()">
				<i class="fa fa-user left" aria-hidden="true"></i> Nova Persona
			</a>
			<a href="javascript:;" class="btn waves-effect waves-light light-blue lighten-1 criar-pov disabled tooltipped"
				data-position="bottom" data-delay="50" data-tooltip="Crie uma persona primeiro">
				<i class="fa fa-eye left" aria-hidden="true"></i> Novo Point of View
			</a>
			<%-- TOOLKIT-PERSONA-LISTA-MODELAGEM/4: contador visivel do que esta marcado --%>
			<span id="pov-selection-count" class="grey-text" style="align-self:center; font-size:0.85rem;"></span>
		</div>
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
					<%-- TOOLKIT-PERSONA-LISTA-MODELAGEM/1: copy atualizada, sobrou do FAB antigo --%>
					<c:if test="${empty personas}"><tr><td colspan="4" class="center-align grey-text" style="padding: 30px;">Nenhuma persona criada ainda. Clique em "Nova Persona" para criar a primeira.</td></tr></c:if>
					<c:forEach var="tempPersona" items="${personas}">
						
						<c:url var="viewLink" value="/persona/empatia/mapa">
							<c:param name="personaId" value="${tempPersona.id}" />
						</c:url>
						
						<%-- TK-26: excluir persona virou form POST --%>
						<form id="deletePersonaForm${tempPersona.id}" action="${pageContext.request.contextPath}/persona/deletar" method="POST" style="display:none;">
							<input type="hidden" name="csrfToken" value="${csrfToken}"/>
							<input type="hidden" name="personaId" value="${tempPersona.id}"/>
						</form>

						<tr>
							<td>
								<%-- TK-41: aria-label no input, label fica vazio de proposito --%>
								<%-- TK-45b: ID antes do nome no value, nome pode ter "+" --%>
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
								<%-- TOOLKIT-PERSONA-LISTA-MODELAGEM/5: aria-label pra nao depender so do tooltip --%>
								<a href="${viewLink}" class="btn-floating btn-small blue-grey darken-1 tooltipped" aria-label="Abrir Mapa de Empatia"
									data-position="top"
									data-delay="50"
									data-tooltip="Abrir Mapa de Empatia">
									<%-- UX-TOOLKIT-EMPATIA-ICONE: fa-map (era fa-eye, igual ao "Novo Point of View" acima). --%>
									<i class="fa fa-map" aria-hidden="true"></i>
								</a>
								<a href="javascript:;" class="btn-floating btn-small blue-grey darken-1 tooltipped" aria-label="Editar"
									data-position="top"
									data-delay="50"
									data-tooltip="Editar"
									data-id="${tempPersona.id}" data-name="<c:out value='${tempPersona.name}'/>" data-age="${tempPersona.age}" onclick="Toolkit.Persona.buildPersonaEditModal(this.dataset.id, this.dataset.name, this.dataset.age)">
									<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
								</a>
								<a href="javascript:;" class="btn-floating btn-small blue-grey darken-1 tooltipped" aria-label="Excluir"
									data-position="top"
									data-delay="50"
									data-tooltip="Excluir"
									onclick="if (confirm('Excluir esta persona? Esta ação não pode ser desfeita.')) document.getElementById('deletePersonaForm${tempPersona.id}').submit();">
									<i class="fa fa-trash" aria-hidden="true"></i>
								</a>
							</td>
						</tr>					
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	
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

<t:footer></t:footer>