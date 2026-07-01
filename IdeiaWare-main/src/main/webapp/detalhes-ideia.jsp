<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"  session="true"%>
<%@include file="header/headerCookies.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <title>IdeiaWare - Detalhes da Ideia</title>
    </head>
    <body class="center-align teal darken-1">
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
                            <div class="col s3">
                                <!--                                se é lider, volta para minha-ideia, se é usuário, volta para lista-ideia-->
                                <a href="minha-ideia.jsp" class="btn teal lighten-1">Voltar</a></p>
                            </div>
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
                        <%-- UX-01: estado vazio do grupo de colaboradores --%>
                        <c:set var="grupoIdeia" value="${ideiaUsuarioDAO.listarParametro(ideiaUsuario)}" />
                        <c:if test="${empty grupoIdeia}">
                            <p class="grey-text">Ainda não há colaboradores neste grupo.</p>
                        </c:if>
                        <c:forEach var="usuario" items="${grupoIdeia}" varStatus="id" >
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
                        </c:forEach>
                        <c:if test="${sessionScope.lider eq 'S'}" >
                            <p/>
                            <a href="#modalFecharGrupo" class="btn orange darken-1 modal-trigger" data-target="modalFecharGrupo" data-codigoideia="${sessionScope.ideia.codigo}">Detalhes</a>
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
                                    <c:forEach var="usuario" items="${ideiaUsuarioDAO.listarParametro(ideiaUsuario)}" varStatus="id" >
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
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                        <div class="">
                            <input hidden="true" name="codigo" />
                            <input class="btn orange darken-1" type="submit" name="fechar grupo" value="Fechar Grupo" />
                        </div>
                    </div>
                </form>
            </div>
        </div>
    <script src="js/csrf.js"></script>
  </body>

    <%@include file="header/footer.jsp" %>

    <script type="text/javascript">
        $(document).ready(function () {
            $('.modal').modal({
                ready: function (modal, trigger) {
                    modal.find('input[name="codigo"]').val(trigger.data('codigoideia'));
                }
            });
        });
    </script>
</html>