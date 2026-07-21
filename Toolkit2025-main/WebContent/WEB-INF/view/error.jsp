<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<%-- TK-06: view error do ErrorController nao existia --%>
<t:header></t:header>
	<div id="error-page">
		 <div class="row">
	      <div class="col s12">
	        <div class="card-panel red">
	          <div class="white-text center-align" style="font-size:45px;"><i class="fa fa-exclamation-triangle" aria-hidden="true"></i></div>
	          <div class="white-text center-align" style="text-transform:uppercase;">Ops, algo deu errado.</div>
	          <c:if test="${not empty errorMsg}">
	            <div class="white-text center-align"><c:out value="${errorMsg}"/></div>
	          </c:if>
	          <div class="right-align">
	            <a class="white-text" href="${pageContext.request.contextPath}/" style="text-transform:uppercase;">
	              <i class="fa fa-angle-double-left" aria-hidden="true"></i> Voltar
	            </a>
	          </div>
	        </div>
	      </div>
	    </div>
    </div>
<t:footer></t:footer>
