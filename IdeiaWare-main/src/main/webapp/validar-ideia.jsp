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
    
    // COLM-01: evita NullPointerException caso a conta tenha sido removida
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
    <title>IdeiaWare - Validar Ideias</title>
  </head>
  <body class="center-align light-blue darken-1 knowledge">
    <nav>
      <div class="nav-wrapper light-blue darken-2 knowledge">
        <a href="index.jsp" class="brand-logo" style="left: 50px">
          <ul style="width:300px" id="nav-mobile" class="left hide-on-med-and-down">
            <div class="row" style="padding-left: 10px">
              <div class="col s1 light-blue darken-1 knowledge" style=" width: 50px;  height: 50px; 
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
          </c:if>
      </ul>
    </div>
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="js/materialize.min.js"></script>
    <script type="text/javascript" src="js/materialize.js"></script>
    
    
    <div class="white" style="min-height: 90vh" role="main">
      <div class="container">
        <div style="padding: 10px; border-radius: 0.2em" class="white">
          <h1>Validar Ideias</h1>
          <jsp:useBean id="ideiaDAO" class="edu.unisc.lic.dao.IdeiaDAO" />
          <jsp:useBean id="ideia2" class="edu.unisc.lic.domain.Ideia" />
          <jsp:setProperty name="ideia2" property="status" value="PE"/>
          <jsp:useBean id="data" class="edu.unisc.lic.classes.Data" />
          <ul class="collapsible" data-collapsible="accordion">
            <c:set var="ideiasPendentes" value="${ideiaDAO.listarParametro(ideia2)}" />
            <c:forEach var="ideia" items="${ideiasPendentes}" varStatus="id">
                <li>
                  <div class="collapsible-header">
                    <span style="width: 20%; text-align: left;" class="truncate"    ><c:out value="${ideia.usuario.nome}"/></span>
                    <span style="width: 25%; text-align: left;" class="truncate"    ><c:out value="${ideia.titulo}"/></span>
                    <span style="width: 50%; text-align: left;" class="truncate"    ><c:out value="${ideia.descricao}"/></span>
                    <i class="material-icons" style="width: 5%; text-align: right; ">add</i>
                  </div>
                  <div class="collapsible-body">
                    <div id="parteCima" style="text-align: justify;">
                      <b>Usuário: </b> <c:out value="${ideia.usuario.nome}"/><br />
                      <b>Título: </b> <c:out value="${ideia.titulo}"/><br />
                      <b>Descrição inicial: </b><c:out value="${ideia.descricao}"/><br />
                      <b>Data de criação: </b>${data.formatarData(ideia.dtCriacao)}<br />
                    </div>
                    <div id="rodape" style="text-align: right;">
                      <span>
                        <form name="validarIdeia" action="ValidarIdeiaServlet" method="POST">
                          <input hidden="true" value="${ideia.codigo}" name="codigo" />
                          <input hidden="true" value="${sessionScope.codigoUsuario}" name="codUsuario" />
                          <button class="btn green lighten-1" type="submit" value="validar" name="validar">Validar</button>
                          <a href="#modalRejeitar" class="btn modal-trigger red lighten-1" data-target="modalRejeitar" data-codigo="${ideia.codigo}" data-titulo="<c:out value='${ideia.titulo}'/>" data-descricao="<c:out value='${ideia.descricao}'/>" data-usuario="<c:out value='${ideia.usuario.nome}'/>">Rejeitar</a>
                        </form>
                      </span>
                    </div>
                  </div>
                </li>
            </c:forEach>
          </ul>
          <%-- UX-01: estado vazio com mensagem contextual. --%>
          <c:if test="${empty ideiasPendentes}">
            <div class="center-align grey-text" style="padding: 40px 20px;">
              <i class="material-icons" style="font-size: 3rem; display:block;">done_all</i>
              Não há ideias pendentes de validação no momento.
            </div>
          </c:if>
        </div>
      </div>
    </div>

    <!--modal para rejeição das ideias-->
    <div id="modalRejeitar" class="modal">
      <form name="validarIdeia" action="ValidarIdeiaServlet" onsubmit="return check(this)" method="POST">
        <input hidden="true" value="${sessionScope.codigoUsuario}" name="codUsuario" />
        <div class="modal-content">
          <h4>Rejeição da ideia</h4>
          <p>
          <div class="input-field">
            <!--nessa parte do código é onde vai ser inserido o que foi passado para a modal-->
            <div style="text-align: justify">
              <div id="divTitulo"></div>
              <div id="divDescricao"></div>
              <div id="divUsuario"></div>
            </div>

            <!--colocando o id em um input-->
            <input id="codigo" type="text" name="codigo" hidden="true"/>

            <div class="input-field col-s12">
              <textarea id="motivo" type="text" class="materialize-textarea" name="motivo" data-length="200" maxlength="200"></textarea>
              <label for="motivo">Motivo</label>
            </div>
          </div>
          </p>
        </div>
        <div class="modal-footer ">
          <div style="width: 95%">
            <a href="#!" class="modal-action modal-close btn-flat">Cancelar</a>
            <button class="btn red lighten-1 btn" type="submit" value="rejeitar" name="validar">Rejeitar</button>
          </div>
        </div>
      </form>
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
  <script type="text/javascript">
      function check(form) {
        if (document.getElementById("motivo").value === '') {
          return false;
        } else {
          return true;
        }
      }
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

          }
        });
      });
  </script>
</html>

