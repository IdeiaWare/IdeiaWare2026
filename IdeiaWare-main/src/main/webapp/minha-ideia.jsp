<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="header/headerCookies.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html lang="pt-BR">
  <head>
    <title>IdeiaWare - Minhas ideias</title>

    <link type="text/css" rel="stylesheet" href="css/colaboracao.css"/>
    <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@600' rel='stylesheet'>
    <style>
      .titulo-modulo { font-family: 'Poppins', sans-serif; font-weight: 600; margin-bottom: 28px; }
      /* UX-MINHA-IDEIA-BOTAO-CENTRO: os botoes da coluna de acao viraram <button> (o VALUE
         de <input type=submit> nao respeitava text-align de forma confiavel quando o JS
         mais abaixo forcava uma largura diferente da largura natural do texto) -- <button>
         renderiza o texto como conteudo DOM normal, que centraliza sem essa inconsistencia.
         white-space:nowrap: a coluna de acao e' so' 10% da tabela (ver colgroup) -- rotulos
         de 2 palavras como "Em análise" quebravam linha dentro dessa largura (diferente do
         <input>, que nunca quebra), e a altura fixa de 36px do Materialize cortava a 2a
         linha pra fora da caixa. Sem quebra, o botao estica a coluna alem dos 10% (table-
         layout automatico permite isso) em vez de cortar o texto. */
      #lista-minhas tbody td:last-child .btn { text-align: center; white-space: nowrap; }
    </style>
  </head>

  <body class="center-align blue-grey lighten-5">
    <main>
    <div class="" style="min-height: 90vh" role="main">
      <div class="container">
        <div style="padding: 10px;" class="">
          <h1 class="blue-grey-text text-darken-2 titulo-modulo">Minhas Ideias</h1>
          <%-- UX-VOLTAR-V2: icone circular flutuante (mesmo padrao do colaboracao.jsp, nao mexe no header compartilhado). --%>
          <a href="index-colaboracao.jsp" class="btn-floating btn-large teal lighten-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
          <a href="cadastro-ideia.jsp"><input class="btn orange darken-1" type="submit" value="Cadastrar nova ideia" name="Cadastrar ideia"/></a>
            <jsp:useBean id="ideiaUsuarioDAO" class="edu.unisc.lic.dao.IdeiaUsuarioDAO" />
            <jsp:useBean id="ideiaUsuario" class="edu.unisc.lic.domain.IdeiaUsuario" />
            <jsp:useBean id="data" class="edu.unisc.lic.classes.Data" />
            <jsp:setProperty name="ideiaUsuario" property="usuario" value="${usuarioClasse}" />
            <c:set var="minhasIdeias" value="${ideiaUsuarioDAO.listarParametro(ideiaUsuario)}" />
          <p/>
          <c:if test="${empty minhasIdeias}">
            <div class="center-align grey-text" style="padding: 40px 20px;">
              <i class="material-icons" style="font-size: 3rem; display:block;">lightbulb_outline</i>
              Você ainda não cadastrou nenhuma ideia. Clique em &quot;Cadastrar nova ideia&quot; para começar.
            </div>
          </c:if>
          <c:if test="${not empty minhasIdeias}">
          <div class="input-field" style="margin:0 0 6px;">
            <input id="filtro-minhas" type="text" placeholder="Buscar ideia (título, descrição)" aria-label="Buscar ideia">
          </div>
          <table class="responsive-table my-ideas" id="lista-minhas">
            <colgroup>
              <col style="width: 15%;" />
              <col style="width: 25%;" />
              <col style="width: 40%;" />
              <col style="width: 10%;" />
              <col style="width: 10%;" />
            </colgroup>
            <thead>
              <tr class="highlight" style="font-weight: bold ">
                <td>Usuário</td>
                <td>Título</td>
                <td>Descrição da Ideia</td>
                <td>Status</td>
                <td></td>
              </tr>
            </thead>
            <tbody>
              <c:forEach var="ideia" items="${minhasIdeias}" varStatus="id">
                  <tr>
                    <td class="name">
                        <div class="ellipsis"><c:out value="${ideia.ideia.usuario.nome}"/></div>
                    </td>
                    <%-- M.5: estilo INLINE, nao via colaboracao.css (CSS externo fica em cache, F5 nao rebaixa). --%>
                    <td class="title" style="white-space: normal; word-break: break-word;">
                        <div><c:out value="${ideia.ideia.titulo}"/></div>
                    </td>
                    <td class="description">
                        <div class="ellipsis"><c:out value="${ideia.ideia.descricao}"/></div>
                        <i class="material-icons info-icon"
                           style="color:#00796b; font-size:21px; cursor: pointer;">info_outline</i>
                    </td>
                    <td class="status">
                      <c:choose>
                          <c:when test="${ideia.ideia.status eq 'VA'}">Validada</c:when>
                          <c:when test="${ideia.ideia.status eq 'RE'}">Rejeitada</c:when>
                          <c:when test="${ideia.ideia.status eq 'PE'}">Pendente</c:when>
                          <c:when test="${ideia.ideia.status eq 'DE'}">Em desenvolvimento</c:when>
                          <c:when test="${ideia.ideia.status eq 'ST'}">Storytelling</c:when>
                          <c:when test="${ideia.ideia.status eq 'CF'}">Caixa de Ferramentas</c:when>
                          <c:when test="${ideia.ideia.status eq 'CV'}">Canvas</c:when>
                          <c:when test="${ideia.ideia.status eq 'FN'}">Finalizado</c:when>
                      </c:choose>
                    </td>
                    <td>
                      <c:choose>
                          <c:when test="${ideia.ideia.status eq 'VA' or ideia.ideia.status eq 'DE'}">
                              <%-- M.2: pendente/rejeitado ve o status em vez de "Entrar" (aprovado/legado segue normal). --%>
                              <c:choose>
                                  <c:when test="${ideia.flStatusVinculo eq 'P'}">
                                      <%-- GT-10: "Em análise" (nao "Pendente", que colide com a coluna Status); type="button" evita sublinhado pontilhado do Materialize. --%>
                                      <button type="button" class="btn disabled" disabled="disabled">Em análise</button>
                                  </c:when>
                                  <c:when test="${ideia.flStatusVinculo eq 'R'}">
                                      <a href="#modalMembroRejeitado" class="btn modal-trigger red lighten-2" data-motivomembro="<c:out value='${ideia.motivoRejeicaoMembro}'/>" data-titulo="<c:out value='${ideia.ideia.titulo}'/>">Não aprovado</a>
                                  </c:when>
                                  <c:otherwise>
                                      <c:choose>
                                          <c:when test="${ideia.ideia.statusGrupo eq 'AB'}" >
                                              <form name="entrarColaboracao" action="EntrarDetalheServlet" method="POST">
                                                <input hidden="true" value="${ideia.ideia.codigo}" name="codigo" />
                                                <input hidden="true" value="${ideia.flLider}" name="lider" />
                                                <button class="btn teal darken-1" type="submit" name="Entrar" value="Entrar">Entrar</button>
                                              </form>
                                          </c:when>
                                          <c:otherwise>
                                              <form name="entrarColaboracao" action="EntrarColaboracaoServlet" method="POST">
                                                <input hidden="true" value="${ideia.ideia.codigo}" name="ideiaId" />
                                                <button class="btn teal darken-1" type="submit" name="Entrar" value="Entrar">Entrar</button>
                                              </form>
                                          </c:otherwise>
                                      </c:choose>
                                  </c:otherwise>
                              </c:choose>
                          </c:when>
                          <c:when test="${ideia.ideia.status eq 'RE'}">
                              <a href="#modalRejeitar" class="btn modal-trigger red lighten-2" data-motivo="<c:out value='${ideia.ideia.motivoRejeicao}'/>" data-usuario="<c:out value='${ideia.ideia.usuario.nome}'/>" data-titulo="<c:out value='${ideia.ideia.titulo}'/>" data-descricao="<c:out value='${ideia.ideia.descricao}'/>">Motivo</a>
                          </c:when>
                          <c:when test="${ideia.ideia.status eq 'ST'}">
                              <form name="entrarStory" action="EntrarStorytellingServlet" method="POST">
                                <input hidden="true" value="${ideia.ideia.codigo}" name="ideiaId" />
                                <button class="btn indigo lighten-1" type="submit" name="StoryTelling" value="Entrar">Entrar</button>
                              </form>
                          </c:when>
                          <c:when test="${ideia.ideia.status eq 'CF'}">
                              <form name="entrarCaixa" action="EntrarCaixaServlet" method="POST">
                                <input hidden="true" value="${ideia.ideia.codigo}" name="ideiaId" />
                                <input hidden="true" value="<c:out value='${nome}'/>" name="usuarioNome" />
                                <button class="btn red darken-1" type="submit" name="Caixa" value="Entrar">Entrar</button>
                              </form>
                          </c:when>
                          <c:when test="${ideia.ideia.status eq 'CV'}">
                              <%-- CAN-ACESSO-V2: liberado p/ TODOS os participantes (antes so o lider); acesso real reforcado no servlet. --%>
                              <form name="entrarCanva" action="EntrarCanvaServlet" method="POST">
                                <input hidden="true" value="${ideia.ideia.codigo}" name="ideiaId" />
                                <input hidden="true" value="<c:out value='${nome}'/>" name="usuarioNome" />
                                <button class="btn blue darken-4" type="submit" name="Canva" value="Entrar">Entrar</button>
                              </form>
                          </c:when>
                          <c:when test="${ideia.ideia.status eq 'FN'}">
                              <%-- M.13: ideia finalizada abre a Retencao via GerenciarIdeiaServlet (antes, botao "Entrar" morto). --%>
                              <form name="entrarGerenciamento" action="GerenciarIdeiaServlet" method="POST">
                                <input hidden="true" value="${ideia.ideia.codigo}" name="ideiaId" />
                                <button class="btn deep-orange lighten-2" type="submit" name="Detalhes" value="Detalhes">Detalhes</button>
                              </form>
                          </c:when>
                          <c:otherwise>
                            <%-- GT-10: mesmo botao de vinculo do rotulo "Em análise" acima. --%>
                              <button type="button" class="btn disabled" disabled="disabled">Entrar</button>
                          </c:otherwise>
                      </c:choose>
                    </td>
                  </tr>
              </c:forEach>
            </tbody>
          </table>
          <script>
            (function(){
              var i=document.getElementById('filtro-minhas');
              if(!i)return;
              var t=[].slice.call(document.querySelectorAll('#lista-minhas tbody tr'));
              i.addEventListener('input',function(){
                var s=i.value.toLowerCase();
                t.forEach(function(r){
                  // UX-BUSCA: busca escopada a nome+titulo+descricao+status.
                  var nome   = (r.querySelector('.name')        || {}).textContent || '';
                  var titulo = (r.querySelector('.title')       || {}).textContent || '';
                  var desc   = (r.querySelector('.description') || {}).textContent || '';
                  var status = (r.querySelector('.status')      || {}).textContent || '';
                  var alvo = (nome + ' ' + titulo + ' ' + desc + ' ' + status).toLowerCase();
                  r.style.display = alvo.indexOf(s) > -1 ? '' : 'none';
                });
              });
            })();

            <%-- UX-MINHA-IDEIA-BOTAO-LARGURA: usa o maior botao natural como referencia, nunca encolhe. --%>
            (function(){
              var botoes = [].slice.call(document.querySelectorAll('#lista-minhas tbody td:last-child .btn'));
              if (!botoes.length) return;
              var maiorLargura = 0;
              botoes.forEach(function(b){
                var largura = b.getBoundingClientRect().width;
                if (largura > maiorLargura) maiorLargura = largura;
              });
              if (!maiorLargura) return;
              botoes.forEach(function(b){ b.style.width = maiorLargura + 'px'; });
            })();
          </script>
          </c:if>
        </div>
        <!--modal para rejeição das ideias-->
        <div id="modalRejeitar" class="modal">
          <div class="modal-content">
            <h4>Ideia rejeitada</h4>
            <br>
            <div class="input-field">
              <div style="text-align: justify">
                <div id="divTitulo"></div>
                <div id="divDescricao"></div>
                <div id="divUsuario"></div>
                <div id="divMotivo"></div>
              </div>
            </div>
            </p>
          </div>
          <div class="modal-footer">
            <a href="#!" class="modal-action modal-close  btn-flat grey lighten-1">Fechar</a>
          </div>
        </div>
        
        <!-- Modal para visualizar detalhadamente a ideia -->
        <div id="modal-detalhe-ideia" class="modal">
          <div class="modal-content" style="text-align:left;">
              <h4>Detalhe da ideia</h4>
              <div class='nome-ideia'></div>
              <div class='titulo-ideia'></div>
              <div class='descricao-ideia'></div>
          </div>
          <div class="modal-footer">
            <a href="#!" class="modal-action modal-close  btn-flat grey lighten-1">Fechar</a>
          </div>
        </div>

        <%-- M.2/M.3: modal do participante REJEITADO do grupo -- mostra o motivo que o lider informou. --%>
        <div id="modalMembroRejeitado" class="modal">
          <div class="modal-content">
            <h4>Não aprovado no grupo</h4>
            <br>
            <div style="text-align: justify">
              <div id="divMembroTitulo"></div>
              <div id="divMembroMotivo"></div>
            </div>
          </div>
          <div class="modal-footer">
            <a href="#!" class="modal-action modal-close btn-flat grey lighten-1">Fechar</a>
          </div>
        </div>
      </div>
    </div>
  <script src="js/csrf.js"></script>
  </main>
    </body>

  <%@include file="header/footer.jsp" %>
  <script type="text/javascript">
      $(document).ready(function () {
        $('.modal').modal({
          ready: function (modal, trigger) {
            // GT-09: modal-detalhe-ideia abre sem trigger -- so roda o preenchimento quando trigger existe.
            if (!trigger) { return; }

            modal.find('input[name="codigo"]').val(trigger.data('codigo'));

            // SEC-05: escapa dado do usuario (trigger.data() decodifica entidades -> innerHTML re-parseia = XSS).
            function escapeHtml(s){if(s==null)return '';return String(s).replace(/[&<>"']/g,function(c){return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];});}
            document.getElementById('divTitulo').innerHTML = "<b>Ideia:</b> " + escapeHtml(trigger.data('titulo'));
            document.getElementById('divDescricao').innerHTML = "<b>Descrição:</b> " + escapeHtml(trigger.data('descricao'));
            document.getElementById('divUsuario').innerHTML = "<b>Usuário:</b> " + escapeHtml(trigger.data('usuario'));
            document.getElementById('divMotivo').innerHTML = "<b>Motivo:</b> " + escapeHtml(trigger.data('motivo'));

            // M.2/M.3: modal "Não aprovado" do participante rejeitado do grupo.
            if (trigger.data('motivomembro') != null) {
              document.getElementById('divMembroTitulo').innerHTML = "<b>Ideia:</b> " + escapeHtml(trigger.data('titulo'));
              document.getElementById('divMembroMotivo').innerHTML = "<b>Motivo:</b> " + escapeHtml(trigger.data('motivomembro'));
            }

          }
        });
      });
      
      $(".info-icon").click(function(){
        var nome = $(this).closest('tr').find(".name div").html();
        var titulo = $(this).closest('tr').find(".title div").html();
        var descricao = $(this).closest('tr').find(".description div").html();
        
        $("#modal-detalhe-ideia .modal-content .nome-ideia").html("<strong>Usuário: </strong>" + nome);
        $("#modal-detalhe-ideia .modal-content .titulo-ideia").html("<strong>Título: </strong>" + titulo);
        $("#modal-detalhe-ideia .modal-content .descricao-ideia").html("<strong>Descrição: </strong>" + descricao);
        $('#modal-detalhe-ideia').modal('open');  
      });
          
          
  </script>
</html>