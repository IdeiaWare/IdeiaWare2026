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
%>

<jsp:useBean id="storyDAO"    class="edu.unisc.lic.dao.StorytellingDAO" />
<jsp:useBean id="ideiaLogDAO" class="edu.unisc.lic.dao.LogColaboracaoDAO" />

<c:set var="story"    value="${storyDAO.buscar(sessionScope.storytellingId)}" />
<c:set var="ideiaLog" value="${ideiaLogDAO.buscar(sessionScope.ideiaLog)}" />
<c:set var="descLog"  value="${ideiaLogDAO.buscarDescricaoFinal(ideiaLog)}" />

<!DOCTYPE html>
<%-- STR-07: removida estrutura HTML duplicada — havia dois <html> e dois <body> --%>
<html lang="pt-BR">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>IdeiaWare - Storytelling</title>

    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link type="text/css" rel="stylesheet" href="css/materialize.min.css" media="screen,projection"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link href='https://fonts.googleapis.com/css?family=Condiment' rel='stylesheet'>
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">

    <style>
      .title-app { color:#fff; font-family: 'Condiment'; font-size: 42px; line-height: 25px }
    </style>
  </head>
  <body class="indigo lighten-5">
    <%-- UX-VOLTAR-V2: mesmo padrao do resto do app -- icone circular flutuante no
         canto superior esquerdo. Visao somente-leitura do storytelling, so tinha
         a logo do cabecalho (pra index.jsp) -- volta pra lista-storytelling.jsp,
         igual storytelling.jsp (o editor). --%>
    <a href="lista-storytelling.jsp" class="btn-floating btn-large indigo lighten-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar"><i class="material-icons">arrow_back</i></a>

    <nav>
      <div class="nav-wrapper indigo lighten-1 z-depth-2">
        <a href="index.jsp" class="brand-logo" style="left: 50px">
          <ul style="width:300px" id="nav-mobile" class="left hide-on-med-and-down">
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
          <li><a href="#modalDescricao" class="modal-trigger" data-target="modalDescricao">
            Descrição Ideia <i class="material-icons right">description</i>
          </a></li>
          <li><a href="LogOutServlet">Sair<i style="padding-left:20px" class="fa fa-sign-out" aria-hidden="true"></i></a></li>
        </ul>
      </div>
    </nav>

    <div>
      <div class="row" style="min-height: 80vh; padding-top: 20px; width: 95%">
        <div class="col s12">
          <div class="grey lighten-4 z-depth-2" id="workBoard" style="min-height: 85vh">
            <div id="container" class="mycanvas"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal com descrição da ideia -->
    <div id="modalDescricao" class="modal">
      <div class="grey-text text-darken-3 modal-content">
        <h4>Descrição da Ideia</h4>
        <div class="input-field">
          <div style="text-align:justify;">
            <br>
            <div><br><b>Ideia:</b> <c:out value="${story.ideia.titulo}"/></div>
            <div><br><b>Descrição:</b><br>
              <c:choose>
                <c:when test="${not empty descLog}"><c:out value="${descLog.descricao}"/></c:when>
                <c:otherwise><c:out value="${story.ideia.descricao}"/></c:otherwise>
              </c:choose>
            </div>
            <div><br><b>Usuário:</b> <c:out value="${story.ideia.usuario.nome}"/></div>
            <br>
          </div>
        </div>
      </div>
    </div>

    <script type="text/javascript" src="js/Bibliotecas/jquery-3.2.1.js"></script>
    <script type="text/javascript" src="js/controleAdd.js"></script>
    <script type="text/javascript" src="js/Bibliotecas/base64.js"></script>
    <script type="text/javascript" src="js/Bibliotecas/Konva.min.js"></script>
    <%-- UX-03: removido o 2o carregamento de jQuery (CDN). A pagina ja carrega
         jQuery local acima (js/Bibliotecas/jquery-3.2.1.js); carregar de novo
         reinicializava o jQuery e podia derrubar handlers/plugins ja ligados. --%>
    <script type="text/javascript" src="js/materialize.min.js"></script>
    <script type="text/javascript" src="js/materialize.js"></script>

    <script type="text/javascript">
      $(document).ready(function () {
        $('.modal').modal();
      });

      window.onload = function () {
        var width  = $('#container').innerWidth();
        var height = 850;

        var stage = new Konva.Stage({
          container: 'container',
          width:  width,
          height: height
        });

        var layer = new Konva.Layer({ name: "camadaImagens" });

        // Carrega elementos em modo somente leitura
        $.get("RetornaElementos", function (responseJson) {
          $.each(responseJson, function (index, elemento) {
            if (elemento.tipo === "IMG") {
              var imageObj = new Image();
              imageObj.onload = function () {
                var el = new Konva.Image({
                  image:     this,
                  draggable: false,
                  x:         elemento.x,
                  y:         elemento.y,
                  width:     elemento.largura,
                  height:    elemento.altura,
                  id:        elemento.codigo
                });
                layer.add(el);
                stage.add(layer);
              };
              imageObj.src = elemento.caminho;
            } else if (elemento.tipo === "TXT") {
              var txtLayer = new Konva.Layer();
              stage.add(txtLayer);
              var textNode = new Konva.Text({
                text:       elemento.informacaoTexto,
                x:          elemento.x,
                y:          elemento.y,
                fontSize:   elemento.tamanhoFonte,
                fontFamily: elemento.fonte,
                fill:       elemento.corFonte,
                draggable:  false,
                id:         elemento.codigo
              });
              txtLayer.add(textNode);
              txtLayer.draw();
            }
          });
        });
      };
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
