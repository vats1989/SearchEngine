<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<script>
	$(function() {
		var availableTags = [ "ActionScript", "AppleScript", "Asp",
				"BASIC", "C", "C++", "Clojure", "COBOL", "ColdFusion",
				"Erlang", "Fortran", "Groovy", "Haskell", "Java", "JavaScript",
				"Lisp", "Perl", "PHP", "Python", "Ruby", "Scala", "Scheme" ];
		$("#search").autocomplete({
			source : availableTags
		});
	});
</script>
<style>
.center {
	margin: auto;
	width: 60%;
	padding: 10px;
}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
</head>
<body>

	<div class="page-header" align="center">
		<h1>
			CS454 <small>Search Engine</small>
		</h1>
	</div>

	<div>
		<form action="search.html" method="get">
			<div class="center" style="align: auto">
				<div class="col-lg-6">
					<div class="input-group" style="width: 800px;">
						<input type="text" name="term" id="search" class="form-control"
							placeholder="Search for...">
					</div>
					<span class="input-group-btn"> <input type="submit"
						class="btn btn-default" value="Search" />
					</span>

				</div>
			</div>
		</form>
	</div>
	<div></div>
	<div></div>
	<div></div>

	<c:if test="${result == null}">
		<table align="center" border="0" style="width: 800px">
			<tr>
				<td>
					<h3 align="center">
						<font style="" color="#0066FF">*No Result Found...Search
							Again...</font>
					</h3>
				</td>
			</tr>
		</table>
	</c:if>
	<br/><br/><br/><br/><br/><br/>
	<c:if test="${result != null}">
		<center>
			<div class="input-group" style="width: 800px;">
				<table align="center" border="0" style="width: 800px">
					<thead>
						<tr>
							<th>Document</th>
							<th>Score</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${result}" var="res">
							<tr>
								<td><a href="${res.key}">${res.key}</a></td>
								<td>${res.value}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</center>
	</c:if>
</body>
</html>