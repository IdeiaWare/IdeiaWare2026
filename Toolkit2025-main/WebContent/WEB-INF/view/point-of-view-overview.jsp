<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<%-- UX-VOLTAR-V2: icone circular flutuante volta pra listagem de POV --%>
	<a href="${pageContext.request.contextPath}/point-of-view/lista" class="btn-floating btn-large red darken-3 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar"><i class="material-icons">arrow_back</i></a>
	<nav class="crumb">
	    <div class="nav-wrapper">
	        <a href="${pageContext.request.contextPath}/point-of-view/lista" class="breadcrumb">Point of View</a>
	        <span class="breadcrumb active">Visão Geral</span>
	    </div>
  	</nav>
  	<div class="row">
  		<div class="col m5">
  			<h2 class="title-page">Point of View - Visão Geral</h2>
  		</div>
  		<div class="col m7 right-align">
			<form:form action="${pageContext.request.contextPath}/point-of-view/exportar-geral" modelAttribute="overview" method="POST" class="col s12">
				<form:hidden id="file-name" path="fileName"/>
				<form:hidden id="file-location" path="fileLocation"/>
				<form:hidden path="fileTypeIdentification" value="pov"/>
				
				<button class="waves-effect waves-light btn red darken-3 exportar" type="button" name="action" onclick="Toolkit.PointOfView.exportTable()">
			    	Exportar<i class="material-icons right">share</i>
			  	</button>
		  	<input type="hidden" name="csrfToken" value="${csrfToken}"/></form:form>
  		</div>
  	</div>
  	<div class="row">
		<div class="col s12 pov-all-data-overview">
			<table>
		        <thead>
		          <tr>
	          		<th>Persona(s)</th>
	          		<th>Usuário(s)</th>
		          	<th>Necessidade(s)</th>
		           	<th>Introspecção(ões)</th>
		          </tr>
		        </thead>
		        <tbody>
					<c:forEach var="tempPOV" items="${povs}">						
						<tr>
							<td class="pov-names"><c:out value="${tempPOV.value.names}"/></td>
							<td><c:out value="${tempPOV.value.user}"/></td>
							<td><c:out value="${tempPOV.value.need}"/></td>
							<td><c:out value="${tempPOV.value.insight}"/></td>
						</tr>					
					</c:forEach>
				</tbody>
			</table>
		</div>
		
		<div id="canvas"></div>
	</div>
	
	<script type="text/javascript">	
		$(document).ready(function () {
			$("#file-name").val("Point of View ("+ $('.pov-names').text() +") - Visão Geral");
 		});
	</script>
<t:footer></t:footer>