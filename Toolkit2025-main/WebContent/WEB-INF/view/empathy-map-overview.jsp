<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:no-container-header></t:no-container-header>
	<%-- UX-VOLTAR-V2: mesmo padrao do resto do app -- icone circular flutuante no
	     canto superior esquerdo, volta pro Mapa de Empatia (pai imediato). --%>
	<a href="${pageContext.request.contextPath}/persona/empatia/mapa?personaId=${persona.id}" class="btn-floating btn-large red darken-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar"><i class="material-icons">arrow_back</i></a>
	<nav class="crumb">
	    <div class="nav-wrapper">
	        <a href="${pageContext.request.contextPath}/persona/lista" class="breadcrumb">Personas</a>
	        <a href="${pageContext.request.contextPath}/persona/empatia/mapa?personaId=${persona.id}" class="breadcrumb">Mapa de Empatia</a>
	        <span class="breadcrumb active">Visão Geral</span>
	    </div>
  	</nav>
  	
  	<div class="row">
  		<div class="col s12 m4">
			<h2 class="title-page">Mapa de Empatia - Visão Geral</h2>
		</div>
		<div class="col m8 right-align">
			<form:form action="${pageContext.request.contextPath}/persona/empatia/exportar-geral" modelAttribute="overview" method="POST" class="col s12">
				<form:hidden path="fileName" value="Mapa Empatia (${persona.name}) - Visão Geral"/>
				<form:hidden id="file-location" path="fileLocation"/>
				<form:hidden path="fileTypeIdentification" value="persona"/>
				
				<button class="waves-effect waves-red btn-flat exportar" type="button" name="action" onclick="Toolkit.Persona.exportEmpathyMap()">Exportar
			    	<i class="fa fa-file-pdf-o left" aria-hidden="true"></i>
			  	</button>
		  	<input type="hidden" name="csrfToken" value="${csrfToken}"/></form:form>
		</div>
	</div>
	<div class="persona-all-data-overview">
		<div class="persona-pre-data">
			<div class="row">
				<div class="col s6 m3">
					<label>Nome: </label>
					<span><c:out value="${persona.name}"/></span>
				</div>
				<div class="col s6 m3">
					<label>Idade: </label>
					<span>${persona.age}</span>
				</div>
			</div>
		</div>
		<div class="persona-overview">
			<div class="col s12">
				<div class="row" style="position:relative;">
					<div class="col s12 m6 grey lighten-5 section">
						<h6 class="center-align"><strong>O que ela SENTE E PENSA?</strong></h6>
						<div class="row">
							<c:forEach var="tempAttribute" items="${thinkFeelAtributes}">
								<div class="col m4">
									<div class="card <c:out value='${tempAttribute.cardColor}'/>">
						            	<div class="card-content ellipsed-data">
						              		<p><c:out value="${tempAttribute.attributeText}"/></p>
						            	</div>
						          	</div>
					          	</div>
							</c:forEach>
						</div>
					</div>
					<div class="col s12 m6 grey lighten-5 section">
						<h6 class="center-align"><strong>O que ela VÊ?</strong></h6> 
						<div class="row">
							<c:forEach var="tempAttribute" items="${seeAtributes}">
								<div class="col m4">
									<div class="card <c:out value='${tempAttribute.cardColor}'/>">
						            	<div class="card-content ellipsed-data">
						              		<p><c:out value="${tempAttribute.attributeText}"/></p>
						            	</div>
						          	</div>
						        </div>
							</c:forEach>
						</div>
					</div>
					<i class="fa fa-user-circle-o centered-icon" aria-hidden="true"></i>
					<div class="col s12 m6 grey lighten-5 section">
						<h6 class="center-align"><strong>O que ela DIZ E FAZ?</strong></h6>
						<div class="row">
							<c:forEach var="tempAttribute" items="${sayDoAtributes}">
								<div class="col m4">
									<div class="card <c:out value='${tempAttribute.cardColor}'/>">
						            	<div class="card-content ellipsed-data">
						              		<p><c:out value="${tempAttribute.attributeText}"/></p>
						            	</div>
						          	</div>
					          	</div>
							</c:forEach>
						</div>
					</div>
					<div class="col s12 m6 grey lighten-5 section">
						<h6 class="center-align"><strong>O que ela ESCUTA?</strong></h6>
						<div class="row">
							<c:forEach var="tempAttribute" items="${hearAtributes}">
								<div class="col m4">
									<div class="card <c:out value='${tempAttribute.cardColor}'/>">
						            	<div class="card-content ellipsed-data">
						              		<p><c:out value="${tempAttribute.attributeText}"/></p>
						            	</div>
						          	</div>
					        	</div>
							</c:forEach>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col s12 m6 section">
						<h6 class="center-align"><strong>DORES</strong></h6>
						<div class="row">
							<c:forEach var="tempAttribute" items="${painAtributes}">
								<div class="col m4">
									<div class="card <c:out value='${tempAttribute.cardColor}'/>">
						            	<div class="card-content ellipsed-data">
						              		<p><c:out value="${tempAttribute.attributeText}"/></p>
						            	</div>
						          	</div>
					          	</div>
							</c:forEach>
						</div>
					</div>
					<div class="col s12 m6 section"> 
						<h6 class="center-align"><strong>GANHOS</strong></h6>
						<div class="row">
							<c:forEach var="tempAttribute" items="${gainAtributes}">
								<div class="col m4">
									<div class="card <c:out value='${tempAttribute.cardColor}'/>">
						            	<div class="card-content ellipsed-data">
						              		<p><c:out value="${tempAttribute.attributeText}"/></p>
						            	</div>
						          	</div>
						        </div>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="canvas"></div>
	<div class="modal modal-card-info">
	   	<div class="modal-content">
     		<div class="card">
            	<div class="card-content"></div>
          	</div>     		
	    </div>
  	</div>
<t:no-container-footer></t:no-container-footer>