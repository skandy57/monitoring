/**
 * 
 */
$(document).ready(function() {
	function getCpuAllServerAvg() {
		$.ajax({
			type: "GET",
			url: "/server/getCpuAllServerAvg",
			success: function(cpuUsage) {
				$("#cpuUsage").html(cpuUsage);
				$("#cpuProgress").css("width", cpuUsage + "%").attr("aria-valuenow", cpuUsage);
			},
			error: function() {
				console.log("Error fetching CPU usage");
			}
		});
	}
	function getMemoryAllServerAvg() {
		$.ajax({
			type: "GET",
			url: "/server/getMemoryAllServerAvg",
			success: function(memoryUsage) {
				$("#memoryUsage").html(memoryUsage);
				$("#memoryProgress").css("width", memoryUsage + "%").attr("aria-valuenow", memoryUsage);
			},
			error: function() {
				console.log("Error fetching Memory usage");
			}
		});
	}
	function getDiskAllServerAvg() {
		$.ajax({
			type: "GET",
			url: "/server/getDiskAllServerAvg",
			success: function(diskUsage) {
				$("#diskUsage").html(diskUsage);
				$("#diskProgress").css("width", diskUsage).attr("aria-valuenow", diskUsage);
			
			},
			error: function() {
				console.log("Error fetching Disk usage");
			}
		});
	}
	getCpuAllServerAvg();
	getMemoryAllServerAvg();
	getDiskAllServerAvg();
	setInterval(getCpuAllServerAvg, 1000);
	setInterval(getMemoryAllServerAvg, 1000);
});
