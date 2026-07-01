<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true" %>
<%@include file="header/headerCookies.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
  <head>
    <link href="js/quill/quill.snow.css" rel="stylesheet">
    <title>IdeiaWare - Editar Texto</title>
  </head>
  <body class="center-align">
    <div class="white" style="min-height: 90vh;">
      <div class="container">
        <jsp:useBean id="colaboracaoIdeia" class="edu.unisc.lic.domain.ColaboracaoIdeia" />
        <jsp:useBean id="colaboracaoIdeiaDAO" class="edu.unisc.lic.dao.ColaboracaoIdeiaDAO" />
        <jsp:useBean id="ideiaDAO" class="edu.unisc.lic.dao.IdeiaDAO" />
        <jsp:setProperty name="colaboracaoIdeia" property="ideia" value="${ideiaDAO.buscar(sessionScope.ideiaId)}" />
        <h1>Editar Texto</h1></p>
        <h3><c:out value="${sessionScope.ideiaTitulo}"/></h3>
        <div style="padding: 10px" class="white" >
          <form action="SalvarTextoServlet" name="Salvar" method="POST">
            <div id="editor">
              <c:out value="${sessionScope.ideiaDescricao}"/>
            </div>
            <div>
              <br/>
              <input hidden="true" name="idUsuario" value="${idUsuario}">
              <input hidden="true" name="ideiaId" value="${ideiaId}">
              <input hidden="true" id="texto" name="texto">
              <input class="btn-large orange darken-1 right" name="conluir" type="submit" value="Concluir" onclick="javascript:document.getElementById('texto').value = document.getElementById('editor').innerHTML;">
            </div>
          </form>
        </div>
      </div>
      <div style="height: 90px"></div>
  <script src="js/csrf.js"></script>
  </body>
  <%@include file="header/footer.jsp" %>
</html>
<!-- Include the Quill library -->
<script src="js/quill/quill.js"></script>

<!-- Initialize Quill editor -->
<script>

                  /*!
                   * Quill Editor v1.3.2
                   * https://quilljs.com/
                   * Copyright (c) 2014, Jason Chen
                   * Copyright (c) 2013, salesforce.com
                   */
                  var Bold = Quill.import('formats/bold');
                  Bold.tagName = 'B';   // Quill uses <strong> by default
                  Quill.register(Bold, true);

                  var ListItem = Quill.import('formats/list/item');

                  class PlainListItem extends ListItem {
                    formatAt(index, length, name, value) {
                      if (name === 'list') {
                        // Allow changing or removing list format
                        super.formatAt(name, value);
                      }
                      // Otherwise ignore
                    }
                  }

                  Quill.register(PlainListItem, true);

                  var ColorClass = Quill.import('attributors/style/color');
//                                var SizeStyle = Quill.import('attributors/style/size');
                  Quill.register(ColorClass, true);
//                                Quill.register(SizeStyle, true);


                  var Size = Quill.import('attributors/style/size');
                  Size.whitelist = ['10px', '18px', '32px'];
                  Quill.register(Size, true);

//                                var fontSizeStyle = Quill.import('attributors/style/size');
//                                fontSizeStyle.whitelist = ['small', 'large', 'huge'];
//                                Quill.register(fontSizeStyle, true);



                  var toolbarOptions = [
                    ['bold', 'italic', 'underline', 'strike'], // toggled buttons

//                                    [{'size': ['small', false, 'large', 'huge']}], // custom dropdown
                    [{'size': ['10px', false, '18px', '32px']}],

//                                  [{'list': 'ordered'}, {'list': 'bullet'}],
                    [{'color': ['black', 'red', 'yellow', 'blue', 'green', 'purple', 'orange']}],
                    [{'script': 'sub'}, {'script': 'super'}], // superscript/subscript
//                    [{'indent': '-1'}, {'indent': '+1'}], // outdent/indent

//                    [{'align': []}],

                    ['clean']                                         // remove formatting button
                  ];

                  var quill = new Quill('#editor', {
                    modules: {
                      toolbar: toolbarOptions,
                    },
                    theme: 'snow'
                  });
                  console.log(Quill.imports);


</script>