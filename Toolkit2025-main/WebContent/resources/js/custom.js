(function($, global) {
	'use strict'

	global.Toolkit = global.Toolkit || {};
	
	Toolkit.Persona = Toolkit.Persona || {};
	Toolkit.EmpathyAttribute = Toolkit.EmpathyAttribute || {};
	Toolkit.PointOfView = Toolkit.PointOfView || {};
	Toolkit.Validation = Toolkit.Validation || {};

	Toolkit.Persona.buildPersonaModal = function(){
		$(".persona-modal").modal("open");
		$('.persona-modal .adicionar input').val("Adicionar");
		
		$(".persona-modal #persona-id").val(0);
		$(".persona-modal #name").val("");
		$(".persona-modal #age").val(0);
	}
	
	Toolkit.Persona.buildPersonaEditModal = function(personaId, name, age){
		$(".persona-modal").modal("open");
		$('.persona-modal .adicionar input').val("Atualizar");
		
		$(".persona-modal #persona-id").val(personaId);
		$(".persona-modal #name").val(name);
		$(".persona-modal #age").val(age);
		
		Materialize.updateTextFields();
	}	
	
	// BUG-POV-VAZIO: contava <tr> direto, mas a linha do estado vazio tambem e um <tr> --
	// checkbox so existe nas linhas reais, e um indicador confiavel.
	Toolkit.Persona.thereIsPersonaCreated = function(){
		return $(".personas-list .chkPersona").length > 0;
	}
	
	Toolkit.Persona.thereIsPersonaSelected = function(){
		var isSelected = false;
		
		$(".personas-list table tbody tr").each(function(){		
			if($(this).find(".chkPersona").is(":checked")){
				isSelected = true;
			}
		})
		
		return isSelected;
	}
	
	Toolkit.Persona.selectColorPicker = function(){
		// TK-A11Y-COR: post-its de cor sao <div>, so respondiam a mouse -- keydown (Enter/Espaco) adicionado.
		function selecionarCor(el){
			$(".card-color").val($(el).attr("data-color"))
			$(".post-it .btn").removeClass("active");
			$(el).addClass("active")
		}
		$(".post-it .btn").click(function(){
			selecionarCor(this);
		})
		$(".post-it .btn").on("keydown", function(e){
			if (e.key === "Enter" || e.key === " " || e.keyCode === 13 || e.keyCode === 32) {
				e.preventDefault();
				selecionarCor(this);
			}
		})
	}
	
	Toolkit.Persona.setSameHeight = function(section){
		 var highestBox = 0;
	        $(section).each(function(){  
	                if($(this).height() > highestBox){  
	                highestBox = $(this).height();  
	        }
	    });    
	    $(section).height(highestBox);
	}
	
	
	Toolkit.Persona.exportFile = function(areaClass, forceOrient){
		// TK-08/09/10/14/17: usa "onrendered" (API de callback do html2canvas 0.5.0-beta3, nao Promise .then).
		$("#overlay").attr('style','display: block !important');

		var section = $(areaClass)[0];
		if (!section) {
			$("#overlay").attr('style','display: none !important');
			return;
		}

		// TK-08/09/10/14/17/CAN-13: mesma abordagem do Canvas do LIC -- scrollTo(0,0) + width/height=scrollWidth/scrollHeight.
		window.scrollTo(0, 0);
		var fullWidth  = section.scrollWidth;
		var fullHeight = section.scrollHeight;

		html2canvas(section, {
			background: '#ffffff',
			width:  fullWidth,
			height: fullHeight,
			windowWidth:  fullWidth,
			windowHeight: fullHeight,
			onrendered: function (canvas) {
				// TK-10: pinta fundo branco atras do screenshot (export e JPEG sem alpha, transparente vira preto).
				var whiteCanvas = document.createElement('canvas');
				whiteCanvas.width  = canvas.width;
				whiteCanvas.height = canvas.height;
				var wctx = whiteCanvas.getContext('2d');
				wctx.fillStyle = '#ffffff';
				wctx.fillRect(0, 0, whiteCanvas.width, whiteCanvas.height);
				wctx.drawImage(canvas, 0, 0);
				canvas = whiteCanvas;

				var contentWidth  = canvas.width;
				var contentHeight = canvas.height;

				var pageData = canvas.toDataURL('image/jpeg', 1.0);

				// TK-17: folha A4 padrao com margem, imagem centralizada; formato 'a4' nomeado evita o jsPDF girar a pagina.
				var orient = forceOrient || ((contentWidth >= contentHeight) ? 'l' : 'p');
				var pdf = new jsPDF(orient, 'pt', 'a4');
				var pageW = (orient === 'l') ? 841.89 : 595.28; // A4 em pt
				var pageH = (orient === 'l') ? 595.28 : 841.89;
				var margin = 28; // ~1cm

				// TK-10: pinta a folha A4 inteira de branco antes da imagem (senao ficava transparente/preta ao abrir).
				pdf.setFillColor(255, 255, 255);
				pdf.rect(0, 0, pageW, pageH, 'F');
				var scale = Math.min((pageW - margin * 2) / contentWidth,
				                     (pageH - margin * 2) / contentHeight);
				var drawW = contentWidth * scale;
				var drawH = contentHeight * scale;
				var x = (pageW - drawW) / 2; // centra na horizontal
				var y = margin;              // alinha ao TOPO (cara de documento; conteudo
				                             // curto como a tabela POV nao "flutua" no meio)
				pdf.addImage(pageData, 'JPEG', x, y, drawW, drawH);

				var blob = pdf.output("blob");
				var reader = new FileReader();
				reader.readAsDataURL(blob);
				// TK-PDF-ERR: onloadend dispara em sucesso OU falha -- checa reader.error antes de usar reader.result.
				reader.onloadend = function () {
					if (reader.error) {
						console.error('Falha ao ler o PDF gerado:', reader.error);
						$("#overlay").attr('style','display: none !important');
						alert('Não foi possível gerar o PDF. Tente novamente.');
						return;
					}
					$("#file-location").val(reader.result);
					$("#overlay").attr('style','display: none !important');
					$("form#overview, form#detailed").submit();
				};
			}
		}).catch(function (err) {
			// TK-PDF-ERR: catch() esconde o overlay em falha do html2canvas (antes ficava visivel pra sempre).
			console.error('Falha ao gerar o PDF:', err);
			$("#overlay").attr('style','display: none !important');
			alert('Não foi possível gerar o PDF. Tente novamente.');
		});
	}
	
	// EXP-EMP: grade de post-its vetorial no PDF (antes screenshot html2canvas, cortava no Ctrl+P).
	Toolkit.Persona.exportEmpathyMap = function() {
		$("#overlay").attr('style', 'display: block !important');

		var name = $('.persona-pre-data .col').eq(0).find('span').text().trim();
		var age  = $('.persona-pre-data .col').eq(1).find('span').text().trim();

		var sections = [];
		$('.persona-overview .section').each(function() {
			var title = $(this).find('h6').first().text().replace(/\s+/g, ' ').trim();
			var cards = [];
			$(this).find('.card').each(function() {
				cards.push({
					text: $(this).find('.card-content').text().replace(/\s+/g, ' ').trim(),
					bg: $(this).css('background-color')
				});
			});
			sections.push({ title: title, cards: cards });
		});

		var pdf = new jsPDF('p', 'pt', 'a4');
		var pageW = 595.28, pageH = 841.89, margin = 30;
		var colGap = 12;
		var colW = (pageW - margin * 2 - colGap) / 2;
		var titleH = 16, innerPad = 6, cardPad = 5, cardGap = 5, lineH = 10, font = 8;
		var cardW = colW - innerPad * 2;

		function parseRGB(s) {
			var m = /(\d+)\D+(\d+)\D+(\d+)/.exec(s || '');
			return m ? [+m[1], +m[2], +m[3]] : [245, 245, 245];
		}
		function darkText(rgb) { // luminancia -> escolhe texto preto ou branco
			return (0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2]) > 140;
		}
		function sectionHeight(sec) {
			pdf.setFontSize(font);
			var h = titleH + innerPad;
			if (sec.cards.length === 0) { h += lineH + cardGap; }
			for (var i = 0; i < sec.cards.length; i++) {
				var n = pdf.splitTextToSize(sec.cards[i].text, cardW - cardPad * 2).length;
				h += n * lineH + cardPad * 2 + cardGap;
			}
			return h + innerPad;
		}
		function drawSection(sec, x, y, boxH) {
			pdf.setDrawColor(33); pdf.setLineWidth(0.7);
			pdf.setFillColor(250, 250, 250);
			pdf.rect(x, y, colW, boxH, 'FD');
			pdf.setFontSize(9); pdf.setTextColor(33, 33, 33);
			var tw = pdf.getStringUnitWidth(sec.title) * 9;
			pdf.text(sec.title, x + (colW - tw) / 2, y + 11);
			pdf.setFontSize(font);
			var cy = y + titleH + innerPad;
			for (var i = 0; i < sec.cards.length; i++) {
				var rgb = parseRGB(sec.cards[i].bg);
				var lines = pdf.splitTextToSize(sec.cards[i].text, cardW - cardPad * 2);
				var ch = lines.length * lineH + cardPad * 2;
				pdf.setFillColor(rgb[0], rgb[1], rgb[2]);
				pdf.rect(x + innerPad, cy, cardW, ch, 'F');
				if (darkText(rgb)) { pdf.setTextColor(33, 33, 33); } else { pdf.setTextColor(255, 255, 255); }
				for (var li = 0; li < lines.length; li++) {
					pdf.text(lines[li], x + innerPad + cardPad, cy + cardPad + lineH * (li + 1) - 2);
				}
				cy += ch + cardGap;
			}
			pdf.setTextColor(33, 33, 33);
		}

		pdf.setFontSize(14); pdf.setTextColor(33, 33, 33);
		pdf.text('Mapa de Empatia - Visão Geral', margin, margin + 4);
		pdf.setFontSize(10);
		pdf.text('Nome: ' + name + '          Idade: ' + age, margin, margin + 22);

		var rows = [[0, 1], [2, 3], [4, 5]];
		var y = margin + 36;
		for (var r = 0; r < rows.length; r++) {
			var left = sections[rows[r][0]], right = sections[rows[r][1]];
			if (!left && !right) { continue; }
			var boxH = Math.max(left ? sectionHeight(left) : 0, right ? sectionHeight(right) : 0);
			if (y + boxH > pageH - margin) { pdf.addPage(); y = margin; }
			if (left)  { drawSection(left,  margin, y, boxH); }
			if (right) { drawSection(right, margin + colW + colGap, y, boxH); }
			y += boxH + colGap;
		}

		var blob = pdf.output('blob');
		var reader = new FileReader();
		reader.readAsDataURL(blob);
		reader.onloadend = function() {
			$("#file-location").val(reader.result);
			$("#overlay").attr('style', 'display: none !important');
			$("form#overview, form#detailed").submit();
		};
	};

	// EXP-POV: tabela nativa vetorial no PDF (antes screenshot cortava a borda direita no Ctrl+P).
	Toolkit.PointOfView.exportTable = function() {
		$("#overlay").attr('style', 'display: block !important');

		var headers = ['Persona(s)', 'Usuário(s)', 'Necessidade(s)', 'Introspecção(ões)'];
		var rows = [];
		$('.pov-all-data-overview tbody tr').each(function() {
			var r = [];
			$(this).find('td').each(function() {
				r.push($(this).text().replace(/\s+/g, ' ').trim());
			});
			if (r.length) { rows.push(r); }
		});

		var pdf = new jsPDF('p', 'pt', 'a4');
		var pageW = 595.28, pageH = 841.89, margin = 40;
		var nCols = headers.length;
		var colW = (pageW - margin * 2) / nCols;
		var pad = 5, lineH = 11;

		pdf.setDrawColor(117); // #757575
		pdf.setLineWidth(0.5);

		function measure(cells, font) {
			pdf.setFontSize(font);
			var out = [], max = 1;
			for (var c = 0; c < nCols; c++) {
				var t = pdf.splitTextToSize(cells[c] == null ? '' : String(cells[c]), colW - pad * 2);
				out.push(t);
				if (t.length > max) { max = t.length; }
			}
			return { lines: out, height: max * lineH + pad * 2 };
		}

		function drawRow(cells, y, font, isHeader) {
			var m = measure(cells, font);
			if (!isHeader && y + m.height > pageH - margin) {
				pdf.addPage();
				y = drawRow(headers, margin, 10, true); // repete cabecalho
				m = measure(cells, font);
			}
			pdf.setFontSize(font);
			for (var c = 0; c < nCols; c++) {
				var x = margin + c * colW;
				if (isHeader) {
					pdf.setFillColor(235, 235, 235);
					pdf.rect(x, y, colW, m.height, 'FD');
				} else {
					pdf.rect(x, y, colW, m.height);
				}
				for (var li = 0; li < m.lines[c].length; li++) {
					pdf.text(m.lines[c][li], x + pad, y + pad + lineH * (li + 1) - 2);
				}
			}
			return y + m.height;
		}

		var y = drawRow(headers, margin, 10, true);
		for (var i = 0; i < rows.length; i++) {
			y = drawRow(rows[i], y, 9, false);
		}

		var blob = pdf.output('blob');
		var reader = new FileReader();
		reader.readAsDataURL(blob);
		reader.onloadend = function() {
			$("#file-location").val(reader.result);
			$("#overlay").attr('style', 'display: none !important');
			$("form#overview, form#detailed").submit();
		};
	};

	Toolkit.EmpathyAttribute.editAttribute = function(id, attributeText, postItColor){
		$(".attribute form .attribute-id").val(id);
		
		$(".attribute form textarea").val(attributeText);
		
		$(".post-it .btn").removeClass("active")
		$(".post-it .btn").each(function(){
			if($(this).hasClass(postItColor)){
				$(this).addClass("active")
				$(".card-color").val(postItColor)
			}
		})
		
		// TK-12: seletor era ".btn input" (nao casava nada); botao e <input class="btn">, nao <div><input>.
		$(".attribute form input.btn").val("Atualizar");
		$(".attribute form .btn-cancelar").closest("div").show();
		
		Materialize.updateTextFields();
	}

	Toolkit.EmpathyAttribute.cancelEditAttribute = function(){
		$(".attribute form .btn-cancelar").click(function(){
			$(".attribute form input.btn").val("Adicionar"); // TK-12: ver acima
			$(".attribute form .btn-cancelar").closest("div").hide();
			$(".attribute form textarea").val("");
			$('.attribute form textarea').blur();
			$(".attribute form .attribute-id").val(0);
			$(".attribute form .post-it .btn").removeClass("active");
			$(".attribute form .card-color").val(0);
		})
	}
	
	Toolkit.EmpathyAttribute.buildCardViewOnModal = function(){
		$(".persona-overview .card").click(function(){
			var info = $(this).find(".card-content").text().trim();
			var color = $(this).attr("class");

			// TK-19: .empty()+.append().text() em vez de .html() (evita re-injetar entidades decodificadas como HTML, XSS).
			$(".modal-card-info .card-content").empty().append($("<p>").text(info));
			$(".modal-card-info .card-content").css("word-break", "break-all")
			$(".modal-card-info .card").attr("class", color);

			$('.modal-card-info').modal('open');
		})
	}
	
	Toolkit.PointOfView.setPersonasIdInHiddenField = function(){
		var ids = [];
		
		$(".persona-data").each(function(){
			ids.push($(this).attr("data-id"));
		})
		
		$(".ids").val(ids);
	}
	
	Toolkit.PointOfView.buildPOVInfoViewOnModal = function(){
		$(".pov-info").click(function(){
			var names = $(this).closest("tr").find("td:first-child .pov-info").text().trim();
			var user = $(this).closest("tr").find("td:nth-child(2) .pov-info").text().trim();
			var need = $(this).closest("tr").find("td:nth-child(3) .pov-info").text().trim();
			var insight = $(this).closest("tr").find("td:nth-child(4) .pov-info").text().trim();

			// TK-20: mesmo padrao do TK-19 (buildCardViewOnModal), 4 pontos de injecao (nome/usuario/necessidade/introspeccao).
			$(".modal-pov-info .names").empty().append($("<p>").text(names));
			$(".modal-pov-info .user").empty().append($("<p>").text(user));
			$(".modal-pov-info .need").empty().append($("<p>").text(need));
			$(".modal-pov-info .insight").empty().append($("<p>").text(insight));

			$('.modal-pov-info').modal('open');
		})
	}
	
	Toolkit.PointOfView.buildPOVInfoEditOnModal = function(povId, names, user, need, insight, personasId){

		$(".modal-pov-edit").modal('open', {
			complete: function() {
				$("#name-tags").materialtags('removeAll');
				$("#personas-id").val(0)
		    }
		});
		
		$(".modal-pov-edit #pov-id").val(povId)
		
		$(".modal-pov-edit #user-text").val(user)
		$(".modal-pov-edit #need-text").val(need)
		$(".modal-pov-edit #insight-text").val(insight)
		
		Materialize.updateTextFields();
		
		var currentPersonaItem = getCurrentPersonaItemOjbect(names, personasId)		
  		var allPersonasItems = getPersonasOjbect($(".allPersonas .names").text(), $(".allPersonas .ids").text())
  		
  		setMaterializeTagsDataProperty(currentPersonaItem, allPersonasItems)
		
	}
	
	Toolkit.Validation.personaForm = function(){
		$(".persona-modal form").bind('submit', function(e){
            var nameFilled = $("#name").val().trim() == null || $("#name").val().trim() == "" ? "Defina um nome" : ""
            $(".name-error").html(returnBlockquoteHtml(nameFilled));
            
            var ageFilled = $("#age").val() != "" ? "" : "Defina uma idade"
            var ageRangeMin = $("#age").val() <= 0 ? "A idade deve ser maior ou igual a 1" : ""
            var ageRangeMax = $("#age").val() > 120 ? "A idade deve ser menor ou igual a 120" : ""
            $(".age-error").html(returnBlockquoteHtml(ageFilled) +
            						returnBlockquoteHtml(ageRangeMin) +
            						returnBlockquoteHtml(ageRangeMax));
            
            if (nameFilled != "" || ageFilled != "" || ageRangeMin != "" || ageRangeMax != "")
            	e.preventDefault(e);
        });
	}
	
	Toolkit.Validation.empathyAttributeForm = function(){
		$(".attribute form").bind('submit', function(e){
			var fieldValue = $("#attribute-text").val().trim()
			var attributeFilled = fieldValue == null || fieldValue == "" ? "Este campo n&atilde;o pode ser vazio" : ""
			$(".attribute-error").html(returnBlockquoteHtml(attributeFilled));
			
			if($(".card-color").val().trim() == "")
				$(".card-color").val('yellow')
			
			if (attributeFilled != "")
            	e.preventDefault(e);
		})
	}
	
	Toolkit.Validation.pointOfViewForm = function(){
		$("form#pov").bind('submit', function(e){
			
			var personasField = $("#name-tags").val();
			var userField = $("#user-text").val().trim()
			var needField = $("#need-text").val().trim()
			var insightField = $("#insight-text").val().trim()
			
			personasField = personasField.length < 1 ? "Este campo n&atilde;o pode ser vazio" : ""
			$(".names-error").html(returnBlockquoteHtml(personasField));
			
			userField = userField == null || userField == "" ? "Este campo n&atilde;o pode ser vazio" : ""
			$(".user-error").html(returnBlockquoteHtml(userField));
			
			needField = needField == null || needField == "" ? "Este campo n&atilde;o pode ser vazio" : ""
			$(".need-error").html(returnBlockquoteHtml(needField));
			
			insightField = insightField == null || insightField == "" ? "Este campo n&atilde;o pode ser vazio" : ""
			$(".insight-error").html(returnBlockquoteHtml(insightField));
			
			if (personasField != "" || userField != "" || needField != "" || insightField != "")
            	e.preventDefault(e);
		})
	};
	
})(jQuery, window)

