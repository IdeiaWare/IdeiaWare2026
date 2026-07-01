<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true" %>
<%@include file="header/headerCookies.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
  <head>
    <title>Cadastro de Ideia</title>
  </head>
  <body class="center-align teal darken-1">
    <div class="white" style="min-height: 90vh" role="main">
      <div class="container">
        <div style="padding: 10px;" class="white">
          <h1>Cadastro de Ideia</h1>

          <%-- COL-15: exibe mensagem de erro vinda do servidor --%>
          <c:if test="${not empty param.erro}">
            <div class="card-panel red lighten-4 red-text text-darken-4" style="text-align:left;">
              <c:choose>
                <c:when test="${param.erro eq 'titulo_obrigatorio'}">
                  <i class="material-icons left">error_outline</i> O título é obrigatório.
                </c:when>
                <c:when test="${param.erro eq 'titulo_longo'}">
                  <i class="material-icons left">error_outline</i> O título deve ter no máximo 50 caracteres.
                </c:when>
                <c:when test="${param.erro eq 'descricao_obrigatoria'}">
                  <i class="material-icons left">error_outline</i> A descrição é obrigatória.
                </c:when>
                <c:when test="${param.erro eq 'descricao_longa'}">
                  <i class="material-icons left">error_outline</i> A descrição deve ter no máximo 200 caracteres.
                </c:when>
                <c:otherwise>
                  <i class="material-icons left">error_outline</i> Preencha todos os campos corretamente.
                </c:otherwise>
              </c:choose>
            </div>
          </c:if>

          <div class="row">
            <form name="cadastroIdeia" action="CadastroIdeiaServlet" onsubmit="return check(this)" method="POST">
              <p/>
              <div class="input-field">
                <div class="input-field">
                  <input autocomplete="off" id="titulo" type="text" name="titulo"
                         data-length="50" maxlength="50" minlength="4">
                  <label for="titulo">Título</label>
                </div>
                <div class="input-field col-s12">
                  <textarea id="descricao" type="text" class="materialize-textarea"
                            name="descricao" maxlength="200" data-length="200"></textarea>
                  <label for="descricao">Descrição</label>
                </div>
                <br />
                <input class="btn orange darken-1" type="submit" value="Inserir" name="Inserir" />
              </div>
            </form>
            <a href="minha-ideia.jsp" class="btn-flat grey-text text-darken-1" name="Cancelar">Cancelar</a>
          </div>
        </div>
      </div>
    </div>
  <script src="js/csrf.js"></script>
  </body>
  <%@include file="header/footer.jsp" %>
  <script>
    function check(form) {
      var titulo    = document.getElementById("titulo").value.trim();
      var descricao = document.getElementById("descricao").value.trim();
      if (titulo === '' || descricao === '') {
        M.toast({html: 'Preencha o título e a descrição antes de continuar.'});
        return false;
      }
      return true;
    }
  </script>
</html>
