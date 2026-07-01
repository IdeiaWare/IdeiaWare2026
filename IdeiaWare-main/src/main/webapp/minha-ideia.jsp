<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="header/headerCookies.jsp" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>

<html lang="pt-BR">
  <head>
    <title>IdeiaWare - Minhas ideias</title>
    
    <link type="text/css" rel="stylesheet" href="css/colaboracao.css"/>
  </head>

  <body class="center-align teal darken-1">
    <div class="white" style="min-height: 90vh" role="main">
      <div class="container">
        <div style="padding: 10px;" class="white">
          <h1>Minhas Ideias</h1>
          <a href="cadastro-ideia.jsp"><input class="btn orange darken-1" type="submit" value="Cadastrar nova ideia" name="Cadastrar ideia"/></a>
            <jsp:useBean id="ideiaUsuarioDAO" class="edu.unisc.lic.dao.IdeiaUsuarioDAO" />
            <jsp:useBean id="ideiaUsuario" class="edu.unisc.lic.domain.IdeiaUsuario" />
            <jsp:useBean id="data" class="edu.unisc.lic.classes.Data" />
            <jsp:setProperty name="ideiaUsuario" property="usuario" value="${usuarioClasse}" />
          <p/>
          <%-- Busca client-side: filtra as linhas da tabela pelo texto digitado. --%>
          <div class="input-field" style="margin:0 0 6px;">
            <input id="filtro-minhas" type="text" placeholder="Buscar ideia (título, descrição, status...)" aria-label="Buscar ideia">
          </div>
          <table class="responsive-table my-ideas" id="lista-minhas">
            <colgroup>
              <col style="width: 15%;" />
              <col style="width: 25%;" />
              <col style="width: 40%;" />
              <!--<col style="width: 13%;" />-->
              <col style="width: 10%;" />
              <col style="width: 10%;" />
            </colgroup>
            <thead>
              <tr class="highlight" style="font-weight: bold "> 
                <td>Usuário</td>
                <td>Título</td>
                <td>Descrição da Ideia</td>
                <!--<td>Data de criação</td>-->
                <td>Status</td>
                <td></td>
              </tr>
            </thead>
            <tbody>
              <%-- UX-01: estado vazio com mensagem contextual em vez de tabela vazia. --%>
              <c:set var="minhasIdeias" value="${ideiaUsuarioDAO.listarParametro(ideiaUsuario)}" />
              <c:if test="${empty minhasIdeias}">
                <tr><td colspan="5" class="center-align grey-text" style="padding: 30px;">Você ainda não cadastrou nenhuma ideia. Clique em &quot;Cadastrar nova ideia&quot; para começar.</td></tr>
              </c:if>
              <c:forEach var="ideia" items="${minhasIdeias}" varStatus="id">
                  <tr>
                    <td class="name">
                        <div class="ellipsis"><c:out value="${ideia.ideia.usuario.nome}"/></div>
                    </td>
                    <td class="title">
                        <div class="ellipsis"><c:out value="${ideia.ideia.titulo}"/></div>
                    </td>
                    <td class="description">
                        <div class="ellipsis"><c:out value="${ideia.ideia.descricao}"/></div>
                        <i class="material-icons info-icon"  
                           style="color:#00796b; font-size:21px; cursor: pointer;">info_outline</i>
                    </td>