function returnBlockquoteHtml(val){
	return val != "" ?  "<blockquote>" + val + "</blockquote>" : ""
}

function getCurrentPersonaItemOjbect(names, personasId){
	var chipsNames = []	
	var itemNames = names.split(",")
	$.each(itemNames, function(i, val){
		chipsNames.push({"text": val.trim()})
	})
		
	var chipsPersonaId = []
	var itemPersonasID = personasId.split("|");
	$.each(itemPersonasID, function(i, val){
		chipsPersonaId.push({"value": parseInt(val.trim())})
	})
		
	$("#personas-id").val(itemPersonasID)
		
	var merged_arr_item = []

	for(var i = 0; i < Math.max(chipsNames.length, chipsPersonaId.length); i++){
	    merged_arr_item[i] = $.extend({}, chipsNames[i], chipsPersonaId[i]);
	}
	
	return merged_arr_item;
}

function getPersonasOjbect(names, ids){
	var chipsAllNames = []
	var allPersonasNames = names.replace(/\s+/g, " ").trim()		
	var arrPersonasNames = allPersonasNames.split(",")
	
	$.each(arrPersonasNames, function(i, val){
		chipsAllNames.push({"text": val.trim()})
	})
		
	var chipsAllPersonasId = []
	var allPersonasId = ids.replace(/\s+/g, " ").trim()
	var arrPersonasId = allPersonasId.split(",")
	
	$.each(arrPersonasId, function(i, val){
		chipsAllPersonasId.push({"value": val.trim()})
	})
		
	var merged_arr_items = []

	for(var i = 0; i < Math.max(chipsAllNames.length, chipsAllPersonasId.length); i++){
		merged_arr_items[i] = $.extend({}, chipsAllNames[i], chipsAllPersonasId[i]);
	}
	
	return merged_arr_items;
}

