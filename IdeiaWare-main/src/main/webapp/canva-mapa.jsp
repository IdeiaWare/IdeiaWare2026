<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"  session="true"%>
<!-- HEADER -->
<%@include file="header/headerCookiesCV.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <title>IdeiaWare - Canva</title>
        <link rel="stylesheet" href="css/materialize.min.css">
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@600' rel='stylesheet'>
        <%-- TITULO-MODULO-POPPINS: titulos dos 4 quadrantes do mapa. --%>
        <style>.main_title { font-family: 'Poppins', sans-serif; font-weight: 600; }</style>
    </head>
    <body class="center-align blue-grey lighten-5 m-5">
    <main>
        <%-- UX-VOLTAR-V2: icone flutuante padrao do resto do app. --%>
        <a href="lista-canvas.jsp" class="btn-floating btn-large blue darken-4 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
    	<!-- VARIAVEIS -->
        <jsp:useBean id="data" class="edu.unisc.lic.classes.Data" />
        <jsp:useBean id="ideiaUsuarioDAO" class="edu.unisc.lic.dao.IdeiaUsuarioDAO" />
        <jsp:useBean id="ideiaUsuario" class="edu.unisc.lic.domain.IdeiaUsuario" />
        <jsp:useBean id="ideiaUsuario2" class="edu.unisc.lic.domain.IdeiaUsuario" />
        <jsp:setProperty name="ideiaUsuario2" property="usuario" value="${usuarioClasse}" />
        <jsp:setProperty name="ideiaUsuario2" property="ideia" value="${sessionScope.ideia}" />
        <jsp:setProperty name="ideiaUsuario" property="ideia" value="${sessionScope.ideia}" />
        
        <!-- MENU DE NAVEGAÇÃO -->
        <div class="row center-align navCanva">
	    	<div class="col s6">
		    	<nav class="crumb blue darken-4">
					<div class="nav-wrapper">
						<a href="lista-canvas.jsp" class="breadcrumb">Canva</a>
					    <span class="breadcrumb active">Mapa</span>
					</div>
		  		</nav>
	  		</div>
	    </div>
	    
	    <!-- BOTÃO DE SUGESTÃO -->
	    <a id="sugest_btn" class="btn btn-large waves-effect waves-light modal-trigger" href="#sugestModal">SUGESTÃO</a>
        
        <!-- INICIO DO MAPA -->
        <div class="row canva_box">
        
       		<!-- INICIO DO MAPA SUPERIOR -->
			<div class="col s12 top_box">
				<div class="row equal-cols">
				
					<!-- INICIO DO MAPA (COMO?)-->
					<div class="col s5">
						<div class="row">
							<div class="col s12 light-blue canva_title">
								<h3 class="card-paragrafer main_title">Como?</h3>
							</div>
							<div class="col s12">
								<div class="row">
								
									<!-- INICIO DO MAPA SUPERIOR COMO/PARCERIA-->
									<a href="EntrarCanvaParceriaServlet">
										<div class="col s6 canva_solo_box b-right h100">
											<div class="mt1 p1">
												<h4 class="card-paragrafer">Parcerias Principais</h4>
												<p>Rede de Fornecedores e parceiros que ajudam a sua empresa a funcionar</p>
											</div>
											<div class="row navCanva">
												<c:choose>
													<c:when test="${parceria.size() > 0}">
														<c:forEach var="cv" items="${parceria}" varStatus="id">
															<div class="col s8 card ${cv.color}">
													    		<div class="card-content card-paragrafer"><c:out value="${cv.text}"/></div>
													    	</div>
														</c:forEach>
													</c:when>
								    			</c:choose>
							    			</div>
										</div>
									</a>
									<!-- FIM DO MAPA SUPERIOR COMO/PARCERIA-->
									<div class="col s6">
										<div class="row">
											<!-- INICIO DO MAPA SUPERIOR COMO/ATIVIDADE-->
											<a href="EntrarCanvaAtividadeServlet">
												<div class="col s12 canva_double_box b-bot">
													<div class="mt5 p1">
														<h4 class="card-paragrafer">Atividades Principais</h4>
														<p>Ações importantes que sua empresa deve realizar para fazer seu Modelo de Negócios funcionar</p>
													</div>
													<div class="row navCanva">
														<c:choose>
															<c:when test="${atividade.size() > 0}">
																<c:forEach var="cv" items="${atividade}" varStatus="id">
																	<div class="col s8 card ${cv.color}">
															    		<div class="card-content card-paragrafer"><c:out value="${cv.text}"/></div>
															    	</div>
																</c:forEach>
															</c:when>
										    			</c:choose>
									    			</div>
												</div>
											</a>
											<!-- FIM DO MAPA SUPERIOR COMO/ATIVIDADE-->
											
											<!-- INICIO DO MAPA SUPERIOR COMO/RECURSO-->
											<a href="EntrarCanvaRecursoServlet">
												<div class="col s12 canva_double_box">
													<div class="mt5 p1">
														<h4 class="card-paragrafer">Recursos Principais</h4>
														<p>Recursos mais importantes exigidos para fazer o Modelo de Negócios funcionar</p>
													</div>
													<div class="row navCanva">
														<c:choose>
															<c:when test="${recurso.size() > 0}">
																<c:forEach var="cv" items="${recurso}" varStatus="id">
																	<div class="col s8 card ${cv.color}">
															    		<div class="card-content card-paragrafer"><c:out value="${cv.text}"/></div>
															    	</div>
																</c:forEach>
															</c:when>
										    			</c:choose>
									    			</div>
												</div>
											</a>
											<!-- FIM DO MAPA SUPERIOR COMO/RECURSO-->
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<!-- FIM DO MAPA (COMO?)-->
					
					<!-- INICIO DO MAPA (O QUE?)-->
					<div class="col s2 what">
						<div class="col s12 red canva_title">
							<h3 class="card-paragrafer main_title">O que?</h3>
						</div>
						<div class="row">
						
							<!-- INICIO DO MAPA SUPERIOR O QUE/PROPOSTA-->
							<a href="EntrarCanvaPropostaServlet">
								<div class="col s12 canva_solo_box">
									<div class="mt1 p1">
										<h4 class="card-paragrafer">Proposta de valor</h4>
										<p>Qual seu pacode de produtos e serviços e o valor que ele possui para os clientes</p>
									</div>
									<div class="row navCanva">
										<c:choose>
											<c:when test="${proposta.size() > 0}">
												<c:forEach var="cv" items="${proposta}" varStatus="id">
													<div class="col s8 card ${cv.color}">
											    		<div class="card-content card-paragrafer"><c:out value="${cv.text}"/></div>
											    	</div>
												</c:forEach>
											</c:when>
						    			</c:choose>
					    			</div>
								</div>
							</a>
							<!-- FIM DO MAPA SUPERIOR O QUE/PROPOSTA-->
						</div>
					</div>
					<!-- FIM DO MAPA (O QUE?)-->
					
					<!-- INICIO DO MAPA (PARA QUEM?)-->
					<div class="col s5 ">
						<div class="row ">
							<div class="col s12 green canva_title">
								<h3 class="card-paragrafer main_title">Para quem?</h3>
							</div>
							<div class="col s6">
								<div class="row">
								
									<!-- INICIO DO MAPA SUPERIOR PARA QUEM/RELACIONAMENTO-->
									<a href="EntrarCanvaRelacionamentoServlet">
										<div class="col s12 canva_double_box b-bot">
											<div class="mt5 p1">
												<h4 class="card-paragrafer">Relacionamento com Clientes</h4>
												<p>Tipos de relação que uma empresa estabelece com Clientes para que possa conquistá-los e mante-los</p>
											</div>
											<div class="row navCanva">
												<c:choose>
													<c:when test="${relacionamento.size() > 0}">
														<c:forEach var="cv" items="${relacionamento}" varStatus="id">
															<div class="col s8 card ${cv.color}">
													    		<div class="card-content card-paragrafer"><c:out value="${cv.text}"/></div>
													    	</div>
														</c:forEach>
													</c:when>
								    			</c:choose>
							    			</div>
										</div>
									</a>
									<!-- FIM DO MAPA SUPERIOR PARA QUEM/RELACIONAMENTO-->
									
									<!-- INICIO DO MAPA SUPERIOR PARA QUEM/CANAL-->
									<a href="EntrarCanvaCanalServlet">
										<div class="col s12 canva_double_box">
											<div class="mt5 p1">
												<h4 class="card-paragrafer">Canais</h4>
												<p>Como sua empresa se comunica e alcança sua proposta de Valor</p>
											</div>
											<div class="row navCanva">
												<c:choose>
													<c:when test="${canal.size() > 0}">
														<c:forEach var="cv" items="${canal}" varStatus="id">
															<div class="col s8 card ${cv.color}">
													    		<div class="card-content card-paragrafer"><c:out value="${cv.text}"/></div>
													    	</div>
														</c:forEach>
													</c:when>
								    			</c:choose>
							    			</div>
										</div>
									</a>
									<!-- FIM DO MAPA SUPERIOR PARA QUEM/CANAL-->
								</div>
							</div>
							
							<!-- INICIO DO MAPA SUPERIOR PARA QUEM/SEGMENTO-->
							<a href="EntrarCanvaSegmentoServlet">
								<div class="col s6 canva_solo_box b-left h100">
									<div class="mt2 p1">
										<h4 class="card-paragrafer">Segmento de Clientes</h4>
										<p>Quem são os clientes que você pretende atender?</p>
										<p>Eles tem um perfil especifico?</p>
										<p>Como eles estão agrupados?</p>
										<p>Onde estão localizados</p>
									</div>
									<div class="row navCanva">
										<c:choose>
											<c:when test="${segmento.size() > 0}">
												<c:forEach var="cv" items="${segmento}" varStatus="id">
													<div class="col s8 card ${cv.color}">
											    		<div class="card-content card-paragrafer"><c:out value="${cv.text}"/></div>
											    	</div>
												</c:forEach>
											</c:when>
						    			</c:choose>
					    			</div>
								</div>
							</a>
							<!-- FIM DO MAPA SUPERIOR PARA QUEM/SEGMENTO-->
						</div>
					</div>
				</div>
				<!-- FIM DO MAPA (PARA QUEM?)-->
			</div>
			<!-- FIM DO MAPA SUPERIOR-->
			<div class="col s12 white middle_box">
			</div>
			<!-- INICIO DO MAPA INFERIOR -->
			<div class="col s12 bottom_box">
				<div class="row">
					<div class="col s12 yellow canva_title">
						<h3 class="card-paragrafer main_title">Quanto?</h3>
					</div>
					<div class="row">
					
						<!-- INICIO DO MAPA INFERIOR QUANTO/CUSTO-->
						<a href="EntrarCanvaEstruturaServlet">
							<div class="col s6 canva_solo_box_bottom">
								<div class="mt3 p1">
									<h4 class="card-paragrafer">Estrutura de Custos</h4>
									<p>Todos os custos envolvidos na operação do seu Modelo de Negócios</p>
								</div>
								<div class="row navCanva">
									<c:choose>
										<c:when test="${custo.size() > 0}">
											<c:forEach var="cv" items="${custo}" varStatus="id">
												<div class="col s8 card ${cv.color}">
												    <div class="card-content card-paragrafer"><c:out value="${cv.text}"/></div>
												   </div>
											</c:forEach>
										</c:when>
							    	</c:choose>
						    	</div>
							</div>
						</a>
						<!-- FIM DO MAPA INFERIOR QUANTO/CUSTO-->
						
						<!-- INICIO DO MAPA INFERIOR QUANTO/RECEITA-->
						<a href="EntrarCanvaReceitaServlet">
	                   		<div class="col s6 canva_solo_box_bottom b-left">
								<div class="mt4 p1">
									<h4 class="card-paragrafer">Receita</h4>
									<p>Dinheiro que a empresa gera</p>
									<p>Quanto e como você vai receber dos clientes</p>
								</div>
								<div class="row navCanva">
									<c:choose>
										<c:when test="${receita.size() > 0}">
											<c:forEach var="cv" items="${receita}" varStatus="id">
												<div class="col s8 card ${cv.color}">
												    <div class="card-content card-paragrafer"><c:out value="${cv.text}"/></div>
												   </div>
											</c:forEach>
										</c:when>
							    	</c:choose>
						    	</div>
							</div>
	                   	</a>
	                   	<!-- FIM DO MAPA INFERIOR QUANTO/RECEITA-->
					</div>
				</div>
			</div>
			<!-- FIM DO MAPA INFERIOR-->
		</div>
		<!-- FIM DO MAPA -->
		<p class="referencia">Referência: OSTERWALDER, Alexander; PIGNEUR, Yves. Business model generation: inovação em modelos de negócios. Alta Books, 2020</p>
    <script type="text/javascript">
      // MODAL-INIT: #sugestModal nunca tinha nenhuma chamada de init em lugar nenhum (nem
      // aqui, nem no header/footer compartilhado) -- o botao SUGESTAO nunca abriu o modal.
      $(document).ready(function () {
        $('.modal').modal();
      });
    </script>
    <script src="js/csrf.js"></script>
  </main>
    </body>
    
    <!-- FOOTER -->
   	<%@include file="header/canva-footer.jsp" %>
   	
   	<!-- INICIO ESTRUTURA DO MODAL DE SUGESTÃO -->
	<div id="sugestModal" class="modal" style="width: 80%; height: 80%;">
	  	<div class="modal-footer">
	      <a href="#!" class="modal-close waves-effect waves-green green btn-flat">Fechar</a>
	    </div>
	    <div class="modal-content">
	      	<h4>Sugestão para o Canva</h4>
	    	<p>Essa é uma sugestão da ordem a ser seguida na construção do Canva</p>
	      	<div class="row canva_box">
				<div class="col s12 top_box">
					<div class="row equal-cols">
						<div class="col s5">
							<div class="row">
								<div class="col s12 light-blue canva_title">
									<h3 class="card-paragrafer main_title">Como?</h3>
								</div>
								<div class="col s12">
									<div class="row">
										<div class="col s6 canva_solo_box b-right h100">
											<div class="mt1 p1">
												<h4 class="card-paragrafer">Parcerias Principais</h4>
												<p>Rede de Fornecedores e parceiros que ajudam a sua empresa a funcionar</p>
												<i class="btn-floating teal darken-1">8</i>
											</div>
										</div>
										<div class="col s6">
											<div class="row">
												<div class="col s12 canva_double_box b-bot">
													<div class="mt5 p1">
														<h4 class="card-paragrafer">Atividades Principais</h4>
														<p>Ações importantes que sua empresa deve realizar para fazer seu Modelo de Negócios funcionar</p>
														<i class="btn-floating teal darken-1">7</i>
													</div>
												</div>
												<div class="col s12 canva_double_box">
													<div class="mt5 p1">
														<h4 class="card-paragrafer">Recursos Principais</h4>
														<p>Recursos mais importantes exigidos para fazer o Modelo de Negócios funcionar</p>
														<i class="btn-floating teal darken-1">6</i>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col s2 what">
							<div class="col s12 red canva_title">
								<h3 class="card-paragrafer main_title">O que?</h3>
							</div>
							<div class="row">
								<div class="col s12 canva_solo_box">
									<div class="mt1 p1">
										<h4 class="card-paragrafer">Proposta de valor</h4>
										<p>Qual seu pacode de produtos e serviços e o valor que ele possui para os clientes</p>
										<i class="btn-floating teal darken-1">2</i>
									</div>
								</div>
							</div>
						</div>
						<div class="col s5 ">
							<div class="row ">
								<div class="col s12 green canva_title">
									<h3 class="card-paragrafer main_title">Para quem?</h3>
								</div>
								<div class="col s6">
									<div class="row">
										<div class="col s12 canva_double_box b-bot">
											<div class="mt5 p1">
												<h4 class="card-paragrafer">Relacionamento com Clientes</h4>
												<p>Tipos de relação que uma empresa estabelece com Clientes para que possa conquistá-los e mante-los</p>
												<i class="btn-floating teal darken-1">4</i>
											</div>
										</div>
										<div class="col s12 canva_double_box">
											<div class="mt5 p1">
												<h4 class="card-paragrafer">Canais</h4>
												<p>Como sua empresa se comunica e alcança sua proposta de Valor</p>
												<i class="btn-floating teal darken-1">3</i>
											</div>
										</div>
									</div>
								</div>
								<div class="col s6 canva_solo_box b-left h100">
									<div class="mt2 p1">
										<h4 class="card-paragrafer">Segmento de Clientes</h4>
										<p>Quem são os clientes que você pretende atender?</p>
										<p>Eles tem um perfil especifico?</p>
										<p>Como eles estão agrupados?</p>
										<p>Onde estão localizados</p>
										<i class="btn-floating teal darken-1">1</i>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col s12 white middle_box">
				</div>
				<div class="col s12 bottom_box">
					<div class="row">
						<div class="col s12 yellow canva_title">
							<h3 class="card-paragrafer main_title">Quanto?</h3>
						</div>
						<div class="row">
							<div class="col s6 canva_solo_box_bottom">
								<div class="mt3 p1">
									<h4 class="card-paragrafer">Estrutura de Custos</h4>
									<p>Todos os custos envolvidos na operação do seu Modelo de Negócios</p>
									<i class="btn-floating teal darken-1">9</i>
								</div>
							</div>
		                   	<div class="col s6 canva_solo_box_bottom b-left">
								<div class="mt4 p1">
									<h4 class="card-paragrafer">Receita</h4>
									<p>Dinheiro que a empresa gera</p>
									<p>Quanto e como você vai receber dos clientes</p>
									<i class="btn-floating teal darken-1">5</i>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- FIM ESTRUTURA DO MODAL DE SUGESTÃO -->
</html>