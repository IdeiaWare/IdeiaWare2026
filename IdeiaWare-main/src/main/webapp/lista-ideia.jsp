<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header/headerCookies.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <title>IdeiaWare - Lista de Ideias</title>
        <link type="text/css" rel="stylesheet" href="css/colaboracao.css"/>
    </head>
    <body class="center-align teal darken-1">
        <div class="white" style="min-height: 90vh" role="main">
            <div class="container">
                <div style="padding: 10px; border-radius: 0.2em;" class="white">
                    <h1>Lista de Ideias</h1>
                    <jsp:useBean id="ideiaDAO" class="edu.unisc.lic.dao.IdeiaDAO" />
                    <jsp:useBean id="data"    class="edu.unisc.lic.classes.Data" />

                    <form class="input-field">
                        <input placeholder="Buscar título de ideia" aria-label="Buscar título de ideia" id="filtro-nome" type="text" class="validate">
                    </form>

                    <table id="lista" class="list-ideas">
                        <thead>
                            <tr class="highlight" style="font-weight: bold">
                                <td>Título</td>
                                <td>Descrição da Ideia</td>
                                <td>Data de criação</td>
                                <td></td>
                            </tr>
                        </thead>
                        <tbody>
                            <%--
                                listarIdeiasDisponiveis faz UMA única query HQL que:
                                  1. filtra status=VA e statusGrupo=AB
                                  2. exclui ideias em que o usuário já está inscrito
                                Resolve COL-10 (N+1 queries) e COL-14 (carrega tudo).
                            --%>
                            <%-- UX-01: estado vazio com mensagem contextual. --%>
                            <c:set var="ideiasDisponiveis" value="${ideiaDAO.listarIdeiasDisponiveis(usuarioClasse)}" />
                            <c:if test="${empty ideiasDisponiveis}">
                                <tr><td colspan="4" class="center-align grey-text" style="padding: 30px;">No momento não há ideias de outros usuários disponíveis para colaboração. Volte mais tarde!</td></tr>
                            </c:if>
                            <c:forEach var="ideia" items="${ideiasDisponiveis}">
                                <tr>
                                    <form name="detalhes" action="EntrarDetalheServlet" method="POST">
                                        <td class="title">
                                            <div class="truncate" style="max-width: 150px"><c:out value="${ideia.titulo}"/></div>
                                        </td>
                                        <td class="description">
                                            <div class="truncate" style="display: inline-block; max-width: 250px;"><c:out value="${ideia.descricao}"/></div>
                                            <i class="material-icons info-icon"
                                               style="color:#00796b; cursor: pointer; display: inline-block; font-size:21px;">info_outline</i>
                                        </td>
                                        <td>${data.formatarData(ideia.dtCriacao)}</td>
                                        <input type="hidden" value="${ideia.codigo}" name="codigo" />
                                        <td><input class="btn orange darken-1" type="submit" value="Participar" name="participar" /></td>
                                    </form>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>

                <!-- Modal para visualizar detalhes da ideia -->
                <div id="modal-detalhe-ideia" class="modal">
                    <div class="modal-content" style="text-align:left;">
                        <h4>Detalhe da ideia</h4>
                        <div class="titulo-ideia"></div>
                        <div class="descricao-ideia"></div>
                    </div>
                    <div class="modal-footer">
                        <a href="#!" class="modal-action modal-close btn-flat grey lighten-1">Fechar</a>
                    </div>
                </div>
            </div>
        </div>
    <script src="js/csrf.js"></script>
  </body>
    <script>
        $(document).ready(function () {
            $('.modal').modal();
        });

        $(".info-icon").click(function () {
            var titulo    = $(this).closest('tr').find(".title div").html();
            var descricao = $(this).closest('tr').find(".description div").html();
            $("#modal-detalhe-ideia .modal-content .titulo-ideia").html("<strong>Título: </strong>" + titulo);
            $("#modal-detalhe-ideia .modal-content .descricao-ideia").html("<strong>Descrição: </strong>" + descricao);
            $('#modal-detalhe-ideia').modal('open');
        });

        var input = document.getElementById('filtro-nome');
        var trs   = Array.prototype.slice.call(document.querySelectorAll('#lista tbody tr'));

        input.addEventListener('input', function () {
            var search = input.value.toLowerCase();
            trs.forEach(function (elem) {
                // Busca só em título + descrição (antes usava o textContent da linha
                // inteira, incluindo a data e o botão "Participar").
                var titulo    = (elem.querySelector('.title')       || {}).textContent || '';
                var descricao = (elem.querySelector('.description')  || {}).textContent || '';
                var alvo = (titulo + ' ' + descricao).toLowerCase();
                elem.style.display = alvo.includes(search) ? '' : 'none';
            });
        });
    </script>
    <%@include file="header/footer.jsp" %>
</html>