function setMaterializeTagsDataProperty(currentPersonaItem, allPersonasItems){
	var data = allPersonasItems
	var names = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('text'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        local: data
    });
		
	names.initialize();
		
	var elt = $('#name-tags');
	elt.materialtags({
        itemValue: 'value',
        itemText: 'text',
        typeaheadjs: {
            name: 'names',
            displayKey: 'text',
            source: names.ttAdapter()
        }
    });
		
	$.each(currentPersonaItem, function(i, val){
		elt.materialtags('add', val);
	})
	
	elt.on('itemAdded', function(event) {
		var items = elt.materialtags('items')
		var ids = []
		$.each(items, function(i, val){
			ids.push(val.value)  
		})
		
		$("#personas-id").val(ids)
	});
	
	elt.on('itemRemoved', function(event) {
		var items = elt.materialtags('items')
		var ids = []
		$.each(items, function(i, val){
			ids.push(val.value)  
		})
		
		$("#personas-id").val(ids)
	});
}

function openFileData(data){
	var blob = b64toBlob(data, 'application/pdf')
	var filePath = window.open(URL.createObjectURL(blob));
}

function b64toBlob(b64Data, contentType) {
	contentType = contentType || '';
	var sliceSize = 512;
	b64Data = b64Data.replace(/^[^,]+,/, '');
	b64Data = b64Data.replace(/\s/g, '');
	var byteCharacters = window.atob(b64Data);
	var byteArrays = [];

	for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
	    var slice = byteCharacters.slice(offset, offset + sliceSize);

	    var byteNumbers = new Array(slice.length);
	    for (var i = 0; i < slice.length; i++) {
	        byteNumbers[i] = slice.charCodeAt(i);
	    }

	    var byteArray = new Uint8Array(byteNumbers);

	    byteArrays.push(byteArray);
	}

	var blob = new Blob(byteArrays, {type: contentType});
	return blob;
}

