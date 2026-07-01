document.write(unescape("%3Cscript src='js/Bibliotecas/jspdf.js' type='text/javascript'%3E%3C/script%3E"));

/*
 * Gera o PDF do Canvas e envia ao banco (ExportCanvaServlet).
 *
 * CAN-NATIVO (2026-06-23): antes era um SCREENSHOT (html2canvas do #myPDF), que saia
 * empilhado numa coluna e em raster (cortava no Ctrl+P). Agora desenha o QUADRO Business
 * Model Canvas 2D NATIVO (vetorial, paisagem A4): os 9 blocos nas posicoes classicas, com
 * os post-its como retangulos coloridos (cor + texto lidos do DOM do #myPDF). Os cards
 * encolhem a fonte pra caber no bloco, com um PISO de tamanho (nao viram formiga); se
 * ainda assim nao couber tudo, corta o excedente. Print-safe (nada e cortado na direita).
 */
function geraPDF() {
	$("#overlay").css('display', 'block');

	// --- le os blocos do #myPDF: titulo -> [{text, bg}] ---
	var data = {};
	$('#myPDF .postit_title').each(function() {
		var title = $(this).text().replace(/\s+/g, ' ').trim();
		var cards = [];
		$(this).closest('div.col').find('.card').each(function() {
			cards.push({
				text: $(this).find('.card-content').text().replace(/\s+/g, ' ').trim(),
				bg: $(this).css('background-color')
			});
		});
		data[title] = cards;
	});

	function parseRGB(s) {
		var m = /(\d+)\D+(\d+)\D+(\d+)/.exec(s || '');
		return m ? [+m[1], +m[2], +m[3]] : [245, 245, 245];
	}
	function darkText(c) { return (0.299 * c[0] + 0.587 * c[1] + 0.114 * c[2]) > 140; }

	var DESC = {
		'Parcerias Principais': 'Rede de Fornecedores e parceiros que ajudam a sua empresa a funcionar',
		'Atividades Principais': 'Ações importantes que a empresa deve realizar para o Modelo de Negócios funcionar',
		'Recursos Principais': 'Recursos mais importantes exigidos para o Modelo de Negócios funcionar',
		'Proposta de Valor': 'Pacote de produtos e serviços e o valor que ele entrega para os clientes',
		'Relacionamento com Cliente': 'Tipos de relação que a empresa estabelece com os clientes',
		'Canais de Comunicação': 'Como a empresa se comunica e alcança sua proposta de valor',
		'Segmento de Clientes': 'Para quem você cria valor? Quem são seus principais clientes?',
		'Estrutura de Custos': 'Todos os custos envolvidos na operação do seu Modelo de Negócios',
		'Receita': 'Como e quanto a empresa gera de receita dos clientes'
	};
	var band = { como: [3, 169, 244], oque: [239, 83, 80], paraquem: [76, 175, 80], quanto: [255, 213, 79] };

	var pdf = new jsPDF('l', 'pt', 'a4');
	var pageW = 841.89, pageH = 595.28, margin = 20;
	var gx = margin, gW = pageW - margin * 2;
	var y0 = margin + 16, superH = 19, rGap = 5;
	var canvasH = pageH - margin - y0;
	var topRegionH = (canvasH - rGap) * 0.65;
	var botRegionH = canvasH - rGap - topRegionH;
	var topBY = y0 + superH, topBH = topRegionH - superH;
	var botBandY = y0 + topRegionH + rGap, botBY = botBandY + superH, botBH = botRegionH - superH;

	var w1 = gW * 0.208, w2 = gW * 0.208, w3 = gW * 0.166, w4 = gW * 0.208;
	var w5 = gW - (w1 + w2 + w3 + w4);
	var cx1 = gx, cx2 = cx1 + w1, cx3 = cx2 + w2, cx4 = cx3 + w3, cx5 = cx4 + w4;

	// faixas coloridas dos grupos (como na tela)
	var bands = [
		{ t: 'Como?',      x: cx1, y: y0,       w: w1 + w2, c: band.como },
		{ t: 'O que?',     x: cx3, y: y0,       w: w3,      c: band.oque },
		{ t: 'Para quem?', x: cx4, y: y0,       w: w4 + w5, c: band.paraquem },
		{ t: 'Quanto?',    x: gx,  y: botBandY, w: gW,      c: band.quanto }
	];
	// posicoes classicas do Business Model Canvas
	var blocks = [
		{ x: cx1, y: topBY,           w: w1, h: topBH,     key: 'Parcerias Principais' },
		{ x: cx2, y: topBY,           w: w2, h: topBH / 2, key: 'Atividades Principais' },
		{ x: cx2, y: topBY + topBH/2, w: w2, h: topBH / 2, key: 'Recursos Principais' },
		{ x: cx3, y: topBY,           w: w3, h: topBH,     key: 'Proposta de Valor' },
		{ x: cx4, y: topBY,           w: w4, h: topBH / 2, key: 'Relacionamento com Cliente' },
		{ x: cx4, y: topBY + topBH/2, w: w4, h: topBH / 2, key: 'Canais de Comunicação' },
		{ x: cx5, y: topBY,           w: w5, h: topBH,     key: 'Segmento de Clientes' },
		{ x: gx,          y: botBY, w: gW / 2, h: botBH,   key: 'Estrutura de Custos' },
		{ x: gx + gW / 2, y: botBY, w: gW / 2, h: botBH,   key: 'Receita' }
	];

	// escreve texto quebrado e CENTRADO; devolve a altura usada
	function wrapCentered(text, x, w, y, font, rgb, maxLines, lineH) {
		pdf.setFontSize(font);
		pdf.setTextColor(rgb[0], rgb[1], rgb[2]);
		var lines = pdf.splitTextToSize(text, w);
		if (lines.length > maxLines) { lines = lines.slice(0, maxLines); lines[maxLines - 1] += '…'; }
		for (var i = 0; i < lines.length; i++) {
			var lw = pdf.getStringUnitWidth(lines[i]) * font;
			pdf.text(lines[i], x + Math.max(0, (w - lw) / 2), y + lineH * (i + 1));
		}
		return lines.length * lineH;
	}

	function drawCards(cards, x, y, w, h) {
		if (!cards.length || h <= 6) { return; }
		var cardPad = 2.5, cardGap = 3, floor = 5, maxFont = 7.5, font = floor;
		for (var f = maxFont; f >= floor; f -= 0.5) {
			pdf.setFontSize(f);
			var lh = f + 1.5, total = 0;
			for (var i = 0; i < cards.length; i++) {
				total += pdf.splitTextToSize(cards[i].text, w - cardPad * 2).length * lh + cardPad * 2 + cardGap;
			}
			if (total <= h) { font = f; break; }
		}
		pdf.setFontSize(font);
		var lineH = font + 1.5, cy = y;
		for (var j = 0; j < cards.length; j++) {
			var rgb = parseRGB(cards[j].bg);
			var lines = pdf.splitTextToSize(cards[j].text, w - cardPad * 2);
			var ch = lines.length * lineH + cardPad * 2;
			if (cy + ch > y + h) { break; } // piso atingido: nao cabe mais
			pdf.setFillColor(rgb[0], rgb[1], rgb[2]);
			pdf.rect(x, cy, w, ch, 'F');
			var tc = darkText(rgb) ? 33 : 255;
			pdf.setTextColor(tc, tc, tc);
			for (var li = 0; li < lines.length; li++) {
				pdf.text(lines[li], x + cardPad, cy + cardPad + lineH * (li + 1) - 1.5);
			}
			cy += ch + cardGap;
		}
	}

	// conteudo do bloco (fundo branco + titulo + descricao + post-its). SEM borda aqui.
	function drawBlockContent(blk) {
		pdf.setFillColor(255, 255, 255);
		pdf.rect(blk.x, blk.y, blk.w, blk.h, 'F');
		var pad = 4, ix = blk.x + pad, iw = blk.w - pad * 2, yy = blk.y + 4;
		yy += wrapCentered(blk.key, ix, iw, yy, 8, [33, 33, 33], 2, 9) + 1;
		if (DESC[blk.key]) { yy += wrapCentered(DESC[blk.key], ix, iw, yy, 5, [130, 130, 130], 2, 6) + 3; }
		drawCards(data[blk.key] || [], ix, yy, iw, blk.y + blk.h - yy - pad);
	}

	// titulo do documento
	pdf.setFontSize(13); pdf.setTextColor(33, 33, 33);
	pdf.text('Business Model Canvas', gx, margin + 9);

	// 1) faixas coloridas (preenchimento + titulo do grupo)
	for (var bi = 0; bi < bands.length; bi++) {
		var bd = bands[bi];
		pdf.setFillColor(bd.c[0], bd.c[1], bd.c[2]);
		pdf.rect(bd.x, bd.y, bd.w, superH, 'F');
		pdf.setFontSize(11);
		var btc = darkText(bd.c) ? 33 : 255; pdf.setTextColor(btc, btc, btc);
		var btw = pdf.getStringUnitWidth(bd.t) * 11;
		pdf.text(bd.t, bd.x + (bd.w - btw) / 2, bd.y + superH / 2 + 3.8);
	}
	// 2) conteudo dos blocos
	for (var k = 0; k < blocks.length; k++) { drawBlockContent(blocks[k]); }
	// 3) TODAS as bordas por cima (separadores limpos entre cores adjacentes)
	pdf.setDrawColor(80); pdf.setLineWidth(0.6);
	for (var bi2 = 0; bi2 < bands.length; bi2++) { pdf.rect(bands[bi2].x, bands[bi2].y, bands[bi2].w, superH, 'S'); }
	for (var k2 = 0; k2 < blocks.length; k2++) { pdf.rect(blocks[k2].x, blocks[k2].y, blocks[k2].w, blocks[k2].h, 'S'); }

	// --- envia ao banco (mesmo fluxo de antes) ---
	var blob = pdf.output("blob");
	var reader = new window.FileReader();
	reader.readAsDataURL(blob);
	reader.onloadend = function() {
		$.ajax({
			type: 'POST',
			url: "ExportCanvaServlet",
			contentType: false,
			processData: false,
			data: reader.result,
			success: function() {
				if (window.isRetencaoCanva) {
					window.location.href = "lista-ideia-gerenciamento.jsp";
				} else {
					window.location.href = "canvas-finalizado.jsp";
				}
			},
			error: function() {
				$("#overlay").css('display', 'none');
				alert('Erro ao exportar o PDF');
			}
		});
	};
}
