<%-- 
    Document   : legal
    Created on : 30-Mar-2014, 3:19:31 PM
    Author     : Steven
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <jsp:useBean id="legalVM" type="models.LegalViewModel" scope="session" />
        <jsp:useBean id="user" type="models.User" scope="session" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript"></script>
        <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js" type="text/javascript"></script>
        <script src="js/footable.js" type="text/javascript"></script>
        <script src="js/footable.paginate.js" type="text/javascript"></script>
        <link href="css/footable.core.css" rel="stylesheet" type="text/css" />
        <link href="css/footable.metro.css" rel="stylesheet" type="text/css" />
        <link rel="stylesheet" type="text/css" href="css/style.css">
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/start/jquery-ui.css">
        <script type="text/javascript">
            
        </script>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Legal Homepage</title>
    </head>
    <body class="backgreen">
        <div class="nav"> 
            <p> Logged in as: <%= user.getStringParam("fname") %> <%= user.getStringParam("lname") %> </p>
            <a href="LogoutServlet"> Log Out </a>
        </div>
        <div class="view-container rounded backteal">            
            
        </div>
    </body>
</html>
