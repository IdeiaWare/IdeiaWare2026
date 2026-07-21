<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<%-- UX-PADRAO-ETAPA-FINALIZADA: ver AvisoController. --%>
<t:header></t:header>
	<div id="aviso-etapa-encerrada">
		<div class="row">
			<div class="col s12 m8 offset-m2">
				<div class="card-panel red darken-1" style="margin-top: 40px;">
					<div class="white-text center-align" style="text-transform:uppercase;"><c:out value="${etapaEncerradaErro}"/></div>
				</div>
			</div>
		</div>
	</div>
	<script>
		alert('<c:out value="${etapaEncerradaErro}"/>');
		window.location.href = '<c:out value="${redirecionarPara}"/>';
	</script>
	<noscript>
		<div class="row">
			<div class="col s12 center-align">
				<a href="${redirecionarPara}">Continuar</a>
			</div>
		</div>
	</noscript>
<t:footer></t:footer>
