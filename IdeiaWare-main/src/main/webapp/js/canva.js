$( document ).ready(function() {
	/*Iterando modal*/
	if($('.modal').length > 0){
		$('.modal').modal()
	}
	
	/*Listener para os botões de edição de post-it*/
	$('.editCanva').on("click", function(event){
		event.preventDefault();
		// TK-21: .val() em vez de .attributes.value.nodeValue (forma nao-padrao/fragil).
		$("#idCanva").val($(this).attr("canva-id"));
		// TK-45: .closest('.card').find('.card-content') em vez de travessia posicional rigida.
		// TK-45: .text() em vez de .innerHTML -- innerHTML re-escapa "&" -> "&amp;" a cada edicao.
		$("#descricao")[0].value = $(this).closest('.card').find('.card-content').text();
	});

	/*Listener para os botões de coloração de post-it*/
	$(".post-it .btn").click(function(){
		// TK-21: mesmo fix de .val() acima.
		$("#color-input").val($(this).attr("data-color"));
		$(".post-it .btn").removeClass("active");
		$(this).addClass("active")			
		$("#enviar").prop('disabled', false);
	});
});
