/**
 * 
 */
$(document).ready(function() {
	function getCpuAllServerAvg() {

		document.getElementById("cpuUsage").innerText = "loading...";
		$.ajax({
			type: "GET",
			url: "/server/getCpuAllServerAvg",
			success: function(cpuUsage) {
				$("#cpuUsage").html(cpuUsage + " %");
				$("#cpuProgress").css("width", cpuUsage + "%").attr("aria-valuenow", cpuUsage);
			},
			error: function() {
				console.log("Error fetching CPU usage");
			}
		});
	}
	function getMemoryAllServerAvg() {
		document.getElementById("memoryUsage").innerText = "loading...";
		$.ajax({
			type: "GET",
			url: "/server/getMemoryAllServerAvg",
			success: function(memoryUsage) {
				$("#memoryUsage").html(memoryUsage + " %");
				$("#memoryProgress").css("width", memoryUsage + "%").attr("aria-valuenow", memoryUsage);
			},
			error: function() {
				console.log("Error fetching Memory usage");
			}
		});
	}
	function getDiskAllServerAvg() {
		document.getElementById("diskUsage").innerText = "loading...";

		$.ajax({
			type: "GET",
			url: "/server/getDiskAllServerAvg",
			success: function(diskUsage) {
				$("#diskUsage").html(diskUsage + " %");
				$("#diskProgress").css("width", diskUsage).attr("aria-valuenow", diskUsage);

			},
			error: function() {
				console.log("Error fetching Disk usage");
			}
		});
	}
	function getCountEncErrorAllServer() {
		var url = "/server/getCountEncErrorAllServer";
		document.getElementById("EncErrorCount").innerText = "loading...";

		fetch(url)
			.then(response => {
				return response.json();
			})
			.then(data => {
				document.getElementById("EncErrorCount").innerText = data;
			})
			.catch(error => {
				document.getElementById("EncErrorCount").innerText = "Error!";
				console.error("Error fetching Enc Error Counting:", error);
			});
	}
	getCpuAllServerAvg();
	getMemoryAllServerAvg();
	getDiskAllServerAvg();
	getCountEncErrorAllServer();
	setInterval(getCpuAllServerAvg, 10000);
	setInterval(getMemoryAllServerAvg, 10000);
});
