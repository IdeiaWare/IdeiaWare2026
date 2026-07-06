<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- CAN-07: pageEncoding UTF-8 (era UTF-8, que corrompia os acentos) --%>
<%@page contentType="text/html" pageEncoding="UTF-8"  session="true"%>
<!-- HEADER -->
<%@include file="header/headerCookiesCV.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
	<head>
		<meta charset="UTF-8">
		<title>Visão Geral</title>
	</head>
	<body class="center-align">
		<%-- UX-VOLTAR-V2: mesmo padrao do Colaborativo/Storytelling/canva-mapa.jsp. --%>
		<a href="EntrarCanvaServlet" class="btn-floating btn-large blue darken-4 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
		<%-- expõe o fluxo (retenção x finalização normal) para o geraPDFCanva.js
		     decidir o redirect pós-export sem corte seco. --%>
		<script>window.isRetencaoCanva = ('${sessionScope.isRetencao}' === 'true');</script>
		<div style="min-height: 80vh">
			<!-- VARIAVEIS -->
	    	<jsp:useBean id="ideiaDAO" class="edu.unisc.lic.dao.IdeiaDAO" />
		    <jsp:useBean id="canva" class="edu.unisc.lic.domain.Canva" />
		    
		    <!-- MENU DE NAVEGAÇÃO -->
	    	<div class="row center-align navCanva">
	    		<div class="col s6">
		    		<nav class="crumb blue darken-4">
					    <div class="nav-wrapper">
					        <a href="lista-canvas.jsp" class="breadcrumb">Canva</a>
					        <a href="EntrarCanvaServlet" class="breadcrumb">Mapa</a>
					        <span class="breadcrumb active">Visão Geral</span>
					    </div>
		  			</nav>
		  		</div>
		  		
		  		<!-- BOTÃO DE EXPORTAÇÃO -->
		  		<%-- UX-COR: blue darken-4 (cor do proprio modulo Canvas); era red, sem
		  		     nenhuma relacao com o header/footer azul deste modulo. --%>
		  		<div>
					<button class="btn btn-lg blue darken-4 mt4" onclick="geraPDF()">Exportar</button>
				</div>
			</div>
			<div id="overlay">
				<div class="loader"></div>
			</div>

			<!-- INICIO DA DIV DE EXPORTAÇÃO / LISTAGEM DE POST-ITS -->
			<%-- TEST-04 (2026-07-06): esta pagina e so leitura (nenhum form/botao de
			     edicao aqui, so o Exportar acima, fora deste bloco) -- gate-la inteira
			     por isRetencao deixava a pagina em BRANCO no modo retencao, diferente
			     das 9 canva-*.jsp irmas (que so escondem o FORM de adicionar post-it,
			     nunca a listagem). Removido o gate: mostra sempre. --%>
			<div id="myPDF">
				<!-- INICIO SEGMENTO DE CLIENTES -->
				    <c:set var="numPost" value="0" />
					<div class="row">
						<div class="col s10 offset-s1">
							<c:choose>
								<c:when test="${segmento.size() > 0}">
									<h4 class="postit_title">Segmento de Clientes</h4>
					    			<div class="row equal-cols">
										<c:forEach var="cv" items="${segmento}" varStatus="id">
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
														
														<div class="card-content card-paragrafer"><p><c:out value="${cv.text}"/></p></div>
													</div>
												</div>
											</div>
											<c:set var="numPost" value="${numPost+1}" />
										</c:forEach>
									</div>
								</c:when>
							</c:choose>
						</div>
					</div>
					<!-- FIM SEGMENTO DE CLIENTES -->
					
					<!-- INICIO PROPOSTA DE VALOR -->
				    <c:set var="numPost" value="0" />
					<div class="row">
						<div class="col s10 offset-s1">
							<c:choose>
								<c:when test="${proposta.size() > 0}">
									<h4 class="postit_title">Proposta de Valor</h4>
					    			<div class="row equal-cols">
										<c:forEach var="cv" items="${proposta}" varStatus="id">
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
													</div>
												</div>
											</div>
											<c:set var="numPost" value="${numPost+1}" />
										</c:forEach>
									</div>
								</c:when>
							</c:choose>
						</div>
					</div>
					<!-- FIM PROPOSTA DE VALOR -->
					
					<!-- INICIO CANAIS -->
				    <c:set var="numPost" value="0" />
					<div class="row">
						<div class="col s10 offset-s1">
							<c:choose>
								<c:when test="${canal.size() > 0}">
									<h4 class="postit_title">Canais de Comunicação</h4>
					    			<div class="row equal-cols">
										<c:forEach var="cv" items="${canal}" varStatus="id">
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
													</div>
												</div>
											</div>
											<c:set var="numPost" value="${numPost+1}" />
										</c:forEach>
									</div>
								</c:when>
							</c:choose>
						</div>
					</div>
					<!-- FIM CANAIS -->
					
					<!-- INICIO RELACIONAMENTO COM CLIENTES -->
				    <c:set var="numPost" value="0" />
					<div class="row">
						<div class="col s10 offset-s1">
							<c:choose>
								<c:when test="${relacionamento.size() > 0}">
									<h4 class="postit_title">Relacionamento com Cliente</h4>
					    			<div class="row equal-cols">
										<c:forEach var="cv" items="${relacionamento}" varStatus="id">
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
													</div>
												</div>
											</div>
											<c:set var="numPost" value="${numPost+1}" />
										</c:forEach>
									</div>
								</c:when>
							</c:choose>
						</div>
					</div>
					<!-- FIM RELACIONAMENTO COM CLIENTES -->
				
					<!-- INICIO RECEITA -->
					<c:set var="numPost" value="0" />
					<div class="row">
						<div class="col s10 offset-s1">
							<c:choose>
								<c:when test="${receita.size() > 0}">
									<h4 class="postit_title">Receita</h4>
					    			<div class="row equal-cols">
										<c:forEach var="cv" items="${receita}" varStatus="id">
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
													</div>
												</div>
											</div>
											<c:set var="numPost" value="${numPost+1}" />
										</c:forEach>
									
									</div>
								</c:when>
							</c:choose>
						</div>
					</div>
					<!-- FIM RECEITA -->
					
					<!-- INICIO RECURSOS PRINCIPAIS -->
				   	<c:set var="numPost" value="0" />
					<div class="row">
						<div class="col s10 offset-s1">
							<c:choose>
								<c:when test="${recurso.size() > 0}">
									<h4 class="postit_title">Recursos Principais</h4>
					    			<div class="row equal-cols">
								
										<c:forEach var="cv" items="${recurso}" varStatus="id">
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
													</div>
												</div>
											</div>
											<c:set var="numPost" value="${numPost+1}" />
										</c:forEach>
									</div>
								</c:when>
							</c:choose>
						</div>
					</div>
					<!-- FIM RECURSOS PRINCIPAIS -->
					
				    <!-- INICIO ATIVIDADES PRINCIPAIS -->
				    <c:set var="numPost" value="0" />
					<div class="row">
						<div class="col s10 offset-s1">
							<c:choose>
								<c:when test="${atividade.size() > 0}">
									<h4 class="postit_title">Atividades Principais</h4>
					    			<div class="row equal-cols">
										<c:forEach var="cv" items="${atividade}" varStatus="id">
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
													</div>
												</div>
											</div>
											<c:set var="numPost" value="${numPost+1}" />
										</c:forEach>
									</div>
								</c:when>
							</c:choose>
						</div>
					</div>
					<!-- FIM ATIVIDADES PRINCIPAIS -->
				    
				    <!-- INICIO PARCERIAS PRINCIPAIS -->
				    <c:set var="numPost" value="0" />
					<div class="row">
						<div class="col s10 offset-s1">
							<c:choose>
								<c:when test="${parceria.size() > 0}">
									<h4 class="postit_title">Parcerias Principais</h4>
					    			<div class="row equal-cols">
										<c:forEach var="cv" items="${parceria}" varStatus="id">
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
													</div>
												</div>
											</div>
											<c:set var="numPost" value="${numPost+1}" />
										</c:forEach>
									</div>
								</c:when>
							</c:choose>
						</div>
					</div>
					<!-- FIM PARCERIAS PRINCIPAIS -->
					
					<!-- INICIO ESTRUTURA DE CUSTOS -->
				    <c:set var="numPost" value="0" />
					<div class="row">
						<div class="col s10 offset-s1">
							<c:choose>
								<c:when test="${custo.size() > 0}">
									<h4 class="postit_title">Estrutura de Custos</h4>
					    			<div class="row equal-cols">
										<c:forEach var="cv" items="${custo}" varStatus="id">
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
													</div>
												</div>
											</div>
											<c:set var="numPost" value="${numPost+1}" />
										</c:forEach>
									</div>
								</c:when>
							</c:choose>
						</div>
					</div>
					<!-- FIM ESTRUTURA DE CUSTOS -->
		    </div>
		    <!-- FIM DA DIV DE EXPORTAÇÃO -->
	    </div>
	<script src="js/csrf.js"></script>
  </body>
	<%@include file="header/canva-footer.jsp" %>
</html>