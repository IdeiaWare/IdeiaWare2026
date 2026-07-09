<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<div id="no-authenticaded">
		 <div class="row">
	      <div class="col s12">
	        <div class="card-panel red">
	          <div class="white-text center-align" style="text-transform:uppercase;">Erro ao identificar ideia.</div>
	          <div class="white-text center-align">Por favor, acesse sua ideia novamente na área de colaboração.</div>
	          <div class="right-align">
	          	<a class="white-text" href="${initParam.licBasePath}" style="text-transform:uppercase;">
	          		<i class="fa fa-angle-double-left" aria-hidden="true"></i> Ir
	          	</a>
	          </div>          
	        </div>
	      </div>
	    </div>
    </div>
<t:footer></t:footer>