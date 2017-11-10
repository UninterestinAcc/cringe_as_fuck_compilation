<!DOCTYPE HTML>
<html>
<head>
	<?php
	$handle = mysqli_connect("dedicated-sql.reborncraft.co", "www-data", "MDHwMZLttSMVxW3k", "SysLogin_BanManager");
	if (mysqli_connect_errno() !== null && !empty ($handle)) {
		?>
		<script type="text/javascript"
				src="https://www.gstatic.com/charts/loader.js"></script>
		<script>
			google.charts.load('current', {'packages': ['corechart']});
			google.charts.setOnLoadCallback(drawChart);

			function drawChart() {
				var data = new google.visualization.arrayToDataTable(
					<?php
					$data = array();
					foreach (["Ban", "Mute", "Warn"] as $t) {
						foreach (doRawQuery($handle, "SELECT `BY` FROM `User{$t}s`") as $entry) {
							if (empty($data[$entry['BY']])) {
								$data[$entry['BY']] = 1;
							} else {
								$data[$entry['BY']]++;
							}
						}
					}
					$serialized = [["Staff Name", "Ban Count"]];
					foreach ($data as $staffName => $banCount) {
						array_push($serialized, [$staffName, $banCount]);
					}
					echo json_encode($serialized);
					?>
				);
				var chart = new google.visualization.PieChart(document.getElementById('graph'));
				chart.draw(data, {
					is3D: true,
					sliceVisibilityThreshold: 1 / 50,
					legend: {
						position: 'labelled'
					},
					height: 480
				});
			}
		</script>
		<?php
	}
	?>
</head>
<body>
<div id='graph'></div>
</body>
</html>
<?php
function doRawQuery($mysql, $query) {
	try {
		$r = array();
		$res = mysqli_query($mysql, $query);
		if ($res !== false) {
			while ($row = mysqli_fetch_assoc($res)) {
				array_push($r, $row);
			}
		}
		return $r;
	} catch (Exception $e) {
		return array();
	}
}

?>
