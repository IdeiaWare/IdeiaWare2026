<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<%-- UX-VOLTAR-V2: icone circular flutuante, volta pro Mapa de Empatia (pai imediato). --%>
	<a href="${pageContext.request.contextPath}/persona/empatia/mapa?personaId=${personaId}" class="btn-floating btn-large red darken-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar"><i class="material-icons">arrow_back</i></a>
	<nav class="crumb">
	    <div class="nav-wrapper">
	        <a href="${pageContext.request.contextPath}/persona/lista" class="breadcrumb">Personas</a>
	        <a href="${pageContext.request.contextPath}/persona/empatia/mapa?personaId=${personaId}" class="breadcrumb">Mapa de Empatia</a>
	        <span class="breadcrumb active">O que ela Diz e Faz</span>
	    </div>
  	</nav>
	<div class="row">
		<div class="col s12 m6 vertical-divider">
			<h4>O que ela Diz e Faz?</h4>
			<h5>Imagine o que a cliente pode dizer ou como se comporta em público</h5>
			<ul class="browser-default">
				<li>Qual a atitude dela?</li>
				<li>O que ela pode estar dizendo para outras pessoas?</li>
				<li>Preste atenção principalmente nos conflitos potenciais entre o 
					que um cliente pode dizer e o que realmente pensa e sente.</li>				
			</ul>
		</div>
		<div class="col s12 l6 attribute">
			<form:form action="o-que-diz-e-faz/save-attribute" modelAttribute="attribute" method="POST" class="col s12">
				<div class="row">					
					<form:hidden class="attribute-id" path="id"/>
					<form:hidden path="personaId" value="${personaId}"/>
					<form:hidden path="attribute" value="say_do"/>
						
					<div class="input-field col s12">
			          <form:textarea id="attribute-text" class="materialize-textarea" path="attributeText" />
			          <label for="attribute-text">O que ela Diz e Faz?</label>
			          <div class="attribute-error"></div>
			        </div>
			        <label class="lbl-post-it" for="cardColor">Cor do post-it</label>
			        <div class="input-field col s12">			        	
			        	<div class="post-it">		        
					  		<div class="btn blue" data-color="blue" role="button" tabindex="0" aria-label="Azul"></div>
					  		<div class="btn green" data-color="green" role="button" tabindex="0" aria-label="Verde"></div>
					  		<div class="btn yellow" data-color="yellow" role="button" tabindex="0" aria-label="Amarelo"></div>
					  		<div class="btn orange" data-color="orange" role="button" tabindex="0" aria-label="Laranja"></div>
					  		<div class="btn pink" data-color="pink" role="button" tabindex="0" aria-label="Rosa"></div>
				  		</div>
					    <form:hidden path="cardColor" class="card-color"/>
					</div>
			        <div class="input-field col s4">
			        	<input type="submit" value="Adicionar" class="waves-effect waves-light btn"/>
			        </div>
			        <div class="input-field col s4" style="display:none;">
			        	<a class="waves-effect waves-light btn grey lighten-4 black-text btn-cancelar">Cancelar</a>
			        </div>
		        </div>
			<input type="hidden" name="csrfToken" value="${csrfToken}"/></form:form>
		</div>
	</div>
	<div class="row">
		<div class="col s12 attributes-list">
			<div class="row">
				<c:if test="${empty attributes}"><div class="col s12 center-align grey-text" style="padding: 30px 20px;">Nenhum item adicionado ainda.</div></c:if>
				<c:forEach var="tempAttribute" items="${attributes}">
					<%-- TK-26: mesmo fix do gain.jsp. --%>
					<form id="deleteAttributeForm${tempAttribute.id}" action="${pageContext.request.contextPath}/persona/empatia/o-que-diz-e-faz/delete" method="POST" style="display:none;">
						<input type="hidden" name="csrfToken" value="${csrfToken}"/>
						<input type="hidden" name="personaId" value="${tempAttribute.personaId}"/>
						<input type="hidden" name="attributeId" value="${tempAttribute.id}"/>
					</form>

			        <div class="col s12 m4 item">
		          		<div class="card <c:out value='${tempAttribute.cardColor}'/>">
			            	<div class="card-content">
			              		<p><c:out value="${tempAttribute.attributeText}"/></p>
			            	</div>
		            		<div class="card-action">
		              			<a href="javascript:;" data-id="${tempAttribute.id}" data-text="<c:out value='${tempAttribute.attributeText}'/>" data-color="<c:out value='${tempAttribute.cardColor}'/>" onclick="Toolkit.EmpathyAttribute.editAttribute(this.dataset.id, this.dataset.text, this.dataset.color)">
			              			<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
			              		</a>
			              		<a href="javascript:;"
									onclick="if (confirm('Você tem certeza que deseja deletar este atributo?')) document.getElementById('deleteAttributeForm${tempAttribute.id}').submit();">
										<i class="fa fa-trash" aria-hidden="true"></i>
								</a>
			            	</div>
			          	</div>
			        </div>
				</c:forEach>
			</div>
		</div>
	</div>
<t:footer></t:footer>