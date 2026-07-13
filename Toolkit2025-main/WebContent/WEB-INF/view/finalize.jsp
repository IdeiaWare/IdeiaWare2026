<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<%-- TK-11: tela de confirmacao ao finalizar (antes redirecionava sozinho pro Canva do LIC). --%>
<t:header></t:header>
	<div id="success">
		 <div class="row">
	      <div class="col s12">
	        <div class="card-panel red darken-1">
	          <div class="white-text center-align" style="font-size:45px;"><i class="fa fa-smile-o" aria-hidden="true"></i></div>
	          <div class="white-text center-align" style="text-transform:uppercase;">Caixa de Ferramentas finalizada!</div>
	          <div class="right-align">
	          	<a class="white-text" href="${initParam.licBasePath}/minha-ideia.jsp" style="text-transform:uppercase;">
	          		<i class="fa fa-angle-double-left" aria-hidden="true"></i> Ver minhas ideias
	          	</a>
	          </div>
	        </div>
	      </div>
	    </div>
    </div>
<t:footer></t:footer>
