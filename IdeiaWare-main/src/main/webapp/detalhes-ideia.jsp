<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"  session="true"%>
<%@include file="header/headerCookies.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <title>IdeiaWare - Detalhes da Ideia</title>
    </head>
    <body class="center-align teal darken-1">
        <%-- UX-VOLTAR-V2: icone flutuante padrao (antes era um botao inline facil de perder). --%>
        <c:choose>
            <c:when test="${sessionScope.lider eq 'S'}">
                <a href="minha-ideia.jsp" class="btn-floating btn-large teal darken-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
            </c:when>
            <c:otherwise>
                <a href="lista-ideia.jsp" class="btn-floating btn-large teal darken-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
            </c:otherwise>
        </c:choose>
        <jsp:useBean id="data" class="edu.unisc.lic.classes.Data" />
        <jsp:useBean id="ideiaUsuarioDAO" class="edu.unisc.lic.dao.IdeiaUsuarioDAO" />
        <jsp:useBean id="ideiaUsuario" class="edu.unisc.lic.domain.IdeiaUsuario" />
        <jsp:useBean id="ideiaUsuario2" class="edu.unisc.lic.domain.IdeiaUsuario" />
        <jsp:setProperty name="ideiaUsuario2" property="usuario" value="${usuarioClasse}" />
        <jsp:setProperty name="ideiaUsuario2" property="ideia" value="${sessionScope.ideia}" />
        <jsp:setProperty name="ideiaUsuario" property="ideia" value="${sessionScope.ideia}" />
        <div class="white" style="min-height: 90vh" role="main">
            <div class="container">
                <div style="padding: 10px" class="white">
                    <h1>Detalhes da Ideia</h1>
                    <div class="container">
                        <div class="row">
                            <div class="col s6 left-align">
                                <!--                                aparece somente se não é lider-->
                                <h6>Usuário: <c:out value="${sessionScope.ideia.usuario.nome}"/></h6>
                            </div>
                            <div class="col s6 right-align">
                                <h6>Data de Criação: ${data.formatarData(sessionScope.ideia.dtCriacao)}</h6>
                            </div>
                        </div>
                        <div class="card blue-grey darken-1">
                            <div class="card-content white-text">
                                <span class="card-title"><c:out value="${sessionScope.ideia.titulo}"/></span>
                                <p><c:out value="${sessionScope.ideia.descricao}"/></p>

                            </div>

                        </div>
                        <div class="row">
                            <!--                                se é lider, não aparece-->
                            <c:if test="${sessionScope.lider != 'S'}" >
                                <c:if test="${(ideiaUsuarioDAO.listarParametro(ideiaUsuario2)).size() eq 0}">
                                    <div class="col s3 offset-s6">
                                        <form name="entraIdeia" action="EntrarIdeiaServlet" method="POST">
                                            <input hidden="true" value="${sessionScope.ideia.codigo}" name="codigo" />
                                            <input class="btn orange darken-1" type="submit" value="Participar" name="participar" />
                                        </form>
                                    </div>
                                </c:if>
                            </c:if>
                        </div>
                        <br/>
                    </div>
                    <div>
                        <!--                        aparece somente se está participando da ideia-->
                        <h5>Grupo</h5></p>
                        <%-- UX-01: estado vazio do grupo de colaboradores, com icone (padrao
                             ja usado nas demais listagens do sistema). --%>
                        <c:set var="grupoIdeia" value="${ideiaUsuarioDAO.listarParametro(ideiaUsuario)}" />
                        <c:if test="${empty grupoIdeia}">
                            <div class="center-align grey-text" style="padding: 40px 20px;">
                                <i class="material-icons" style="font-size: 3rem; display:block;">group</i>
                                Ainda não há colaboradores neste grupo.
                            </div>
                        </c:if>
                        <%-- M.2: o grupo mostra so MEMBROS APROVADOS (pendentes/rejeitados nao aparecem). --%>
                        <c:forEach var="usuario" items="${grupoIdeia}" varStatus="id" >
                            <c:if test="${usuario.flStatusVinculo ne 'P' and usuario.flStatusVinculo ne 'R'}">
                                <c:choose>
                                    <c:when test="${usuario.flLider eq 'S'}" >
                                        <div class="chip">
                                            <c:out value="${usuario.usuario.nome}"/> <i class="tiny material-icons orange-text">stars</i>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="chip">
                                            <c:out value="${usuario.usuario.nome}"/>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </c:forEach>

                        <%-- M.2/M.3: lista de espera, so o lider ve -- Aprovar/Rejeitar (com motivo). --%>
                        <c:if test="${sessionScope.lider eq 'S'}" >
                            <c:set var="temPendente" value="false" />
                            <c:forEach var="pend" items="${grupoIdeia}"><c:if test="${pend.flStatusVinculo eq 'P'}"><c:set var="temPendente" value="true" /></c:if></c:forEach>
                            <c:if test="${temPendente}">
                                <h6 style="margin-top:20px;">Solicitações de entrada</h6>
                                <%-- GT-11: thead adicionado (faltava contexto de coluna pra leitor de tela). --%>
                                <table class="highlight" style="max-width:520px; margin:0 auto;">
                                    <thead>
                                      <tr style="font-weight: bold">
                                        <td style="text-align:left;">Usuário</td>
                                        <td></td>
                                      </tr>
                                    </thead>
                                    <c:forEach var="pend" items="${grupoIdeia}">
                                        <c:if test="${pend.flStatusVinculo eq 'P'}">
                                            <tr>
                                                <td style="text-align:left;"><c:out value="${pend.usuario.nome}"/></td>
                                                <td style="text-align:right; white-space:nowrap;">
                                                    <form action="AprovarMembroServlet" method="POST" style="display:inline; margin:0;">
                                                        <input hidden="true" name="vinculo" value="${pend.codigo}" />
                                                        <button class="btn green lighten-1" type="submit">Aprovar</button>
                                                    </form>
                                                    <a href="#modalRejeitarMembro" class="btn red lighten-1 modal-trigger" data-vinculo="${pend.codigo}" data-nome="<c:out value='${pend.usuario.nome}'/>">Rejeitar</a>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>
                                </table>
                            </c:if>
                        </c:if>

                        <c:if test="${sessionScope.lider eq 'S'}" >
                            <p/>
                            <a href="#modalFecharGrupo" class="btn orange darken-1 modal-trigger" data-target="modalFecharGrupo" data-codigoideia="${sessionScope.ideia.codigo}">Fechar Grupo</a>
                        </c:if>
                    </div>
                    <div>

                    </div>

                </div>
            </div>
        </div>

        <!--modal para fechar ideia e começar o desenvolvimento -->
        <div id="modalFecharGrupo" class="modal">                     <%-- Precisa fazer com que os dados no form do fechar funcionem --%>
            <div class="modal-content">
                <form name="fechar" action="FecharGrupoServlet" method="POST">
                    <div class="row">
                        <div class="col s12 m4 l11">
                            <h4>Definir líder</h4>
                        </div>

                        <p>
                        <div class="row">
                            <div class="col s3">
                                <h6>Lider atual</h6>
                                <c:forEach var="usuario" items="${ideiaUsuarioDAO.listarParametro(ideiaUsuario)}" varStatus="id" >
                                    <c:if test="${usuario.flLider eq 'S'}" >
                                        <div class="chip">
                                            <c:out value="${usuario.usuario.nome}"/> <i class="tiny material-icons orange-text">stars</i>
                                        </div>
                                    </c:if>
                                </c:forEach>
                            </div>
                            <!--                    para cada user colaborador-->
                            <div class="col s3 offset-s3">
                                <h6>Grupo</h6>
                                <div style="text-align: left">
                                    <%-- M.2 (2026-07-06): so membros APROVADOS podem ser escolhidos
                                         lider (pendentes/rejeitados sao removidos ao fechar). --%>
                                    <c:forEach var="usuario" items="${ideiaUsuarioDAO.listarParametro(ideiaUsuario)}" varStatus="id" >
                                        <c:if test="${usuario.flStatusVinculo ne 'P' and usuario.flStatusVinculo ne 'R'}">
                                            <c:if test="${usuario.flLider eq 'S'}" >
                                                <input name="radio" type="radio" id="${usuario.usuario.codigo}" value="${usuario.usuario.codigo}" checked="checked"/>
                                                <label for="${usuario.usuario.codigo}"><c:out value="${usuario.usuario.nome}"/></label>
                                                <br />
                                            </c:if>
                                            <c:if test="${usuario.flLider != 'S'}" >
                                                <input name="radio" type="radio" id="${usuario.usuario.codigo}" value="${usuario.usuario.codigo}"/>
                                                <label for="${usuario.usuario.codigo}"><c:out value="${usuario.usuario.nome}"/></label>
                                                <br />
                                            </c:if>
                                        </c:if>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                        <div class="">
                            <input hidden="true" name="codigo" />
                            <input class="btn orange darken-1" type="submit" name="fechar grupo" value="Fechar Grupo" onclick="return confirm('Fechar o grupo encerra as inscrições. Confirmar?')"/>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <%-- M.3 (2026-07-06): modal de REJEITAR a entrada de um membro -- motivo obrigatorio
             (mesmo padrao de rejeitar uma ideia). Envia pro RejeitarMembroServlet. --%>
        <div id="modalRejeitarMembro" class="modal">
            <form name="rejeitarMembro" action="RejeitarMembroServlet" method="POST" onsubmit="return document.getElementById('motivoMembro').value.trim() !== '';">
                <div class="modal-content">
                    <h4>Rejeitar entrada</h4>
                    <p>Rejeitar <b><span class="nome-membro-rejeitar"></span></b> do grupo?</p>
                    <input hidden="true" name="vinculo" value="" />
                    <div class="input-field">
                        <textarea id="motivoMembro" name="motivo" class="materialize-textarea" data-length="200" maxlength="200"></textarea>
                        <label for="motivoMembro">Motivo (obrigatório)</label>
                    </div>
                </div>
                <div class="modal-footer">
                    <a href="#!" class="modal-action modal-close btn-flat">Cancelar</a>
                    <button class="btn red lighten-1" type="submit">Rejeitar</button>
                </div>
            </form>
        </div>
    <script src="js/csrf.js"></script>
  </body>

    <%@include file="header/footer.jsp" %>

    <script type="text/javascript">
        $(document).ready(function () {
            $('.modal').modal({
                ready: function (modal, trigger) {
                    modal.find('input[name="codigo"]').val(trigger.data('codigoideia'));
                    // M.3: modal de rejeitar membro -- preenche o vinculo e o nome.
                    if (trigger.data('vinculo') != null) {
                        modal.find('input[name="vinculo"]').val(trigger.data('vinculo'));
                        modal.find('.nome-membro-rejeitar').text(trigger.data('nome') || '');
                    }
                }
            });
        });
    </script>
</html>