<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<nav class="crumb"></nav>
	
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
	      		data-position="left" data-delay="50" data-tooltip="Criar Point Of View">
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
		           	<th>Ação</th>
		          </tr>
		        </thead>
		        <tbody>
					<!-- loop over and print our personas -->
					<c:if test="${empty personas}"><tr><td colspan="4" class="center-align grey-text" style="padding: 30px;">Nenhuma persona criada ainda. Clique no botao + para criar a primeira.</td></tr></c:if>
					<c:forEach var="tempPersona" items="${personas}">
						
						<c:url var="viewLink" value="/persona/empatia/mapa">
							<c:param name="personaId" value="${tempPersona.id}" />
						</c:url>
						
						<c:url var="deleteLink" value="/persona/deletar">
					<c:param name="csrfToken" value="${csrfToken}"/>
							<c:param name="personaId" value="${tempPersona.id}" />
						</c:url>
						
						<tr>
							<td>
								<input type="checkbox" value="<c:out value='${tempPersona.name}'/>+${tempPersona.id}" class="filled-in chkPersona" id="filled-in-box${tempPersona.id}"/>
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
								<a href="javascript:;" class="tooltipped" 
									data-position="top" 
									data-delay="50" 
									data-tooltip="Editar"
									data-id="${tempPersona.id}" data-name="<c:out value='${tempPersona.name}'/>" data-age="${tempPersona.age}" onclick="Toolkit.Persona.buildPersonaEditModal(this.dataset.id, this.dataset.name, this.dataset.age)">
									<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
								</a>
								<i class="fa fa-ellipsis-v" aria-hidden="true"></i>
								<a href="${deleteLink}" class="tooltipped" 
									data-position="top" 
									data-delay="50" 
									data-tooltip="Excluir"
									onclick="if (!(confirm('Vocé tem certeza que deseja deletar esta persona?'))) return false">
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
<t:footer></t:footer>