<!--                                    <td style="text-align: center;">${data.formatarData(ideia.ideia.dtCriacao)}</td>-->
                    <td>
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
                              <c:choose>
                                  <c:when test="${ideia.ideia.statusGrupo eq 'AB'}" >
                                      <form name="entrarColaboracao" action="EntrarDetalheServlet" method="POST">
                                        <input hidden="true" value="${ideia.ideia.codigo}" name="codigo" />
                                        <input hidden="true" value="${ideia.flLider}" name="lider" />
                                        <input class="btn teal darken-1" type="submit" value="Entrar"  name="Entrar" />
                                      </form>
                                  </c:when>
                                  <c:otherwise>
                                      <form name="entrarColaboracao" action="EntrarColaboracaoServlet" method="POST">
                                        <input hidden="true" value="${ideia.ideia.codigo}" name="ideiaId" />
                                        <!--<input hidden="true" value="${ideia.flLider}" name="lider" />-->
                                        <input class="btn teal darken-1" type="submit" value="Entrar"  name="Entrar" />
                                      </form>
                                  </c:otherwise>
                              </c:choose>
                          </c:when>
                          <c:when test="${ideia.ideia.status eq 'RE'}">
                              <a href="#modalRejeitar" class="btn modal-trigger red lighten-2" data-motivo="<c:out value='${ideia.ideia.motivoRejeicao}'/>" data-usuario="<c:out value='${ideia.ideia.usuario.nome}'/>" data-titulo="<c:out value='${ideia.ideia.titulo}'/>" data-descricao="<c:out value='${ideia.ideia.descricao}'/>">Motivo</a>
                          </c:when>
                          <c:when test="${ideia.ideia.status eq 'ST'}">
                              <form name="entrarStory" action="EntrarStorytellingServlet" method="POST">
                                <input hidden="true" value="${ideia.ideia.codigo}" name="ideiaId" />
                                <input class="btn indigo lighten-1" type="submit" value="Entrar"  name="StoryTelling" />
                              </form>
                          </c:when>
                          <c:when test="${ideia.ideia.status eq 'CF'}">
                              <form name="entrarCaixa" action="EntrarCaixaServlet" method="POST">
                                <input hidden="true" value="${ideia.ideia.codigo}" name="ideiaId" />
                                <input hidden="true" value="${nome}" name="usuarioNome" />
                                <input class="btn red darken-1" type="submit" value="Entrar"  name="Caixa" />
                              </form>
                          </c:when>
                          <c:when test="${ideia.ideia.status eq 'CV'}">
                              <%-- CAN-ACESSO: botao do Canvas no Minhas Ideias (faltava o case CV ->
                                   ficava cinza pra todos). Opcao A: so o LIDER entra por aqui, pois
                                   o canvas NAO trava edicao por lider no codigo (so pelo acesso).
                                   Ver pendencia "endurecer edicao do canvas" no RELATORIO. --%>
                              <c:choose>
                                  <c:when test="${ideia.flLider eq 'S'}">
                                      <form name="entrarCanva" action="EntrarCanvaServlet" method="POST">
                                        <input hidden="true" value="${ideia.ideia.codigo}" name="ideiaId" />
                                        <input hidden="true" value="${nome}" name="usuarioNome" />
                                        <input class="btn blue darken-1" type="submit" value="Entrar"  name="Canva" />
                                      </form>
                                  </c:when>
                                  <c:otherwise>
                                      <input class="btn disabled" disabled="true" value="Entrar" />
                                  </c:otherwise>
                              </c:choose>
                          </c:when>
                          <c:otherwise>
                              <input class="btn disabled" disabled="true" value="Entrar" />
                          </c:otherwise>
                      </c:choose>
                    </td>
                  </tr>
              </c:forEach>
            </tbody>
          </table>
          <script>
            (function(){var i=document.getElementById('filtro-minhas');if(!i)return;var t=[].slice.call(document.querySelectorAll('#lista-minhas tbody tr'));i.addEventListener('input',function(){var s=i.value.toLowerCase();t.forEach(function(r){r.style.display=r.textContent.toLowerCase().indexOf(s)>-1?'':'none';});});})();
          </script>
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
      </div>
    </div>
  <script src="js/csrf.js"></script>
  </body>

  <%@include file="header/footer.jsp" %>
  <script type="text/javascript">
      $(document).ready(function () {
        $('.modal').modal({
          ready: function (modal, trigger) {
            modal.find('input[name="codigo"]').val(trigger.data('codigo'));

            //                        aqui é onde tudo é inserido na div, dá pra criar divs depois do igual
            // SEC-05: escapa dado do usuario (trigger.data() decodifica entidades -> innerHTML re-parseia = XSS).
            function escapeHtml(s){if(s==null)return '';return String(s).replace(/[&<>"']/g,function(c){return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];});}
            document.getElementById('divTitulo').innerHTML = "<b>Ideia:</b> " + escapeHtml(trigger.data('titulo'));
            document.getElementById('divDescricao').innerHTML = "<b>Descrição:</b> " + escapeHtml(trigger.data('descricao'));
            document.getElementById('divUsuario').innerHTML = "<b>Usuário:</b> " + escapeHtml(trigger.data('usuario'));
            document.getElementById('divMotivo').innerHTML = "<b>Motivo:</b> " + escapeHtml(trigger.data('motivo'));

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