$(document).ready(function(){
	$(".button-collapse").sideNav();
	$('.collapsible').collapsible();

	// TK-40: deriva aria-label de data-tooltip pra qualquer elemento sem um -- cobre todos os botoes so-icone.
	$('[data-tooltip]:not([aria-label])').each(function(){
		$(this).attr('aria-label', $(this).attr('data-tooltip'));
	});

		// TK-13 (v2 - JS): wrapper do Waves (input do tamanho do texto) nao submetia se clicado na borda -- repassa o clique pro input.
		$(document).on('click', '.waves-input-wrapper', function(e){
			if (e.target === this) {
				var inp = this.querySelector('.waves-button-input');
				if (inp) { inp.click(); }
			}
		});
	
	Toolkit.Persona.selectColorPicker();
	Toolkit.Persona.setSameHeight(".persona-overview .section");
	Toolkit.EmpathyAttribute.cancelEditAttribute();
	Toolkit.EmpathyAttribute.buildCardViewOnModal();
	Toolkit.PointOfView.buildPOVInfoViewOnModal();
	
	Toolkit.Validation.personaForm();
	Toolkit.Validation.empathyAttributeForm();
	Toolkit.Validation.pointOfViewForm();
	
	// UX-FAB-VISIVEL: tooltip do botao "Novo Point of View" reflete o estado.
	Toolkit.PointOfView.updateCriarPovTooltip = function(){
		var $btn = $(".btn.criar-pov");
		var texto = $btn.hasClass("disabled")
			? "Crie uma persona primeiro"
			: (Toolkit.Persona.thereIsPersonaSelected()
				? "Criar Point of View com a(s) persona(s) selecionada(s)"
				: "Marque uma persona na lista abaixo primeiro");
		$btn.attr("data-tooltip", texto).attr("aria-label", texto);
	}

	// TOOLKIT-PERSONA-LISTA-MODELAGEM/4: contador visivel + linha destacada, sem depender so do tooltip.
	Toolkit.PointOfView.updatePovSelectionCount = function(){
		var n = $(".personas-list .chkPersona:checked").length;
		var texto = n === 0 ? "" : (n === 1 ? "1 persona selecionada" : n + " personas selecionadas");
		$("#pov-selection-count").text(texto);
		$(".personas-list .chkPersona").each(function(){
			$(this).closest("tr").toggleClass("row-selected", $(this).is(":checked"));
		});
	}

	if(Toolkit.Persona.thereIsPersonaCreated()){
		$(".btn.criar-pov").removeClass("disabled")
	}
	Toolkit.PointOfView.updateCriarPovTooltip();
	Toolkit.PointOfView.updatePovSelectionCount();

	$(document).on("change", ".chkPersona", function(){
		Toolkit.PointOfView.updateCriarPovTooltip();
		Toolkit.PointOfView.updatePovSelectionCount();
	});

	$('.attributes-list').masonry({
		itemSelector: '.item',
		fitWidth: true
	});

	$(".btn.criar-pov").click(function(){
		if($(this).hasClass("disabled")) return;
		if(Toolkit.Persona.thereIsPersonaSelected()){
			var data = [];

			$(".personas-list table tbody tr").each(function(){
				if($(this).find(".chkPersona").is(":checked")){
					var value = $(this).find(".chkPersona").val();
					data.push("personas=" + encodeURIComponent(value));
				}
			})

			// TK-45b: personas via query string (path com espaco/"+" dava 404 no Tomcat); 1 "personas=" por persona (nao junta com ",", evita colisao de delimitador).
			window.location.href = contextPath + "/point-of-view/criar-pov?" + data.join("&");
		}
		else
			Materialize.toast('Voc&ecirc; deve selecionar pelo menos uma Persona', 4000)
	})
	
	// TK-39: binding trocado de #logout (id) pra .logout-link (classe) -- "Sair" existe 2x no HTML (nav + drawer mobile).
	$(".logout-link").click(function(){
		Toolkit.eraseCookie("ideiaId")
		Toolkit.eraseCookie("usuarioNome")
		Toolkit.eraseCookie("ideiaSig")
		// LOGOUT-FIX: vai direto pro LogOutServlet (invalida sessao, 1 clique). DRY-LIC: licBasePath vem global de header.tag.
		window.location.href = licBasePath + "/LogOutServlet"
	})
	
	// TK-43: if sem chaves rodava sempre + readCookie() null falhava em `!= ""` -- leitura unica com checagem truthy.
	var nomeUsuario = Toolkit.readCookie("usuarioNome");
	if (nomeUsuario) {
		$("footer .usuario").text(decodeURIComponent(nomeUsuario.replace(/\+/g, '%20')));
	}
})
