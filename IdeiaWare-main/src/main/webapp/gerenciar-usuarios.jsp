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

    // RKM-03: evita NullPointerException caso a conta tenha sido removida com a sessao ainda ativa.
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
<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@600' rel='stylesheet'>

<style>
    .titulo-modulo { font-family: 'Poppins', sans-serif; font-weight: 600; margin-bottom: 28px; }
    .title-app {
        color:#fff;
        font-family: 'Condiment';
        font-size: 42px;
        line-height: 25px
    }
    .title-app2 {
        font-family: 'Condiment';
        font-size: 100px;
    }
    .card .icon {
        font-size:50px;
    }
    .card-content .card-title {
        color:#fff !important;
    }
    .card a {
        color:#fff;
    }
    .fa-sign-out {
        color:#fff;
    }
    .icon .fa-circle {
        color:#fff;
    }
    .new-idea .icon .fa-lightbulb-o {
        color:#4db6ac;
    }
    .storytelling .icon .fa-pencil {
        color:#5c6bc0
    }
    .toolkit .icon .fa-briefcase {
        color:#c62828;
    }
    .knowledge .icon .fa-database {
        color:#0288d1;
    }
    .fa-hand-o-right {
        border-radius:50%;
        box-shadow: 0 0 0 rgba(255,255,255, 0.4);
        animation: pulse 2s infinite;
    }
    .fa-hand-o-right:hover {
        animation: none;
    }

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

    /* LISTAGEM-CINZA: fundo quase-branco + sombra, no lugar do #fff puro. */
    ul.collapsible { box-shadow: 0 2px 2px 0 rgba(0,0,0,.14), 0 3px 1px -2px rgba(0,0,0,.2), 0 1px 5px 0 rgba(0,0,0,.12); }
    .collapsible-header, .collapsible-body { background-color: #f5f5f5; }
</style>

<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <title>IdeiaWare - Gerenciar usuários</title>
    </head>
    <body class="center-align blue-grey lighten-5">
    <main>
        <%-- UX-VOLTAR-V2: icone circular flutuante, faltava saida de volta pra tela inicial. --%>
        <a href="index.jsp" class="btn-floating btn-large blue-grey darken-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
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
                    <li><a href="LogOutServlet">Sair<i style="padding-left: 20px" class="fa fa-sign-out" aria-hidden="true"></i></a></li>
                </ul>

            </div>
        </nav>

        <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
        <script type="text/javascript" src="js/materialize.min.js"></script>
        <script type="text/javascript" src="js/materialize.js"></script>

        <div class="" style="min-height: 90vh" role="main">
            <div class="container">
                <div style="padding: 10px; border-radius: 0.2em" class="">
                    <h1 class="blue-grey-text text-darken-2 titulo-modulo">Gerenciar usuários</h1>
                    <jsp:useBean id="usuarioDao" class="edu.unisc.lic.dao.UsuarioDAO" />
                    <jsp:useBean id="usuarioAux" class="edu.unisc.lic.domain.Usuario" />
                    <jsp:setProperty name="usuarioAux" property="anonimizado" value="N"/>
                    <ul class="collapsible" data-collapsible="accordion">
                        <c:set var="listaUsuarios" value="${usuarioDao.listarParametro(usuarioAux, false)}" />
                        <c:forEach var="usuario" items="${listaUsuarios}" varStatus="id">   
                            <li>
                                <div class="collapsible-header">
                                    <span style="width: 15%; text-align: left;" class="truncate"    >${usuario.permissao}</span>
                                    <span style="width: 20%; text-align: left;" class="truncate"    ><c:out value="${usuario.nome}"/></span>
                                    <span style="width: 25%; text-align: left;" class="truncate"    ><c:out value="${usuario.usuario}"/></span>
                                    <span style="width: 35%; text-align: left;" class="truncate"    ><c:out value="${usuario.email}"/></span>
                                    <i class="material-icons" style="width: 5%; text-align: right; ">add</i>
                                </div>
                                <div class="collapsible-body">
                                    <div id="parteCima" style="text-align: justify;">
                                        <b>Usuário: </b> <c:out value="${usuario.usuario}"/><br />
                                        <b>Nome: </b> <c:out value="${usuario.nome}"/><br />
                                        <b>E-mail: </b><c:out value="${usuario.email}"/><br />
                                        <b>Permissão: </b>${usuario.permissao}<br />
                                    </div>
                                    <div id="rodape" style="text-align: right;">
                                        <span>
                                            <form name="permissaoUsuario" action="PermissaoUsuarioServlet" method="POST">
                                                <input hidden="true" value="${usuario.codigo}" id="codigoAlterar" name="codigoAlterar" />
                                                <c:choose>
                                                    <c:when test="${usuario.permissao eq 'adm'}" >
                                                        <button class="btn green lighten-1" type="submit" id="colaborador" value="colaborador" name="colaborador">Tornar colaborador</button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button class="btn green lighten-1" type="submit" id="gestor" value="gestor" name="gestor">Tornar gestor</button>
                                                    </c:otherwise>
                                                </c:choose>
                                            </form>
                                        </span>
                                    </div>
                                </div>
                            </li>    
                        </c:forEach>    
                    </ul>
                    <%-- UX-01: estado vazio com mensagem contextual. --%>
                    <c:if test="${empty listaUsuarios}">
                        <div class="center-align grey-text" style="padding: 40px 20px;">
                            <i class="material-icons" style="font-size: 3rem; display:block;">people_outline</i>
                            Nenhum usuário cadastrado.
                        </div>
                    </c:if>
                </div>
            </div>
        </div>

    <script src="js/csrf.js"></script>
  </main>
    </body>
    <%-- UX-FOOTER-PADRAO: footer com cor do header da home, antes era teal. --%>
    <footer class="center blue-grey lighten-1 page-footer">
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
