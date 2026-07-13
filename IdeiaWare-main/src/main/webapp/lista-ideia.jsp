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
                    <h1>Outras Ideias</h1>
                    <%-- UX-VOLTAR-V2: icone circular flutuante (mesmo padrao do colaboracao.jsp, nao mexe no header compartilhado). --%>
                    <a href="index-colaboracao.jsp" class="btn-floating btn-large teal lighten-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
                    <jsp:useBean id="ideiaDAO" class="edu.unisc.lic.dao.IdeiaDAO" />
                    <jsp:useBean id="data"    class="edu.unisc.lic.classes.Data" />

                    <%-- COL-10/COL-14: 1 unica query HQL (status=VA, statusGrupo=AB, exclui ja-inscritas). --%>
                    <c:set var="ideiasDisponiveis" value="${ideiaDAO.listarIdeiasDisponiveis(usuarioClasse)}" />

                    <%-- UX-01: busca e tabela so aparecem quando ha ideias. --%>
                    <c:if test="${empty ideiasDisponiveis}">
                        <div class="center-align grey-text" style="padding: 40px 20px;">
                            <i class="material-icons" style="font-size: 3rem; display:block;">web_asset</i>
                            No momento não há ideias de outros usuários disponíveis para colaboração. Volte mais tarde!
                        </div>
                    </c:if>

                    <c:if test="${not empty ideiasDisponiveis}">
                    <form class="input-field">
                        <input placeholder="Buscar ideia (título, descrição)" aria-label="Buscar ideia" id="filtro-nome" type="text" class="validate">
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
                            <c:forEach var="ideia" items="${ideiasDisponiveis}">
                                <tr>
                                    <form name="detalhes" action="EntrarDetalheServlet" method="POST">
                                        <td class="title">
                                            <%-- M.5 (2026-07-06): titulo (max 50) completo, sem truncate/ellipsis de 150px. --%>
                                            <div style="word-break: break-word;"><c:out value="${ideia.titulo}"/></div>
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
                    </c:if>
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

        // UX: guard evita TypeError ao chamar addEventListener em null quando a lista esta vazia (campo nao existe no DOM).
        var input = document.getElementById('filtro-nome');
        if (input) {
            var trs = Array.prototype.slice.call(document.querySelectorAll('#lista tbody tr'));
            input.addEventListener('input', function () {
                var search = input.value.toLowerCase();
                trs.forEach(function (elem) {
                    // Busca so em titulo + descricao (antes usava o textContent da linha inteira, incluindo data e botao).
                    var titulo    = (elem.querySelector('.title')       || {}).textContent || '';
                    var descricao = (elem.querySelector('.description')  || {}).textContent || '';
                    var alvo = (titulo + ' ' + descricao).toLowerCase();
                    elem.style.display = alvo.includes(search) ? '' : 'none';
                });
            });
        }
    </script>
    <%@include file="header/footer.jsp" %>
</html>
