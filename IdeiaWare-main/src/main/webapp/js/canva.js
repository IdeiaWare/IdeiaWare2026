$( document ).ready(function() {
	if($('.modal').length > 0){
		$('.modal').modal()
	}

	// UX-CANVA-ENVIAR-TRAVADO: habilita conforme o TEXTO (minlength=5), nao so' o clique na
	// cor -- o azul ja vem pre-selecionado por padrao, entao digitar e mandar sem tocar na
	// cor (comportamento natural) deixava o botao travado pra sempre.
	function atualizarBotaoEnviar() {
		var texto = $("#descricao").val() || "";
		$("#enviar").prop('disabled', texto.trim().length < 5);
	}

	$("#descricao").on('input', atualizarBotaoEnviar);

	$('.editCanva').on("click", function(event){
		event.preventDefault();
		// TK-21: .val() em vez de acesso não-padrão a .value.
		$("#idCanva").val($(this).attr("canva-id"));
		// TK-45: .text() em vez de .innerHTML, evita re-escapar "&".
		$("#descricao")[0].value = $(this).closest('.card').find('.card-content').text();
		atualizarBotaoEnviar();
	});

	$(".post-it .btn").click(function(){
		// TK-21: mesmo fix de .val() acima.
		$("#color-input").val($(this).attr("data-color"));
		$(".post-it .btn").removeClass("active");
		$(this).addClass("active");
	});

	atualizarBotaoEnviar();
});
