<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<%-- UX-VOLTAR-V2: icone circular flutuante, volta pro Mapa de Empatia (pai imediato). --%>
	<a href="${pageContext.request.contextPath}/persona/empatia/mapa?personaId=${persona.id}" class="btn-floating btn-large red darken-3 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar"><i class="material-icons">arrow_back</i></a>
	<nav class="crumb">
	    <div class="nav-wrapper">
	        <a href="${pageContext.request.contextPath}/persona/lista" class="breadcrumb">Personas</a>
	        <a href="${pageContext.request.contextPath}/persona/empatia/mapa?personaId=${persona.id}" class="breadcrumb">Mapa de Empatia</a>
	        <span class="breadcrumb active">Visão Detalhada</span>
	    </div>
  	</nav>
  	
  	<div class="row">
  		<div class="col s12 m4">
			<h2 class="title-page">Mapa de Empatia - Visão Detalhada</h2>
		</div>
		<div class="col m8 right-align">
			<form:form action="${pageContext.request.contextPath}/persona/empatia/exportar-detalhada" modelAttribute="detailed" method="POST" class="col s12">
				<form:hidden path="fileName" value="Mapa Empatia (${persona.name}) - Visão Detalhada"/>
				<form:hidden id="file-location" path="fileLocation"/>
				<form:hidden path="fileTypeIdentification" value="persona"/>
				
				<button class="waves-effect waves-light btn red darken-3 exportar" type="button" name="action" onclick="Toolkit.Persona.exportFile('.persona-all-data-detailed')">
			    	Exportar<i class="material-icons right">share</i>
			  	</button>
		  	<input type="hidden" name="csrfToken" value="${csrfToken}"/></form:form>
		</div>
	</div>
	
	<div class="persona-all-data-detailed">
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
		<div class="persona-detailed">
			<div class="col s12">
				<div class="row">
					<h6><strong>O que ela SENTE E PENSA?</strong></h6>
					<hr>
					<div class="row attributes-list">
						<c:forEach var="tempAttribute" items="${thinkFeelAtributes}">
							<div class="col m3 item">
								<div class="card <c:out value='${tempAttribute.cardColor}'/>">
					            	<div class="card-content">
					              		<p><c:out value="${tempAttribute.attributeText}"/></p>
					            	</div>
					          	</div>
				          	</div>
						</c:forEach>
					</div>
					<h6><strong>O que ela VÊ?</strong></h6> 
					<hr>
					<div class="row attributes-list">
						<c:forEach var="tempAttribute" items="${seeAtributes}">
							<div class="col m3 item">
								<div class="card <c:out value='${tempAttribute.cardColor}'/>">
					            	<div class="card-content">
					              		<p><c:out value="${tempAttribute.attributeText}"/></p>
					            	</div>
					          	</div>
					        </div>
						</c:forEach>
					</div>
					<h6><strong>O que ela DIZ E FAZ?</strong></h6>
					<hr>
					<div class="row attributes-list">
						<c:forEach var="tempAttribute" items="${sayDoAtributes}">
							<div class="col m3 item">
								<div class="card <c:out value='${tempAttribute.cardColor}'/>">
					            	<div class="card-content">
					              		<p><c:out value="${tempAttribute.attributeText}"/></p>
					            	</div>
					          	</div>
				          	</div>
						</c:forEach>
					</div>
					<h6><strong>O que ela ESCUTA?</strong></h6>
					<hr>
					<div class="row attributes-list">
						<c:forEach var="tempAttribute" items="${hearAtributes}">
							<div class="col m3 item">
								<div class="card <c:out value='${tempAttribute.cardColor}'/>">
					            	<div class="card-content">
					              		<p><c:out value="${tempAttribute.attributeText}"/></p>
					            	</div>
					          	</div>
				        	</div>
						</c:forEach>
					</div>
					<h6><strong>DORES</strong></h6>
					<hr>
					<div class="row attributes-list">
						<c:forEach var="tempAttribute" items="${painAtributes}">
							<div class="col m3 item">
								<div class="card <c:out value='${tempAttribute.cardColor}'/>">
					            	<div class="card-content">
					              		<p><c:out value="${tempAttribute.attributeText}"/></p>
					            	</div>
					          	</div>
				          	</div>
						</c:forEach>
					</div>
					<h6><strong>GANHOS</strong></h6>
					<hr>
					<div class="row attributes-list">
						<c:forEach var="tempAttribute" items="${gainAtributes}">
							<div class="col m3 item">
								<div class="card <c:out value='${tempAttribute.cardColor}'/>">
					            	<div class="card-content">
					              		<p><c:out value="${tempAttribute.attributeText}"/></p>
					            	</div>
					          	</div>
					        </div>
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
		
		<div id="canvas"></div>
	</div>
<t:footer></t:footer>