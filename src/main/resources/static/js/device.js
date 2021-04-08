$(function(){
	$("#add").click(add_device);
	$("#change").click(change_device);
	$(".close").click(delete_msg);
});

function add_device() {
	$("#addDevice").modal("hide");
	var userID = $("#addUserID").val();
	var deviceID = $("#addDeviceID").val();
	var activationCode = $("#addActivationCode").val();
	var purpose = $("#addPurpose").val();
	var other = $("#addOther").val();
	var latitude = $("#addLatitude").val();
	var longitude = $("#addLongitude").val();
	$.post(
		CONTEXT_PATH+"/addDevice",
		{"userId":userID,"deviceId":deviceID,"activationCode":activationCode,
			"purpose":purpose,"other":other,"latitude":latitude,"longitude":longitude},
		function (data) {
			data = $.parseJSON(data);
			if(data.code == 0){
				$("#addHintBody").text(data.msg);
			}else{
				$("#addHintBody").text(data.msg);
			}

			$("#addHintModal").modal("show");
			setTimeout(function(){
				$("#addHintModal").modal("hide");
				location.reload();
			}, 2000);

		}
	)

}
function change_device() {
	$("#changeDevice").modal("hide");
	var deviceID = $("#changeDeviceID").val();
	var purpose = $("#changePurpose").val();
	var other = $("#changeOther").val();
	var latitude = $("#changeLatitude").val();
	var longitude = $("#changeLongitude").val();
	$.post(
		CONTEXT_PATH+"/changeDevice",
		{"deviceId":deviceID,"purpose":purpose,"other":other,"latitude":latitude,"longitude":longitude},
		function (data) {
			data = $.parseJSON(data);
			if(data.code == 0){
				$("#addHintBody").text(data.msg);
			}else{
				$("#addHintBody").text(data.msg);
			}

			$("#addHintModal").modal("show");
			setTimeout(function(){
				$("#addHintModal").modal("hide");
				location.reload();
			}, 2000);

		}
	)

}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}