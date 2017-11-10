<!DOCTYPE HTML>
<html>
<head>
	<?php
	$month = (empty($_GET['month']) ? date("Ym") : $_GET['month']) * 1;
	$handle = mysqli_connect("dedicated-sql.reborncraft.co", "www-data", "MDHwMZLttSMVxW3k", "SysLogin_BanManager");
	if (mysqli_connect_errno() !== null && !empty ($handle)) {
		?>
		<script type="text/javascript"
				src="https://www.gstatic.com/charts/loader.js"></script>
		<script>
			google.charts.load('current', {'packages': ['table']});
			google.charts.setOnLoadCallback(drawChart);

			function drawChart() {
				var data = new google.visualization.arrayToDataTable(
					<?php
					$data = array();
					foreach (doRawQuery($handle, "SELECT `USERNAME`,`VOTES` FROM `MonthlyVoteData` WHERE `MONTH` = " . $month) as $entry) {
						$data[$entry['USERNAME']] = $entry['VOTES'];
					}
					arsort($data);
					$serialized = [["Monthly Ranking ($month)", "Player Name", "Vote Count"]];
					$ranking = 0;
					$previousVoteCount = 2147483647;
					foreach ($data as $staffName => $voteCount) {
						if ($previousVoteCount > $voteCount) {
							$ranking++;
						}
						array_push($serialized, [$ranking, $staffName, $previousVoteCount = $voteCount * 1]);
					}
					echo json_encode($serialized);
					?>
				);
				var chart = new google.visualization.Table(document.getElementById('graph'));
				chart.draw(data, {});
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
