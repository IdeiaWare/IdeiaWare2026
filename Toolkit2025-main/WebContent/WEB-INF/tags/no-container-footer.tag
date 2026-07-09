<%@ tag pageEncoding="UTF-8" %>
				</div>
			</div>
		</div>
		
		<footer class="center red darken-1 page-footer">
		    <div class="container">
		      <div class="row">
		        <i class="small material-icons">account_circle</i><h6 class="white-text usuario"></h6>
		      </div>
		    </div>
		    <div class="footer-copyright">
		      <div class="container">
		        © 2026 IdeiaWare UNISC
		      </div>
		    </div>
	  	</footer>
		<script src="${pageContext.request.contextPath}/resources/materialize/js/materialize.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/corejs-typeahead.bundle.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/materialize-tags/js/materialize-tags.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/masonry.pkgd.min.js"></script>
		<%-- REVISAO 2026-07-08 (varredura Toolkit, achado MEDIA): este tag NAO tinha o
		     cache-busting que footer.tag ja tem pros mesmos 2 arquivos -- reabria o
		     mesmo bug que o comentario de footer.tag diz ter corrigido (navegador
		     servindo html2canvas.min.js/custom.js velho do cache), e este tag e usado
		     justamente pelas paginas de export/overview, onde esse JS foi reescrito
		     mais recentemente. --%>
		<script src="${pageContext.request.contextPath}/resources/js/html2canvas.min.js?v=<%= System.currentTimeMillis() %>"></script>
		<script src="${pageContext.request.contextPath}/resources/js/jspdf.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/base64.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/sprintf.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/enjoyhint.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/custom.tour.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/custom.js?v=<%= System.currentTimeMillis() %>"></script>
	</body>
</html>