/**
 * 
 */
$(document).ready(function() {
	function getCpuUsage() {
		$.ajax({
			type: "GET",
			url: "/server/getCpuUsage",
			success: function(cpuUsage) {
				$("#cpuUsage").html(cpuUsage);
				$("#cpuProgress").css("width", cpuUsage + "%").attr("aria-valuenow", cpuUsage);
			},
			error: function() {
				console.log("Error fetching CPU usage");
			}
		});
	}
	function getMemoryUsage() {
		$.ajax({
			type: "GET",
			url: "/server/getMemoryUsage",
			success: function(memoryUsage) {
				$("#memoryUsage").html(memoryUsage);
				$("#memoryProgress").css("width", memoryUsage + "%").attr("aria-valuenow", memoryUsage);
			},
			error: function() {
				console.log("Error fetching Memory usage");
			}
		});
	}
	function getDiskUsage() {
		$.ajax({
			type: "GET",
			url: "/server/getDiskUsage",
			success: function(diskUsage) {
				$("#diskUsage").html(diskUsage);
				$("#diskProgress").css("width", diskUsage).attr("aria-valuenow", diskUsage);
			
			},
			error: function() {
				console.log("Error fetching Disk usage");
			}
		});
	}
	getCpuUsage();
	getMemoryUsage();
	getDiskUsage();
	setInterval(getCpuUsage, 1000);
	setInterval(getMemoryUsage, 1000);
});
