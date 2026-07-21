<%
    Boolean resposta = (Boolean) request.getAttribute("resposta");
    Boolean respostaCadastro = (Boolean) request.getAttribute("respostaCadastro");
    Boolean respostaCadastro2 = (Boolean) request.getAttribute("respostaCadastro2");
    Boolean respostaCadastro3 = (Boolean) request.getAttribute("respostaCadastro3");
    Boolean respostaCadastro4 = (Boolean) request.getAttribute("respostaCadastro4");
    Boolean resposta5 = (Boolean) request.getAttribute("resposta5");
    Boolean ErroRedefinicaoSenha = (Boolean) request.getAttribute("ErroRedefinicaoSenha");
    Boolean SucessoRedefinicaoSenha = (Boolean) request.getAttribute("SucessoRedefinicaoSenha");
    Boolean SenhaRedefinidaComSucesso = (Boolean) request.getAttribute("SenhaRedefinidaComSucesso");

%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="pt-BR">
    <head>
        <title>IdeiaWare - Login</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">
        <link rel='stylesheet prefetch' href='https://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700,900'>
        <%-- TITULO-MODULO-POPPINS: Montserrat trocada pela Poppins. --%>
        <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@600' rel='stylesheet'>
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href='https://fonts.googleapis.com/css?family=Condiment' rel='stylesheet'>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/style.css">
        <link type="text/css" rel="stylesheet" href="css/materialize.min.css"  media="screen,projection"/>
        <%-- MAT-CONSOLIDA: jQuery precisa carregar antes do materialize.min.js local. --%>
        <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
        <script src="js/materialize.min.js"></script>
    </head>
    <style>
        .title-app {
            color:#fff;
            font-family: 'Condiment';
            font-size: 42px;
            line-height: 25px
        }
        .title-app2 {
            color:#fff;
            font-family: 'Condiment';
            font-size: 100px;
        }
        .card .icon {
            font-size:50px;
        }
        .card-content .card-title {
            color:#fff !important;
        }
        .card a {
            color:#fff;
        }
        .icon .fa-circle {
            color:#fff;
        }
        .new-idea .icon .fa-comments {
            color:#4db6ac;
        }
        .storytelling .icon .fa-pencil {
            color:#5c6bc0
        }
        .toolkit .icon .fa-briefcase {
            color:#c62828;
        }
        .knowledge .icon .fa-database {
            color:#0288d1;
        }
        .fa-hand-o-right {
            border-radius:50%;
            box-shadow: 0 0 0 rgba(255,255,255, 0.4);
            animation: pulse 2s infinite;
        }
        .fa-hand-o-right:hover {
            animation: none;
        }      

        @-webkit-keyframes pulse {
            0% {
                -webkit-box-shadow: 0 0 0 0 rgba(255,255,255, 0.4);
            }
            70% {
                -webkit-box-shadow: 0 0 0 10px rgba(255,255,255, 0);
            }
            100% {
                -webkit-box-shadow: 0 0 0 0 rgba(255,255,255, 0);
            }
        }
        @keyframes pulse {
            0% {
                -moz-box-shadow: 0 0 0 0 rgba(255,255,255, 0.4);
                box-shadow: 0 0 0 0 rgba(255,255,255, 0.4);
            }
            70% {
                -moz-box-shadow: 0 0 0 10px rgba(255,255,255, 0);
                box-shadow: 0 0 0 10px rgba(255,255,255, 0);
            }
            100% {
                -moz-box-shadow: 0 0 0 0 rgba(255,255,255, 0);
                box-shadow: 0 0 0 0 rgba(255,255,255, 0);
            }
        }
        .password-strength {
            height: 10px;
            border-radius: 5px;
            margin-top: 10px;
            background-color: #e0e0e0;
            overflow: hidden;
        }
        .strength-bar {
            height: 100%;
            width: 0;
            background-color: red;
            transition: width 0.3s;
        }
        .strength-text {
            margin-top: 10px;
            text-align: center;
            font-weight: bold;
        }
        /* CADASTRO-ESPACAMENTO: com a logo grande removida, sobrou um vao antes do 1o campo. */
        .container .info {
            margin: 25px auto;
        }
        /* CADASTRO-REQUISITOS-CARD: cartao com fundo/borda pra separar visualmente do checkbox de termos. */
        .criterio-card {
            background-color: #eceff1;
            border-radius: 6px;
            padding: 12px 16px;
            margin: 16px 0;
            text-align: left;
        }
        .criterio-card-title {
            font-size: 12px;
            color: #607d8b;
            font-weight: 600;
            margin: 0 0 6px 0;
        }
        .criterio {
            list-style: none;
            padding: 0;
            margin: 0;
            font-size: 12px;
        }
        .criterio li {
            color: #c62828;
            padding: 2px 0;
        }
        .criterio li.valid {
            color: #2e7d32;
        }
        .criterio li::before {
            content: "✕  ";
            font-weight: bold;
        }
        .criterio li.valid::before {
            content: "✓  ";
        }
        /* CADASTRO-PLACEHOLDER-DUPLICADO: campos do cadastro alinhados a esquerda (like os labels), nao mais centralizados. */
        .register-form input {
            text-align: left;
        }

        .modal-content h4{
            color: #2c3e50;
            border-bottom: 2px solid #3498db;
            padding-bottom: 10px;
            margin-bottom: 20px;
            margin-top: 20px;
            font-size: 24px;
        }
        .modal-content ol{
            list-style: decimal inside;
            padding-left: 0;
        }
        .modal-content li{
            margin: 5px 5px 10px 5px;
        }
        .modal-content p{
            margin: 5px 5px 10px 5px;
        }
        .perfil-label {
            display: block;
            text-align: left;
            font-size: 13px;
            color: #607d8b;
            font-weight: 500;
            margin: 14px 0 2px 0;
        }
        /* UX-LOGIN-BOTAO-HOVER: .blue.accent-1 do Materialize trava a cor com !important, sem hover. */
        .login-form button.blue.accent-1,
        .register-form button.blue.accent-1,
        .reset-password-form button.blue.accent-1 {
            background-color: #448aff !important;
        }
        .login-form button.blue.accent-1:hover,
        .register-form button.blue.accent-1:hover,
        .reset-password-form button.blue.accent-1:hover {
            background-color: #82b1ff !important;
        }
    </style>

    <body class="blue-grey lighten-5" id="app">
    <main>
        <nav>
            <div class="nav-wrapper blue-grey lighten-1 z-depth-2">
                <a  class="brand-logo" style="left: 50px">
                    <ul style="width:300px" id="nav-mobile" class="left hide-on-med-and-down">
                        <div class="row" style="padding-left: 10px">
                            <div class="col s1 blue-grey lighten-3" style=" width: 50px;  height: 50px;
                                 margin-top: 5px;  padding: 6px 6px;
                                 border-radius: 100%;  box-sizing: border-box;">
                                <img style="display: block;  width: 62%;  margin-left: 7px; margin-top: -1px" src="imagens/idea.png" alt="IdeiaWare"/>
                            </div>
                            <h1 class=" col s4 center-align title-app">IdeiaWare</h1>
                        </div>
                    </ul>
                </a>

            </div>
        </nav>
        <div class="container">
            <div class="info">
                <h4 class=" blue-grey-text text-darken-2 title-app2">IdeiaWare</h4>
                <%-- LOGIN-TITULO-BUG: id novo pro JS atualizar o texto ao trocar de formulario (antes ficava sempre "Login"). --%>
                <h5 id="form-heading" class="text-darken-2 blue-grey-text" style="font-family:'Poppins',sans-serif; font-weight:600;">Login</h5>
            </div>
        </div>
        <div class="form">
            <c:if test="${resposta}">
                <div class="erro">
                    <h6>Login e/ou senha inválido(s)!</h6>
                </div> 
            </c:if>
            <c:if test="${respostaCadastro}">
                <div class="erro">
                    <h6>Usuário já existente!</h6>
                </div> 
            </c:if>
            <c:if test="${respostaCadastro3}">
                <div class="erro">
                    <h6>Preencha todos os campos para criar a conta.</h6>
                </div>
            </c:if>
            <c:if test="${respostaCadastro4}">
                <div class="erro">
                    <h6>E-mail já cadastrado!</h6>
                </div> 
            </c:if>
            <c:if test="${resposta5}">
                <div class="erro">
                    <h6>E-mail não cadastrado para redefinição de senha!</h6>
                </div> 
            </c:if>
            <c:if test="${ErroRedefinicaoSenha}">
                <div class="erro">
                    <h6>Houve um problema ao enviar e-mail de redefinição de senha!</h6>
                </div> 
            </c:if>
            <c:if test="${SucessoRedefinicaoSenha}">
                <div class="sucesso">
                    <h6>Se o e-mail informado existir, enviamos um link para redefinição de senha!</h6>
                </div>
            </c:if>
            <c:if test="${SenhaRedefinidaComSucesso}">
                <div class="sucesso">
                    <h6>Senha redefinida com sucesso! Faça login com a nova senha.</h6>
                </div>
            </c:if>

            <form id="login-form" class="login-form" action="LogInServlet" method="POST" >
                <div class="thumbnail">
                    <img src="imagens/idea.png" alt="IdeiaWare"/>
                </div>
                <input name="usuario" type="text" placeholder="usuário" aria-label="usuário" pattern=".{4,32}" required title="O campo nome de usuario deve conter entre 4 e 32 caracteres"/>
                <div style="position:relative;">
                    <input name="senha" id="login-senha" type="password" placeholder="senha" aria-label="senha"/>
                    <button type="button" id="toggle-login-senha" tabindex="-1" aria-label="Mostrar senha" onclick="toggleSenhaVisibility('login-senha','toggle-login-senha')" style="position:absolute; right:6px; top:50%; transform:translateY(-50%); width:32px; height:32px; min-width:0; background:none; border:0; padding:0; margin:0; cursor:pointer; display:flex; align-items:center; justify-content:center;"><i class="material-icons" style="color:#9e9e9e;">visibility_off</i></button>
                </div>
                <p class="forgot-password"><a href="#" id="forgot-password-link">Esqueceu a senha?</a></p>

                <button class="blue accent-1" type="submit">login</button>
                <p class="message">Não é cadastrado? <a href="#" id="show-register-form">Crie uma conta</a></p>
            </form>

            <form id="register-form" class="register-form" action="CadastroUsuarioServlet" method="POST" style="display:none;">
                <%-- CADASTRO-PLACEHOLDER-DUPLICADO: placeholder nao repete mais o label. --%>
                <label for="reg-nome" class="perfil-label">Nome</label>
                <input id="reg-nome" name="nome" type="text" placeholder="Digite seu nome completo" aria-label="nome" pattern=".{4,64}" required title="O campo nome deve conter entre 4 e 64 caracteres" />
                <label for="reg-usuario" class="perfil-label">Usuário</label>
                <input id="reg-usuario" name="usuario" type="text" placeholder="Escolha um nome de usuário" aria-label="usuário" pattern=".{4,32}" required title="O campo nome de usuario deve conter entre 4 e 32 caracteres" />
                <label for="reg-email" class="perfil-label">E-mail</label>
                <input id="reg-email" name="email" type="email" placeholder="seu@email.com" aria-label="email" pattern="\w+(\+?\w+)@\w+(\.\w+)+" required title="email@exemplo.com" maxlength="100"/>
                <label for="senha" class="perfil-label">Senha</label>
                <!-- SENHA-#: pattern antigo so aceitava @$!%*?& e rejeitava '#' -- agora aceita qualquer especial. -->
                <div style="position:relative;">
                    <input name="senha" id="senha" type="password" placeholder="Crie uma senha" aria-label="senha" pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9])\S{8,32}$" required title="O campo senha deve conter entre 8 e 32 caracteres. E os 5 requisitos abaixo." maxlength="32" oninput="checkPasswordStrength()"/>
                    <button type="button" id="toggle-reg-senha" tabindex="-1" aria-label="Mostrar senha" onclick="toggleSenhaVisibility('senha','toggle-reg-senha')" style="position:absolute; right:6px; top:50%; transform:translateY(-50%); width:32px; height:32px; min-width:0; background:none; border:0; padding:0; margin:0; cursor:pointer; display:flex; align-items:center; justify-content:center;"><i class="material-icons" style="color:#9e9e9e;">visibility_off</i></button>
                </div>

                <label for="reg-senha2" class="perfil-label">Confirmar senha</label>
                <div style="position:relative;">
                    <input id="reg-senha2" name="senha2" type="password" placeholder="Repita a senha" aria-label="digite a senha novamente" pattern=".{4,32}" required title="O campo repetir senha deve conter entre 4 e 32 caracteres" />
                    <button type="button" id="toggle-reg-senha2" tabindex="-1" aria-label="Mostrar senha" onclick="toggleSenhaVisibility('reg-senha2','toggle-reg-senha2')" style="position:absolute; right:6px; top:50%; transform:translateY(-50%); width:32px; height:32px; min-width:0; background:none; border:0; padding:0; margin:0; cursor:pointer; display:flex; align-items:center; justify-content:center;"><i class="material-icons" style="color:#9e9e9e;">visibility_off</i></button>
                </div>

                <%-- UX-CADASTRO-ORDEM: forca da senha depois de Senha+Confirmar (antes cortava o fluxo entre os 2 campos). --%>
                <%-- SENHA-FORCA-PRISTINE: barra/texto so aparecem depois que o usuario comeca a digitar (senao nasce em vermelho "Muito fraca" sem culpa do usuario). --%>
                <div id="strength-meter" style="display:none;">
                    <div class="password-strength">
                        <div class="strength-bar" id="strength-bar"></div>
                    </div>
                    <div class="strength-text" id="strength-text"></div>
                </div>
                <%-- CADASTRO-REQUISITOS-CARD: lista ficava solta, colada no checkbox de termos logo abaixo -- agora tem cartao proprio (fundo/borda/espacamento). --%>
                <div class="criterio-card">
                    <p class="criterio-card-title">Sua senha deve conter:</p>
                    <ul class="criterio">
                        <li id="length-criterio">Pelo menos 8 caracteres</li>
                        <li id="uppercase-criterio">Pelo menos uma letra maiúscula</li>
                        <li id="lowercase-criterio">Pelo menos uma letra minúscula</li>
                        <li id="number-criterio">Pelo menos um número</li>
                        <li id="special-criterio">Pelo menos um caractere especial</li>
                    </ul>
                </div>

                <c:if test="${respostaCadastro2}"><div class="erro"><h6>A senha e a confirmação não coincidem.</h6></div> </c:if>
                <div style="text-align: left; margin-bottom: 10px">
                    <input type="checkbox" id="termos" name="termos" value="termos" required title="Necessário aceitar termos de uso para realizar cadastro." />
                    <label class="message2" for="termos">Termos de uso. <a href="#modal1" class="modal-trigger terms-link">Para ler clique aqui.</a></label>
                </div>
                <button class="blue accent-1" type="submit">cadastrar</button>
                <p class="message">Já é cadastrado? <a href="#" id="show-login-form">Entre</a></p>
            </form>

            <form id="reset-password-form" class="reset-password-form" action="ResetPasswordServlet" method="POST" style="display:none;">
                <input name="email" type="email" placeholder="email" aria-label="email" pattern="\w+(\+?\w+)@\w+(\.\w+)+" required title="email@exemplo.com" maxlength="100"/>
                <button class="blue accent-1" type="submit" >Enviar</button>
                <p class="message"><a href="#" id="back-to-login">Voltar ao login</a></p>
            </form>
        </div>    

        <div id="modal1" class="modal">
            <div class="modal-content">
                <h4>Termos de Uso</h4>
                <ol>
                    <b><li>Aceitação dos Termos</li></b>
                    <p>Ao se registrar na plataforma, o usuário concorda com os presentes Termos de Uso. Estes termos regem o acesso e uso da plataforma colaborativa.</p>
                    <b><li>Uso da Plataforma</li></b>
                    <p>A plataforma é destinada a colaboração entre usuários. Qualquer atividade na plataforma deve estar de acordo com as políticas da organização. O uso indevido ou fora do propósito pode resultar na suspensão ou cancelamento do acesso.</p>
                    <b><li>Propriedade dos Dados</li></b>
                    <p>Os dados inseridos na plataforma são de propriedade da organização. Esses dados não devem ser compartilhados ou utilizados fora da plataforma sem autorização expressa.</p>
                    <b><li>Alterações nos Termos</li></b>
                    <p>A organização pode alterar os Termos de Uso. Caso isso ocorra, o usuário será notificado por e-mail.</p>
                    <b><li>Responsabilidades do Usuário</li></b>
                    <p>O usuário se compromete a não violar leis aplicáveis ao utilizar a plataforma, e a manter as informações de cadastro corretas e atualizadas.</p>
                </ol>

                <h4>Política de Privacidade</h4>
                <ol>
                    <b><li>Dados Coletados</li></b>
                    <p>Durante o cadastro, coletamos apenas nome, usuário e e-mail. O e-mail é usado para redefinição de senha e para envio dos dados pessoais registrados na ferramenta.</p>
                    <b><li>Uso dos Dados</li></b>
                    <p>Os dados coletados têm como única finalidade o funcionamento da plataforma. Não há compartilhamento de informações com terceiros.</p>
                    <b><li>Direitos do Usuário</li></b>
                    <p>O usuário pode solicitar a anonimização de seus dados pessoais, assim como pode solicitar, a qualquer momento, a lista dos dados pessoais que a plataforma possui sobre ele.</p>
                </ol>
            </div>
            <div class="modal-footer">
                <a href="#" class="modal-close waves-effect waves-green btn-flat">Fechar</a>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // MAT-CONSOLIDA: Materialize local expõe window.Materialize, não window.M.
                var modalInstance = Materialize.Modal.init($('#modal1'))[0];
                $('.modal-trigger').on('click', function (e) {
                    e.preventDefault();
                    modalInstance.open();
                });
            });

            function toggleSenhaVisibility(inputId, buttonId) {
                var input = document.getElementById(inputId);
                var icon = document.getElementById(buttonId).querySelector('i');
                if (input.type === 'password') {
                    input.type = 'text';
                    icon.textContent = 'visibility';
                } else {
                    input.type = 'password';
                    icon.textContent = 'visibility_off';
                }
            }

            function checkPasswordStrength() {
                const password = document.getElementById('senha').value;
                const strengthBar = document.getElementById('strength-bar');
                const strengthText = document.getElementById('strength-text');
                document.getElementById('strength-meter').style.display = password.length > 0 ? 'block' : 'none';
                let strength = 0;

                const lengthcriterio = document.getElementById('length-criterio');
                const uppercasecriterio = document.getElementById('uppercase-criterio');
                const lowercasecriterio = document.getElementById('lowercase-criterio');
                const numbercriterio = document.getElementById('number-criterio');
                const specialcriterio = document.getElementById('special-criterio');

                lengthcriterio.classList.remove('valid');
                uppercasecriterio.classList.remove('valid');
                lowercasecriterio.classList.remove('valid');
                numbercriterio.classList.remove('valid');
                specialcriterio.classList.remove('valid');

                if (password.length >= 8) {
                    strength += 1;
                    lengthcriterio.classList.add('valid');
                }
                if (/[A-Z]/.test(password)) {
                    strength += 1;
                    uppercasecriterio.classList.add('valid');
                }
                if (/[a-z]/.test(password)) {
                    strength += 1;
                    lowercasecriterio.classList.add('valid');
                }
                if (/[0-9]/.test(password)) {
                    strength += 1;
                    numbercriterio.classList.add('valid');
                }
                if (/[^A-Za-z0-9]/.test(password)) {
                    strength += 1;
                    specialcriterio.classList.add('valid');
                }

                let strengthPercentage = (strength / 5) * 100;
                let strengthColor = '';
                let strengthMsg = '';

                switch (strength) {
                    case 0:
                    case 1:
                        strengthColor = 'red';
                        strengthMsg = 'Muito Fraca';
                        break;
                    case 2:
                        strengthColor = 'orange';
                        strengthMsg = 'Fraca';
                        break;
                    case 3:
                        strengthColor = 'yellow';
                        strengthMsg = 'Média';
                        break;
                    case 4:
                        strengthColor = 'lightgreen';
                        strengthMsg = 'Forte';
                        break;
                    case 5:
                        strengthColor = 'green';
                        strengthMsg = 'Muito Forte';
                        break;
                }

                strengthBar.style.width = strengthPercentage + '%';
                strengthBar.style.backgroundColor = strengthColor;
                strengthText.textContent = strengthMsg;
            }
        </script>

        <script>
            $(document).ready(function () {
                // LOGIN-TITULO-BUG: titulo ("Login"/"Cadastro"/"Redefinir Senha") acompanha o formulario visivel.
                function setFormHeading(text) {
                    $('#form-heading').text(text);
                }

                $('#show-register-form').click(function (e) {
                    e.preventDefault();
                    $('#login-form').hide();
                    $('#reset-password-form').hide();
                    $('#register-form').fadeIn();
                    setFormHeading('Cadastro');
                });

                $('#show-login-form').click(function (e) {
                    e.preventDefault();
                    $('#register-form').hide();
                    $('#reset-password-form').hide();
                    $('#login-form').fadeIn();
                    setFormHeading('Login');
                });

                $('#forgot-password-link').click(function (e) {
                    e.preventDefault();
                    $('#login-form').hide();
                    $('#register-form').hide();
                    $('#reset-password-form').fadeIn();
                    setFormHeading('Redefinir Senha');
                });

                $('#back-to-login').click(function (e) {
                    e.preventDefault();
                    $('#reset-password-form').hide();
                    $('#register-form').hide();
                    $('#login-form').fadeIn();
                    setFormHeading('Login');
                });

                // UX-LOGIN-REABRE: reabre o formulario do erro, nao sempre o login.
                var erroCadastro = ${respostaCadastro or respostaCadastro2 or respostaCadastro3 or respostaCadastro4};
                var erroRedefinicao = ${resposta5 or ErroRedefinicaoSenha};
                if (erroCadastro) {
                    $('#login-form').hide();
                    $('#reset-password-form').hide();
                    $('#register-form').show();
                    setFormHeading('Cadastro');
                } else if (erroRedefinicao) {
                    $('#login-form').hide();
                    $('#register-form').hide();
                    $('#reset-password-form').show();
                    setFormHeading('Redefinir Senha');
                }
            });
        </script>
    <script src="js/csrf.js"></script>
  </main>
    </body>
</html>

