<%@page import="edu.unisc.lic.domain.IdeiaUsuario"%>
<%@page import="java.util.List"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
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

    // RET-05: evita NullPointerException caso a conta tenha sido removida
    // enquanto a sessão ainda estava ativa.
    if (usuario == null || !usuario.getPermissao().equals("adm")) {
        response.sendRedirect("index.jsp");
        return;
    }
%>

<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<link type="text/css" rel="stylesheet" href="css/materialize.min.css"  media="screen,projection"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<link href='https://fonts.googleapis.com/css?family=Condiment' rel='stylesheet'>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
<style>
  .title-app {color:#fff; font-family: 'Condiment'; font-size: 42px; line-height: 25px}
  .title-app2 {font-family: 'Condiment'; font-size: 100px;}
  .card .icon {font-size:50px;}
  .card-content .card-title {color:#fff !important;}
  .card a {color:#fff;}
  .fa-sign-out {color:#fff;}
  .icon .fa-circle {color:#fff;}
  .new-idea .icon .fa-lightbulb-o {color:#4db6ac;}
  .storytelling .icon .fa-pencil {color:#5c6bc0}
  .toolkit .icon .fa-briefcase {color:#c62828;}
  .knowledge .icon .fa-database {color:#0288d1;}
  .fa-hand-o-right {border-radius:50%; box-shadow: 0 0 0 rgba(255,255,255, 0.4); animation: pulse 2s infinite;}
  .fa-hand-o-right:hover {animation: none;}

  @-webkit-keyframes pulse {
      0% {
          -webkit-box-shadow: 0 0 0 0 rgba(255,255,255, 0.4);
      }
      70% {
          -webkit-box-shadow: 0 0 0 10px rgba(255,255,255, 0);
      }
      100% {
          -webkit-box-shadow: 0 0 0 0 rgba(255,255,255, 0);
      }
  }
  @keyframes pulse {
      0% {
          -moz-box-shadow: 0 0 0 0 rgba(255,255,255, 0.4);
          box-shadow: 0 0 0 0 rgba(255,255,255, 0.4);
      }
      70% {
          -moz-box-shadow: 0 0 0 10px rgba(255,255,255, 0);
          box-shadow: 0 0 0 10px rgba(255,255,255, 0);
      }
      100% {
          -moz-box-shadow: 0 0 0 0 rgba(255,255,255, 0);
          box-shadow: 0 0 0 0 rgba(255,255,255, 0);
      }
  }
</style>
<!DOCTYPE html>
<html lang="pt-BR">
  <head>
    <title>IdeiaWare - Gerenciamento de Ideias</title>
  </head>
  <body class="center-align ">
    <nav>
      <div class="nav-wrapper light-blue darken-2 knowledge z-depth-2">
        <a href="index.jsp" class="brand-logo" style="left: 50px">
          <ul style="width:300px" id="nav-mobile" class="left hide-on-med-and-down">
            <div class="row" style="padding-left: 10px">
              <div class="col s1 light-blue knowledge" style=" width: 50px;  height: 50px; 
                   margin-top: 5px;  padding: 6px 6px; 
                   border-radius: 100%;  box-sizing: border-box;">
                <img style="display: block;  width: 62%;  margin-left: 7px; margin-top: -1px" src="imagens/idea.png" alt="IdeiaWare"/>
              </div>
              <h1 class=" col s4 center-align title-app">IdeiaWare</h1>
            </div>
          </ul>
        </a>
        <ul id="nav-mobile" class="right hide-on-med-and-down" >
          <c:if  test="${permicao eq 'adm'}" >
              <li><a class='dropdown-button' data-constrainWidth="false" href='#'  data-beloworigin="true" data-activates='dropdown2'>Gestor<i class="material-icons right">arrow_drop_down</i></a></li>
              <ul id='dropdown2' class='dropdown-content'>
                <li><a href="lista-ideia-gerenciamento.jsp"><i class="material-icons">assessment</i>Gerenciar Ideias</a></li>
                  <jsp:useBean id="tesDao" class="edu.unisc.lic.dao.IdeiaDAO" />
                  <jsp:useBean id="tesX" class="edu.unisc.lic.domain.Ideia" />
                  <jsp:setProperty name="tesX" property="status" value="PE"/>
                  <c:choose>
                      <c:when test="${tesDao.listarParametro(tesX).size() eq 0}" >
                      <li class="tooltipped disabled" data-position="left" data-delay="50" data-tooltip="Sem ideias no momento"><a class="btn-flat disabled" disabled="true"><i class="material-icons">done</i>Validar Ideias</a></li>
                      </c:when>
                      <c:otherwise>
                      <li><a href="validar-ideia.jsp"><i class="material-icons">done</i>Validar Ideias</a></li>
                      </c:otherwise>
                  </c:choose>
              </ul>
          </c:if>
          <li><a href="LogOutServlet">Sair<i style="padding-left: 20px" class="fa fa-sign-out" aria-hidden="true"></i></a></li>
        </ul>

      </div>
    </nav>
    <div class="fixed-action-btn">
        <a class="btn-floating btn-large deep-orange lighten-2">
            <i class="large material-icons">menu</i>
        </a>
        <ul>
            <c:if  test="${permicao eq 'adm'}" >
                <li><a class="btn-floating tooltipped light-blue darken-1" href="validar-ideia.jsp" data-position="left" data-delay="50" data-tooltip="Validar ideias"><i class="material-icons">done</i></a></li>
                <li><a class="btn-floating tooltipped light-blue darken-1" href="lista-ideia-gerenciamento.jsp" data-position="left" data-delay="50" data-tooltip="Gerenciar ideias"><i class="material-icons">assessment</i></a></li>
            </c:if>
        </ul>
    </div>
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="js/materialize.min.js"></script>
    <script type="text/javascript" src="js/materialize.js"></script>
    <div class="white" style="min-height: 90vh" role="main">
      <div class="container">
        <div style="padding: 10px; border-radius: 0.2em" class="white">

          <!--instanciando objetos-->
          <jsp:useBean id="ideiaDao" class="edu.unisc.lic.dao.IdeiaDAO" />
          <%--<jsp:setProperty name="ideiaUsuario" property="usuario" value="${usuarioClasse}" />--%>

          <h1>Gerenciamento de Ideias</h1>
          <%-- Busca client-side: filtra as linhas da tabela pelo texto digitado. --%>
          <div class="input-field" style="margin:0 0 6px;">
            <input id="filtro-gerenciamento" type="text" placeholder="Buscar ideia (título, descrição, status...)" aria-label="Buscar ideia">
          </div>
          <!--inicio da tabela-->
          <table class="responsive-table" id="lista-gerenciamento">
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
                <td>Situação</td>
                <td></td>
              </tr>
            </thead>
            <tbody>
              <!--inicio do corpo-->
              <%-- UX-01: estado vazio com mensagem contextual. --%>
              <c:set var="todasIdeias" value="${ideiaDao.listar()}" />
              <c:if test="${empty todasIdeias}">
                <tr><td colspan="5" class="center-align grey-text" style="padding: 30px;">Nenhuma ideia cadastrada no sistema ainda.</td></tr>
              </c:if>
              <c:forEach var="ideia" items="${todasIdeias}" varStatus="id">
                  <tr>
                    <td style="white-space: nowrap; text-overflow:ellipsis; overflow: hidden; max-width:1px;"><c:out value="${ideia.usuario.nome}"/></td>
                    <td style="white-space: nowrap; text-overflow:ellipsis; overflow: hidden; max-width:1px;"><c:out value="${ideia.titulo}"/></td>
                    <td style="word-break: break-word; white-space: normal;"><c:out value="${ideia.descricao}"/></td>
                    <td>
                      <c:choose>
                          <c:when test="${ideia.status eq 'VA'}">Validada</c:when>   
                          <c:when test="${ideia.status eq 'RE'}">Rejeitada</c:when>
                          <c:when test="${ideia.status eq 'PE'}">Pendente</c:when>
                          <c:when test="${ideia.status eq 'DE'}">Em desenvolvimento</c:when>
                          <c:when test="${ideia.status eq 'ST'}">Storytelling</c:when>
                          <c:when test="${ideia.status eq 'CF'}">Caixa de Ferramentas</c:when>
                          <c:when test="${ideia.status eq 'CV'}">Canvas</c:when>
                          <c:when test="${ideia.status eq 'FN'}">Finalizado</c:when>
                      </c:choose>
                    </td>
                    <td>
                      <form name="entrarGerenciamento" action="GerenciarIdeiaServlet"  method="POST">
                        <input hidden="true" value="${ideia.codigo}" name="ideiaId" />
                        <input class="btn deep-orange lighten-2" type="submit" value="Detalhes" name="Entrar" />
                      </form>
                    </td>
                  </tr>
              </c:forEach>
            </tbody>
          </table>
          <script>
            (function(){var i=document.getElementById('filtro-gerenciamento');if(!i)return;var t=[].slice.call(document.querySelectorAll('#lista-gerenciamento tbody tr'));i.addEventListener('input',function(){var s=i.value.toLowerCase();t.forEach(function(r){r.style.display=r.textContent.toLowerCase().indexOf(s)>-1?'':'none';});});})();
          </script>
        </div>
      </div>
    </div>
  <script src="js/csrf.js"></script>
  </body>
  <footer class="light-blue darken-2 page-footer">
    <div class="container">
      <div class="row">
        <i class="small material-icons">account_circle</i><h6 class="white-text"> <c:out value="${nome}"/></h6>
      </div>
    </div>
    <div class="footer-copyright">
      <div class="container">
        © 2026 IdeiaWare UNISC
      </div>
    </div>
  </footer>
</html>