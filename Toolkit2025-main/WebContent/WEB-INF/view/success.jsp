<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<div id="success">
		 <div class="row">
	      <div class="col s12">
	        <div class="card-panel teal">
	          <div class="white-text center-align" style="font-size:45px;"><i class="fa fa-smile-o" aria-hidden="true"></i></div>
	          <div class="white-text center-align" style="text-transform:uppercase;">Exportação finalizada com sucesso!!</div>
	          <div class="white-text center-align">Acesse a área de Retenção do Conhecimento para visualizar os seus dados exportados.</div>
	          <div class="right-align">
	          	<a class="white-text" href="${pageContext.request.contextPath}${fallBackURL}" style="text-transform:uppercase;">
	          		<i class="fa fa-angle-double-left" aria-hidden="true"></i> Voltar
	          	</a>
	          </div>     
	        </div>
	      </div>
	    </div>
    </div>
<t:footer></t:footer>