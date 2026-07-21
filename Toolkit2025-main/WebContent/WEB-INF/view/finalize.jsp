<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<%-- TK-11: tela de confirmacao ao finalizar a Caixa --%>
<%-- UX-SUCESSO-CONSISTENCIA: alinhado com canvas-finalizado.jsp/storytelling-finalizado.jsp (LIC). --%>
<t:header></t:header>
	<div id="success">
		 <div class="row">
	      <div class="col s12 m8 offset-m2">
	        <div class="card-panel red darken-1" style="margin-top: 40px;">
	          <div class="white-text center-align" style="font-size:55px;"><i class="fa fa-smile-o" aria-hidden="true"></i></div>
	          <div class="white-text center-align" style="text-transform:uppercase; font-size:20px; padding-top:6px;">Caixa de Ferramentas finalizada!</div>
	          <div class="white-text center-align" style="padding-top:8px;">Sua Caixa de Ferramentas foi finalizada com sucesso.</div>
	          <%-- UX-SUCESSO-PROXIMA-ETAPA: informa o proximo modulo (IdeiaDAOImpl.finalize seta status CANVAS/"CV"). --%>
	          <div class="white-text center-align" style="padding-top:4px;">Sua ideia avan&ccedil;ou para a etapa do Canvas.</div>
	          <div class="right-align" style="padding-top:16px;">
	          	<a class="white-text" href="${initParam.licBasePath}/minha-ideia.jsp" style="text-transform:uppercase;">
	          		<i class="fa fa-angle-double-left" aria-hidden="true"></i> Ver minhas ideias
	          	</a>
	          </div>
	        </div>
	      </div>
	    </div>
    </div>
<t:footer></t:footer>
