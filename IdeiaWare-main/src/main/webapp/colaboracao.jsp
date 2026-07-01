<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true"%>
<%@include file="header/headerCookies.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
  <head>
    <title>IdeiaWare - Colaboração</title>
  </head>
  <body class="center-align teal darken-1">
    <div class="white" style="min-height: 90vh" role="main">
      <div class="container">
        <div style="padding: 10px;" class="white">
          <h1>Colaboração da Ideia</h1>
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
                <span class="card-title"><b style="font-size: 32px;"><c:out value="${ideiaTitulo}"/></b></span>
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
                    <input class="btn-large orange darken-1 right" name="Finalizar Ideia" value="Finalizar Ideia" type="submit">
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

          <table id="tabelaColab">
            <%-- COL-12: usa ${colaboracoes} (já em cache) — sem nova query --%>
            <c:choose>
              <c:when test="${empty colaboracoes}">
                <br><br><h5>Ainda não há colaborações para esta ideia</h5>
              </c:when>
              <c:otherwise>
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
                  <tr>
                    <td><c:out value="${cola.usuario.nome}"/></td>
                    <td><c:out value="${cola.descricaoIdeiaAtual}"/></td>
                    <c:if test="${sessionScope.lider eq 'S' and isRetencao eq false}">
                      <c:choose>
                        <c:when test="${cola.flSalvado eq null}">
                          <td>
                            <form name="addDescricao" id="addDescricao" action="AddDescricaoServlet" method="POST">
                              <input type="hidden" value="${cola.codigo}" id="colaboracao" name="colaboracao">
                              <button class="btn-floating teal darken-1 tooltipped"
                                      data-position="right" data-delay="50"
                                      data-tooltip="Adicionar à Descrição">
                                <a type="submit" name="adicionar"><i class="material-icons">add</i></a>
                              </button>
                            </form>
                          </td>
                        </c:when>
                        <c:otherwise>
                          <td>
                            <button class="btn-floating disabled teal darken-1" disabled="true">
                              <a name="adicionar"><i class="material-icons">add</i></a>
                            </button>
                          </td>
                        </c:otherwise>
                      </c:choose>
                    </c:if>
                  </tr>
                </c:forEach>
              </c:otherwise>
            </c:choose>
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
  </body>

  <script>
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
        },
        error: function () {
          alert("Erro ao adicionar colaboração à descrição. Tente novamente.");
        }
      });
    }

    // SEC-05: escapa dado do usuario (nome/descricao da colaboracao vindos do JSON)
    // antes de injetar no DOM via .html() -> impede XSS armazenado.
    function escapeHtml(s){if(s==null)return '';return String(s).replace(/[&<>"']/g,function(c){return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];});}

    function chamaAjaxEnviar() {
      if ($("#descricao").val() === "") return;

      $.ajax({
        type: 'POST',
        url: 'EnviarColaboracaoServlet',
        data: { descricao: $("#descricao").val() },
        success: function (responseJson) {
          var tr    = $("<tr>");
          var texto = "<td>" + escapeHtml(responseJson.usuario.nome) + "</td>\n"
                    + "<td>" + escapeHtml(responseJson.descricaoIdeiaAtual) + "</td>\n";

          if ("${sessionScope.lider}" === "S") {
            if (responseJson.flSalvado == null) {
              texto += "<td><form name=\"addDescricao\" id=\"addDescricao\" action=\"AddDescricaoServlet\" method=\"POST\">\n"
                     + "<input type=\"hidden\" value=\"" + responseJson.codigo + "\" id=\"colaboracao\" name=\"colaboracao\">\n"
                     + "<button class=\"btn-floating teal darken-1 tooltipped\" data-position=\"right\" data-delay=\"50\" data-tooltip=\"Adicionar à Descrição\">"
                     + "<a type=\"submit\" name=\"adicionar\"><i class=\"material-icons\">add</i></a>"
                     + "</button></form></td>\n";
            } else {
              texto += "<td><button class=\"btn-floating disabled teal darken-1\" disabled=\"true\">"
                     + "<a name=\"adicionar\"><i class=\"material-icons\">add</i></a>"
                     + "</button></td>\n";
            }
          }

          tr.html(texto).appendTo("#tabelaColab");
          $("#descricao").val("");
          numMensagens++;
        },
        error: function () {
          alert("Erro ao enviar colaboração. Tente novamente.");
        }
      });
    }

    <%-- COL-12: usa colaboracoes já em cache — sem 4ª query ao banco --%>
    <%-- COL-13: var declara variável local (não global) --%>
    var numMensagens = ${not empty colaboracoes ? colaboracoes.size() : 0};

    setInterval(function () {
      $.ajax({
        type: 'POST',
        url: 'RetornaMensagensServlet',
        data: { numMensagens: numMensagens },
        success: function (responseJson) {
          if (responseJson == null || responseJson.usuario == null) return;

          var tr    = $("<tr>");
          var texto = "<td>" + escapeHtml(responseJson.usuario.nome) + "</td>\n"
                    + "<td>" + escapeHtml(responseJson.descricaoIdeiaAtual) + "</td>\n";

          if ("${sessionScope.lider}" === "S") {
            if (responseJson.flSalvado == null) {
              texto += "<td><form name=\"addDescricao\" id=\"addDescricao\" action=\"AddDescricaoServlet\" method=\"POST\">\n"
                     + "<input type=\"hidden\" value=\"" + responseJson.codigo + "\" id=\"colaboracao\" name=\"colaboracao\">\n"
                     + "<button class=\"btn-floating teal darken-1 tooltipped\" data-position=\"right\" data-delay=\"50\" data-tooltip=\"Adicionar à Descrição\">"
                     + "<a type=\"submit\" name=\"adicionar\"><i class=\"material-icons\">add</i></a>"
                     + "</button></form></td>\n";
            } else {
              texto += "<td><button class=\"btn-floating disabled teal darken-1\" disabled=\"true\">"
                     + "<a name=\"adicionar\"><i class=\"material-icons\">add</i></a>"
                     + "</button></td>\n";
            }
          }

          tr.html(texto).appendTo("#tabelaColab");
          numMensagens++;
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
