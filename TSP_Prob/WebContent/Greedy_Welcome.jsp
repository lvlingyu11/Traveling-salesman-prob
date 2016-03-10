<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>TSP with Greedy Search</title>
</head>
<body>
<CENTER>
<h1><b>Welcome to TSP Problem with Greedy Heuristic</b></h1>
</CENTER>
Please start with clicking with Start <BR>
<form ACTION="Greedy_Servlet">
<table>
       <tr>
       <td>Please select the number of cities(one at a time):</td>
       <td><input type="checkbox" name="num_city" value="30">30</td>
       </tr>
       <tr>
       <td></td>
       <td><input type="checkbox" name="num_city" value="40">40</td>
       </tr>
       <tr>
       <td></td>
       <td><input type="submit" value="Submit"></td>
       </tr> 
</table>
</form>
</body>
</html>