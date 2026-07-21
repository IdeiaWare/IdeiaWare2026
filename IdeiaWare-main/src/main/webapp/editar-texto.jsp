<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true" %>
<%@include file="header/headerCookies.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
  <head>
    <link href="js/quill/quill.snow.css" rel="stylesheet">
    <title>IdeiaWare - Editar Texto</title>
    <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@600' rel='stylesheet'>
    <style>
      /* M.9: altura fixa no #editor, o .ql-editor interno ja rola. */
      #editor {
        height: 40vh;
      }
      .titulo-modulo { font-family: 'Poppins', sans-serif; font-weight: 600; margin-bottom: 8px; }
    </style>
  </head>
  <body class="center-align blue-grey lighten-5">
    <main>
    <%-- UX-VOLTAR-V2: icone flutuante padrao (antes so tinha o "Cancelar" no fim do form). --%>
    <a href="colaboracao.jsp" class="btn-floating btn-large teal lighten-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
    <div class="" style="min-height: 90vh;">
      <div class="container">
        <jsp:useBean id="colaboracaoIdeia" class="edu.unisc.lic.domain.ColaboracaoIdeia" />
        <jsp:useBean id="colaboracaoIdeiaDAO" class="edu.unisc.lic.dao.ColaboracaoIdeiaDAO" />
        <jsp:useBean id="ideiaDAO" class="edu.unisc.lic.dao.IdeiaDAO" />
        <jsp:setProperty name="colaboracaoIdeia" property="ideia" value="${ideiaDAO.buscar(sessionScope.ideiaId)}" />
        <h1 class="blue-grey-text text-darken-2 titulo-modulo">Editar Texto</h1></p>
        <h3><c:out value="${sessionScope.ideiaTitulo}"/></h3>
        <div style="padding: 10px" class="" >
          <form action="SalvarTextoServlet" name="Salvar" method="POST">
            <div id="editor"></div>
            <%-- UX-EDITAR-TEXTO: textarea carregado no Quill via dangerouslyPasteHTML. --%>
            <textarea id="descricao-inicial" style="display:none;"><c:out value="${sessionScope.ideiaDescricao}"/></textarea>
            <div>
              <br/>
              <input hidden="true" name="idUsuario" value="${idUsuario}">
              <input hidden="true" name="ideiaId" value="${ideiaId}">
              <input hidden="true" id="texto" name="texto">
              <%-- UX-VOLTAR: botao Cancelar (antes so dava pra sair pelo logo, perdendo o contexto). --%>
              <input class="btn-large orange darken-1 right" name="conluir" type="submit" value="Concluir" onclick="document.getElementById('texto').value = quill.root.innerHTML;">
              <a href="colaboracao.jsp" class="btn-large btn-flat grey-text text-darken-1 right" style="margin-right: 8px;">Cancelar</a>
            </div>
          </form>
        </div>
      </div>
      <div style="height: 90px"></div>
  <script src="js/csrf.js"></script>
  </main>
    </body>
  <%@include file="header/footer.jsp" %>
</html>
<!-- Include the Quill library -->
<script src="js/quill/quill.js"></script>

<!-- Initialize Quill editor -->
<script>

                  var Bold = Quill.import('formats/bold');
                  Bold.tagName = 'B';
                  Quill.register(Bold, true);

                  var ListItem = Quill.import('formats/list/item');

                  class PlainListItem extends ListItem {
                    formatAt(index, length, name, value) {
                      if (name === 'list') {
                        super.formatAt(name, value);
                      }
                    }
                  }

                  Quill.register(PlainListItem, true);

                  var ColorClass = Quill.import('attributors/style/color');
                  Quill.register(ColorClass, true);


                  var Size = Quill.import('attributors/style/size');
                  Size.whitelist = ['10px', '18px', '32px'];
                  Quill.register(Size, true);

                  var toolbarOptions = [
                    ['bold', 'italic', 'underline', 'strike'],
                    [{'size': ['10px', false, '18px', '32px']}],
                    [{'color': ['black', 'red', 'yellow', 'blue', 'green', 'purple', 'orange']}],
                    [{'script': 'sub'}, {'script': 'super'}],
                    ['clean']
                  ];

                  var quill = new Quill('#editor', {
                    modules: {
                      toolbar: toolbarOptions,
                    },
                    theme: 'snow'
                  });

                  // UX-EDITAR-TEXTO: carrega a descricao no Quill via clipboard.dangerouslyPasteHTML.
                  var descInicial = document.getElementById('descricao-inicial').value;
                  if (descInicial && descInicial.trim() !== '') {
                    quill.clipboard.dangerouslyPasteHTML(descInicial);
                  }


</script>