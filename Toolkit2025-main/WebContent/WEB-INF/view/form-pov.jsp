<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<nav class="crumb">
	    <div class="nav-wrapper">
	        <a href="${pageContext.request.contextPath}/persona/lista" class="breadcrumb">Inicio</a>
	        <span class="breadcrumb active">Formulário Point Of View</span>
	    </div>
  	</nav>
  	
  	<div class="row">
  		<div class="col s12 m5">
			<h2 class="title-page">Point of View - Novo</h2>
		</div>
	</div>
	
	<div class="pov-form-wrapper">
		<div class="persona-names">
			<strong><em>Persona(s)</em>:</strong> 
			<c:forEach var="tempPersona" items="${personas}">
				<c:set var="personaParts" value="${fn:split(tempPersona, '+')}" />
				<span class="persona-data" data-id="<c:out value='${personaParts[1]}'/>"><c:out value="${personaParts[0]}"/></span>
			</c:forEach>
		</div>
		<div class="row">
			<form:form action="/toolkit/point-of-view/salvar-pov" modelAttribute="pov" method="POST" class="col s12">
				<form:hidden class="attribute-id" path="id"/>
				<form:hidden class="ids" path="personasId"/>
				<form:hidden path="ideiaCodigo" value="1"/>
	
				<table>
					<thead>
						<tr>
							<th>Usuário(s)</th>
							<th>Necessidade(s)</th>
							<th>Introspecção(ões)</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td class="user-tour">
								<div class="input-field col s12">
						          <form:textarea id="user-text" class="materialize-textarea" path="userText" />
						          <label for="user-text">Descrição</label>
						          <div class="user-error"></div>
						        </div>
							</td>
							<td class="need-tour">
								<div class="input-field col s12">
						          <form:textarea id="need-text" class="materialize-textarea" path="needText" />
						          <label for="necessity-text">Descrição</label>
						          <div class="need-error"></div>
						        </div>
							</td>
							<td class="insight-tour">
								<div class="input-field col s12">
						          <form:textarea id="insight-text" class="materialize-textarea" path="insightText" />
						          <label for="insight-text">Descrição</label>
						          <div class="insight-error"></div>
						        </div>
							</td>
						</tr>
					</tbody>
				</table>
				<div class="input-field col s3 offset-s9 right-align">
		        	<input type="submit" value="Adicionar" class="waves-effect waves-light btn add-pov"/>
		        </div>
			<input type="hidden" name="csrfToken" value="${csrfToken}"/></form:form>
		</div>
		<div class="row">
			<div class="col s12">
				<h6><strong>Exemplo para auxílio:</strong></h6>
				<img src="${pageContext.request.contextPath}/resources/imgs/pov-madlib.png" />
				<p>Uma pessoa adulta que vive na cidade ... 
				<strong>precisa de</strong> acesso a um carro compartilhado 1 a 4 vezes por 10 até 60 minutos por semana ... 
				<strong>porque</strong> ele prefere compartilhar um carro com mais pessoas, já que fica mais barato e diminui danos ao meio ambiente. No entanto, ainda deve ser fácil para mais pessoas compartilhar.</p>
			</div>
		</div>
	</div>
	<script>
		$(document).ready(function(){
			Toolkit.PointOfView.setPersonasIdInHiddenField();
		})
	</script>
<t:footer></t:footer>