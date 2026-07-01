<%@ tag pageEncoding="UTF-8" %>
					</div>
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
	        © 2017 IdeiaWare UNISC
	      </div>
	    </div>
	  </footer>
		<script src="${pageContext.request.contextPath}/resources/materialize/js/materialize.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/corejs-typeahead.bundle.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/materialize-tags/js/materialize-tags.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/masonry.pkgd.min.js"></script>
		<%-- cache-busting: a lib foi trocada (1.2.4 -> 0.5.0-beta3) e o navegador
		     serviria a antiga do cache. O timestamp forca o download da nova. --%>
		<script src="${pageContext.request.contextPath}/resources/js/html2canvas.min.js?v=<%= System.currentTimeMillis() %>"></script>
		<script src="${pageContext.request.contextPath}/resources/js/jspdf.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/enjoyhint.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/custom.tour.js"></script>
		<%-- cache-busting: o navegador estava servindo o custom.js do CACHE, ignorando
		     os deploys novos ("altero e nao muda"). O timestamp forca o fetch da versao
		     atual a cada carga. --%>
		<script src="${pageContext.request.contextPath}/resources/js/custom.js?v=<%= System.currentTimeMillis() %>"></script>
	</body>
</html>