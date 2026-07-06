<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="edu.unisc.lic.dao.UsuarioDAO"%>
<%@page import="edu.unisc.lic.domain.Usuario"%>
<%
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    Usuario usuario = null;
    // RET-11: faltava o return apos o redirect de login. Sem ele, quando a sessao
    // nao tinha codigoUsuario o codigo seguia ate o segundo sendRedirect
    // (index.jsp, linha abaixo) e o Tomcat lancava "Cannot call sendRedirect()
    // after the response has been committed" (erro 500). Agora encerra a
    // requisicao ja no primeiro redirect.
    if (session.getAttribute("codigoUsuario") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    usuario = usuarioDAO.buscar((Long) session.getAttribute("codigoUsuario"));

    if (usuario != null) {
        request.setAttribute("idUsuario", usuario.getCodigo());
        request.setAttribute("usuarioClasse", usuario);
        request.setAttribute("nome", usuario.getNome());
        request.setAttribute("permicao", usuario.getPermissao());
        request.setAttribute("usuario", usuario.getUsuario());
    }

    // RET-04: se o usuário não existe mais (conta removida com sessão ativa),
    // redireciona para o login em vez de causar NullPointerException.
    // RETENCAO-ACESSO: antes so admin acessava. A checagem de QUAL ideia o
    // usuario pode ver ja aconteceu no GerenciarIdeiaServlet (so chega aqui se
    // for admin OU participante da ideia -> session.ideiaId vem validado).
    if (usuario == null) {
        response.sendRedirect("index.jsp");
        return;
    }

    // RET-03: se a página for acessada diretamente (sem passar pelo
    // GerenciarIdeiaServlet), não há ideiaId na sessão. Evita o erro 500
    // de Long.parseLong(null) redirecionando para a lista de gerenciamento.
    if (session.getAttribute("ideiaId") == null) {
        response.sendRedirect("lista-ideia-gerenciamento.jsp");
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
<!DOCTYPE html>
<html lang="pt-BR">
  <head>
    <title>IdeiaWare - Gerenciamento de Ideias</title>
  </head>
  <body class="center-align light-blue darken-1">
    <%-- UX-VOLTAR-V2: mesmo padrao do Colaborativo/Storytelling/Canvas -- icone
         circular flutuante no canto superior esquerdo (fixed). Entrar nos detalhes
         de uma ideia aqui nao tinha NENHUMA saida de volta pra listagem. --%>
    <a href="lista-ideia-gerenciamento.jsp" class="btn-floating btn-large light-blue darken-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
    <nav>
      <div class="nav-wrapper light-blue darken-2">
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
          <%-- UX: "Gerenciar Ideias" removido -- e a propria tela (redirecionaria p/
               si mesma). "Validar Ideias" removido -- ja acessivel pelo cabecalho
               do modulo colaborativo. --%>
          <li><a href="LogOutServlet">Sair<i style="padding-left: 20px" class="fa fa-sign-out" aria-hidden="true"></i></a></li>
        </ul>

      </div>
    </nav>
    <%-- UX: FAB removido -- so tinha atalhos do modulo COLABORATIVO (Cadastrar/Minhas/
         Outras Ideias), nenhum deles e uma acao de Retencao do Conhecimento. --%>
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="js/materialize.min.js"></script>
    <script type="text/javascript" src="js/materialize.js"></script>
    <script type="text/javascript" src="js/Bibliotecas/base64.js"></script>
    <script type="text/javascript" src="js/Bibliotecas/sprintf.min.js"></script>
    <script type="text/javascript" src="js/master.js"></script>
    <div class="white" style="min-height: 90vh" role="main">
      <div class="container">
        <div style="padding: 10px; border-radius: 0.2em" class="white">
          <!--criando variáveis-->
          <c:set var="statusIdeia" value="false"></c:set>
          <c:set var="visualizacao" value="false"></c:set>
          <c:set var="inscricao" value="false"></c:set>
          <c:set var="encerramento" value="false"></c:set>
          <c:set var="colaboracao" value="false"></c:set>
          <c:set var="mapaConceitual" value="false"></c:set>
          <c:set var="storytelling" value="false"></c:set>
          <c:set var="persona" value="false"></c:set>
          <c:set var="pointOfView" value="false"></c:set>
          <c:set var="canva" value="false"></c:set>

              <!--instanciando objetos-->
          <jsp:useBean id="iDAO" class="edu.unisc.lic.dao.IdeiaDAO" />
          <jsp:useBean id="uDAO" class="edu.unisc.lic.dao.UsuarioDAO" />

          <jsp:useBean id="iuDAO" class="edu.unisc.lic.dao.IdeiaUsuarioDAO" />
          <jsp:useBean id="iu" class="edu.unisc.lic.domain.IdeiaUsuario" />

          <jsp:useBean id="stDAO" class="edu.unisc.lic.dao.StorytellingDAO" />
          <jsp:useBean id="st" class="edu.unisc.lic.domain.Storytelling" />

          <jsp:useBean id="data" class="edu.unisc.lic.classes.Data" />
          <c:set var="ideia" value="${iDAO.buscar(Long.parseLong(sessionScope.ideiaId))}" />
          <jsp:useBean id="usu" class="edu.unisc.lic.domain.Usuario" />

          <jsp:useBean id="logDAO" class="edu.unisc.lic.dao.LogColaboracaoDAO" />
          <jsp:useBean id="log" class="edu.unisc.lic.domain.LogColaboracao" />
          <jsp:setProperty name="log" property="ideia" value="${ideia}" />
          <c:set var="log2" value="${logDAO.buscarDescricaoFinal(log)}" />

          <jsp:setProperty name="st" property="ideia" value="${ideia}" />

          <c:set var="storyList" value="${stDAO.listarParametro(st)}" />
          <c:if test="${storyList.size() > 0}">
              <c:set var="story" value="${storyList.get(0)}" />
          </c:if>
          
          <jsp:useBean id="exportCanvaDAO" class="edu.unisc.lic.dao.CanvaexportDAO" />
          <jsp:useBean id="exportCanva" class="edu.unisc.lic.domain.Canvaexport" />
          <jsp:setProperty name="exportCanva" property="ideia" value="${ideia}" />
          
          <c:set var="canvaList" value="${exportCanvaDAO.listarParametro(exportCanva)}" />
          <c:if test="${canvaList.size() > 0}">
              <c:set var="canva" value="${canvaList.get(0)}" />
          </c:if>

          <div id="idea-description" style="display:none;"><c:out value="${ideia.descricao}"/></div>
          <h1>Gerenciamento de Ideias</h1>
          <div class="container">
            <div class="card blue-grey darken-1">
              <div class="card-content white-text">
                <span class="card-title"><b style="font-size: 32px;"><c:out value="${ideia.titulo}"/></b></span>
                <p style="text-align: justify" id="descricaoAtual"><c:out value="${log2.getDescricao()}"/></p>
              </div>
            </div>
            <br/>
          </div>
          <div class="container">
            <div class="center-align">
              <div class="row">
                <!--inicio ideia criada-->
                <div class="col s2">
                  <a href="#modalGenerico" class="modal-trigger red lighten-2" data-target="ideiaCriada" data-titulo="Ideia criada">
                    <div><i class="large material-icons">info_outline</i></div>
                  </a>
                  <div>Ideia criada</div>

                </div>
                <!--fim ideia criada-->

                <!--inicio validacao do gestor-->
                <div class="col s1 center-align valign-wrapper" style="height: 110px;">
                  <i class="small material-icons">arrow_forward</i>
                </div>

                <div class="col s2">
                  <a href="#modalGenerico" class="modal-trigger red lighten-2" data-target="validacaoGestor" data-titulo="Validação gestor">
                    <div><i class="large material-icons">settings</i></div>
                  </a>
                  <div>Validação gestor</div>

                  <c:set var="statusIdeia" value="true" />

                </div>
                <!--fim validacao do gestor-->

                <!--inicio status da ideia-->
                <c:if test="${statusIdeia == 'true'}">
                    <c:choose>
                        <c:when test="${ideia.status == 'RE' && ideia.dtRejeicao != null}">
                            <div class="col s1 center-align valign-wrapper" style="height: 110px;">
                              <i class="small material-icons">arrow_forward</i>
                            </div>

                            <div class="col s2">
                              <a id="ideiaRejeitada" href="#modalGenerico" class="modal-trigger red lighten-2" data-target="ideiaRejeitada" data-titulo="Ideia rejeitada">
                                <div><i class="large material-icons">thumb_down</i></div>
                              </a>
                              <div>Ideia rejeitada</div>
                            </div>
                        </c:when>
                        <c:when test="${ideia.status != 'RE' && ideia.dtValidacao != null}">
                            <div class="col s1 center-align valign-wrapper" style="height: 110px;">
                              <i class="small material-icons">arrow_forward</i>
                            </div>

                            <div class="col s2">
                              <a id="ideiaValidada" href="#modalGenerico" class="modal-trigger red lighten-2" data-target="ideiaValidada" data-titulo="Ideia validada">
                                <div><i class="large material-icons">thumb_up</i></div>
                              </a>
                              <div>Ideia validada</div>
                            </div>

                            <c:set var="visualizacao" value="true"/>

                        </c:when>
                    </c:choose>
                </c:if>
                <!--fim do status da ideia-->

                <!--inicio visualização da ideia-->
                <c:if test="${visualizacao == 'true'}">
                    <div class="col s1 center-align valign-wrapper" style="height: 110px;">
                      <i class="small material-icons">arrow_forward</i>
                    </div>

                    <div class="col s2">
                      <a id="visualizacao" href="#modalGenerico" class="modal-trigger red lighten-2" data-target="visualizacao" data-titulo="Visualização da ideia">
                        <div><i class="large material-icons">remove_red_eye</i></div>
                      </a>
                      <div>Visualização da ideia</div>
                    </div>

                    <c:set var="inscricao" value="true"/>

                    <c:if test="${inscricao == 'true'}">
                        <div class="col s1 center-align valign-wrapper" style="height: 110px;">
                          <i class="small material-icons">arrow_forward</i>
                        </div>
                    </c:if>
                </c:if>
                <!--fim visualização da ideia-->
              </div>


              <!--início da segunda linha de opções-->
              <c:if test="${inscricao == 'true'}">
                  <div class="row">

                    <div class="col s2">
                      <a id="inscricao" href="#modalGenerico" class="modal-trigger red lighten-2" data-target="inscricao" data-titulo="Inscrição dos colaboradores">
                        <div><i class="large material-icons">group</i></div>
                      </a>
                      <div>Inscrição colaboradores</div>

                      <c:if test="${ideia.dtInicioDesenv != null}">
                          <c:set var="encerramento" value="true"/>
                      </c:if>

                    </div>

                    <!--início encerramento inscrição-->
                    <c:if test="${encerramento == 'true'}">

                        <div class="col s1 center-align valign-wrapper" style="height: 110px;">
                          <i class="small material-icons">arrow_forward</i>
                        </div>

                        <div class="col s2">
                          <a id="encerramento" href="#modalGenerico" class="modal-trigger red lighten-2" data-target="encerramento" data-titulo="Encerramento inscrições">
                            <div><i class="large material-icons">lock</i></div>
                          </a>
                          <div class="center-align">Encerramento inscrição</div>

                          <c:set var="colaboracao" value="true"/>

                        </div>
                    </c:if>
                    <!--fim encerramento inscrição-->

                    <!--início processo de colaboração-->
                    <c:if test="${colaboracao == 'true'}">
                        <div class="col s1 center-align valign-wrapper" style="height: 110px;">
                          <i class="small material-icons">arrow_forward</i>
                        </div>

                        <div class="col s2">

                          <form id="procColab" name="entrarColaboracao" action="EntrarColaboracaoServlet" method="POST">
                            <input hidden="true" value="${ideia.codigo}" name="ideiaId" />
                            <input hidden="true" value="true" name="retencao" />
                            <a href="javascript:{}" class="red lighten-2" onclick="document.getElementById('procColab').submit(); return false;">
                              <div><i class="large material-icons">chat</i></div>
                            </a>
                            <div>Processo de colaboração</div>
                          </form>

                          <c:set var="storytelling" value="true"/>

                        </div>
                    </c:if>
                    <!--fim processo de colaboração-->

                    <!--início storytelling-->
                    <c:if test="${storytelling == 'true'}">
                        <div class="col s1 center-align valign-wrapper" style="height: 110px;">
                          <i class="small material-icons">arrow_forward</i>
                        </div>

                        <div class="col s2">
                          <a id="storytelling" href="#modalGenerico" class="modal-trigger red lighten-2" data-target="storytelling" data-titulo="Construção storytelling">
                            <div><i class="large material-icons">brush</i></div>
                          </a>
                          <div>Construção storytelling</div>
                        </div>
                        <c:set var="persona" value="true"/>
                    </c:if>
                    <!--fim storytelling-->
                    <div class="col s1 center-align valign-wrapper" style="height: 110px;">
                      <i class="small material-icons">arrow_forward</i>
                    </div>
                  </div>
              </c:if>
              <!--fim da segunda linha de opções-->

              <!--início da terceira linha de opções-->
              <c:if test="${persona == 'true'}">
                  <div class="row">                
                    <!--início persona -->
                    <div class="col s2">
                      <!--<form id="abrirPersona" action="AbrirPersona" method="post" target="_blank"> href="javascript:{}"   onclick="document.getElementById('abrirPersona').submit();"-->
                      <a href="#modalGenerico" id="persona" class="modal-trigger red lighten-2" data-target="persona" data-titulo="Seleção Persona">
                        <div><i class="large material-icons">person</i></div>
                      </a>
                      <!--</form>  importante mandar dps-->
                      <div>Persona</div>
                    </div>
                    <c:set var="pointOfView" value="true"/>		             
                    <!--fim persona-->

                    <!--início pointOfView -->
                    <c:if test="${pointOfView == 'true'}">
                        <div class="col s1 center-align valign-wrapper" style="height: 110px;">
                          <i class="small material-icons">arrow_forward</i>
                        </div>

                        <div class="col s2">
                          <!--<form id="abrirPOV" action="AbrirPointOfView" method="post" target="_blank"> href="javascript:{}" onclick="document.getElementById('abrirPOV').submit();"-->
                          <a href="#modalGenerico" id="pointOfView" class="modal-trigger red lighten-2" data-target="pov" data-titulo="Seleção Point of View">
                            <div><i class="large material-icons">person_pin</i></div>
                          </a>
                          <!--</form>-->
                          <div>Point Of View</div>
                        </div>
                        <c:set var="canva" value="true"/>
                    </c:if>
                    <!--fim pointOfView-->
                    
                     <!--início Canva -->
                    <c:if test="${canva == 'true'}">
                        <div class="col s1 center-align valign-wrapper" style="height: 110px;">
                          <i class="small material-icons">arrow_forward</i>
                        </div>

                        <div class="col s2">
                          <a href="#modalGenerico" id="canva" class="modal-trigger red lighten-2" data-target="canva" data-titulo="Seleção Canva">
                            <div><i class="large material-icons">person_pin</i></div>
                          </a>
                          <!--</form>-->
                          <div>Canva</div>
                        </div>
                    </c:if>
                    <!--fim Canva-->
                  </div>
              </c:if>     
            </div>
          </div>
        </div>
      </div>
    </div>

    <!--modal generico-->
    <div id="modalGenerico" class="modal">
      <div class="modal-content">
        <div id="titulo"></div>

        <div class="input-field">
          <!--inicio corpo-->
          <div style="text-align: justify">
            <div id="corpo"></div>
          </div>
          <!--fim do corpo-->
        </div>
      </div>
      <div class="modal-footer">
        <a class="modal-action modal-close waves-effect waves-green btn-flat grey">Fechar</a>
      </div>
    </div>
    <!--fim do modal-->
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

     $(document).ready(function(){
        $('.modal').modal({
          ready: function (modal, trigger) {
            //                    colocando o titulo para o modal
            // SEC-05: escapa dado do usuario (trigger.data() decodifica entidades -> innerHTML re-parseia = XSS).
            function escapeHtml(s){if(s==null)return '';return String(s).replace(/[&<>"']/g,function(c){return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];});}
            document.getElementById('titulo').innerHTML = "<h4>" + escapeHtml(trigger.data('titulo')) + "</h4>";
            var target = trigger.data('target');

            var msg = "";

            if (target === "ideiaCriada") {
              msg = ideiaCriada();
            } else if (target === "validacaoGestor") {
              msg = validacaoGestor();
            } else if (target === "ideiaRejeitada") {
              msg = ideiaRejeitada();
            } else if (target === "ideiaValidada") {
              msg = ideiaValidada();
            } else if (target === "visualizacao") {
              msg = visualizacao();
            } else if (target === "inscricao") {
              msg = inscricao();
            } else if (target === "encerramento") {
              msg = encerramento();
            } else if (target === "storytelling") {
              msg = storytelling();
            } else if (target === "pov") {
              msg = pov();
            } else if (target === "persona") {
              msg = persona();
            } else if (target === "canva") {
              msg = canva();
            }

            document.getElementById('corpo').innerHTML = msg;
          }
        });
      });

      //        $("#ideiaCriada").click(function () {
      //            //          alert("ideiaCriada");divIdeiaCriada
      //            document.getElementById('titulo').innerHTML = document.getElementById('divIdeiaCriada').innerHTML;
      //            document.getElementById('corpo').innerHTML = ideiaCriada();
      //        });
      //
      //        $("#validacaoGestor").click(function () {
      //            alert("validacaoGestor");
      //        });
      //
      //        $("#ideiaRejeitada").click(function () {
      //            alert("ideiaRejeitada");
      //        });
      //
      //        $("#ideiaValidada").click(function () {
      //            alert("ideiaValidada");
      //        });
      //
      //        $("#visualizacao").click(function () {
      //            alert("visualizacao");
      //        });
      //
      //        $("#inscricao").click(function () {
      //            alert("inscricao");
      //        });
      //
      //        $("#encerramento").click(function () {
      //            alert("encerramento");
      //        });
      //
      //        $("#storytelling").click(function () {
      //            alert("storytelling");
      //        });
      //
      //        $("#pointOfView").click(function () {
      //            alert("pointOfView");
      //        });
      //
      //        $("#persona").click(function () {
      //            alert("persona");
      //        });



      function pov() {
        var html = "";
        html += "<jsp:useBean id="export" class="edu.unisc.lic.domain.ExportFile" />";
        html += "<jsp:useBean id="exportDAO" class="edu.unisc.lic.dao.ExportFileDAO" />";
        html += "<jsp:setProperty name="export" property="ideia" value="${ideia}" />";
        html += "<jsp:setProperty name="export" property="fileTypeIdentification" value="pov" />";
        html += "<table class=\"responsive-table\">\n" +
                "            <colgroup>\n" +
                "              <col style=\"width: 55%;\" />\n" +
                "              <col style=\"width: 25%;\" />\n" +
                "              <col style=\"width: 15%;\" />\n" +
                "              <col style=\"width: 5%;\" />\n" +
                "            </colgroup>\n" +
                "            <thead>\n" +
                "              <tr class=\"highlight\" style=\"font-weight: bold \"> \n" +
                "                <td>Nome</td>\n" +
                "                <td>Criado em</td>\n" +
                "                <td></td>\n" +
                "                <td></td>\n" +
                "              </tr>\n" +
                "            </thead>";
        html += "<tbody>";
        // UX: mensagem consistente com storytelling()/canva() abaixo (CTA com link
        // pra onde exportar, em vez de um texto seco sem acao nenhuma).
        if (${exportDAO.listarParametro(export).size()} === 0)
          html = "<div style='text-align:center'>\n" +
                 "   Nenhum Point of View foi exportado. <a href='lista-caixa-de-ferramentas.jsp'>Acesse-o agora</a>, exporte-o e volte aqui :)\n" +
                 "</div>";
        else {
          html += "<c:forEach var="pov" items="${exportDAO.listarParametro(export)}">";
          html += "<tr>";
          html += "<td style='white-space: nowrap; text-overflow:ellipsis; overflow: hidden; max-width:1px;'><c:out value="${pov.fileName}"/></td>";
          html += "<td>";
          html += " ${data.formatarDataHoraCompleta(pov.created)}";
          html += "</td>";
          html += "<td>";
          html += " <form id='abrirPOV' action='AbrirPointOfView' method='post' target='_blank' style='margin-bottom:0;'>";
          html += "     <input hidden='true'  value='${pov.id}'  name='codigoPOV' />";
          html += "     <a class='btn red darken-1' onclick='openFileData(\"${pov.fileLocation}\")' href='javascript:;' target='_blank'>Entrar<a/>";
          html += " </form>";
          html += "</td>";
          html += "<td>";
          html += " <i class='material-icons' title='Excluir' data-id='${pov.id}' onclick='deleteFile(${pov.id})' style='color:#f44336; cursor: pointer; display: inline-block; font-size:21px;'>delete</i>";
          html += "</td>";
          html += "</tr>";
          html += "</c:forEach>";
          html += "</tbody>";
          html += "</table>";
        }

        return html;
      }

      function persona() {
        var html = "";
        html += "<jsp:useBean id="export1" class="edu.unisc.lic.domain.ExportFile" />";
        html += "<jsp:useBean id="export1DAO" class="edu.unisc.lic.dao.ExportFileDAO" />";
        html += "<jsp:setProperty name="export1" property="ideia" value="${ideia}" />";
        html += "<jsp:setProperty name="export1" property="fileTypeIdentification" value="persona" />";
        html += "<table class='responsive-table'>\n" +
                "            <colgroup>\n" +
                "              <col style=\"width: 55%;\" />\n" +
                "              <col style=\"width: 25%;\" />\n" +
                "              <col style=\"width: 15%;\" />\n" +
                "              <col style=\"width: 5%;\" />\n" +
                "            </colgroup>\n" +
                "            <thead>\n" +
                "              <tr class='highlight' style='font-weight: bold '> \n" +
                "                <td>Nome</td>\n" +
                "                <td>Criado em</td>\n" +
                "                <td></td>\n" +
                "                <td></td>\n" +
                "              </tr>\n" +
                "            </thead>";
        html += "<tbody>";
        // UX: mensagem consistente com storytelling()/canva() abaixo (CTA com link
        // pra onde exportar, em vez de um texto seco sem acao nenhuma).
        if (${export1DAO.listarParametro(export1).size()} === 0)
          html = "<div style='text-align:center'>\n" +
                 "   Nenhuma Persona foi exportada. <a href='lista-caixa-de-ferramentas.jsp'>Acesse-o agora</a>, exporte-a e volte aqui :)\n" +
                 "</div>";
        else {
          html += "<c:forEach var="persona" items="${export1DAO.listarParametro(export1)}">";
          html += "<tr>";
          html += "<td style='white-space: nowrap; text-overflow:ellipsis; overflow: hidden; max-width:1px;'><c:out value="${persona.fileName}"/></td>";
          html += "<td>";
          html += " ${data.formatarDataHoraCompleta(persona.created)}";
          html += "</td>";
          html += "<td>";
          html += " <form id='abrirPOV' action='AbrirPointOfView' method='post' target='_blank' style='margin-bottom:0;'>";
          html += "     <input hidden='true'  value='${persona.id}' name='codigoPOV' />";
          html += "     <a class='btn red darken-1' onclick='openFileData(\"${persona.fileLocation}\")' href='javascript:;' target='_blank'>Entrar</a>";
          html += " </form>";
          html += "</td>";
          html += "<td>";
          html += " <i class='material-icons' title='Excluir' data-id='${persona.id}' onclick='deleteFile(${persona.id})' style='color:#f44336; cursor: pointer; display: inline-block; font-size:21px;'>delete</i>";
          html += "</td>";
          html += "</tr>";
          html += "</c:forEach>";
          html += "</tbody>";
          html += "</table>";
        }
        return html;
      }

      function storytelling() {
        if ("${story.status}" !== "FN") {
          var html = "";
          html += "<div style='text-align:center'>\n" +
                  "   O processo do Storytelling não foi finalizado. <a href='lista-storytelling.jsp'>Acesse-o agora</a>, exporte-o e volte aqui :)\n" +
                  "</div>";
          return html;
        } else {
          var html = "";
          html += "<jsp:useBean id="exportStory" class="edu.unisc.lic.domain.Storytelling" />";
          html += "<jsp:useBean id="exportStoryDAO" class="edu.unisc.lic.dao.StorytellingDAO" />";
          html += "<jsp:setProperty name="exportStory" property="ideia" value="${ideia}" />";
          html += "<table class='responsive-table'>\n" +
                  "   <colgroup>\n" +
                  "       <col style='width: 60%;' />\n" +
                  "       <col style='width: 40%;' />\n" +
                  "   </colgroup>\n" +
                  "   <thead>\n" +
                  "       <tr class='highlight' style='font-weight: bold '> \n" +
                  "           <td>Nome</td>\n" +
                  "           <td></td>\n" +
                  "       </tr>\n" +
                  "    </thead>";
          html += "<tbody>";
          html += "<c:forEach var="storytelling" items="${exportStoryDAO.listarParametro(exportStory)}">";
          html += "   <tr>";
          html += "       <td style='white-space: nowrap; text-overflow:ellipsis; overflow: hidden; max-width:1px;'><c:out value="${ideia.titulo}"/></td>";
          html += "       <td style='text-align:right;'>";
          // UX: "Abrir quadro" removido da Retencao -- nao servia p/ nada util aqui
          // (reabria o quadro em modo leitura, mas o PDF ja mostra tudo). So
          // "Visualizar PDF" fica, no mesmo padrao dos outros passos da timeline.
          // UX-ALINHAMENTO: esta tabela so tem 2 colunas (sem Data/Excluir como as
          // outras) -- a celula fica LARGA e sem align o botao caia colado na
          // esquerda. text-align:right alinha com o padrao visual dos outros passos.
          // UX-COR: indigo lighten-1 (cor EXATA do header/footer do modulo
          // Storytelling); indigo darken-1 (#3949ab) ficava visivelmente diferente
          // do header (indigo lighten-1, #5c6bc0).
          html += "             <a class='btn indigo lighten-1' onclick='openFileData(\"${storytelling.caminhoFinalizado}\")' href='javascript:;' target='_blank'>Visualizar PDF</a>";
          html += "       </td>";
          html += "   </tr>";
          html += "</c:forEach>";
          html += "</tbody>";
          html += "</table>";
          return html;
        }
      }
      
      function canva() {
          // RET-07: a variável 'canva' é a string "false" quando não há canva
          // exportado, então "${canva}" == null nunca era verdadeiro e a mensagem
          // de aviso jamais aparecia. Usamos o tamanho real da lista.
          if (${canvaList.size()} === 0) {
            var html = "";
            html += "<div style='text-align:center'>\n" +
                    "   Nenhum Canva foi exportado. <a href='lista-canvas.jsp'>Acesse-o agora</a>, exporte-o e volte aqui :)\n" +
                    "</div>";
            return html;
          } else {
            var html = "";
            html += "<jsp:useBean id="eCanva" class="edu.unisc.lic.domain.Canvaexport" />";
            html += "<jsp:useBean id="eCanvaDAO" class="edu.unisc.lic.dao.CanvaexportDAO" />";
            html += "<jsp:setProperty name="eCanva" property="ideia" value="${ideia}" />";
            html += "<table class='responsive-table'>\n" +
                    "   <colgroup>\n" +
                    "       <col style='width: 55%;' />\n" +
                    "       <col style='width: 25%;' />\n" +
                    "       <col style='width: 15%;' />\n" +
                    "       <col style='width: 5%;' />\n" +
                    "   </colgroup>\n" +
                    "   <thead>\n" +
                    "       <tr class='highlight' style='font-weight: bold '> \n" +
                    "           <td>Nome</td>\n" +
                    "           <td>Criado em</td>\n" +
                    "           <td></td>\n" +
                    "           <td></td>\n" +
                    "       </tr>\n" +
                    "    </thead>";
            html += "<tbody>";
            html += "<c:forEach var="canva" items="${eCanvaDAO.listarParametro(eCanva)}">";
            html += "   <tr>";
            html += "       <td style='white-space: nowrap; text-overflow:ellipsis; overflow: hidden; max-width:1px;'><c:out value="${ideia.titulo}"/></td>";
            html += "       <td> ${data.formatarDataHoraCompleta(canva.date)}</td>";
            html += "       <td>";
            // UX-COR: blue darken-4 (cor do proprio modulo Canvas), era red generico.
            html += "           <a class='btn blue darken-4' onclick='openFileData(\"${canva.file}\")' href='javascript:;' target='_blank'>Entrar</a>";
            html += "       </td>";
            html += "       <td>";
            html += "           <i class='material-icons' title='Excluir' data-id='${canva.codigo}' onclick='deleteCanvaExport(${canva.codigo})' style='color:#f44336; cursor: pointer; display: inline-block; font-size:21px;'>delete</i>";
            html += "       </td>";
            html += "   </tr>";
            html += "</c:forEach>";
            html += "</tbody>";
            html += "</table>";
            return html;
          }
        }

      function encerramento() {
        var html = "";

        html += "<b>Data de criação: </b>${data.formatarData(ideia.dtCriacao)} às ${data.formatarHora(ideia.dtCriacao)} </br>";
        html += "<b>Data de validação: </b>${data.formatarData(ideia.dtValidacao)} às ${data.formatarHora(ideia.dtValidacao)}</br>";
        html += "<b>Data do fechamento das inscrições </b>${data.formatarData(ideia.dtInicioDesenv)} às ${data.formatarHora(ideia.dtInicioDesenv)}</br>";
        html += "<b>Usuário que fechou a ideia: </b> <c:out value="${ideia.usuario.nome}"/></br>";
        html += "<b>Período disponível para inscrição: </b>${data.diferencaDatas(ideia.dtValidacao, ideia.dtInicioDesenv)}</br>";

        return html;
      }

      function inscricao() {
        var html = "";

        //            busca o usuário líder
        html += "<b>Líder:</b></br>";
        html += "<jsp:setProperty name="iu" property="flLider" value="S" />";
        html += "<jsp:setProperty name="iu" property="ideia" value="${ideia}" />";
        html += "<c:forEach var="ideiaUsuario" items="${iuDAO.listarParametro(iu)}" varStatus="id">";
        html += "   <div style='margin-left:2.5em'>- <c:out value="${ideiaUsuario.usuario.nome}" /></br></div>";
        html += "</c:forEach>";

        html += "</br>";
        //            busca os demais usuários cadastrados
        html += "<b>Demais usuários</b></br>";
        html += "<jsp:setProperty name="iu" property="flLider" value="N" />";
        html += "<c:forEach var="ideiaUsuario" items="${iuDAO.listarParametro(iu)}" varStatus="cod">";
        html += "   <div style='margin-left:2.5em'>[<c:out value="${data.formatarDataHoraCompleta(ideiaUsuario.dtInscricao)}" />] - <c:out value="${ideiaUsuario.usuario.nome}" /></br></div>";
        html += "</c:forEach>";

        return html;
      }

      function visualizacao() {

        var html = "";
        var status = "${ideia.statusGrupo}";

        if (status === "AB") {
          html += "<b>Tempo que a ideia está disponível: </b>";
        } else {
          html += "<b>Tempo que a ideia ficou disponível: </b>";
        }

        html += "${data.diferencaDatas(ideia.dtValidacao, ideia.dtInicioDesenv)}";

        return html;
      }

      function ideiaValidada() {
        var html = "";

        html += "<b>Gestor que aprovou a ideia: </b><c:out value="${ideia.gestor.nome}"/></br>";
        html += "<b>Data de validação: </b>${data.formatarData(ideia.dtValidacao)} às ${data.formatarHora(ideia.dtValidacao)}</br>";
        html += "<b>Tempo de resposta do gestor: </b>${data.diferencaDatas(ideia.dtCriacao, ideia.dtValidacao)}</br>";

        return html;
      }

      function ideiaRejeitada() {

        var html = "";

        html += "<b>Gestor que rejeitou a ideia: </b><c:out value="${ideia.gestor.nome}"/></br>";
        html += "<b>Data de rejeição: </b>${data.formatarData(ideia.dtRejeicao)} às ${data.formatarHora(ideia.dtRejeicao)} </br>";
        html += "<b>Motivo da rejeição: </b><c:out value="${ideia.motivoRejeicao}"/></br>";
        html += "<b>Tempo de resposta do gestor: </b>${data.diferencaDatas(ideia.dtCriacao, ideia.dtRejeicao)}</br>";

        return html;
      }

      function validacaoGestor() {

        var html = "";
        var statusGrupo = "${ideia.statusGrupo}";

        if (statusGrupo === "AB") {
          html += "<jsp:useBean id="u" class="edu.unisc.lic.domain.Usuario" />";
          html += "<jsp:setProperty name="u" property="permissao" value="adm" />";
          html += "<b>Tempo no aguardo: </b>${data.diferencaDatas(ideia.dtCriacao, null)}</br>";
          html += "<b>Usuários com permissão de administrador:</b></br>";
          html += "<c:forEach var="usuario" items="${uDAO.listarParametro(u, true)}" varStatus="id">";
          html += "<div style='margin-left:2.5em'>- <c:out value="${usuario.nome}" /></br></div>";
          html += "</c:forEach>";
        } else {
          html += "<b>Gestor que avaliou a ideia: </b><c:out value="${ideia.gestor.nome}"/></br>";
          html += "<b>Tempo no aguardo: </b>${data.diferencaDatas(ideia.dtCriacao, ideia.dtValidacao)}</br>";
        }

        return html;
      }

      function ideiaCriada() {

        var statusIdeia = "";
        var statusGrupo = "";

        var statusIdeiaAtual = "${ideia.status}";
        var statusGrupoAtual = "${ideia.statusGrupo}";

        if (statusIdeiaAtual === "VA") {
          statusIdeia = "Validada";
        } else if (statusIdeiaAtual === "RE") {
          statusIdeia = "Rejeitada";
        } else if (statusIdeiaAtual === "PE") {
          statusIdeia = "Pendente";
        } else if (statusIdeiaAtual === "DE") {
          statusIdeia = "Desenvolvimento";
        } else if (statusIdeiaAtual === "ST") {
          statusIdeia = "Storytelling";
        } else if (statusIdeiaAtual === "FN") {
          statusIdeia = "Finalizado";
        }

        if (statusGrupoAtual === "AB") {
          statusGrupo = "Aberto";
        } else if (statusGrupoAtual === "FE") {
          statusGrupo = "Fechado";
        }
        
        var html = "<b>Título: </b><c:out value="${ideia.titulo}"/></br>";
        html += "<b>Descrição: </b>" + $("#idea-description").html() + "</br>";
        html += "<b>Usuário: </b><c:out value="${ideia.usuario.nome}"/></br>";
        html += "<b>Data de criação: </b>${data.formatarData(ideia.dtCriacao)} às ${data.formatarHora(ideia.dtCriacao)} </br>";
        html += "<b>Status da ideia: </b>" + statusIdeia + "</br>";
        html += "<b>Status do grupo: </b>" + statusGrupo + "</br>";

        return html;
      }
  </script>
</html>