<%@ tag pageEncoding="UTF-8" %>
					</div>
				</div>
			</div>
		</main>
		
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
		<%-- EXPORT-CORTADO: cache-busting (?v=timestamp) -- navegador servia lib velha do cache. --%>
		<script src="${pageContext.request.contextPath}/resources/js/html2canvas.min.js?v=<%= System.currentTimeMillis() %>"></script>
		<script src="${pageContext.request.contextPath}/resources/js/jspdf.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/enjoyhint.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/custom.tour.js"></script>
		<%-- TK-44: cache-busting no custom.js tambem (mesmo motivo). --%>
		<script src="${pageContext.request.contextPath}/resources/js/custom.js?v=<%= System.currentTimeMillis() %>"></script>
	</body>
</html>