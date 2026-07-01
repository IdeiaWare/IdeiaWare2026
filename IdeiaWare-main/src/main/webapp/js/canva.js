$( document ).ready(function() {
	/*Iterando modal*/
	if($('.modal').length > 0){
		$('.modal').modal()
	}
	
	/*Listener para os botões de edição de post-it*/
	$('.editCanva').on("click", function(event){
		event.preventDefault();
		$("#idCanva")[0].attributes.value.nodeValue = $(this).attr("canva-id")
		$("#descricao")[0].value = $(this).parent().parent().children()[0].innerHTML
	});
	
	/*Listener para os botões de coloração de post-it*/
	$(".post-it .btn").click(function(){
		$("#color-input")[0].attributes.value.nodeValue = $(this).attr("data-color")
		$(".post-it .btn").removeClass("active");
		$(this).addClass("active")			
		$("#enviar").prop('disabled', false);
	});
});
