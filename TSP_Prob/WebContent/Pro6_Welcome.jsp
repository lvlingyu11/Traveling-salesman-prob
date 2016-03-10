<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Final Project</title>
</head>
<body>
<h1><b>Welcome to the Cutting Stock App</b></h1>
<form ACTION="Pro6_Servlet">
<table>
      <tr>
      <td>Please enter the problem need to be solved(1 to 10): </td>
      <td><input type="text" name="prob_num"></td>
      </tr>
      <tr>
      <td>Population: </td>
      <td><input type="text" name="pop"></td>
      </tr>
      <tr>
      <td>Generation Size: </td>
      <td><input type="text" name="gen_size"></td>
      </tr>
      <tr>
      <td></td>
      <td><input type="submit" value="Submit"></td>
      </tr>
</table>
</form>
</body>
</html>