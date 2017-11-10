<!DOCTYPE HTML>
<html>
<head>
	<script type="text/javascript"
			src="https://www.gstatic.com/charts/loader.js"></script>
	<script>
		google.charts.load('current', {'packages': ['timeline']});
		google.charts.setOnLoadCallback(drawChart);

		function drawChart() {
			var data = new google.visualization.DataTable();
			data.addColumn('string', 'Staff');
			data.addColumn('string', 'Duration');
			data.addColumn('date', 'Login Time');
			data.addColumn('date', 'Logout Time');
			<?php
			$handle = mysqli_connect("dedicated-sql.reborncraft.co", "www-data", "MDHwMZLttSMVxW3k", "SysLogin_BanManager");
			if (mysqli_connect_errno() !== null && !empty ($handle)) {
				$beginTime = date("c", time() - (isset($_GET['coverage']) ? $_GET['coverage'] : 172800));
				$endTime = date("c", time());
				$statsData = doRawQuery($handle, "SELECT * FROM `StaffStats` WHERE `LOGOUT` >= '$beginTime' AND `LOGIN` <= '$endTime'");
				foreach ($statsData as $statsDataRow) {
					$staff = $statsDataRow ['USERNAME'];
					$startUnix = strtotime($statsDataRow ['LOGIN']);
					$start = date("c", $startUnix);
					$endUnix = strtotime($statsDataRow ['LOGOUT']);
					$end = date("c", $endUnix);
					echo "data.addRow(['$staff', '" . timeToStr($endUnix - $startUnix) . "', new Date('$start'), new Date('$end')]);\r\n";
				}
			}
			?>
			var chart = new google.visualization.Timeline(document.getElementById('graph'));
			chart.draw(data, {
				timeline: {
					colorByRowLabel: true,
					showBarLabels: false
				},
				avoidOverlappingGridLines: false,
				hAxis: {
					minValue: new Date('<?php echo $beginTime;?>'),
					maxValue: new Date('<?php echo $endTime;?>')
				},
				height: 480
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

function timeToStr($totalSeconds = 0) {
	$hours = $totalSeconds / 3600;
	$minutes = $totalSeconds % 3600 / 60;
	$seconds = $totalSeconds % 60;
	return ($totalSeconds >= 3600 ? floor($hours) . " hour" . (floor($hours) != 1 ? "s" : "") . " " : "") . ($totalSeconds >= 60 ? floor($minutes) . " minute" . (floor($minutes) != 1 ? "s" : "") . " " : "") . floor($seconds) . " second" . (floor($seconds) != 1 ? "s" : "");
}

?>
