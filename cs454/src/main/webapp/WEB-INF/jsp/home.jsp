<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

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
</body>
</html>