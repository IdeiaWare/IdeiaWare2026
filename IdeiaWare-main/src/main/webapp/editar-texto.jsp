<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8" session="true" %>
<%@include file="header/headerCookies.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
  <head>
    <link href="js/quill/quill.snow.css" rel="stylesheet">
    <title>IdeiaWare - Editar Texto</title>
    <style>
      /* M.9 (2026-07-06): o editor crescia demais e empurrava os botoes Concluir/Cancelar
         pra muito abaixo (precisava rolar quase 1 pagina). O Quill modela altura assim:
         .ql-container (=#editor) e .ql-editor tem height:100% -- entao a altura vinha do
         pai (min-height:90vh). O jeito certo (uso pretendido do Quill) e dar uma ALTURA
         fixa no #editor/container; o .ql-editor de dentro ja tem overflow-y:auto e rola
         sozinho. Setar so max-height no .ql-editor (tentativa anterior) nao resolvia
         porque o container continuava com height:100%. */
      #editor {
        height: 40vh;
      }
    </style>
  </head>
  <body class="center-align">
    <%-- UX-VOLTAR-V2 (2026-07-06, achado real testando): faltava o icone flutuante
         padrao do resto do app -- so tinha o "Cancelar" no fim do form. Mesmo destino
         (colaboracao.jsp le tudo de sessao, ver comentario UX-VOLTAR mais abaixo). --%>
    <a href="colaboracao.jsp" class="btn-floating btn-large teal lighten-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
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
            <div id="editor"></div>
            <%-- A descricao vai num textarea ESCONDIDO e e carregada no Quill via
                 clipboard (dangerouslyPasteHTML) no fim da pagina. Antes ela ficava
                 com <c:out> DENTRO do #editor -> o HTML vinha ESCAPADO e o Quill
                 mostrava as tags (<p>, <b>...) como TEXTO literal no editor. --%>
            <textarea id="descricao-inicial" style="display:none;"><c:out value="${sessionScope.ideiaDescricao}"/></textarea>
            <div>
              <br/>
              <input hidden="true" name="idUsuario" value="${idUsuario}">
              <input hidden="true" name="ideiaId" value="${ideiaId}">
              <input hidden="true" id="texto" name="texto">
              <%-- UX-VOLTAR: editor nao tinha cancelar -- so dava pra sair perdendo o
                   contexto (voltando pelo logo). colaboracao.jsp le tudo de sessao
                   (ideiaId/lider/isRetencao ja setados por EntrarColaboracaoServlet),
                   entao um link direto reconstroi a tela certa sem precisar de servlet. --%>
              <input class="btn-large orange darken-1 right" name="conluir" type="submit" value="Concluir" onclick="document.getElementById('texto').value = quill.root.innerHTML;">
              <a href="colaboracao.jsp" class="btn-large btn-flat grey-text text-darken-1 right" style="margin-right: 8px;">Cancelar</a>
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

                  // Carrega a descricao atual DENTRO do Quill pela API de clipboard
                  // (converte o HTML no modelo do Quill e mantem so os formatos
                  // conhecidos). Resolve os DOIS bugs:
                  //  (1) as tags apareciam como texto literal (era HTML escapado);
                  //  (2) ao salvar, document.getElementById('editor').innerHTML trazia
                  //      a marcacao INTERNA do Quill (ql-editor/ql-clipboard, atributos
                  //      contenteditable) = os "caracteres estranhos/bugados".
                  // Agora o Concluir usa quill.root.innerHTML = so o conteudo limpo.
                  var descInicial = document.getElementById('descricao-inicial').value;
                  if (descInicial && descInicial.trim() !== '') {
                    quill.clipboard.dangerouslyPasteHTML(descInicial);
                  }


</script>