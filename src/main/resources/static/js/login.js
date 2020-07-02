$("#zhanghaoBtn").click(function(){
	var zhanghu_yonghuming = $("#zhanghu_yonghuming").val();
	var zhanghu_mima = $("#zhanghu_mima").val();
	if(zhanghu_yonghuming==""){
		$("#login-form-tips1").show().text("请输入用户名")
	}
	if(zhanghu_mima==""){
		$("#login-form-tips2").show().text("请输入密码")
	}
	if(zhanghu_yonghuming!=""&& zhanghu_mima!=""){
		console.log("用户名和密码已输入")
	}
});
$("#zhanghu_yonghuming").change(function(){
	$("#login-form-tips1").hide();
});
$("#zhanghu_mima").change(function(){
	$("#login-form-tips2").hide();
});


if (!(!!window.ActiveXObject || "ActiveXObject" in window)){//非ie
	$('a[href="#name"]').click(function(){
		$('.tip_ca').hide();
	});
	$('a[href="#CA"]').click(function(){
		$('.tip_ca').show();
	});
	$('#btn_submit_ca').attr('disabled', true);
};
// ca随机码签名
$("#btn_submit_ca").click(function() {
	var random = $('#random').val();
	var encode = myControl.SignData(random);
	console.log('encode: ' + encode);
	alert('encode: ' + encode);
	$('#random').val(encode);
});
// 用户登录验证码
$("#img").click(function(){
	$("#img").attr("src","/cas/code?"+new Date().getTime());
});
