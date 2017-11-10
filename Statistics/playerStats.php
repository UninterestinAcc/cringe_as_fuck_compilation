<!DOCTYPE HTML>
<html>
<head>
	<?php
	$handle = mysqli_connect("dedicated-sql.reborncraft.co", "www-data", "MDHwMZLttSMVxW3k", "SysLogin_BanManager");
	if (mysqli_connect_errno() !== null && !empty ($handle)) {
	$beginTime = date("c", time() - (isset($_GET['coverage']) ? $_GET['coverage'] : 172800));
	$endTime = date("c", time());
	$statsData = doRawQuery($handle, "SELECT * FROM `PlayerStatistics` WHERE `TIME` >= '$beginTime' AND `TIME` <= '$endTime'");
	$statArray = array();
	foreach ($statsData as $data) {
		$timeKey = round(strtotime($data["TIME"]) / 300);
		foreach (array("LOGIN_COUNT", "LOGOUT_COUNT", "PLAYER_COUNT") as $key) {
			$statArray[$timeKey][$key] = $data[$key] + (isset($statArray[$timeKey][$key]) ? $statArray[$timeKey][$key] : 0);
		}
	}
	?>
	<script type="text/javascript"
			src="https://www.gstatic.com/charts/loader.js"></script>
	<script>
		google.charts.load('current', {'packages': ['line']});
		google.charts.setOnLoadCallback(drawChart);

		function drawChart() {
			var data = new google.visualization.DataTable();
			data.addColumn('date', 'Time');
			data.addColumn('number', 'Join Count');
			data.addColumn('number', 'Quit Count');
			data.addColumn('number', 'Players Count');
			<?php
			echo "data.addRows([";
			$dataBuffer = "";
			foreach ($statArray as $time => $data) {
				$dataBuffer .= "[new Date(" . ($time * 300000) . ")," . $data['LOGIN_COUNT'] . "," . $data['LOGOUT_COUNT'] . "," . $data['PLAYER_COUNT'] . "],";
			}
			echo substr($dataBuffer, 0, -1) . "]);";
			}
			?>
			var chart = new google.charts.Line(document.getElementById('graph'));
			chart.draw(data, {
				height: 480,
				curveType: 'function',
				hAxis: {
					logScale: true,
					scaleType: "mirrorLog"
				}
			});
		}
	</script>
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
