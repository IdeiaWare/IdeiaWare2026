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

    // RET-05: evita NullPointerException caso a conta tenha sido removida com a sessao ainda ativa.
    if (usuario == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    // RETENCAO-ACESSO: QUALQUER logado ve a Retencao; admin ve TODAS, colaborador so as que participa (mesma logica do EntrarCaixaServlet).
    List<edu.unisc.lic.domain.Ideia> todasIdeiasList;
    if ("adm".equals(usuario.getPermissao())) {
        todasIdeiasList = new edu.unisc.lic.dao.IdeiaDAO().listar();
    } else {
        List<IdeiaUsuario> vinculos = new edu.unisc.lic.dao.IdeiaUsuarioDAO()
                .listarParametro(new IdeiaUsuario(usuario, null, null));
        todasIdeiasList = new java.util.ArrayList<>();
        for (IdeiaUsuario iu : vinculos) {
            todasIdeiasList.add(iu.getIdeia());
        }
    }
    request.setAttribute("todasIdeiasScriptlet", todasIdeiasList);
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
    <%-- UX-VOLTAR-V2: icone circular flutuante, faltava saida de volta pra tela inicial (index.jsp). --%>
    <a href="index.jsp" class="btn-floating btn-large light-blue darken-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
    <nav>
      <div class="nav-wrapper light-blue darken-2 knowledge z-depth-2">
        <a href="index.jsp" class="brand-logo" style="left: 50px">
          <ul style="width:300px" id="nav-logo" class="left hide-on-med-and-down">
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
          <li><a href="LogOutServlet">Sair<i style="padding-left: 20px" class="fa fa-sign-out" aria-hidden="true"></i></a></li>
        </ul>

      </div>
    </nav>
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="js/materialize.min.js"></script>
    <script type="text/javascript" src="js/materialize.js"></script>
    <div class="white" style="min-height: 90vh" role="main">
      <div class="container">
        <div style="padding: 10px; border-radius: 0.2em" class="white">

          <jsp:useBean id="ideiaDao" class="edu.unisc.lic.dao.IdeiaDAO" />

          <h1>Gerenciamento de Ideias</h1>
          <c:set var="todasIdeias" value="${todasIdeiasScriptlet}" />

          <%-- UX-01: estado vazio com mensagem contextual (busca e tabela so aparecem quando ha ideias). --%>
          <c:if test="${empty todasIdeias}">
            <div class="center-align grey-text" style="padding: 40px 20px;">
              <i class="material-icons" style="font-size: 3rem; display:block;">assignment</i>
              Nenhuma ideia cadastrada no sistema ainda.
            </div>
          </c:if>

          <c:if test="${not empty todasIdeias}">
          <%-- M.15: retencao convertida de <table> pra collapsible (mesmo padrao de Storytelling/Canvas/Caixa). --%>
          <div class="input-field" style="margin:0 0 6px;">
            <input id="filtro-gerenciamento" type="text" placeholder="Buscar ideia (título, status)" aria-label="Buscar ideia">
          </div>
          <ul class="collapsible" data-collapsible="accordion" id="lista-gerenciamento">
            <c:forEach var="ideia" items="${todasIdeias}">
              <c:set var="statusLabel"><c:choose><c:when test="${ideia.status eq 'VA'}">Validada</c:when><c:when test="${ideia.status eq 'RE'}">Rejeitada</c:when><c:when test="${ideia.status eq 'PE'}">Pendente</c:when><c:when test="${ideia.status eq 'DE'}">Em desenvolvimento</c:when><c:when test="${ideia.status eq 'ST'}">Storytelling</c:when><c:when test="${ideia.status eq 'CF'}">Caixa de Ferramentas</c:when><c:when test="${ideia.status eq 'CV'}">Canvas</c:when><c:when test="${ideia.status eq 'FN'}">Finalizado</c:when></c:choose></c:set>
              <li>
                <div class="collapsible-header">
                  <span style="width:25%; text-align:left;" class="truncate"><c:out value="${ideia.usuario.nome}"/></span>
                  <span style="width:45%; text-align:left; white-space:normal; word-break:break-word;"><c:out value="${ideia.titulo}"/></span>
                  <span style="width:25%; text-align:left;">${statusLabel}</span>
                  <i class="material-icons" style="width:5%; text-align:right;">add</i>
                </div>
                <div class="collapsible-body">
                  <div style="text-align:justify;">
                    <b>Usuário: </b><c:out value="${ideia.usuario.nome}"/><br>
                    <b>Título: </b><c:out value="${ideia.titulo}"/><br>
                    <b>Descrição: </b><c:out value="${ideia.descricao}"/><br>
                    <b>Situação: </b>${statusLabel}<br>
                  </div>
                  <div style="text-align:right; margin-top:10px;">
                    <form name="entrarGerenciamento" action="GerenciarIdeiaServlet" method="POST">
                      <input hidden="true" value="${ideia.codigo}" name="ideiaId" />
                      <input class="btn deep-orange lighten-2" type="submit" value="Detalhes" name="Detalhes" />
                    </form>
                  </div>
                </div>
              </li>
            </c:forEach>
          </ul>
          <script>
            (function(){var i=document.getElementById('filtro-gerenciamento');if(!i)return;var t=[].slice.call(document.querySelectorAll('#lista-gerenciamento > li'));i.addEventListener('input',function(){var s=i.value.toLowerCase();t.forEach(function(r){r.style.display=r.textContent.toLowerCase().indexOf(s)>-1?'':'none';});});})();
            $(document).ready(function(){ $('.collapsible').collapsible(); });
          </script>
          </c:if>
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