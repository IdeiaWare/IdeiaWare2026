/*
 * CSRF-02 (client): le o cookie XSRF-TOKEN e
 *  (a) injeta <input hidden name="csrfToken"> em TODOS os forms da pagina;
 *  (b) manda o header X-CSRF-Token em todo XHR POST (intercepta XMLHttpRequest
 *      direto -> independe de jQuery e da ordem de carregamento dos scripts).
 * Assim o servidor (CsrfFilter) valida POST por token sem editar cada form/AJAX.
 */
(function () {
	function getCookie(name) {
		var m = document.cookie.match(new RegExp('(?:^|;\\s*)' + name + '=([^;]+)'));
		return m ? decodeURIComponent(m[1]) : '';
	}

	var token = getCookie('XSRF-TOKEN');
	if (!token) { return; }

	// (a) garante o campo escondido csrfToken num form (vanilla, sem jQuery)
	function ensureToken(form) {
		if (form && form.tagName === 'FORM' && !form.querySelector('input[name="csrfToken"]')) {
			var inp = document.createElement('input');
			inp.type = 'hidden';
			inp.name = 'csrfToken';
			inp.value = token;
			form.appendChild(inp);
		}
	}
	function injectForms() {
		var forms = document.getElementsByTagName('form');
		for (var i = 0; i < forms.length; i++) {
			ensureToken(forms[i]);
		}
	}
	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', injectForms);
	} else {
		injectForms();
	}

	// (a2) FIX 2026-06-23: forms criados DINAMICAMENTE via JS (ex.: gerenciamento-ideia.jsp
	// monta o form 'entrarStory' com html+=... depois do DOMContentLoaded) nao sao pegos
	// pela varredura acima -> POST sem token -> 403. Este listener injeta o token no
	// instante do submit (o campo entra na serializacao), cobrindo qualquer form dinamico.
	document.addEventListener('submit', function (e) {
		ensureToken(e.target);
	}, true);

	// (b) header em todo POST via XHR (jQuery.ajax usa XHR por baixo -> coberto)
	if (window.XMLHttpRequest) {
		var open = XMLHttpRequest.prototype.open;
		XMLHttpRequest.prototype.open = function (method) {
			this.__csrfPost = method && String(method).toUpperCase() === 'POST';
			return open.apply(this, arguments);
		};
		var send = XMLHttpRequest.prototype.send;
		XMLHttpRequest.prototype.send = function () {
			if (this.__csrfPost) {
				try { this.setRequestHeader('X-CSRF-Token', token); } catch (e) { /* ja enviado */ }
			}
			return send.apply(this, arguments);
		};
	}
})();
