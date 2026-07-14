<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="edu.unisc.lic.domain.Usuario"%>
<%@page import="edu.unisc.lic.dao.UsuarioDAO"%>
<%
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    Usuario usuario = null;
    if (session.getAttribute("codigoUsuario") == null) {
        response.sendRedirect("login.jsp");
    } else {
        usuario = usuarioDAO.buscar((Long) session.getAttribute("codigoUsuario"));
        request.setAttribute("usuario", usuario);
    }

%>

<!DOCTYPE html>
<html lang="pt-BR">

  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>IdeiaWare - Início</title>
  </head>

  <!-- Compiled and minified CSS -->
  <link rel="stylesheet" href="css/materialize.min.css">
  <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
  <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
  <link href='https://fonts.googleapis.com/css?family=Condiment' rel='stylesheet'>

  <script
      src="https://code.jquery.com/jquery-3.2.1.min.js"
      integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
  crossorigin="anonymous"></script>
  <!-- Compiled and minified JavaScript -->
  <script type="text/javascript" src="js/materialize.min.js"></script>
  <script type="text/javascript" src="js/materialize.js"></script>

  <style>
    .title-app {color:#fff; font-family: 'Condiment'; font-size: 42px; line-height: 25px}
    .title-app2 {font-family: 'Condiment'; font-size: 100px;}
    .card .icon {font-size:50px;}
    .card-content .card-title {color:#fff !important;}
    .card a {color:#fff;}
    .fa-sign-out {color: #fff;}
    .icon .fa-circle {color:#fff;}
    .new-idea .icon .fa-comments {color:#4db6ac;}
    .storytelling .icon .fa-pencil {color:#5c6bc0}
    .toolkit .icon .fa-briefcase {color:#c62828;}
    .canva .icon .fa-th {color:#0D47A1;}
    .knowledge .icon .fa-database {color:#0288d1;}
    .knowledge .icon .fa-banco {color:#757575;}
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
  <body class="blue-grey lighten-5">
    <main>
    <nav>
      <div class="nav-wrapper blue-grey lighten-1 z-depth-2">
        <a href="index.jsp" class="brand-logo" style="left: 50px">
          <ul style="width:300px" id="nav-logo" class="left hide-on-med-and-down">
            <div class="row" style="padding-left: 10px">
              <div class="col s1 blue-grey lighten-3" style=" width: 50px;  height: 50px; 
                   margin-top: 5px;  padding: 6px 6px; 
                   border-radius: 100%;  box-sizing: border-box;">
                <img style="display: block;  width: 62%;  margin-left: 7px; margin-top: -1px" src="imagens/idea.png" alt="IdeiaWare"/>
              </div>
              <h1 class=" col s4 center-align title-app">IdeiaWare</h1>

            </div>
          </ul>
        </a>
        <ul id="nav-mobile" class="right hide-on-med-and-down" >
          <%-- FOOTER-XSS: nome do usuario (texto livre de cadastro) escapado com c:out. --%>
          <li><a href="index-perfil.jsp"><c:out value="${sessionScope.nomeUsuario}"/><i style="padding-left: 10px" class="fa fa-user-o" aria-hidden="true"></i></a></li>
          <%-- UX-GERENCIAR-USUARIOS: nao pertence a nenhum modulo, mora aqui na home em vez do header de algum modulo. --%>
          <c:if test="${usuario.permissao eq 'adm'}">
            <li><a href="gerenciar-usuarios.jsp">Gerenciar Usuários<i style="padding-left: 10px" class="fa fa-users" aria-hidden="true"></i></a></li>
          </c:if>
          <li><a href="LogOutServlet">Sair<i style="padding-left: 20px" class="fa fa-sign-out" aria-hidden="true"></i></a></li>
        </ul>
      </div>

    </nav>
    <div class="container " style="min-height: 88vh">
      <div class="row">
        <div class="col s12" style="padding-top: 20px">
          <h1 class=" blue-grey-text text-darken-2 center-align title-app2">IdeiaWare</h1>
        </div>
      </div>

      <div class="row">
        <div class="col s7">
          <div class="card teal lighten-2 sticky-action new-idea">
            <a href="index-colaboracao.jsp">
              <div class="card-image " style="padding-top: 15px;padding-bottom: 15px;">
                <div class="icon center-align">
                  <span class="fa-stack fa-lg">
                    <i class="fa fa-circle fa-stack-2x"></i>
                    <i class="fa fa-comments fa-stack-1x fa-inverse"></i>
                  </span>
                </div>
              </div>
            </a>
            <div class="card-content">
              <span class="card-title activator grey-text text-darken-4">Colaboração<i class="material-icons right">more_vert</i></span>
              <p><a href="index-colaboracao.jsp"><i class="fa fa-hand-o-right" aria-hidden="true"></i> Crie novas ideias ou colabore nas já existentes!</a></p>
            </div>
            <div class="card-reveal">
              <span class="card-title grey-text text-darken-4">Colaboração<i class="material-icons right">close</i></span>
              <p>Desenvolva, crie e inove aqui com suas ideias!</p>
            </div>
          </div>
        </div>
        <div class="col s5">
          <div class="card indigo lighten-1 sticky-action storytelling">
            <a href="lista-storytelling.jsp">
              <div class="card-image " style="padding-top: 15px;padding-bottom: 15px;">
                <div class="icon center-align">
                  <span class="fa-stack fa-lg">
                    <i class="fa fa-circle fa-stack-2x"></i>
                    <i class="fa fa-pencil fa-stack-1x fa-inverse"></i>
                  </span>
                </div>
              </div>
            </a>
            <div class="card-content">
              <span class="card-title activator grey-text text-darken-4">Storytelling<i class="material-icons right">more_vert</i></span>
              <p><a href="lista-storytelling.jsp"><i class="fa fa-hand-o-right" aria-hidden="true"></i> Apresente suas ideias utilizando formas, imagens, audios e mais!</a></p>
            </div>
            <div class="card-reveal">
              <span class="card-title grey-text text-darken-4">Storytelling<i class="material-icons right">close</i></span>
              <p>Inove e desenvolva aqui suas ideias com o uso de Storytellings!</p>
            </div>
          </div>
        </div>
        <p/>
        <div class="col s5">
          <div class="card red darken-3 sticky-action toolkit">
            <a href="lista-caixa-de-ferramentas.jsp">
              <div class="card-image " style="padding-top: 15px;padding-bottom: 15px;">
                <div class="icon center-align">
                  <span class="fa-stack fa-lg">
                    <i class="fa fa-circle fa-stack-2x"></i>
                    <i class="fa fa-briefcase fa-stack-1x fa-inverse"></i>
                  </span>
                </div>
              </div>
            </a>
            <div class="card-content">
              <span class="card-title activator grey-text text-darken-4">Caixa de Ferramentas<i class="material-icons right">more_vert</i></span>
              <p><a href="lista-caixa-de-ferramentas.jsp"><i class="fa fa-hand-o-right" aria-hidden="true"></i> Descubra como as pessoas irão reagir à suas ideias!</a></p>
            </div>
            <div class="card-reveal">
              <span class="card-title grey-text text-darken-4">Caixa de Ferramentas<i class="material-icons right">close</i></span>
              <p>Inove e desenvolva aqui as suas ideais com esta Caixa de Ferramentas prática!</p>
            </div>
          </div>
        </div>
        <div class="col s7">
          <div class="card blue darken-4 sticky-action canva">
            <a href="lista-canvas.jsp">
              <div class="card-image " style="padding-top: 15px;padding-bottom: 15px;">
                <div class="icon center-align">
                  <span class="fa-stack fa-lg">
                    <i class="fa fa-circle fa-stack-2x"></i>
                    <i class="fa fa-th fa-stack-1x fa-inverse"></i>
                  </span>
                </div>
              </div>
            </a>
            <div class="card-content">
              <span class="card-title activator grey-text text-darken-4">Canva<i class="material-icons right">more_vert</i></span>
              <p><a href="lista-canvas.jsp"><i class="fa fa-hand-o-right" aria-hidden="true"></i> Projete o seu modelo de negócios !</a></p>
            </div>
            <div class="card-reveal">
              <span class="card-title grey-text text-darken-4">Canva<i class="material-icons right">close</i></span>
              <p>Utilize desse método estratégico para esboçar melhor sua ideia!</p>
            </div>
          </div>
        </div>
        <%-- RETENCAO-ACESSO: qualquer usuario acessa (antes, so admin); filtro por participacao no servlet. --%>
        <div class="col s12">
          <div class="card light-blue darken-1 sticky-action knowledge">
            <a href="lista-ideia-gerenciamento.jsp">
              <div class="card-image " style="padding-top: 15px;padding-bottom: 15px;">
                <div class="icon center-align">
                  <span class="fa-stack fa-lg">
                    <i class="fa fa-circle fa-stack-2x"></i>
                    <i class="fa fa-database fa-stack-1x fa-inverse"></i>
                  </span>
                </div>
              </div>
            </a>
            <div class="card-content">
              <span class="card-title activator grey-text text-darken-4">Retenção do conhecimento<i class="material-icons right">more_vert</i></span>
              <p><a href="lista-ideia-gerenciamento.jsp"><i class="fa fa-hand-o-right" aria-hidden="true"></i> Acompanhe o histórico das suas ideias!</a></p>
            </div>
            <div class="card-reveal">
              <span class="card-title grey-text text-darken-4">Retenção do conhecimento<i class="material-icons right">close</i></span>
              <p>Veja aqui todas suas ideias e informações gerais do seu desenvolvimento ideacional!</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  <script src="js/csrf.js"></script>
  </main>
    </body>
  <footer class="center blue-grey lighten-1 page-footer">
    <div class="container">
      <div class="row">
        <i class="small material-icons">account_circle</i><h6 class="white-text"> <c:out value="${usuario.nome}"/></h6>
      </div>
    </div>
    <div class="footer-copyright">
      <div class="container">
        © 2026 IdeiaWare UNISC
      </div>
    </div>
  </footer>
</html>