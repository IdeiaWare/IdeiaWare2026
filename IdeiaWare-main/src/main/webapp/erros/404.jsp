<%-- 
    Document   : 404
    Created on : May 11, 2018, 9:54:28 PM
    Author     : Gustavo Armborst Guedes de Azevedo
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="pt-BR">
  <head>
    <title>IdeiaWare - 404</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">
    <link rel='stylesheet prefetch' href='https://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700,900'>
    <link rel='stylesheet prefetch' href='https://fonts.googleapis.com/css?family=Montserrat:400,700'>
    <link rel='stylesheet prefetch' href='https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css'>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link href='https://fonts.googleapis.com/css?family=Condiment' rel='stylesheet'>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/materialize.min.css"  media="screen,projection"/>

  </head>
  <style>
    .title-app {color:#fff; font-family: 'Condiment'; font-size: 42px; line-height: 25px}
    .title-app2 {color:#fff; font-family: 'Condiment'; font-size: 100px;}
    .card .icon {font-size:50px;}
    .card-content .card-title {color:#fff !important;}
    .card a {color:#fff;}
    .icon .fa-circle {color:#fff;}
    .new-idea .icon .fa-comments {color:#4db6ac;}
    .storytelling .icon .fa-pencil {color:#5c6bc0}
    .toolkit .icon .fa-briefcase {color:#c62828;}
    .knowledge .icon .fa-database {color:#0288d1;}
    .fa-hand-o-right {border-radius:50%; box-shadow: 0 0 0 rgba(255,255,255, 0.4); animation: pulse 2s infinite;}
    .fa-hand-o-right:hover {animation: none;}

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
  </style>

  <body class="blue-grey lighten-5">
    <nav>
      <div class="nav-wrapper blue-grey lighten-1 z-depth-2">
        <a  class="brand-logo" style="left: 50px">
          <ul style="width:300px" id="nav-mobile" class="left hide-on-med-and-down">
            <div class="row" style="padding-left: 10px">
              <div class="col s1 blue-grey lighten-3" style=" width: 50px;  height: 50px; 
                   margin-top: 5px;  padding: 6px 6px; 
                   border-radius: 100%;  box-sizing: border-box;">
                <img style="display: block;  width: 62%;  margin-left: 7px; margin-top: -1px" src="${pageContext.request.contextPath}/imagens/idea.png"/>
              </div>
              <h1 class=" col s4 center-align title-app">IdeiaWare</h1>
            </div>
          </ul>
        </a>
      </div>
    </nav>
    <br>
    <br>
    <br>
    <br>
    <br>
    <br>
    <br>
    <h2 class="center-align">Erro 404 - Página não encontrada</h2>
  </body>
</html>
