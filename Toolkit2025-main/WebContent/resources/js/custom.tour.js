(function($, global) {
	'use strict'

	global.Toolkit = global.Toolkit || {};
	
	Toolkit.startTourApp = function(){
		var enjoyhint_instance = new EnjoyHint({
			onSkip: function(){
				Toolkit.createCookie("skipedTour", true, 1)
			}
		});
		
		if(!Toolkit.readCookie("skipedTour")){
			var enjoyhint_script_steps = []
			var index = Toolkit.readCookie("tourStep")
		
			if (index == 1)
				enjoyhint_script_steps = Toolkit.secondStepPart()
			else if (index == 2)
				enjoyhint_script_steps = Toolkit.thirdStepPart()
			else if (index == 3)
				enjoyhint_script_steps = Toolkit.fourthStepPart()
			else if (index == 4)
				enjoyhint_script_steps = Toolkit.fifthStepPart()
			else if (index == 5)
				enjoyhint_script_steps = Toolkit.sixthStepPart()
			else if (index == 6)
				enjoyhint_script_steps = Toolkit.seventhStepPart()
			else if (index == 7)
				enjoyhint_script_steps = Toolkit.eighthStepPart()
			else
				enjoyhint_script_steps = Toolkit.firstStepPart()
			
			enjoyhint_instance.set(enjoyhint_script_steps);
			enjoyhint_instance.run();
		}
	}
	
	Toolkit.firstStepPart = function(enjoyhint_instance){
		var enjoyhint_script_steps = [
			{	"next .brand-logo" : 'Bem vindo(a)! Vamos come&ccedil;ar?',
				"nextButton" : {text: 'Seguinte'},
				"skipButton" : {text: "Encerrar"}
	  		},
	  		{	'click .criar-persona' : 'Clique para criar uma persona',
	  			'showSkip': false,
	  			onBeforeStart:function(){
	  				$('.fixed-action-btn').openFAB();
	  			}
		  	},
	  		{	'next .name-input' : 'D&ecirc; um "Nome" a persona',
		  		"nextButton" : {text: "Seguinte"},
		  		'showSkip': false
	  		},
		  	{	'next .age-input' : 'Defina a "Idade" da persona',
	  			"nextButton" : {text: "Seguinte"},
	  			'showSkip': false
		  	},		  	
		  	{	'click .btn.adicionar' : 'Clique em "ADICIONAR" para obter sua nova persona',
	  			'showSkip': false,
	  			onBeforeStart:function(){
	  				Toolkit.createCookie("tourStep", 1, 1)
	  			}
		  	}
		];
		
		return enjoyhint_script_steps;
	}
	
	Toolkit.secondStepPart = function(){
		var enjoyhint_script_steps = [
			{	"next .personas-list" : 'Veja aqui a listagem de suas personas criadas',
				"nextButton" : {text: "Seguinte"},
				'showSkip': false
	  		},
	  		{	"click .define-persona" : 'Clique no NOME da Persona para definir os atributos dela',
				'showSkip': false,
				onBeforeStart:function(){
	  				Toolkit.createCookie("tourStep", 2, 1)
	  			}
	  		}
		];
		
		Toolkit.eraseCookie("tourStep");
		
		return enjoyhint_script_steps;
	}
	
	Toolkit.thirdStepPart = function(){
		var enjoyhint_script_steps = [
			{	"click .first" : 'Voc&ecirc; pode selecionar quaisquer dos quadrantes que desejar, mas por enquanto vamos come&ccedil;ar por este Ok? Clique no quadrante destacado para definir sua persona',
				'showSkip': false,
				onBeforeStart:function(){
	  				Toolkit.createCookie("tourStep", 3, 1)
	  			}
	  		}
		];
		
		Toolkit.eraseCookie("tourStep");
		
		return enjoyhint_script_steps;
	}
	
	Toolkit.fourthStepPart = function(){
		var enjoyhint_script_steps = [
			{	"next #attribute-text" : 'Adicione aqui a descri&ccedil;&atilde;o de alguma informa&ccedil;&atilde;o de sua persona',
				"nextButton" : {text: "Seguinte"},
				'showSkip': false
	  		},
	  		{	"next .post-it" : 'Agora selecione a cor do seu cart&atilde;o/post-it caso deseje clusterizar',
	  			"nextButton" : {text: "Seguinte"},
	  			'showSkip': false
	  		},
	  		{	"click .add-attribute" : 'Clique agora em "Adicionar" para cria-lo',
				'showSkip': false,
				onBeforeStart:function(){
	  				Toolkit.createCookie("tourStep", 4, 1)
	  			}
	  		}
		];
		
		Toolkit.eraseCookie("tourStep");
		
		return enjoyhint_script_steps;
	}
	
	Toolkit.fifthStepPart = function(){
		var enjoyhint_script_steps = [
			{	"next .brand-logo" : 'Depois de finalizar a cria&ccedil;&atilde;o dos post-its em todas os quadrantes que julgar necess&aacute;rio(a) sua persona estar&aacute; completa',
				"nextButton" : {text: "Seguinte"},
				'showSkip': false
	  		},
	  		{	"click .breadcrumb:first-child" : 'Clique aqui agora para retornarmos a p&aacute;gina inicial e ent&atilde;o iniciarmos a cria&ccedil;&atilde;o do Point of View de sua Persona',
				'showSkip': false,
				onBeforeStart:function(){
	  				Toolkit.createCookie("tourStep", 5, 1)
	  			}
	  		}
			
		];
		
		Toolkit.eraseCookie("tourStep");
		
		return enjoyhint_script_steps;
	}
	
	Toolkit.sixthStepPart = function(){
		var enjoyhint_script_steps = [
			{	"click td:first-child" : 'Marque aqui qual persona deseja criar o point of view. Voc&ecirc; pode selecionar uma ou mais personas conforme preferir',
				'showSkip': false
	  		},
	  		{	"click .criar-pov" : 'Clique aqui para dar inicio a cria&ccedil;&atilde;o do point of view',
				'showSkip': false,
				onBeforeStart:function(){
	  				$('.fixed-action-btn').openFAB();
	  				Toolkit.createCookie("tourStep", 6, 1)
	  			}
	  		}
			
		];
		
		Toolkit.eraseCookie("tourStep");
		
		return enjoyhint_script_steps;
	}
	
	Toolkit.seventhStepPart = function(){
		var enjoyhint_script_steps = [
			{	"next .user-tour" : 'Descreva aqui o(s) seu(s) usu&aacute;rio(s)',
				"nextButton" : {text: "Seguinte"},
				'showSkip': false
	  		},
	  		{	"next .need-tour" : 'Descreva aqui a(s) necessidade(s) do(s) seu(s) usu&aacute;rio(s)',
	  			"nextButton" : {text: "Seguinte"},
	  			'showSkip': false
	  		},
	  		{	"next .insight-tour" : 'Descreva aqui a(s) introspec&ccedil;&atilde;o(&otilde;es) do(s) seu(s) usu&aacute;rio(s)',
	  			"nextButton" : {text: "Seguinte"},
	  			'showSkip': false
	  		},
	  		{	"click .add-pov" : 'Clique em "Adicionar" para finalizar a cria&ccedil;&atilde;o do Point of View',
				'showSkip': false,
				onBeforeStart:function(){
	  				Toolkit.createCookie("tourStep", 7, 1)
	  			}
	  		}
			
		];
		
		Toolkit.eraseCookie("tourStep");
		
		return enjoyhint_script_steps;
	}
	
	Toolkit.eighthStepPart = function(){
		var enjoyhint_script_steps = [
			{	"next .pov-list" : 'Veja aqui todos os seus point of view criados',
				"nextButton" : {text: "Seguinte"},
				'showSkip': false
	  		},
	  		{	"next .pov-info" : 'Para ter a vis&atilde;o geral sobre o item, clique sobre ele',
	  			"showNext" : false,
	  			"skipButton" : {text: "FIM!"}
	  		}			
		];
		
		Toolkit.eraseCookie("tourStep");
		
		return enjoyhint_script_steps;
	}
	
	Toolkit.callTour = function(){
		Toolkit.eraseCookie("tourStep");
		Toolkit.eraseCookie("skipedTour");
		
		Toolkit.startTourApp();
	}
	
	Toolkit.createCookie = function(name, value, days) {
	    var expires;

	    if (days) {
	        var date = new Date();
	        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
	        expires = "; expires=" + date.toGMTString();
	    } else {
	        expires = "";
	    }
	    document.cookie = encodeURIComponent(name) + "=" + encodeURIComponent(value) + expires + "; path=/";
	}
	
	Toolkit.readCookie = function(name) {
        var nameEQ = name + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') c = c.substring(1, c.length);
            if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
        }
        return null;
    }

	Toolkit.eraseCookie = function(name) {
		Toolkit.createCookie(name, "", -1);
    }
	
})(jQuery, window)

$(document).ready(function(){
	if($("#no-authenticaded").length <= 0)
		Toolkit.startTourApp()
});