<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>TSP - GA with Wisdom of Crowds</title>
</head>
<body>
<h1><b>Welcome to the TSP with GA and Wisdom of Crowds</b></h1>
<form ACTION="WOC_Servlet">
<table>
      <tr>
      <td># of cities(11, 22, 44, 77, 97, 222):</td>
      <td><input type="text" name="nCity"></td>
      </tr>
      <tr>
      <td>Population:</td>
      <td><input type="text" name="pop"></td>
      </tr>
      <tr>
      <td>Generation Size:</td>
      <td><input type="text" name="gen_size"></td>
      </tr>
      <tr>
      <td>Mutation[%]:</td>
      <td><input type="text" name="mut_rate"></td>
      </tr>
      <tr>
      <td>Mutation Method:</td>
      <td><input type="radio" name="mutation_alg" value="Swap">swap</td>
      </tr>
      <tr>
      <td></td>
      <td><input type="radio" name="mutation_alg" value="Reverse_Mutation">Reverse_Mutation</td>
      </tr>
      <tr>
      <td></td>
      <td><input type="submit" value="Submit"></td>
      </tr>
</table>
</form>
</body>
</html>