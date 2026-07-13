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
<link type="text/css" rel="stylesheet" href="css/materialize.min.css"  media="screen,projection"/>
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<link href='https://fonts.googleapis.com/css?family=Condiment' rel='stylesheet'>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">

<!-- INFRA-10: jQuery carregado uma única vez (havia duas inclusões) -->
<script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="js/materialize.min.js"></script>
<script type="text/javascript" src="js/materialize.js"></script>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
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
<html lang="pt-BR">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>IdeiaWare - Lista Caixa de Ferramentas</title>
  </head>
  <body class="red darken-1">
    <%-- UX-VOLTAR-V2: icone circular flutuante, faltava saida de volta pra tela inicial (index.jsp). --%>
    <a href="index.jsp" class="btn-floating btn-large red darken-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
    <nav>
      <div class="nav-wrapper red darken-1 z-depth-2">
        <a href="index.jsp" class="brand-logo" style="left: 50px">
          <ul style="width:300px" id="nav-logo" class="left hide-on-med-and-down">
            <div class="row" style="padding-left: 10px">
              <div class="col s1 red lighten-3" style=" width: 50px;  height: 50px; 
                   margin-top: 5px;  padding: 6px 6px; 
                   border-radius: 100%;  box-sizing: border-box;">
                <img style="display: block;  width: 62%;  margin-left: 7px; margin-top: -1px" src="imagens/idea.png" alt="IdeiaWare"/>
              </div>
              <h1 class=" col s4 center-align title-app">IdeiaWare</h1>
            </div>
          </ul>
        </a>
        <ul id="nav-mobile" class="right hide-on-med-and-down">
          <li><a href="LogOutServlet">Sair<i style="padding-left: 20px" class="fa fa-sign-out" aria-hidden="true"></i></a></li>
        </ul>
      </div>
    </nav>
    <!--final do cabeçalho-->

    <div class="white" style="min-height: 90vh" role="main">
      <div class="container">
        <div style="padding: 10px; border-radius: 0.2em" class="white">
          <h1 class="center">Caixa de Ferramentas</h1>
          <jsp:useBean id="ideiaDAO" class="edu.unisc.lic.dao.IdeiaUsuarioDAO" />
          <jsp:useBean id="logDAO" class="edu.unisc.lic.dao.LogColaboracaoDAO" />
          <jsp:useBean id="log" class="edu.unisc.lic.domain.LogColaboracao" />
          <jsp:useBean id="ideia" class="edu.unisc.lic.domain.IdeiaUsuario" />
          <jsp:setProperty name="ideia" property="usuario" value="${usuarioClasse}" />
          <jsp:useBean id="data" class="edu.unisc.lic.classes.Data" />
          <c:set var="ideiasCaixa" value="${ideiaDAO.listarCaixa(ideia)}" />
          <%-- UX-01: collapsible so aparece com conteudo (vazia, o Materialize desenha a borda mesmo assim). --%>
          <c:if test="${not empty ideiasCaixa}">
          <ul class="collapsible" data-collapsible="accordion">
            <c:forEach var="caixa" items="${ideiasCaixa}" varStatus="id">
                <jsp:setProperty name="log" property="ideia" value="${caixa.ideia}" />
                <c:set var="iLog" value="${logDAO.buscarDescricaoFinal(log)}" />
                <li>
                  <div class="collapsible-header">
                    <%-- M.5: cabecalho so com Usuario + Titulo (descricao ja aparece no corpo ao clicar no +); titulo (max 50) quebra. --%>
                    <span style="width: 30%; text-align: left;" class="truncate"><c:out value="${caixa.ideia.usuario.nome}"/></span>
                    <span style="width: 65%; text-align: left; white-space: normal; word-break: break-word;"><c:out value="${caixa.ideia.titulo}"/></span>
                    <i class="material-icons" style="width: 5%; text-align: right;">add</i>
                  </div>
                  <div class="collapsible-body">
                    <div id="parteCima" style="text-align: justify;">
                      <b>Usuário: </b> <c:out value="${caixa.ideia.usuario.nome}"/>
                      <br>
                      <b>Título: </b> <c:out value="${caixa.ideia.titulo}"/>
                      <br>
                      <b>Descrição: </b><br><c:out value="${iLog.descricao}"/>
                      <br>
                      <b>Data de criação: </b>${data.formatarData(caixa.ideia.dtCriacao)}<br>
                    </div>
                    <div id="rodape" style="text-align: right;">
                      <span>
                        <form name="entrarCAixa" action="EntrarCaixaServlet" method="POST">
                          <input hidden="true" value="${caixa.ideia.codigo}" name="ideiaId" />
                          <input hidden="true" value="${nome}" name="usuarioNome">
                          <input class="btn red darken-1" type="submit" value="Entrar"  name="Caixa" />
                        </form>
                      </span>
                    </div>
                  </div>
                </li>
            </c:forEach>
          </ul>
          </c:if>
          <%-- UX-01: estado vazio com mensagem contextual. --%>
          <c:if test="${empty ideiasCaixa}">
            <div class="center-align grey-text" style="padding: 40px 20px;">
              <i class="material-icons" style="font-size: 3rem; display:block;">build</i>
              Você ainda não tem ideias na Caixa de Ferramentas.
            </div>
          </c:if>
        </div>
      </div>
    </div>


  <script src="js/csrf.js"></script>
  </body>
  <script type="text/javascript">
      $(document).ready(function () {
        $('.modal').modal({
          ready: function (modal, trigger) {
            modal.find('input[name="codigo"]').val(trigger.data('codigo'));

            // SEC-05: escapa dado do usuario (trigger.data() decodifica entidades -> innerHTML re-parseia = XSS).
            function escapeHtml(s){if(s==null)return '';return String(s).replace(/[&<>"']/g,function(c){return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];});}
            document.getElementById('divTitulo').innerHTML = "<b>Ideia:</b> " + escapeHtml(trigger.data('titulo'));
            document.getElementById('divDescricao').innerHTML = "<b>Descrição:</b> " + escapeHtml(trigger.data('descricao'));
            document.getElementById('divUsuario').innerHTML = "<b>Usuário:</b> " + escapeHtml(trigger.data('usuario'));

          }
        });
      });
  </script>

  <footer class="center red darken-1 page-footer">
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
