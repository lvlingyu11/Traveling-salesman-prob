<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Genetic Algorithm</title>
</head>
<body>
<h1><b>Welcome to TSP Solver with Genetic Algorithm</b></h1>
<form ACTION="Genetic_Servlet">
<table>
      <tr>
      <td># of cities:</td>
      <td><input type="text" name="nCity"></td>
      </tr>
      <tr>
      <td>Population:</td>
      <td><input type="text" name="pop"></td>
      </tr>
      <tr>
      <td>Generation size:</td>
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