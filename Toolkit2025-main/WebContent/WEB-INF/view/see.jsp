<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<nav class="crumb">
	    <div class="nav-wrapper">
	        <a href="${pageContext.request.contextPath}/persona/lista" class="breadcrumb">Personas</a>
	        <a href="${pageContext.request.contextPath}/persona/empatia/mapa?personaId=${personaId}" class="breadcrumb">Mapa de Empatia</a>
	        <span class="breadcrumb active">O que ela VÊ</span>
	    </div>
  	</nav>
	<div class="row">
		<div class="col s12 m6 vertical-divider">
			<h4>O que ela VÊ?</h4>
			<h5>Descreva o que a cliente vê em seu ambiente</h5>
			<ul class="browser-default">
				<li>Como é?</li>
				<li>Quem está em torno dela?</li>
				<li>Quem são seus amigos?</li>
				<li>A quais tipos de ofertas ela está exposta diariamente 
					(em oposição ao que todo o mercado oferece)?</li>
				<li>Quais problemas encontra?</li>				
			</ul>
		</div>
		<div class="col s12 l6 attribute">
			<form:form action="o-que-ve/save-attribute" modelAttribute="attribute" method="POST" class="col s12">
				<div class="row">					
					<form:hidden class="attribute-id" path="id"/>
					<form:hidden path="personaId" value="${personaId}"/>
					<form:hidden path="attribute" value="see"/>
						
					<div class="input-field col s12">
			          <form:textarea id="attribute-text" class="materialize-textarea" path="attributeText" />
			          <label for="attribute-text">O que ela VÊ?</label>
			          <div class="attribute-error"></div>
			        </div>
			        <label class="lbl-post-it" for="card-color">Cor do post-it</label>
			        <div class="input-field col s12">			        	
			        	<div class="post-it">		        
					  		<div class="btn blue" data-color="blue"></div>
					  		<div class="btn green" data-color="green"></div>
					  		<div class="btn yellow" data-color="yellow"></div>
					  		<div class="btn orange" data-color="orange"></div>
					  		<div class="btn pink" data-color="pink"></div>
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
					<!-- construct an "delete" link with attribute id -->
					<c:url var="deleteLink" value="/persona/empatia/o-que-ve/delete">
					<c:param name="csrfToken" value="${csrfToken}"/>
						<c:param name="personaId" value="${tempAttribute.personaId}" />
						<c:param name="attributeId" value="${tempAttribute.id}" />						
					</c:url>
				
			        <div class="col s12 m4 item">
		          		<div class="card ${tempAttribute.cardColor}">
			            	<div class="card-content">
			              		<p><c:out value="${tempAttribute.attributeText}"/></p>
			            	</div>
		            		<div class="card-action">
		              			<a href="javascript:;" data-id="${tempAttribute.id}" data-text="<c:out value='${tempAttribute.attributeText}'/>" data-color="<c:out value='${tempAttribute.cardColor}'/>" onclick="Toolkit.EmpathyAttribute.editAttribute(this.dataset.id, this.dataset.text, this.dataset.color)">
			              			<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
			              		</a>
			              		<a href="${deleteLink}"
									onclick="if (!(confirm('Vocé tem certeza que deseja deletar este atributo?'))) return false">
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