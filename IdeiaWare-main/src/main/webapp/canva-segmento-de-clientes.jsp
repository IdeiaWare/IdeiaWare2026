<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"  session="true"%>
<!-- HEADER -->
<%@include file="header/headerCookiesCV.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
	<head>
        <title>IdeiaWare - Canva</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.100.2/css/materialize.min.css">
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <script src="js/jquery-3.2.1.min.js"></script>
    </head>
    <body >
	    <%-- UX-VOLTAR-V2: mesmo padrao do Colaborativo/Storytelling/canva-mapa.jsp. --%>
	    <a href="EntrarCanvaServlet" class="btn-floating btn-large blue darken-4 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
	    <div style="min-height: 80vh">
	    
	    	<!-- VARIAVEIS -->
	    	<jsp:useBean id="ideiaDAO" class="edu.unisc.lic.dao.IdeiaDAO" />
		    <jsp:useBean id="canva" class="edu.unisc.lic.domain.Canva" />
		    <c:set var="isRetencao" value="${sessionScope.isRetencao}" />
		    
		     <!-- MENU DE NAVEGAÇÃO-->
	    	<div class="row center-align navCanva">
	    		<div class="col s6">
		    		<nav class="crumb blue darken-4">
					    <div class="nav-wrapper">
					        <a href="lista-canvas.jsp" class="breadcrumb">Canva</a>
					        <a href="EntrarCanvaServlet" class="breadcrumb">Mapa</a>
					        <span class="breadcrumb active">Segmento de clientes</span>
					    </div>
		  			</nav>
		  			
		  			<!-- ADIÇÃO/EDIÇÃO DE NOVOS POST-ITS -->
		  			<c:if test="${isRetencao eq false}" >
				    	<form name="enviarCanva" action="EnviarCanvaServlet" method="post" id="enviarCanva">
				    		<input hidden="true" value="segmento" id="attribute" name="attribute">
				    		<input hidden="true" value="" id="idCanva" name="idCanva">
				    		<%-- CAN-08: ${sessionScope.ideiaId} (antes ${ideiaId} não resolvia e ia vazio) --%>
				    		<input hidden="true" value="${sessionScope.ideiaId}" name="ideiaId" />
				        	<div class="row">
				            	<div class="input-field">
				                	<textarea id="descricao" type="text" class="materialize-textarea"  name="text" data-length="200" maxlength="200" minlength="5"></textarea>
				                    <label for="descricao">Post-it</label>
				                </div>
				                <label class="lbl-post-it">Cor do post-it</label>
				                <div class="input-field col s12">			        	
						        	<div class="post-it">		        
								  		<div class="btn blue active" data-color="blue"></div>
								  		<div class="btn green" data-color="green"></div>
								  		<div class="btn yellow" data-color="yellow"></div>
								  		<div class="btn orange" data-color="orange"></div>
								  		<div class="btn pink" data-color="pink"></div>
							  		</div>
								    <input hidden="true" id="color-input" path="color" name="color" value="blue"/>
								</div>
				                <button class="btn orange darken-1 mt5" name="enviar" id="enviar" disabled>Enviar
				                    <i class="material-icons right">send</i>
				                </button>
				            </div>
				    	</form>
				    </c:if>
	  			</div>
	    	</div>
	    	
	    	<!-- LISTANDO POST-ITS -->
	    	<c:set var="numPost" value="0" />
	    	<div class="row">
	    		<div class="col s10 offset-s1">
	    			<div class="row equal-cols">
					    <%-- UX-01: estado vazio dos post-its --%>
					    <c:if test="${empty attributes}">
					    	<div class="col s12 center-align grey-text" style="padding: 40px 20px;">
					    		<i class="material-icons" style="font-size: 3rem; display:block;">note_add</i>
					    		Nenhum post-it adicionado nesta seção.
					    	</div>
					    </c:if>
					    <c:choose>
					    	<c:when test="${attributes.size() > 0}">
							    <c:forEach var="cv" items="${attributes}" varStatus="id">
							    	<c:choose>
							    		<c:when test="${numPost % 3 != 0 && numPost != 0}">
							    			<div class="col s2 offset-s3">
							    		</c:when>
							    		<c:otherwise>
							    			<div class="col s2 col_margin">
							    		</c:otherwise>
							    	</c:choose>
							    		<div class="row">
								    		<div class="col s12 card ${cv.color}">
									    		<div class="card-content card-paragrafer"><c:out value="${cv.text}"/></div>
									    		<div class="card-action">
										    		<button class="editCanva btn-floating teal darken-1 tooltipped btn-postit" canva-id="${cv.codigo}" aria-label="Editar post-it" data-tooltip="Editar" data-position="right" data-delay="50">
										    			<i class="fa fa-pencil-square-o" aria-hidden="true"></i>
										    		</button>
													<form name="deleteCanva" id="deleteCanva" action="DeleteCanvaServlet" method="POST">
														<input hidden="true" value="${cv.codigo}" id="canva" name="canva">
														<input hidden="true" value="EntrarCanvaSegmentoServlet" id="context" name="context">
														<button class="btn-floating teal darken-1 tooltipped btn-postit" type="submit" aria-label="Excluir post-it" data-tooltip="Excluir" data-position="right" data-delay="50" onclick="return confirm('Excluir este post-it? Esta ação não pode ser desfeita.')">
															<i class="fa fa-trash" aria-hidden="true"></i>
														</button>
													</form>
												</div>
											</div>
										</div>
							    	</div>
							    	<c:set var="numPost" value="${numPost+1}" />
							    </c:forEach>
					    	</c:when>
					    </c:choose>
				    </div>
			    </div>
		    </div>
	    </div>
	    <%@include file="header/canva-footer.jsp" %>
    <script src="js/csrf.js"></script>
  </body>
    
</html>

	