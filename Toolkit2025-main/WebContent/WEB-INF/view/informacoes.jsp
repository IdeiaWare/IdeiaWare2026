<%@ page language="java" pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<%@taglib tagdir="/WEB-INF/tags" prefix="t"%>

<t:header></t:header>
	<%-- UX-VOLTAR-V2: icone circular flutuante no canto superior esquerdo. --%>
	<a href="${pageContext.request.contextPath}/persona/lista" class="btn-floating btn-large red darken-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar"><i class="material-icons">arrow_back</i></a>
	<nav class="crumb">
	    <div class="nav-wrapper">
	        <a href="${pageContext.request.contextPath}/persona/lista" class="breadcrumb">Personas</a>
	        <span class="breadcrumb active">Informações Gerais</span>
	    </div>
  	</nav>	
	<article>
		<h2 class="title-page">Informações Gerais</h2>
		
		<ul class="collapsible" data-collapsible="accordion">
			<li>	
				<div class="collapsible-header">
					<h3>O que é Persona</h3>
				</div>
				<div class="collapsible-body">
					<p>Segundo MJV (2016), <em>Persona</em> é uma poderosa ferramenta utilizada durante as fases do DT, já que a mesma identifica arquétipos - “estruturas de personalidade bem definidas que se repetem nos indivíduos e que fazem parte do inconsciente coletivo” (SAIANI, 2016). Na figura 4 apresenta-se 12 (doze) deles, que são personagens fictícios, concebidos com base no comportamento observado em perfis extremos. Cada perfil pode assumir características relacionadas a um determinado grupo social e real. Desta forma é possível analisar dados demográficos, culturais, necessidades, expectativas e outras informações de destaque para o desenvolvimento direcionado de uma ideia inovadora de um possível produto ou serviço.
					<p>Esta ferramenta pode ser utilizada inúmeras vezes e em qualquer estágio durante os processos de elaboração de produtos, porém particularmente útil na validação das ideias e nas fases de geração. Ela possibilita agrupar dados que podem ser melhor explorados em fases como a de Ideação e futuramente permite que as ideias possam ser avaliadas e selecionadas de acordo com as <em>personas</em> mais promissoras, auxiliando desta forma a equipe do projeto a criar empatia com os diferentes tipos de perfis de pessoas durante a geração e validação das ideias para o negócio.</p>
					<p>Por fim, depois de identificar todas as polaridades quanto à combinação de aspectos (sexo, faixa etária e classe social), podendo entre estes, encontrar grupos com características bem distintas, cada <em>persona</em> terá recebido um nome e sua história criada com o propósito de representar quais foram as necessidades que outorgaram na personificação deste arquétipo (SIQUEIRA, 2016).</p> 
					<p>Para Osterwalder e Pigneur (2011) o uso do Mapa da Empatia (figura 5) ajuda a desenvolver e analisar facilmente em torno dos clientes uma melhor compreensão de seu ambiente, seus comportamentos, desejos, angústias e no avanço do conhecimento das suas características demográficas. Com estes dados será possível analisar os perfis e nortear o <em>design</em> no intuito de montar melhores propostas de valor, deixando desta forma um canal seguro para compreender e dialogar com o cliente.</p>
					<p class="center-align no-margin-bottom"><strong>Figura 4 - Arquétipos e suas características</strong></p>
			  		<img class="responsive-img materialboxed centered" src="${pageContext.request.contextPath}/resources/imgs/arquetipos.png"/>
					<span class="fonte">Fonte: (SAIANI, 2016)</span>
			
					<p class="center-align no-margin-bottom"><strong>Figura 5 - Mapa da Empatia</strong></p>
					<img class="responsive-img materialboxed centered" src="${pageContext.request.contextPath}/resources/imgs/mapa-de-empatia.png"/>
					<span class="fonte">Fonte: (ADAPTADO DE XPLANE, 2017)</span>
					<p>Segundo Bettoni (2015), inicialmente, define-se o nome e a idade da persona a ser estudada. Feito isto cada quadrante do mapa de empatia deverá ser preenchido de acordo com hipóteses baseadas no funcionamento dos pensamentos do público-alvo em questão. Os quadrantes são preenchidos da seguinte forma:</p>
					<ul class="browser-default">
						<li>O que ela vê: Será o que a persona vê quando consome o produto e questiona quais as influências visuais está sujeita dentro do seu ambiente.</li>
						<ul class="browser-default">  
							<li>Quem está em torno dela?</li>
							<li>Quem são seus amigos?</li>
							<li>Quais são os problemas que encontra?</li>
						</ul>
						
						<li>O que ela escuta: Refere-se a quem o cliente escuta ao consumir o produto.</li> 
						<ul class="browser-default">
							<li>Quais pessoas ou ideias o influenciam?</li> 
							<li>Que canais de mídias são influentes?</li>
						</ul>
								
						<li>O que pensa e sente: Objetiva entender o que o produto desperta no cliente (pensamentos e sentimentos). Tenta desenhar o que acontece na mente dele.</li>
						<ul class="browser-default">
							<li>O que é realmente importante para ele?</li>
							<li>O que o mantém motivado?</li>
						</ul>
						
						<li>O que ela diz e faz: Identifica como o cliente se comporta em público ou o que ele pode dizer quanto ao processo de consumo do produto. Objetivando encontrar o diferencial entre o que ele fala que está sentindo e o que ele realmente está pensando e sentindo.</li>
						<ul class="browser-default">
							<li>Qual a atitude dela?</li>
							<li>Do que gosta de falar?</li>
						</ul>
			
						<li>Quais suas dores: Referem-se às dúvidas, frustrações, obstáculos e medos que o público-alvo precisa lidar ao consumir o produto oferecido.</li>
						<ul class="browser-default">
							<li>Quais riscos teme enfrentar?</li>
							<li>Que obstáculos existem entre ele e o que ele quer e precisa obter?</li>
						</ul>
						
						<li>Quais seus ganhos: Identifica o que a persona quer ou precisa obter, pensando em algo eficaz para surpreendê-la ao consumir o produto ofertado.</li>
						<ul class="browser-default">
							<li>Como mede o sucesso?</li>
							<li>Pense em algumas estratégias que pode utilizar para alcançar os seus objetivos.</li>
						</ul>
					</ul>
					
					<p>A figura 6 apresenta um exemplo de mapa de empatia, considerando as questões em cada quadrante.</p>
					<p class="center-align no-margin-bottom"><strong>Figura 6 - Exemplo de Mapa de Empatia</strong></p>
					<img class="responsive-img materialboxed centered" src="${pageContext.request.contextPath}/resources/imgs/exemplo-mapa-de-empatia.png"/>
					<span class="fonte">Fonte: (KAYO, 2013)</span> 
				</div>
			</li>
		
			<li>
				<div class="collapsible-header">
					<h3>O que é Point Of View</h3>
				</div>
				<div class="collapsible-body">
					<p>De acordo com Dam e Siang (2017) o Point of View é responsável por definir e declarar problemas significativos e acionáveis, que permitem a idealização de maneira que seja orientada ao objetivo; e isto é o que o torna um bom POV, sem perder o foco em seus usuários (users), suas necessidades (needs) e introspecções (insights).</p>
					<p>Durante a fase de ideação o POV será a declaração orientadora, responsável por focar nas percepções e necessidades de um determinado usuário, visto que durante o processo de ideação se não existirem declarações de problemas úteis e acionáveis para manter o foco na essência da pesquisa, você pode se perder facilmente. Um bom Point of View é aquele que lhe mantém no curso/trajeto.</p>
					<p>Dam e Siang (2017) complementam com explicações de como definir um POV o qual consiste basicamente nos quatro passos listados a seguir:</p>
					<ul class="browser-default">
						<li>O primeiro passo será a definição de quais tipos de pessoas o design será voltado para, quais são as necessidades mais importantes a serem selecionadas e quais são os insights desenvolvidos que serão expressados. Neste caso os usuários/clientes podem ser definidos através do desenvolvimento de personas utilizando-se, por exemplo, do mapa de empatia, enquanto as necessidades são aquelas que foram adquiridas através das entrevistas, pesquisas e observações. Lembrando que os insights não podem ser as razões para a necessidade.</li>
						
						<li>No segundo passo todas as definições obtidas serão aplicadas em um template de Point of View como demonstra a figura 7.</li>
						<p class="center-align no-margin-bottom"><strong>Figura 7 - Template Point of View - Exemplo</strong></p>
			 			<img class="responsive-img materialboxed centered" src="${pageContext.request.contextPath}/resources/imgs/template-POV.png"/>
						<span class="fonte">Fonte: (ADAPTADO DE INTERACTION DESIGN, 2017)</span>
						
						<li>O terceiro passo consiste na combinação destes três elementos - usuário, necessidade e introspecção - que permitirá uma clara declaração de problema na condução do trabalho de design. O POV Madlib (figura 8) permite a articulação do POV ao inserir as informações. Madlib vem a ser um jogo de palavras em modelo frasal, no qual o objetivo do jogador é substituir os espaços em branco por uma lista de palavras, a fim de compor uma história (JONES; KISTHARDT; COOPER, 2011). Por exemplo, [Usuário . . . (descritivo)] precisa de [Necessidade . . . (verbo)] porque [Introspecção . . . (atraente)], conforme a figura 8.</li>
						<p class="center-align no-margin-bottom"><strong>Figura 8 - Point of View Madlib</strong></p>
						<img class="responsive-img materialboxed centered" width="567" src="${pageContext.request.contextPath}/resources/imgs/pov-madlib.png"/>
						<span class="fonte">Fonte: (INTERACTION DESIGN, 2017)</span>
						
						<li>O quarto passo contém os pontos importantes que o POV deve possuir, que é aquele que: Fornece um foco direto; Enquadra o problema como uma declaração de problema; Inspira a sua equipe; Orienta seus esforços de inovação; Informa critérios para avaliar ideias concorrentes; É sexy e capta a atenção das pessoas; É válido, perspicaz, acionável, único, direto, significativo e emocionante.</li>
					</ul>
				</div>
			</li>
		</ul>
	</article>
<t:footer></t:footer>