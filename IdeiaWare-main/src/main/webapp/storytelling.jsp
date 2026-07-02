<%--<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header/headerCookies_2.jsp" %>


<html lang="pt-BR">

  <head>
    <%-- STR-08: removido charset UTF-8 que corrompida acentos nos textos do canvas --%>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <title>IdeiaWare - Storytelling</title>

    <link type="text/css" rel="stylesheet" href="css/storytelling.css" >
  </head>
  <body class="indigo lighten-5"> 
    <div id="overlay">
      <div class="loader"></div>
    </div>
    <div>
      <div class="row" style="min-height: 80vh; padding-top: 20px; width: 95%">                 
        <div class="col s3 grey lighten-2 z-depth-2" style="min-height: 85vh; padding-top: 20px;">
          <h5 class="center grey-text text-darken-3" style="padding-top: 10px; padding-bottom: 5px">Ferramentas</h5>

          <!--<div class="story-tools">
            <button class="btn indigo accent-2" id="carregar">Carregar</button>
            <button class="btn indigo accent-2" id="imprimirNome">Imprimir nomes</button>
            <button class="btn indigo accent-2" id="imprimirJSON">Imprimir JSON</button>
          </div>-->
          <div class="story-tools">
            <button class="btn indigo accent-2" style="display: " id="deletarAlgo">Ativar borracha</button>
          </div>
          <ul class="collapsible" data-collapsible="accordion">
            <li class="white">
              <div id="b1" class="collapsible-header"><i class="material-icons">image</i>Adicionar Imagem</div>
              <div class="collapsible-body">
                <form id="enviarImagem" method="POST" onsubmit="salvarProgresso()" action="UploadArquivoServlet" enctype="multipart/form-data">
                  </br><input type="file" id="arquivo" name="UploadImg" value="Carregar Imagem" /></br>
                  </br><input class="btn indigo accent-2" id="b1_1" type="submit" value="inserir arquivo" />
                </form>
                <output id="nomeArquivo"></output>
              </div>
            </li>
            <li class="white">
              <div class="collapsible-header"><i class="material-icons">format_paint</i>Adicionar Forma</div>
              <div class="collapsible-body">	

                <label>Cor</label>
                <select class="browser-default" id="corForma">
                  <option value="yellow">Amarelo</option>
                  <option value="blue">Azul</option>
                  <option value="green">Verde</option>
                  <option value="red">Vermelho</option>
                  <option value="black">Preto</option>
                  <option value="pink">Rosa</option>
                </select>

                <div class="center row">
                  <a class="col s4" id="b2_1" title="Quadrado"><img style="max-height: 4rem" src="imagens/quadradoPreto.png" ></a>
                  <a class="col s4" id="b2_2" title="Círculo"><img style="max-height: 4rem" src="imagens/circuloPreto.png" ></a>
                  <!--<a class="col s4" id="b2_2" onclick="mostra('ob2_2')" title="Círculo"><img style="max-height: 4rem" src="imagens/circulo.png" ></a>-->
                  <a class="col s4" id="b2_3" title="Flecha"><img style="max-height: 4rem" src="imagens/flechaPreto.png" ></a>
                  <div class="col s12" style="padding: 5px"></div>
                  <a class="col s4" id="b2_4" title="Triângulo"><img style="max-height: 4rem" src="imagens/trianguloPreto.png" ></a>
                  <a class="col s4" id="b2_5" title="Saco de Dinheiro"><img style="max-height: 4rem" src="imagens/dinheiroPreto.png" ></a>
                  <a class="col s4" id="b2_6" title="Caveira"><img style="max-height: 4rem" src="imagens/caveiraPreto.png" ></a>
                  <div class="col s12" style="padding: 5px"></div>
                  <a class="col s4" id="b2_7" title="Nuvem"><img style="max-height: 4rem" src="imagens/nuvemPreto.png" ></a>
                  <a class="col s4" id="b2_8" title="Nota"><img style="max-height: 4rem" src="imagens/notaPreto.png" ></a>
                  <a class="col s4" id="b2_9" title="Monitor"><img style="max-height: 4rem" src="imagens/monitorPreto.png" ></a>
                  <div class="col s12" style="padding: 5px"></div>
                  <a class="col s4" id="b2_10" title="Localizacao"><img style="max-height: 4rem" src="imagens/localizacaoPreto.png" ></a>
                  <a class="col s4" id="b2_11" title="Lampada"><img style="max-height: 4rem" src="imagens/lampadaPreto.png" ></a>
                  <a class="col s4" id="b2_12" title="Cadeado"><img style="max-height: 4rem" src="imagens/cadeadoPreto.png" ></a>
                  <div class="col s12" style="padding: 5px"></div>
                  <a class="col s4" id="b2_13" title="Banco"><img style="max-height: 4rem" src="imagens/bancoPreto.png" ></a>
                  <a class="col s4" id="b2_14" title="Balao"><img style="max-height: 4rem" src="imagens/balaoPreto.png" ></a>
                  <a class="col s4" id="b2_15" title="Linha"><img style="max-height: 4rem" src="imagens/linhaPreto.png" ></a>
                </div>
              </div>
            </li>

            <li class="white">
              <div class="collapsible-header"><i class="material-icons">textsms</i>Adicionar Texto</div>
              <div class="collapsible-body">


                <%-- STR-05: fontes e tamanhos corrigidos — values agora correspondem ao texto exibido --%>
                <label>Fonte</label>
                <select class="browser-default" id="fontFamily">
                  <option value="Times New Roman" selected>Times New Roman</option>
                  <option value="Arial">Arial</option>
                  <option value="Arial Black">Arial Black</option>
                  <option value="Calibri">Calibri</option>
                  <option value="Comic Sans MS">Comic Sans MS</option>
                  <option value="Georgia">Georgia</option>
                  <option value="Verdana">Verdana</option>
                  <option value="Tahoma">Tahoma</option>
                  <option value="Courier New">Courier New</option>
                </select>

                <label>Tamanho</label>
                <select class="browser-default" id="fontSize">
                  <option value="12">12</option>
                  <option value="14">14</option>
                  <option value="16" selected>16</option>
                  <option value="18">18</option>
                  <option value="20">20</option>
                  <option value="24">24</option>
                  <option value="30">30</option>
                  <option value="36">36</option>
                  <option value="40">40</option>
                  <option value="48">48</option>
                  <option value="60">60</option>
                  <option value="72">72</option>
                </select>

                <label>Cor</label>
                <input type="color" id="corTexto"></br></br>

                <button id="b3" >INSERIR</button>


              </div>
            </li>

            <!--<li class="white">
              <div id="b4" onclick="mostra('ob4')" class="collapsible-header"><i class="material-icons">edit</i>Desenho Livre </div>
              <div class="collapsible-body">
                <div class="fixed-action-btn">
                  <a class="btn-floating btn-large yellow darken-2 "><i class="large material-icons">brush</i></a>
                  <ul>
                    <li><a class="btn-floating yellow darken-1"><i style="font-size: 2rem" class="material-icons">brightness_1</i></a></li>
                    <li><a class="btn-floating yellow darken-1"><i style="font-size: 1.5rem" class="material-icons">brightness_1</i></a></li>
                    <li><a class="btn-floating yellow darken-1"><i style="font-size: 1rem" class="material-icons">brightness_1</i></a></li>
                    <li><a class="btn-floating yellow darken-1"><i style="font-size: 0.5rem" class="material-icons">brightness_1</i></a></li>
                  </ul>
                </div>
                <p> # Seleciona Cor </p>
                <p> # Adicionar botão 'Borracha'</p>
              </div>
            </li> -->

            <li class="white">
              <div id="b5" class="collapsible-header"><i class="material-icons">keyboard_voice</i>Adicionar Áudio</div>
              <div class="collapsible-body">
                <input id="start-btn" class="center btn indigo accent-2" type="button" name="UploadImg" value="Gravar"  />
                <input id="stop-btn" class="center btn indigo accent-2" type="button" name="UploadImg" value="Parar gravação" />
                <input id="save-btn" class="center btn indigo accent-2" type="button" name="UploadImg" value="Salvar áudio" />
                <ul id="recordingslist"></ul>
              </div>
            </li>
          </ul> 
          <!-- <div class="grey darken-3 divider" style="margin:15px"></div> -->
        </div>
        <div class="col s9 ">
          <div class="grey lighten-4 z-depth-2" id="workBoard" style="min-height: 85vh"> <!-- CANVAS -->
            <!-- <canvas id="myCanvas"> /canvas>  -->
            <div id="container" class="mycanvas"></div>
          </div>
        </div>

      </div>
    </div>
    <div id="modalDescricao" class="modal">
      <div class="grey-text text-darken-3 modal-content">
        <div class="right">
          <!--<button class="modal-action modal-close btn-floating red lighten-1"><i style="line-height: 42px" class="material-icons">close</i></button>-->
        </div>
        <h4>Descrição da Ideia</h4>
        <p>
        <div class="input-field">
          <div style="text-align: justify">
            <br>
            <div id="divTitulo"><br><b>Ideia:</b> <c:out value="${story.ideia.titulo}"/></div>
            <div id="divDescricao"><br><b>Descrição:</b><br> <c:out value="${descLog.descricao}"/></div>
            <div id="divUsuario"><br><b>Usuário:</b> <c:out value="${story.ideia.usuario.nome}"/></div>
            <br>
          </div>
        </div>
      </div>
    </div>

    <div id="modalResize" class="modal">
      <div class="modal-content">
        <div class="row">
          <div class="input-field col s6">
            <input value="" id="AlturaId" placeholder="Altura" aria-label="Altura" type="text" class="validate">
            <label class="active" for="Altura">Altura</label>
          </div>
        </div>
        <div class="row">
          <div class="input-field col s6">
            <input value="" id="LarguraId" placeholder="Largura" aria-label="Largura" type="text" class="validate">
            <label class="active" for="Largura">Largura</label>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <input hidden="true" id="imgaemIdResize" value="">
        <button class="btn" id="btnModificar">Modificar</button>
      </div>
    </div>
  <script src="js/csrf.js"></script>
  </body>

  <script type="text/javascript">
      $(document).ready(function () {
        $('.modal').modal({
          ready: function (modal, trigger) {
          }
        });
      });
  </script>

  <footer class="center indigo lighten-1 page-footer">
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