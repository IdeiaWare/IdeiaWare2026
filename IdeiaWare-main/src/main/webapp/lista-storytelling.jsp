<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="edu.unisc.lic.dao.UsuarioDAO"%>
<%@page import="edu.unisc.lic.domain.Usuario"%>
<%
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    Usuario usuario = null;
    if (session.getAttribute("codigoUsuario") == null) {
        response.sendRedirect("login.jsp");
        return; // RET-13: encerra apos o redirect de login (evita 2o sendRedirect e render com usuario null)
    } else {
        usuario = usuarioDAO.buscar((Long) session.getAttribute("codigoUsuario"));
    }
    if (usuario != null) {
        request.setAttribute("idUsuario", usuario.getCodigo());
        request.setAttribute("usuarioClasse", usuario);
        request.setAttribute("nome", usuario.getNome());
        request.setAttribute("permicao", usuario.getPermissao());
        request.setAttribute("usuario", usuario.getUsuario());
    }
%>

<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<link type="text/css" rel="stylesheet" href="css/materialize.min.css" media="screen,projection"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<link href='https://fonts.googleapis.com/css?family=Condiment' rel='stylesheet'>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
<script type="text/javascript" src="js/Bibliotecas/jquery-3.2.1.js"></script>
<script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="js/materialize.min.js"></script>
<script type="text/javascript" src="js/materialize.js"></script>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<style>
  .title-app { color:#fff; font-family: 'Condiment'; font-size: 42px; line-height: 25px }
</style>
<html lang="pt-BR">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>IdeiaWare - Lista Storytelling</title>
  </head>
  <body class="indigo lighten-5">
    <%-- UX-VOLTAR-V2: icone circular flutuante, faltava saida de volta pra tela inicial (index.jsp). --%>
    <a href="index.jsp" class="btn-floating btn-large indigo lighten-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
    <nav>
      <div class="nav-wrapper indigo lighten-1 z-depth-2">
        <a href="index.jsp" class="brand-logo" style="left: 50px">
          <ul style="width:300px" id="nav-logo" class="left hide-on-med-and-down">
            <div class="row" style="padding-left: 10px">
              <div class="col s1 indigo lighten-3" style="width:50px; height:50px;
                   margin-top:5px; padding:6px; border-radius:100%; box-sizing:border-box;">
                <img style="display:block; width:62%; margin-left:7px; margin-top:-1px" src="imagens/idea.png" alt="IdeiaWare"/>
              </div>
              <h1 class="col s4 center-align title-app">IdeiaWare</h1>
            </div>
          </ul>
        </a>
        <ul id="nav-mobile" class="right hide-on-med-and-down">
          <li><a href="LogOutServlet">Sair<i style="padding-left:20px" class="fa fa-sign-out" aria-hidden="true"></i></a></li>
        </ul>
      </div>
    </nav>

    <div class="white" style="min-height: 90vh" role="main">
      <div class="container">
        <div style="padding: 10px; border-radius: 0.2em" class="white">
          <h1 class="center">Storytelling</h1>

          <jsp:useBean id="iuDAO"  class="edu.unisc.lic.dao.IdeiaUsuarioDAO" />
          <jsp:useBean id="logDAO" class="edu.unisc.lic.dao.LogColaboracaoDAO" />
          <jsp:useBean id="data"   class="edu.unisc.lic.classes.Data" />
          <jsp:useBean id="log"    class="edu.unisc.lic.domain.LogColaboracao" />
          <jsp:useBean id="iu"     class="edu.unisc.lic.domain.IdeiaUsuario" />
          <jsp:setProperty name="iu" property="usuario" value="${usuarioClasse}" />

          <%-- STR-09: inclui lideres e participantes (antes, so lideres viam o storytelling aqui). --%>
          <c:set var="ideiasStory" value="${iuDAO.listarTodasIdeiasStorytelling(iu)}" />
          <%-- UX-01: collapsible so aparece com conteudo (vazia, o Materialize desenha a borda mesmo assim). --%>
          <c:if test="${not empty ideiasStory}">
          <ul class="collapsible" data-collapsible="accordion">
            <c:forEach var="iuItem" items="${ideiasStory}">
              <jsp:setProperty name="log" property="ideia" value="${iuItem.ideia}" />
              <c:set var="iLog" value="${logDAO.buscarDescricaoFinal(log)}" />
              <li>
                <div class="collapsible-header">
                  <%-- M.5: cabecalho so com Usuario + Titulo (descricao ja aparece no corpo ao clicar no +); titulo (max 50) quebra. --%>
                  <span style="width:30%; text-align:left;" class="truncate"><c:out value="${iuItem.usuario.nome}"/></span>
                  <span style="width:65%; text-align:left; white-space: normal; word-break: break-word;"><c:out value="${iuItem.ideia.titulo}"/></span>
                  <i class="material-icons" style="width:5%; text-align:right;">add</i>
                </div>
                <div class="collapsible-body">
                  <div style="text-align:justify;">
                    <b>Usuário: </b><c:out value="${iuItem.usuario.nome}"/><br>
                    <b>Título: </b><c:out value="${iuItem.ideia.titulo}"/><br>
                    <b>Descrição: </b><br>
                    <%-- STR-03: removida referência à variável inexistente 'storytell' --%>
                    <c:choose>
                      <c:when test="${not empty iLog}"><c:out value="${iLog.descricao}"/></c:when>
                      <c:otherwise><c:out value="${iuItem.ideia.descricao}"/></c:otherwise>
                    </c:choose>
                    <br>
                    <b>Data de criação: </b>${data.formatarData(iuItem.ideia.dtCriacao)}<br>
                    <b>Papel: </b>
                    <c:choose>
                      <c:when test="${iuItem.flLider eq 'S'}">Líder</c:when>
                      <c:otherwise>Participante</c:otherwise>
                    </c:choose><br>
                  </div>
                  <div style="text-align:right; margin-top:10px;">
                    <form name="entrarStory" action="EntrarStorytellingServlet" method="POST">
                      <input type="hidden" value="${iuItem.ideia.codigo}" name="ideiaId" />
                      <%-- UX-COR: indigo lighten-1 (cor exata do header/footer desta tela, nao o darken-1). --%>
                      <input class="btn indigo lighten-1" type="submit" value="Entrar" name="StoryTelling" />
                    </form>
                  </div>
                </div>
              </li>
            </c:forEach>
          </ul>
          </c:if>
          <%-- UX-01: estado vazio com mensagem contextual. --%>
          <c:if test="${empty ideiasStory}">
            <div class="center-align grey-text" style="padding: 40px 20px;">
              <i class="material-icons" style="font-size: 3rem; display:block;">brush</i>
              Você ainda não tem ideias em Storytelling.
            </div>
          </c:if>
        </div>
      </div>
    </div>

    <script type="text/javascript">
      $(document).ready(function () {
        // STR-04: removido JS que referenciava elementos DOM inexistentes nesta página
        $('.collapsible').collapsible();
      });
    </script>

    <footer class="center indigo lighten-1 page-footer">
      <div class="container">
        <div class="row">
          <i class="small material-icons">account_circle</i>
          <h6 class="white-text"><c:out value="${nome}"/></h6>
        </div>
      </div>
      <div class="footer-copyright">
        <div class="container">© 2026 IdeiaWare UNISC</div>
      </div>
    </footer>
  <script src="js/csrf.js"></script>
  </body>
</html>
