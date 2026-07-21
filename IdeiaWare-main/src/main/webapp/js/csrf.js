// CSRF-02: injeta csrfToken em forms e header X-CSRF-Token em XHR POST.
(function () {
	function getCookie(name) {
		var m = document.cookie.match(new RegExp('(?:^|;\\s*)' + name + '=([^;]+)'));
		return m ? decodeURIComponent(m[1]) : '';
	}

	var token = getCookie('XSRF-TOKEN');
	if (!token) { return; }

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

	// CSRF-DYN: injeta token no submit, cobre forms montados dinamicamente.
	document.addEventListener('submit', function (e) {
		ensureToken(e.target);
	}, true);

	if (window.XMLHttpRequest) {
		var open = XMLHttpRequest.prototype.open;
		XMLHttpRequest.prototype.open = function (method) {
			this.__csrfPost = method && String(method).toUpperCase() === 'POST';
			return open.apply(this, arguments);
		};
		var send = XMLHttpRequest.prototype.send;
		XMLHttpRequest.prototype.send = function () {
			if (this.__csrfPost) {
				try { this.setRequestHeader('X-CSRF-Token', token); } catch (e) {}
			}
			return send.apply(this, arguments);
		};
	}
})();
