<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true"%>
<%@include file="header/headerCookies.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
  <head>
    <title>IdeiaWare - Colaboração</title>
  </head>
  <body class="center-align teal darken-1">
    <main>
    <div class="white" style="min-height: 90vh" role="main">
      <div class="container">
        <div style="padding: 10px;" class="white">
          <h1>Colaboração da Ideia</h1>
          <%-- UX-VOLTAR-V2: icone flutuante proprio (nao mexe no header compartilhado, ver L.1). --%>
          <c:choose>
            <c:when test="${sessionScope.isRetencao eq false}">
              <a href="minha-ideia.jsp" class="btn-floating btn-large teal lighten-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
            </c:when>
            <c:otherwise>
              <a href="gerenciamento-ideia.jsp" class="btn-floating btn-large teal lighten-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
            </c:otherwise>
          </c:choose>
          <div class="container">
            <jsp:useBean id="colaboracaoIdeia"    class="edu.unisc.lic.domain.ColaboracaoIdeia" />
            <jsp:useBean id="colaboracaoIdeiaDAO" class="edu.unisc.lic.dao.ColaboracaoIdeiaDAO" />
            <jsp:useBean id="log"      class="edu.unisc.lic.domain.LogColaboracao" />
            <jsp:useBean id="logDAO"   class="edu.unisc.lic.dao.LogColaboracaoDAO" />
            <jsp:useBean id="ideiaDAO" class="edu.unisc.lic.dao.IdeiaDAO" />

            <%-- COL-03: sessionScope.ideiaId já é Long — não precisa de Long.parseLong --%>
            <jsp:setProperty name="colaboracaoIdeia" property="ideia" value="${ideiaDAO.buscar(sessionScope.ideiaId)}" />
            <jsp:setProperty name="log"              property="ideia" value="${ideiaDAO.buscar(sessionScope.ideiaId)}" />

            <%-- COL-12: cache em variável — elimina as 4 queries idênticas ao banco --%>
            <c:set var="colaboracoes" value="${colaboracaoIdeiaDAO.listarParametro(colaboracaoIdeia)}" />
            <%-- COL-03: log2 pode ser null (sem LogColaboracao ainda) — guardado para uso null-safe --%>
            <c:set var="log2"       value="${logDAO.buscarDescricaoFinal(log)}" />
            <c:set var="isRetencao" value="${sessionScope.isRetencao}" />

            <div class="card blue-grey darken-1">
              <div class="card-content white-text">
                <%-- M.7: titulo (max 50) cortava no card com fonte 32px fixa -- agora quebra em vez de estourar. --%>
                <span class="card-title" style="display:block; word-break: break-word; line-height:1.2;"><b style="font-size: 28px;"><c:out value="${ideiaTitulo}"/></b></span>
                <%-- COL-03: null-safe — usa descricao da sessão como fallback se log2 não existir --%>
                <p style="text-align: justify" id="descricaoAtual">
                  <c:choose>
                    <c:when test="${not empty log2}"><c:out value="${log2.descricao}"/></c:when>
                    <c:otherwise><c:out value="${sessionScope.ideiaDesc}"/></c:otherwise>
                  </c:choose>
                </p>
              </div>
            </div>

            <div>
              <c:if test="${sessionScope.lider eq 'S' and isRetencao eq false}">
                <div>
                  <form name="finalizar" action="FinalizarColaboracaoServlet" method="post">
                    <input type="hidden" value="${sessionScope.ideiaId}" name="ideiaId">
                    <input class="btn-large orange darken-1 right" name="Finalizar Ideia" value="Finalizar Ideia" type="submit" onclick="return confirm('Finalizar a ideia encerra a colaboração. Esta ação não pode ser desfeita. Confirmar?')">
                  </form>
                </div>
                <div class="left">
                  <form name="editarTexto" action="EditarTextoServlet" method="post">
                    <input type="hidden" value="${sessionScope.ideiaId}" name="ideiaId">
                    <input class="btn-large orange darken-1 right" name="Editar Texto" value="Editar Texto" type="submit">
                  </form>
                </div>
              </c:if>
            </div>
            <br/>
          </div>

          <%-- UX-01/TEST-04: tabela FICA sempre no DOM, so escondida via CSS -- remove-la via c:if quebra o appendTo do polling/envio. --%>
          <div id="colaboracoesVazio" class="center-align grey-text" style="padding: 40px 20px; ${empty colaboracoes ? '' : 'display:none;'}">
            <i class="material-icons" style="font-size: 3rem; display:block;">forum</i>
            Ainda não há colaborações para esta ideia.
          </div>

          <%-- M.6: maior codigo ja renderizado (colaboracoes vem em ordem crescente); o polling so traz codigo maior. --%>
          <c:set var="ultimoCodigoInicial" value="0" />
          <table id="tabelaColab" style="${empty colaboracoes ? 'display:none;' : ''}">
            <%-- COL-12: usa ${colaboracoes} (já em cache) — sem nova query --%>
            <thead>
              <tr class="highlight" style="font-weight: bold">
                <td style="width: 15%" class="usuario">Usuário</td>
                <td style="max-width: 80%; width: 70%; min-width: 70%">Colaboração</td>
                <c:if test="${sessionScope.lider eq 'S' and isRetencao eq false}">
                  <td style="width: 5%"></td>
                </c:if>
              </tr>
            </thead>
            <c:forEach var="cola" items="${colaboracoes}">
              <tr data-codigo="${cola.codigo}">
                <td><c:out value="${cola.usuario.nome}"/></td>
                <%-- M.10: editar (lapis) so pro AUTOR e so antes de virar descricao oficial (reforcado no servlet). --%>
                <td>
                  <span class="colab-texto"><c:out value="${cola.descricaoIdeiaAtual}"/></span>
                  <c:if test="${cola.usuario.codigo eq sessionScope.codigoUsuario and cola.flSalvado eq null and isRetencao eq false}">
                    <a class="btn-editar-colab" data-codigo="${cola.codigo}" href="javascript:;" title="Editar colaboração" aria-label="Editar colaboração" style="margin-left:6px;"><i class="material-icons" style="font-size:16px; vertical-align:middle; color:#00796b;">edit</i></a>
                  </c:if>
                </td>
                <c:if test="${sessionScope.lider eq 'S' and isRetencao eq false}">
                  <c:choose>
                    <c:when test="${cola.flSalvado eq null}">
                      <td>
                        <form name="addDescricao" id="addDescricao" action="AddDescricaoServlet" method="POST">
                          <input type="hidden" value="${cola.codigo}" id="colaboracao" name="colaboracao">
                          <button class="btn-floating teal darken-1 tooltipped"
                                  data-position="right" data-delay="50"
                                  data-tooltip="Adicionar à Descrição" aria-label="Adicionar à Descrição">
                            <a type="submit" name="adicionar"><i class="material-icons">add</i></a>
                          </button>
                        </form>
                      </td>
                    </c:when>
                    <c:otherwise>
                      <td>
                        <button class="btn-floating disabled teal darken-1" disabled="true" aria-label="Adicionar à Descrição (já enviado)">
                          <a name="adicionar"><i class="material-icons">add</i></a>
                        </button>
                      </td>
                    </c:otherwise>
                  </c:choose>
                </c:if>
              </tr>
              <c:set var="ultimoCodigoInicial" value="${cola.codigo}" />
            </c:forEach>
          </table>

          <c:if test="${isRetencao eq false}">
            <form name="enviarColaboracao" action="EnviarColaboracaoServlet" method="post" id="enviarColaboracao">
              <div class="row">
                <div class="input-field">
                  <textarea id="descricao" type="text" class="materialize-textarea"
                            name="descricao" data-length="200" maxlength="200" minlength="5"></textarea>
                  <label for="descricao">Enviar colaboração</label>
                </div>
                <button class="btn orange darken-1" name="enviar" id="enviar">Enviar
                  <i class="material-icons right">send</i>
                </button>
              </div>
            </form>
          </c:if>
        </div>
      </div>
    </div>
  <script src="js/csrf.js"></script>
  </main>
    </body>

  <script>
    // TEST-04: revela a tabela e esconde a mensagem de vazio, chamado apos o 1o appendTo bem-sucedido.
    function revelarTabelaColaboracoes() {
      $("#colaboracoesVazio").hide();
      $("#tabelaColab").show();
    }

    $(document).on("submit", "#enviarColaboracao", function (event) {
      event.preventDefault();
      chamaAjaxEnviar();
    });

    $(document).on("submit", "#addDescricao", function (event) {
      event.preventDefault();
      chamaAjaxAdd($(this));
    });

    function chamaAjaxAdd(form) {
      $.ajax({
        type: 'POST',
        url: "AddDescricaoServlet",
        data: { colaboracao: form[0][0].value },
        success: function (responseTexto) {
          $("#descricaoAtual").text(responseTexto);
          form[0][1].setAttribute("disabled", "true");
          form[0][1].setAttribute("class", "btn-floating disabled teal darken-1");
          // GT-08: aria-label atualizado ao desabilitar (antes ficava com o texto antigo).
          form[0][1].setAttribute("aria-label", "Adicionar à Descrição (já enviado)");
        },
        error: function () {
          alert("Erro ao adicionar colaboração à descrição. Tente novamente.");
        }
      });
    }

    // M.10: editar a propria colaboracao inline (textarea + Salvar/Cancelar) -- so autor, so antes da descricao oficial.
    $(document).on("click", ".btn-editar-colab", function () {
      var $btn = $(this);
      var codigo = $btn.data("codigo");
      var $cell = $btn.closest("td");
      var $span = $cell.find(".colab-texto");
      if ($cell.find(".edit-colab-ta").length) return; // ja esta editando

      var textoAtual = $span.text();
      $span.hide();
      $btn.hide();

      var $ta = $("<textarea class='edit-colab-ta' maxlength='1500' style='width:100%; min-height:60px;' aria-label='Editar colaboração'></textarea>").val(textoAtual);
      var $salvar = $("<a class='btn green lighten-1' href='javascript:;' style='margin-right:6px;'>Salvar</a>");
      var $cancelar = $("<a class='btn-flat' href='javascript:;'>Cancelar</a>");
      var $acoes = $("<div style='text-align:right; margin-top:6px;'></div>").append($salvar).append($cancelar);
      var $editor = $("<div class='edit-colab-box'></div>").append($ta).append($acoes);
      $cell.append($editor);
      $ta.focus();

      function restaurar() {
        $editor.remove();
        $span.show();
        $btn.show();
      }

      $cancelar.on("click", restaurar);

      $salvar.on("click", function () {
        var novo = $.trim($ta.val());
        if (novo === "") { alert("A colaboração não pode ficar vazia."); return; }
        $.ajax({
          type: "POST",
          url: "EditarColaboracaoServlet",
          data: { colaboracao: codigo, descricao: novo },
          success: function (resp) {
            $span.text(resp.descricaoIdeiaAtual);
            restaurar();
          },
          error: function () {
            alert("Não foi possível editar. A colaboração pode já ter sido adicionada à descrição pelo líder.");
            restaurar();
          }
        });
      });
    });

    // SEC-05: escapa dado do usuario (nome/descricao vindos do JSON) antes de injetar via .html() -> impede XSS.
    function escapeHtml(s){if(s==null)return '';return String(s).replace(/[&<>"']/g,function(c){return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];});}

    // M.6: envio e polling so renderizam codigo MAIOR que este (JS single-thread = sem corrida/duplicata).
    var ultimoCodigo = ${ultimoCodigoInicial};
    // M.10: decide se a linha nova leva botao de editar (so AUTOR, so nao-salvada, fora de retencao).
    var meuCodigo = ${sessionScope.codigoUsuario};
    var isRetencaoJS = ${isRetencao};

    // Monta a <tr> de uma colaboracao (reutilizado pelo envio e pelo polling).
    function renderColaboracao(cola) {
      if (cola == null || cola.usuario == null) return;
      if (cola.codigo <= ultimoCodigo) return; // ja renderizada (ou corrida) -> ignora

      // M.10: botao de editar (lapis) so pro autor da colaboracao, nao-salvada, fora de retencao.
      var btnEditar = (!isRetencaoJS && cola.usuario.codigo === meuCodigo && cola.flSalvado == null)
        ? " <a class=\"btn-editar-colab\" data-codigo=\"" + cola.codigo + "\" href=\"javascript:;\" title=\"Editar colaboração\" aria-label=\"Editar colaboração\" style=\"margin-left:6px;\"><i class=\"material-icons\" style=\"font-size:16px; vertical-align:middle; color:#00796b;\">edit</i></a>"
        : "";

      var texto = "<td>" + escapeHtml(cola.usuario.nome) + "</td>\n"
                + "<td><span class=\"colab-texto\">" + escapeHtml(cola.descricaoIdeiaAtual) + "</span>" + btnEditar + "</td>\n";

      if ("${sessionScope.lider}" === "S") {
        // GT-08: aria-label replicado aqui (renderColaboracao() montava o botao sem, diferente da versao JSTL).
        if (cola.flSalvado == null) {
          texto += "<td><form name=\"addDescricao\" id=\"addDescricao\" action=\"AddDescricaoServlet\" method=\"POST\">\n"
                 + "<input type=\"hidden\" value=\"" + cola.codigo + "\" id=\"colaboracao\" name=\"colaboracao\">\n"
                 + "<button class=\"btn-floating teal darken-1 tooltipped\" data-position=\"right\" data-delay=\"50\" data-tooltip=\"Adicionar à Descrição\" aria-label=\"Adicionar à Descrição\">"
                 + "<a type=\"submit\" name=\"adicionar\"><i class=\"material-icons\">add</i></a>"
                 + "</button></form></td>\n";
        } else {
          texto += "<td><button class=\"btn-floating disabled teal darken-1\" disabled=\"true\" aria-label=\"Adicionar à Descrição (já enviado)\">"
                 + "<a name=\"adicionar\"><i class=\"material-icons\">add</i></a>"
                 + "</button></td>\n";
        }
      }

      $("<tr>").attr("data-codigo", cola.codigo).html(texto).appendTo("#tabelaColab");
      revelarTabelaColaboracoes();
      ultimoCodigo = cola.codigo;
    }

    function chamaAjaxEnviar() {
      if ($("#descricao").val() === "") return;

      $.ajax({
        type: 'POST',
        url: 'EnviarColaboracaoServlet',
        data: { descricao: $("#descricao").val() },
        success: function (responseJson) {
          renderColaboracao(responseJson);
          $("#descricao").val("");
        },
        error: function () {
          alert("Erro ao enviar colaboração. Tente novamente.");
        }
      });
    }

    setInterval(function () {
      $.ajax({
        type: 'POST',
        url: 'RetornaMensagensServlet',
        data: { ultimoCodigo: ultimoCodigo },
        success: function (responseJson) {
          // M.6: servlet devolve ARRAY com colaboracoes novas (codigo > ultimoCodigo); renderColaboracao ignora as ja vistas.
          if (!Array.isArray(responseJson)) return;
          responseJson.forEach(renderColaboracao);
        },
        <%-- COL-13: error handler adicionado ao polling --%>
        error: function () {
          console.warn("Polling: falha ao verificar novas colaborações.");
        }
      });
    }, 2000);
  </script>
</html>
<%@include file="header/footer.jsp" %>
