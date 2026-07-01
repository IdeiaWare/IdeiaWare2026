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
	
	Toolkit.Persona.thereIsPersonaCreated = function(){
		return $(".personas-list table tbody tr").length > 0 ? true : false;	
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
		$(".post-it .btn").click(function(){
			$(".card-color").val($(this).attr("data-color"))
			$(".post-it .btn").removeClass("active");
			$(this).addClass("active")			
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
		// Reescrito para usar a API de Promise do html2canvas (.then). A versao
		// antiga usava o callback "onrendered", removido no html2canvas 0.5, por
		// isso o #file-location nunca era preenchido e ia null para o servidor
		// (erro "Column 'file_location' cannot be null" -> 405).
		$("#overlay").attr('style','display: block !important');

		var section = $(areaClass)[0];
		if (!section) {
			$("#overlay").attr('style','display: none !important');
			return;
		}

		// === TK-16: corte do export resolvido usando a MESMA abordagem do Canvas do
		// LIC (CAN-13), que captura o conteudo INTEIRO. A causa do corte: a area de
		// empatia e inline-table e renderiza MAIS LARGA que a janela; sem informar o
		// tamanho real, o html2canvas captura so a largura da janela e corta a direita.
		// Correcao comprovada (geraPDFCanva.js do Canvas, que funciona): rolar pro topo
		// e passar width/height/windowWidth/windowHeight = scrollWidth/scrollHeight.
		// Isso exige um html2canvas que respeite essas opcoes -> a lib do toolkit foi
		// trocada da 1.2.4 (que ignorava e cortava) pela 0.5.0-beta3 do LIC. A API 0.5
		// usa o callback "onrendered" (em vez de Promise .then). Fundo branco: TK-09/10.
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
				// TK-10: pinta um fundo BRANCO real atras do screenshot. O export e
				// JPEG (sem canal alpha) -> todo pixel transparente viraria PRETO.
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

				// FIX 2026-06-23: gera uma FOLHA A4 PADRAO (com margem) em vez de uma pagina
				// do tamanho exato do conteudo (que saia "justa", so onde tinha linhas). A
				// imagem e encaixada na A4 mantendo a proporcao e CENTRALIZADA -> visual de
				// tabela numa folha. Orientacao pela proporcao do conteudo (largo -> paisagem;
				// alto -> retrato) p/ aproveitar a folha. Como o formato e o 'a4' nomeado (e
				// nao um tamanho custom [w,h]), o jsPDF NAO gira a pagina (problema do TK-17).
				// orientacao: a empatia (mapa largo) usa auto -> paisagem (ficou perfeita); o
				// POV passa 'p' (retrato) p/ a tabela imprimir bem numa A4 vertical.
				var orient = forceOrient || ((contentWidth >= contentHeight) ? 'l' : 'p');
				var pdf = new jsPDF(orient, 'pt', 'a4');
				var pageW = (orient === 'l') ? 841.89 : 595.28; // A4 em pt
				var pageH = (orient === 'l') ? 595.28 : 841.89;
				var margin = 28; // ~1cm

				// FIX 2026-06-23: pinta a folha A4 INTEIRA de branco antes da imagem. Sem isso
				// a area do PDF fora da imagem ficava transparente -> o "abrir" da Retencao do
				// LIC (e alguns viewers) mostrava PRETO; so na impressao virava branco.
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
				reader.onloadend = function () {
					$("#file-location").val(reader.result);
					$("#overlay").attr('style','display: none !important');
					$("form#overview, form#detailed").submit();
				};
			}
		});
	}
	
	// FIX 2026-06-23: export do Mapa de Empatia (Visao Geral) NATIVO no PDF (antes era
	// screenshot html2canvas). Desenha a grade 2 colunas (SENTE/VE, DIZ/ESCUTA, DORES/
	// GANHOS) e os post-its como retangulos coloridos (cor + texto lidos do DOM). Cards de
	// largura fixa que quebram em linhas; o quadrante cresce com o conteudo e quebra de
	// pagina se passar da folha. Vetorial -> nitido e print-safe (nao corta no Ctrl+P).
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

	// FIX 2026-06-23: export do POV agora desenha uma TABELA NATIVA (vetorial) no PDF,
	// dentro das margens da A4. Antes era um screenshot (html2canvas) que no Ctrl+P cortava
	// a borda/coluna da direita. Acentos do PT (c,a,o,e acentuados < 256) renderizam ok no
	// jsPDF 1.2.4. A tabela cresce com os dados e quebra de pagina repetindo o cabecalho.
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
		
		// TK-12: o botao e <input class="btn">, nao <div class="btn"><input>.
		// O seletor antigo ".btn input" nao casava nada -> o rotulo nunca mudava
		// para "Atualizar" ao editar um atributo.
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
			
			$(".modal-card-info .card-content").html("<p>"+ info +"</p>")
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
			
			$(".modal-pov-info .names").html("<p>"+ names +"</p>")
			$(".modal-pov-info .user").html("<p>"+ user +"</p>")
			$(".modal-pov-info .need").html("<p>"+ need +"</p>")
			$(".modal-pov-info .insight").html("<p>"+ insight +"</p>")
			
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

		// TK-13 (v2 - JS): a lib Waves envolve <input type="submit" class="btn"> num
		// <i class="btn waves-input-wrapper"><input class="waves-button-input">. O
		// <input> interno fica do tamanho do TEXTO, entao clicar na "borda" do botao
		// (a area do wrapper) nao submetia - so o texto funcionava. O fix por CSS
		// nao resolveu (o wrapper encolhe pro texto). Aqui, ao clicar na area do
		// wrapper que NAO e o proprio input, repassamos o clique para o input -> o
		// botao inteiro volta a ser clicavel. Vale para todos os submits do toolkit.
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
	
	if(Toolkit.Persona.thereIsPersonaCreated()){
		$(".btn.criar-pov").show();
		$(".btn-floating.criar-pov").removeClass("disabled")
	}
	
	$('.attributes-list').masonry({
		itemSelector: '.item',
		fitWidth: true
	});
	
	$(".btn-floating.criar-pov").click(function(){
		if(Toolkit.Persona.thereIsPersonaSelected()){
			var data = [];
			
			$(".personas-list table tbody tr").each(function(){
				if($(this).find(".chkPersona").is(":checked")){
					var value = $(this).find(".chkPersona").val();
					data.push(value);
				}
			})

			// Passa as personas via query string (codificada) em vez de path.
			// Nomes com espacos/"+" no path causavam 404 no Tomcat 9; na query
			// string esses caracteres sao tratados corretamente. A virgula separa
			// a List<String> esperada pelo @RequestParam do controller.
			window.location.href = contextPath + "/point-of-view/criar-pov?personas=" + encodeURIComponent(data.join(","));
		}
		else
			Materialize.toast('Voc&ecirc; deve selecionar pelo menos uma Persona', 4000)
	})
	
	$("#logout").click(function(){
		Toolkit.eraseCookie("ideiaId")
		Toolkit.eraseCookie("usuarioNome")
		Toolkit.eraseCookie("ideiaSig")
		// Sair = LOGOUT de verdade: o LogOutServlet invalida a sessao do LIC e vai pro
		// login num clique so. Antes ia pra listagem de caixas (sem deslogar) -> 2o clique.
		window.location.href = "/LIC/LogOutServlet"
	})
	
	if(Toolkit.readCookie("usuarioNome") != "")
		var nome =  Toolkit.readCookie("usuarioNome")
		$("footer .usuario").text(decodeURIComponent(nome.replace(/\+/g, '%20')))
})
