<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="edu.unisc.lic.dao.UsuarioDAO"%>
<%@page import="edu.unisc.lic.domain.Usuario"%>
<%
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    Usuario usuario = null;
    if (session.getAttribute("codigoUsuario") == null) {
        response.sendRedirect("login.jsp");
        return;
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

<jsp:useBean id="storyDAO" class="edu.unisc.lic.dao.StorytellingDAO" />
<jsp:useBean id="ideiaLogDAO" class="edu.unisc.lic.dao.LogColaboracaoDAO" />

<c:set var="story" value="${storyDAO.buscar(sessionScope.storytellingId)}" />
<c:set var="ideiaLog" value="${ideiaLogDAO.buscar(sessionScope.ideiaLog)}" />
<c:set var="descLog" value="${ideiaLogDAO.buscarDescricaoFinal(ideiaLog)}" />

<link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
<link type="text/css" rel="stylesheet" href="css/materialize.min.css"  media="screen,projection"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<link href='https://fonts.googleapis.com/css?family=Condiment' rel='stylesheet'>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
<script type="text/javascript" src="js/Bibliotecas/jquery-3.2.1.js"></script>

<script type="text/javascript" src="js/Bibliotecas/recorder.js"></script>
<script type="text/javascript" src="js/controle.js"></script>
<script type="text/javascript" src="js/controleAdd.js"></script>
<script type="text/javascript" src="js/MenuSuperior/geraPDF.js"></script>

<script type="text/javascript" src="js/materialize.min.js"></script>
<script type="text/javascript" src="js/materialize.js"></script>
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
  a.disabled {pointer-events: none; cursor: default; color: #e8e8e8;}
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
<body>
  <input type="hidden" id="storytelling-data" value="${story.status}" />
  <nav>
    <div class="nav-wrapper indigo lighten-1 z-depth-2">
      <a href="index.jsp" class="brand-logo" style="left: 50px">
        <ul style="width:300px" id="nav-mobile" class="left hide-on-med-and-down">
          <div class="row" style="padding-left: 10px">
            <div class="col s1 indigo lighten-3" style=" width: 50px;  height: 50px; 
                 margin-top: 5px;  padding: 6px 6px; 
                 border-radius: 100%;  box-sizing: border-box;">
              <img style="display: block;  width: 62%;  margin-left: 7px; margin-top: -1px" src="imagens/idea.png" alt="IdeiaWare"/>
            </div>
            <h1 class=" col s4 center-align title-app">IdeiaWare</h1>
          </div>
        </ul>
      </a>

      <ul id="nav-mobile" class="right hide-on-med-and-down">
        <li><a id="salvarBotao">Salvar <i class="material-icons right">save</i></a></li>
        <li><a href="#modalDescricao" class="modal-trigger"  data-target="modalDescricao"> Descrição Ideia <i class="material-icons right">description</i></a></li>
        <li><a href="#" id="exporta" data-constrainWidth="false">Exportar Storytelling<i class="material-icons right">share</i></a></li>
        <li><a href="LogOutServlet">Sair<i style="padding-left: 20px" class="fa fa-sign-out" aria-hidden="true"></i></a></li>
      </ul>
      <div style="text-align: justify">
        <div id="divTitulo"></div>
        <div id="divDescricao"></div>
        <div id="divUsuario"></div>
      </div>
    </div>
  </nav>
</body>