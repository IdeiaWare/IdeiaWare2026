<%@ tag pageEncoding="UTF-8" %>
				</div>
			</div>
		</main>
		
		<footer class="center red darken-3 page-footer">
		    <div class="container">
		      <div class="row">
		        <i class="small material-icons">account_circle</i><h6 class="white-text usuario"></h6>
		      </div>
		    </div>
		    <%-- UX-FOOTER-2TONS-V4: meio-termo entre "sem contraste" e "contraste forte demais" das tentativas anteriores. --%>
		    <div class="footer-copyright" style="background-color:#a52724;">
		      <div class="container">
		        © 2026 IdeiaWare UNISC
		      </div>
		    </div>
	  	</footer>
		<script src="${pageContext.request.contextPath}/resources/materialize/js/materialize.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/corejs-typeahead.bundle.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/materialize-tags/js/materialize-tags.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/masonry.pkgd.min.js"></script>
		<%-- TK-44: cache-busting (?v=timestamp) -- este tag nao tinha, reabria o bug do footer.tag. --%>
		<script src="${pageContext.request.contextPath}/resources/js/html2canvas.min.js?v=<%= System.currentTimeMillis() %>"></script>
		<script src="${pageContext.request.contextPath}/resources/js/jspdf.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/base64.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/sprintf.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/enjoyhint.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/custom.tour.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/custom.js?v=<%= System.currentTimeMillis() %>"></script>
	</body>
</html>