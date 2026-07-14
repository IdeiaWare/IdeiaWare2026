<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@include file="header/headerCookies.jsp" %>
<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <title>IdeiaWare - Módulo Colaborativo</title>
    </head>
    <body class="center-align teal darken-1">
    <main>
        <%-- UX-VOLTAR-V2: icone circular flutuante, faltava saida de volta pra tela inicial (index.jsp). --%>
        <a href="index.jsp" class="btn-floating btn-large teal darken-1 tooltipped" style="position:fixed; top:75px; left:20px; z-index:998;" data-position="right" data-delay="50" data-tooltip="Voltar" aria-label="Voltar"><i class="material-icons">arrow_back</i></a>
        <div class="" style="min-height: 90vh">
            <div class="container">
                <div style="padding-top: 40px">
                    <h4 class="white-text">MÓDULO COLABORATIVO</h4>
                    <span class="white-text">IDEIAWARE</span>
                    <p/>
                    <div class="teal lighten-1" style="width:150px; height:150px;
                         margin: 0 auto 30px; padding: 50px 30px;
                         border-radius: 100%; box-sizing: border-box;">
                        <img style="display:block; width:80%; margin-left:8px; margin-top:-35px" src="imagens/idea.png" alt="IdeiaWare"/>
                    </div>
                </div>

                <!-- Os 3 cards lado a lado (col m4 cada = 12 colunas) -->
                <%-- COL-16: flexbox iguala a altura dos 3 cards (senao, o de texto menor ficava mais baixo). --%>
                <style>
                  .colab-cards { display: flex; flex-wrap: wrap; }
                  .colab-cards > .col { display: flex; }
                  .colab-cards .card { display: flex; flex-direction: column; width: 100%; }
                  .colab-cards .card .card-content { flex: 1 0 auto; }
                </style>
                <div class="row colab-cards" style="margin-top: 20px;">

                    <!-- Card: Minhas Ideias -->
                    <div class="col s12 m4">
                        <div class="card teal lighten-2 z-depth-2" style="border-radius: 0.5em;">
                            <a href="minha-ideia.jsp">
                                <div class="card-image" style="padding: 30px 0;">
                                    <div class="center-align">
                                        <i class="material-icons white-text" style="font-size: 5rem;">account_box</i>
                                    </div>
                                </div>
                            </a>
                            <div class="card-content white-text" style="padding: 10px 20px 20px;">
                                <span class="card-title" style="font-weight: bold;">Minhas Ideias</span>
                                <p>Acompanhe suas ideias criadas, participe das colaborações em andamento e avance para as próximas etapas.</p>
                            </div>
                            <div class="card-action" style="border-top: 1px solid rgba(255,255,255,0.3);">
                                <a href="minha-ideia.jsp" class="white-text">
                                    <i class="material-icons left">arrow_forward</i>Acessar
                                </a>
                            </div>
                        </div>
                    </div>

                    <!-- Card: Outras Ideias -->
                    <div class="col s12 m4">
                        <div class="card teal darken-1 z-depth-2" style="border-radius: 0.5em;">
                            <a href="lista-ideia.jsp">
                                <div class="card-image" style="padding: 30px 0;">
                                    <div class="center-align">
                                        <i class="material-icons white-text" style="font-size: 5rem;">web_asset</i>
                                    </div>
                                </div>
                            </a>
                            <div class="card-content white-text" style="padding: 10px 20px 20px;">
                                <span class="card-title" style="font-weight: bold;">Outras Ideias</span>
                                <p>Explore ideias de outros usuários disponíveis para colaboração e participe do processo de desenvolvimento.</p>
                            </div>
                            <div class="card-action" style="border-top: 1px solid rgba(255,255,255,0.3);">
                                <a href="lista-ideia.jsp" class="white-text">
                                    <i class="material-icons left">arrow_forward</i>Explorar
                                </a>
                            </div>
                        </div>
                    </div>

                    <!-- Card: Cadastrar nova ideia -->
                    <div class="col s12 m4">
                        <div class="card orange darken-1 z-depth-2" style="border-radius: 0.5em;">
                            <a href="cadastro-ideia.jsp">
                                <div class="card-image" style="padding: 30px 0;">
                                    <div class="center-align">
                                        <i class="material-icons white-text" style="font-size: 5rem;">add_circle_outline</i>
                                    </div>
                                </div>
                            </a>
                            <div class="card-content white-text" style="padding: 10px 20px 20px;">
                                <span class="card-title" style="font-weight: bold;">Cadastrar Nova Ideia</span>
                                <p>Tem uma ideia? Registre-a aqui para receber colaborações e desenvolvê-la com o grupo.</p>
                            </div>
                            <div class="card-action" style="border-top: 1px solid rgba(255,255,255,0.3);">
                                <a href="cadastro-ideia.jsp" class="white-text">
                                    <i class="material-icons left">add</i>Nova ideia
                                </a>
                            </div>
                        </div>
                    </div>

                </div>

            </div>
        </div>
    <script src="js/csrf.js"></script>
  </main>
    </body>
    <%@include file="header/footer.jsp" %>
</html>
