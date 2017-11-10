<!DOCTYPE HTML>
<html>
<head>
	<script type="text/javascript"
			src="https://www.gstatic.com/charts/loader.js"></script>
	<script>
		google.charts.load('current', {'packages': ['corechart']});
		google.charts.setOnLoadCallback(drawChart);

		function drawChart() {
			<?php
			$out = [['Name', "Total Online Time (hours)", "Punishments Issued", "Rank", "Punishes / On-time Ratio"]];
			$handle = mysqli_connect("dedicated-sql.reborncraft.co", "www-data", "MDHwMZLttSMVxW3k", "SysLogin_BanManager");
			if (mysqli_connect_errno() !== null && !empty ($handle)) {
				$staffs = array();
				foreach (doRawQuery($handle, "SELECT * FROM `Staffs`") as $staff) {
					$name = $staff['USERNAME'];
					$staffs[$name] = [$name, 0, 0, getRank($staff['PERMISSIONS']), 0];
				}
				foreach (["Ban", "Mute", "Warn"] as $t) {
					foreach (doRawQuery($handle, "SELECT `BY` FROM `User{$t}s`") as $add) {
						if (isset($staffs[$add["BY"]])) {
							$staffs[$add["BY"]][2] += 1;
						}
					}
				}
				unset($add);
				foreach (doRawQuery($handle, "SELECT `USERNAME`,TIMESTAMPDIFF(SECOND, `LOGIN`, `LOGOUT`) FROM `StaffStats`") as $add) {
					if (!empty($staffs[$add["USERNAME"]])) {
						$staffs[$add["USERNAME"]][1] += $add["TIMESTAMPDIFF(SECOND, `LOGIN`, `LOGOUT`)"] / 3600;
					}
				}
				unset($add);
				foreach ($staffs as $entry) {
					$entry[4] = $entry[2] / $entry[1];
					array_push($out, $entry);
				}
			}
			?>
			var data = new google.visualization.arrayToDataTable(<?php echo json_encode($out);?>);
			var chart = new google.visualization.BubbleChart(document.getElementById('graph'));
			chart.draw(data, {
				title: 'Correlation ban/mute/warn count, rank and online time.',
				hAxis: {
					title: 'Total Online Time (hours)'
				},
				vAxis: {
					title: 'Punishments Issued'
				},
				animation: {
					startup: true
				},
				bubble: {
					opacity: 0.5
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

function getRank($permissions = 0) {
	if ($permissions == -1) {
		return "Owner / Sr. Admin";
	} else if (($permissions >> 3) % 2 == 1) {
		return "Administrator";
	} else if (($permissions >> 2) % 2 == 1) {
		return "Sr. Moderator";
	} else if (($permissions >> 1) % 2 == 1) {
		return "Moderator";
	} else if ($permissions % 2 == 1) {
		return "Helper";
	}
	return "No Ranks";
}

function timeToStr($totalSeconds = 0) {
	$hours = $totalSeconds / 3600;
	$minutes = $totalSeconds % 3600 / 60;
	$seconds = $totalSeconds % 60;
	return ($totalSeconds >= 3600 ? floor($hours) . " hour" . (floor($hours) != 1 ? "s" : "") . " " : "") . ($totalSeconds >= 60 ? floor($minutes) . " minute" . (floor($minutes) != 1 ? "s" : "") . " " : "") . floor($seconds) . " second" . (floor($seconds) != 1 ? "s" : "");
}

?>
