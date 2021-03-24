$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
			CONTEXT_PATH+"/discuss/add",
		    {"title":title,"content":content},
		function (data) {
			data =$.parseJSON(data);
			//在提示框中修改状态
			$("#hintBody").text(data.msg);

			//显示提示框
			$("#hintModal").modal("show");
			//两秒钟后消失
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//成功发布就刷新页面
				if(data.code==0){
					window.location.reload();
				}
			}, 2000);
		}
	)

}