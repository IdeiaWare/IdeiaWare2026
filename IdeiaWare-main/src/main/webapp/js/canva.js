$( document ).ready(function() {
	/*Iterando modal*/
	if($('.modal').length > 0){
		$('.modal').modal()
	}
	
	/*Listener para os botões de edição de post-it*/
	$('.editCanva').on("click", function(event){
		event.preventDefault();
		// REVISAO 2026-07-08 (varredura JS, achado BAIXA): .attributes.value.nodeValue
		// e uma forma nao-padrao/fragil de setar um atributo (funciona por acidente,
		// via referencia direta ao objeto Attr) -- trocado por .val(), a API do jQuery
		// pra isso, ja usado em todo o resto do projeto.
		$("#idCanva").val($(this).attr("canva-id"));
		// REVISAO 2026-07-08 (varredura JS, achado BAIXA): .parent().parent().children()[0]
		// e uma travessia posicional rigida (quebra se a ordem/estrutura do card mudar);
		// trocado por .closest('.card').find('.card-content'), que localiza o alvo pela
		// classe real.
		// REVISAO 2026-07-08 (varredura JS, achado MEDIA): .innerHTML SERIALIZA o DOM de
		// volta pra HTML (re-escapa "&" -> "&amp;"), entao um post-it com "Custo & Receita"
		// (renderizado pelo <c:out> do JSP) devolvia "Custo &amp; Receita" pro textarea de
		// edicao -- se o usuario nao notasse e reenviasse, o texto salvo ficava com a
		// entidade escapada, piorando a cada edicao (dupla, tripla escapada). .text() le o
		// textContent (ja DECODIFICADO), igual ao texto original digitado pelo usuario.
		$("#descricao")[0].value = $(this).closest('.card').find('.card-content').text();
	});

	/*Listener para os botões de coloração de post-it*/
	$(".post-it .btn").click(function(){
		// REVISAO 2026-07-08 (varredura JS, achado BAIXA): mesmo fix de .val() acima.
		$("#color-input").val($(this).attr("data-color"));
		$(".post-it .btn").removeClass("active");
		$(this).addClass("active")			
		$("#enviar").prop('disabled', false);
	});
});
