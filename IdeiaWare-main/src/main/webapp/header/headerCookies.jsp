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
<body>
  <nav>
    <div class="nav-wrapper teal darken-2 z-depth-2">
      <a href="index.jsp" class="brand-logo" style="left: 50px">
        <ul style="width:300px" id="nav-mobile" class="left hide-on-med-and-down">
          <div class="row" style="padding-left: 10px">
            <div class="col s1 teal lighten-1" style=" width: 50px;  height: 50px; 
                 margin-top: 5px;  padding: 6px 6px; 
                 border-radius: 100%;  box-sizing: border-box;">
              <img style="display: block;  width: 62%;  margin-left: 7px; margin-top: -1px" src="imagens/idea.png" alt="IdeiaWare"/>
            </div>
            <h1 class=" col s4 center-align title-app">IdeiaWare</h1>

          </div>
        </ul>
      </a>

      <ul id="nav-mobile" class="right hide-on-med-and-down" >
        <li><a class='dropdown-button' data-constrainWidth="false" href='#'  data-beloworigin="true" data-activates='dropdown'>Ideias<i class="material-icons right">arrow_drop_down</i></a></li>
        <ul id='dropdown' class='dropdown-content' >
          <li><a href="cadastro-ideia.jsp"><i class="material-icons">add</i>Cadastrar Ideia</a></i>
          <li><a href="minha-ideia.jsp"><i class="material-icons">account_box</i>Minhas Ideias</a></li>
          <li><a href="lista-ideia.jsp"><i class="material-icons">web_asset</i>Outras Ideias</a></li>
          <li class="divider"></li>
        </ul>
        <li><a href="LogOutServlet">Sair<i style="padding-left: 20px" class="fa fa-sign-out" aria-hidden="true"></i></a></li>
      </ul>

    </div>
  </nav>
  <div class="fixed-action-btn">
    <a class="btn-floating btn-large orange darken-1">
      <i class="large material-icons">menu</i>
    </a>
    <ul>
      <li><a class="btn-floating tooltipped teal lighten-1" href="cadastro-ideia.jsp" data-position="left" data-delay="50" data-tooltip="Cadastrar nova ideia"><i class="material-icons">add</i></a></li>
      <li><a class="btn-floating tooltipped teal lighten-1" href="minha-ideia.jsp" data-position="left" data-delay="50" data-tooltip="Minhas ideias"><i class="material-icons">account_box</i></a></li>
      <li><a class="btn-floating tooltipped  teal lighten-1" href="lista-ideia.jsp" data-position="left" data-delay="50" data-tooltip="Outras ideias"><i class="material-icons">web_asset</i></a></li>
        <c:if  test="${permicao eq 'adm'}" >
        <li><a class="btn-floating tooltipped teal lighten-2" href="validar-ideia.jsp" data-position="left" data-delay="50" data-tooltip="Validar ideias"><i class="material-icons">done</i></a></li>
        <li><a class="btn-floating tooltipped teal lighten-2" href="lista-ideia-gerenciamento.jsp" data-position="left" data-delay="50" data-tooltip="Gerenciar ideias"><i class="material-icons">assessment</i></a></li>
        <li><a class="btn-floating tooltipped teal lighten-2" href="gerenciar-usuarios.jsp" data-position="left" data-delay="50" data-tooltip="Gerenciar usuários"><i class="material-icons">verified_user</i></a></li>
        </c:if>
    </ul>
  </div>
  <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
  <script type="text/javascript" src="js/materialize.min.js"></script>
  <script type="text/javascript" src="js/materialize.js"></script>
</body>